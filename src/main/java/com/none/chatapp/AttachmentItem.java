package com.none.chatapp;

import com.none.chatapp_commands.Message;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.none.chatapp_commands.SendMessageCommand;

class AttachmentItem extends HBox {
    private static final double PADDING = 10;
    private static final double ARC_SIZE = 20;
    private static final double MAX_BUBBLE_WIDTH = 448; // Set the maximum width for the bubble

    private TextFlow messageTextFlow;
    private ImageView imageView;
    private MediaPlayer mediaPlayer;
    private HBox audioControls;
    Path filePath;
    byte[] content;
    Message.Type fileType;
    String ext;

    public AttachmentItem(Path path) throws IOException {
        filePath = path;

        // Create a Text for the message
        Text messageText = new Text(path.getFileName().toString());
        messageText.setTextAlignment(TextAlignment.LEFT);
        messageText.setFill(Color.WHITE);

        // Create a TextFlow for the text to apply wrapping
        messageTextFlow = new TextFlow(messageText);
        messageTextFlow.setMaxWidth(MAX_BUBBLE_WIDTH); // Set maximum width for the bubble
        messageTextFlow.setPadding(new Insets(PADDING));

        // Style the TextFlow
        messageTextFlow.setBackground(new Background(new BackgroundFill(
                Color.web("#303030"), new CornerRadii(ARC_SIZE), Insets.EMPTY)));

        // Create a close button
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        closeButton.setOnAction(event -> {
            // Remove this AttachmentItem from its parent
            deleteMe();
        });

        // Create a StackPane to position the close button and TextFlow
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(messageTextFlow, closeButton);
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(5)); // Add some margin to position the button

        // Create a VBox to hold the StackPane and ensure it grows with the parent
        VBox vBox = new VBox(stackPane);
        vBox.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        // Add the VBox to the HBox
        this.getChildren().add(vBox);

        // Add padding around the HBox
        this.setSpacing(10); // Space between bubble and labels
        this.setPadding(new Insets(10)); // Add padding around the HBox

        // Make sure HBox resizes based on VBox width
        this.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);

        ext = Utils.getFileExtension(filePath).toLowerCase();
        content = Files.readAllBytes(filePath);
        switch (ext) {
            case "png":
            case "jpg":
            case "jpeg":
                fileType = Message.Type.image;
                setImage();
                break;
            case "mp3":
                fileType = Message.Type.audio;
                setAudio();
                break;
            default:
                fileType = Message.Type.attachment;
                Platform.runLater(() -> {
                    messageTextFlow.setVisible(true);
                });
                break;
        }
    }

    public void setImage() {
        if (content == null) {
            Platform.runLater(() -> {
                messageTextFlow.setVisible(true);
                messageTextFlow.getChildren().clear();
                messageTextFlow.getChildren().add(new Text("Error: Can't Load File"));
            });
        } else {
            Platform.runLater(() -> {
                messageTextFlow.setVisible(false);
                imageView = new ImageView();
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                adjustBubbleSizeForImage(imageView);

                imageView.setImage(new Image(new ByteArrayInputStream(content)));
                adjustBubbleSizeForImage(imageView);

                // Wrap ImageView in a StackPane with padding and rounded corners
                StackPane imageContainer = new StackPane(imageView);
                imageContainer.setPadding(new Insets(PADDING));
                imageContainer.setBackground(new Background(new BackgroundFill(Color.web("#303030"),
                        new CornerRadii(ARC_SIZE), Insets.EMPTY)));

                //this.getChildren().clear();
                this.getChildren().addAll(imageContainer);
            });
        }
    }

    public void setAudio() {
        Platform.runLater(() -> {
            this.getChildren().clear();

            if (content == null) {
                messageTextFlow.setVisible(true);
                messageTextFlow.getChildren().clear();
                messageTextFlow.getChildren().add(new Text("Error: Can't Load Audio"));
            } else {
                messageTextFlow.setVisible(false);

                File audioFile;
                try {
                    audioFile = File.createTempFile("audio_message", ".wav");
                    Files.write(audioFile.toPath(), content, StandardOpenOption.CREATE);
                    audioFile.deleteOnExit();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                Media media = new Media(audioFile.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);

                Slider progressSlider = new Slider();
                progressSlider.setMaxWidth(MAX_BUBBLE_WIDTH);

                Label playPauseLabel = new Label("▶");
                playPauseLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");


                boolean[] isMediaEnded = {false};

                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if (!progressSlider.isValueChanging()) {
                        progressSlider.setValue(newTime.toSeconds());
                    }

                });

                mediaPlayer.setOnReady(() -> {
                    progressSlider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
                });

                progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
                    if (!isChanging) {
                        mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue()));
                    }
                });

                progressSlider.setOnMousePressed(event -> {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue()));
                });

                playPauseLabel.setOnMouseClicked(event -> {
                    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.pause();
                        playPauseLabel.setText("▶");
                    } else {
                        if (isMediaEnded[0]) {
                            isMediaEnded[0] = false;
                            mediaPlayer.seek(mediaPlayer.getCurrentTime()); // Seek to the current slider position
                        }
                        mediaPlayer.play();
                        playPauseLabel.setText("⏸");
                    }
                });

                // Ensure mediaPlayer is not looping
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setOnEndOfMedia(() -> {
                    playPauseLabel.setText("▶");
                    progressSlider.setValue(progressSlider.getMin());
                    isMediaEnded[0] = true;
                    mediaPlayer.pause(); // Ensure it does not restart automatically
                });

                progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (Math.abs(newVal.doubleValue() - progressSlider.getMax()) < 0.1) {
                        playPauseLabel.setText("▶");
                    }
                });

                HBox audioControls = new HBox(playPauseLabel, progressSlider);
                audioControls.setSpacing(10);
                audioControls.setAlignment(Pos.CENTER_LEFT);
                audioControls.setPadding(new Insets(PADDING));
                audioControls.setBackground(new Background(new BackgroundFill(Color.web("#303030"),
                        new CornerRadii(ARC_SIZE), Insets.EMPTY)));


                this.getChildren().addAll(audioControls);
            }
        });
    }

    private void adjustBubbleSizeForImage(ImageView imageView) {
        imageView.imageProperty().addListener((obs, oldImage, newImage) -> {
            if (newImage != null) {
                double imageWidth = newImage.getWidth();
                double imageHeight = newImage.getHeight();

                double maxWidth = 112.0;
                double maxHeight = 72.5;

                if (imageWidth > maxWidth || imageHeight > maxHeight) {
                    double widthScale = maxWidth / imageWidth;
                    double heightScale = maxHeight / imageHeight;
                    double scale = Math.min(widthScale, heightScale);

                    imageView.setFitWidth(imageWidth * scale);
                    imageView.setFitHeight(imageHeight * scale);
                } else {
                    imageView.setFitWidth(imageWidth);
                    imageView.setFitHeight(imageHeight);
                }
            }
        });
    }

    public void upload(HandlerThread thread, int conv_id)
    {
        Message msg = new Message();
        msg.conv_id = conv_id;
        msg.content = ext;
        msg.type = fileType;

        try {
            new SendMessageCommand(msg, content).SendCommand(thread.socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteMe()
    {
        ((Pane) this.getParent()).getChildren().remove(this);
    }

}
