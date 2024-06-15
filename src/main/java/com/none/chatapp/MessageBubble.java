package com.none.chatapp;

import animatefx.animation.FlipOutX;
import com.none.chatapp_commands.Message;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MessageBubble extends HBox {
    private static final double PADDING = 10;
    private static final double ARC_SIZE = 20;
    private static final double MAX_BUBBLE_WIDTH = 448; // Set the maximum width for the bubble

    private TextFlow messageTextFlow;
    private ImageView imageView;
    public int msg_id;
    public Message msg;
    private MediaPlayer mediaPlayer;
    private HBox audioControls;

    public MessageBubble(UsersController controller, ResourceMgr rscMgr, Message msg) {
        this.msg = msg;
        msg_id = msg.id;

        // Create a TextFlow for the text to apply wrapping
        boolean isAttachment = (msg.type == Message.Type.attachment);
        messageTextFlow = createTextFlowWithEmojis(msg.content, isAttachment);
        messageTextFlow.setMaxWidth(MAX_BUBBLE_WIDTH); // Set maximum width for the bubble
        messageTextFlow.setPadding(new Insets(PADDING));

        // Create Labels for time and status
        Label timeLabel = new Label("");
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        Label statusLabel = new Label(msg.seen ? "seen" : "");
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        VBox labelsBox = new VBox(timeLabel, statusLabel);

        // Determine the alignment and colors based on sender
        if (msg.sender_id == controller.selected_user_id) {
            labelsBox.setAlignment(Pos.BOTTOM_LEFT);
            labelsBox.setSpacing(2);
            this.setAlignment(Pos.CENTER_RIGHT);
            messageTextFlow.setBackground(new Background(new BackgroundFill(
                    Color.web("#B8684D"), new CornerRadii(ARC_SIZE), Insets.EMPTY)));
            this.getChildren().addAll(labelsBox, messageTextFlow); // Sent messages: labels on the left
        } else {
            labelsBox.setAlignment(Pos.BOTTOM_RIGHT);
            labelsBox.setSpacing(2);
            this.setAlignment(Pos.CENTER_LEFT);
            messageTextFlow.setBackground(new Background(new BackgroundFill(
                    Color.web("#303030"), new CornerRadii(ARC_SIZE), Insets.EMPTY)));
            this.getChildren().addAll(messageTextFlow, labelsBox); // Received messages: labels on the right
        }

        // Add padding around the HBox
        this.setSpacing(10); // Space between bubble and labels
        this.setPadding(new Insets(10)); // Add padding around the HBox

        // Make sure HBox resizes based on VBox width
        this.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);

        if (isAttachment) {
            Platform.runLater(() -> {
                messageTextFlow.setVisible(true);
                messageTextFlow.setStyle("-fx-font-weight: bold;");
                for (var child : messageTextFlow.getChildren()) {
                    if (child instanceof Text txt) {
                        txt.setStyle(msg.sender_id == controller.selected_user_id ?
                                "-fx-fill: #010927;-fx-underline: true;" :
                                "-fx-fill: #47e4f1;-fx-underline: true;");
                    }
                }
                messageTextFlow.setOnMouseClicked(event -> rscMgr.requestFile(msg, this));
            });
        }
    }


    public void setImage(UsersController controller, byte[] data) {
        if (data == null) {
            Platform.runLater(() -> {
                messageTextFlow.setVisible(true);
                messageTextFlow.getChildren().clear();
                messageTextFlow.getChildren().add(new Text("Error: Can't Load " + msg.content));
            });
        } else {
            Platform.runLater(() -> {
                messageTextFlow.setVisible(false);
                imageView = new ImageView();
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                adjustBubbleSizeForImage(imageView);

                imageView.setImage(new Image(new ByteArrayInputStream(data)));
                adjustBubbleSizeForImage(imageView);

                // Wrap ImageView in a StackPane with padding and rounded corners
                StackPane imageContainer = new StackPane(imageView);
                imageContainer.setPadding(new Insets(PADDING));
                imageContainer.setBackground(new Background(new BackgroundFill(
                        msg.sender_id == controller.selected_user_id ? Color.web("#B8684D") : Color.web("#303030"),
                        new CornerRadii(ARC_SIZE), Insets.EMPTY)));

                this.getChildren().clear();
                VBox spacer = new VBox(); // Empty VBox for spacing consistency
                if (msg.sender_id == controller.selected_user_id) {
                    this.getChildren().addAll(spacer, imageContainer);
                } else {
                    this.getChildren().addAll(imageContainer, spacer);
                }
                // Add click event to ImageView
                imageView.setOnMouseClicked(event -> openImageInNewWindow(imageView.getImage()));
            });
        }
    }

    public void setAudio(UsersController controller, byte[] data) {
        Platform.runLater(() -> {
            this.getChildren().clear();
            VBox spacer = new VBox();

            if (data == null) {
                messageTextFlow.setVisible(true);
                messageTextFlow.getChildren().clear();
                messageTextFlow.getChildren().add(new Text("Error: Can't Load Audio: " + msg.content));
            } else {
                messageTextFlow.setVisible(false);

                File audioFile;
                try {
                    audioFile = File.createTempFile("audio_message", ".wav");
                    Files.write(audioFile.toPath(), data, StandardOpenOption.CREATE);
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
                audioControls.setBackground(new Background(new BackgroundFill(
                        msg.sender_id == controller.selected_user_id ? Color.web("#B8684D") : Color.web("#303030"),
                        new CornerRadii(ARC_SIZE), Insets.EMPTY)));

                if (msg.sender_id == controller.selected_user_id) {
                    this.getChildren().addAll(spacer, audioControls);
                } else {
                    this.getChildren().addAll(audioControls, spacer);
                }
            }
        });
    }




    public void saveAttachment(byte[] data) {
        Platform.runLater(() ->{
            if(data != null)
            {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(msg.content);
                File file = fileChooser.showSaveDialog(null);

                if (file != null) {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(data);
                        Platform.runLater(() -> {
                            System.out.println("File saved: " + file.getAbsolutePath());
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else
            {
                messageTextFlow.setStyle("-fx-underline: false;");
                messageTextFlow.setOnMouseClicked(null);
                messageTextFlow.setStyle("-fx-font-weight: normal;");
                if (messageTextFlow.getChildren().getFirst() instanceof Text txt)
                {
                    txt.setStyle("-fx-fill: #ffffff;-fx-underline: false;");
                    txt.setText("Couldn't Download File: " + txt.getText());
                }

            }
        });
    }

    private void adjustBubbleSizeForImage(ImageView imageView) {
        imageView.imageProperty().addListener((obs, oldImage, newImage) -> {
            if (newImage != null) {
                double imageWidth = newImage.getWidth();
                double imageHeight = newImage.getHeight();

                double maxWidth = 448;
                double maxHeight = 290;

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

    private void openImageInNewWindow(Image image) {
        Stage stage = new Stage();
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        BorderPane root = new BorderPane(imageView);
        root.setStyle("-fx-background-color: #2e2e2e;"); // Set dark background color

        // Apply styles directly to the ImageView
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 10, 0.5, 0, 0);");

        Scene scene = new Scene(root, image.getWidth(), image.getHeight());
        stage.setScene(scene);
        stage.setTitle("Image Viewer");
        stage.show();
    }

    private TextFlow createTextFlowWithEmojis(String text, boolean isAttachment) {
        TextFlow textFlow = new TextFlow();
        Pattern emojiPattern = Pattern.compile(
                "[\\x{1F600}-\\x{1F64F}]|" +    // Emoticons
                        "[\\x{1F300}-\\x{1F5FF}]|" +    // Misc Symbols and Pictographs
                        "[\\x{1F680}-\\x{1F6FF}]|" +    // Transport and Map Symbols
                        "[\\x{1F700}-\\x{1F77F}]|" +    // Alchemical Symbols
                        "[\\x{1F780}-\\x{1F7FF}]|" +    // Geometric Shapes Extended
                        "[\\x{1F800}-\\x{1F8FF}]|" +    // Supplemental Arrows-C
                        "[\\x{1F900}-\\x{1F9FF}]|" +    // Supplemental Symbols and Pictographs
                        "[\\x{1FA00}-\\x{1FA6F}]|" +    // Chess Symbols
                        "[\\x{1FA70}-\\x{1FAFF}]|" +    // Symbols and Pictographs Extended-A
                        "[\\x{1FB00}-\\x{1FBFF}]|" +    // Symbols and Pictographs Extended-B
                        "[\\x{1F1E6}-\\x{1F1FF}]|" +
                        "[\\x{E040}-\\x{F000}]|" +
                        "[\\x{3030}-\\x{3299}]|" +
                        "[\\x{0020}-\\x{00AF}]|" +
                        "[\\x{2600}-\\x{26FF}]|" +
                        "[\\x{2000}-\\x{2F00}]|" + // Misc symbols
                        "[\\x{2700}-\\x{27BF}]|" +      // Dingbats
                        "[\\x{FE00}-\\x{FE0F}]|" +      // Variation Selectors
                        "[\\x{1F100}-\\x{1F1FF}]|" +    // Enclosed Alphanumeric Supplement
                        "[\\x{1F200}-\\x{1F2FF}]|" +    // Enclosed Ideographic Supplement
                        "[\\x{1F300}-\\x{1F5FF}]|" +    // Misc Symbols and Pictographs
                        "[\\x{1F600}-\\x{1F64F}]|" +    // Emoticons
                        "[\\x{1F680}-\\x{1F6FF}]|" +    // Transport and Map Symbols
                        "[\\x{1F700}-\\x{1F77F}]|" +    // Alchemical Symbols
                        "[\\x{1F780}-\\x{1F7FF}]|" +    // Geometric Shapes Extended
                        "[\\x{1F800}-\\x{1F8FF}]|" +    // Supplemental Arrows-C
                        "[\\x{1F900}-\\x{1F9FF}]|" +    // Supplemental Symbols and Pictographs
                        "[\\x{1FA00}-\\x{1FA6F}]|" +    // Chess Symbols
                        "[\\x{1FA70}-\\x{1FAFF}]|" +    // Symbols and Pictographs Extended-A
                        "[\\x{1FB00}-\\x{1FBFF}]"       // Symbols and Pictographs Extended-B
        );
        Matcher matcher = emojiPattern.matcher(text);

        int lastMatchEnd = 0;
        while (matcher.find()) {
            // Add preceding text
            if (matcher.start() > lastMatchEnd) {
                String precedingText = text.substring(lastMatchEnd, matcher.start());
                addStyledTextToFlow(precedingText, textFlow, isAttachment);
            }

            // Add emoji image
            String emojiHex = Integer.toHexString(matcher.group().codePointAt(0));
            try {
                ImageView emojiImageView = new ImageView(new Image(getClass().getResourceAsStream("/com/none/chatapp/icons/emojis/all/" + emojiHex + ".png")));
                double emojiSize = 18; // Set emoji size
                emojiImageView.setFitHeight(emojiSize);
                emojiImageView.setFitWidth(emojiSize);
                emojiImageView.setTranslateY(4); // Adjust vertical alignment if needed
                textFlow.getChildren().add(emojiImageView);
            } catch (Exception e) {
                // Fallback for missing emoji images: display the emoji character itself
                Text fallbackEmoji = new Text(matcher.group());
                fallbackEmoji.setFill(isAttachment ? Color.web("#47e4f1") : Color.WHITE);
                textFlow.getChildren().add(fallbackEmoji);
            }
            lastMatchEnd = matcher.end();
        }

        // Add any remaining text after the last emoji
        if (lastMatchEnd < text.length()) {
            String remainingText = text.substring(lastMatchEnd);
            addStyledTextToFlow(remainingText, textFlow, isAttachment);
        }

        return textFlow;
    }

    private void addStyledTextToFlow(String text, TextFlow textFlow, boolean isAttachment) {
        for (char c : text.toCharArray()) {
            Text txt = new Text(String.valueOf(c));
            if (isAttachment) {
                txt.setFill(Color.web("#47e4f1"));
                txt.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
                txt.setUnderline(true);
            } else {
                txt.setFill(Color.WHITE);
                txt.setFont(Font.font(12));
            }
            textFlow.getChildren().add(txt);
        }
    }

}
