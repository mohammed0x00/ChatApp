package com.none.chatapp_server;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class FTPUploader {
    private static final String FTP_SERVER = "localhost";
    private static final int FTP_PORT = 21;
    private static final String FTP_USERNAME = "chatbus";
    private static final String FTP_PASSWORD = "12345";

    public static String saveFile(int user_id, byte[] data, String ext) {


        FTPClient ftpClient = new FTPClient();
        String filename = null;

        try {
            // Connect to the FTP server
            ftpClient.connect(FTP_SERVER, FTP_PORT);
            ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

            // Check if login was successful
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return null;
            }

            // Enter passive mode
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Create user directory if it doesn't exist
            String userDir = "/" + user_id;
            if (!ftpClient.changeWorkingDirectory(userDir)) {
                if (ftpClient.makeDirectory(userDir)) {
                    ftpClient.changeWorkingDirectory(userDir);
                } else {
                    System.out.println("Failed to create user directory.");
                    return null;
                }
            }

            // Generate a unique filename
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
            LocalDateTime now = LocalDateTime.now();
            String dateTime = now.format(dtf);
            int randomNum = new Random().nextInt(1000);
            filename = dateTime + "_" + randomNum + "." + ext;

            // Upload the byte array as a file to the FTP server
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            boolean success = ftpClient.storeFile(filename, byteArrayInputStream);
            byteArrayInputStream.close();

            if (success) {
                System.out.println("File has been uploaded successfully: " + filename);
            } else {
                System.out.println("Failed to upload file.");
                return null;
            }

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return filename;
    }

    public static byte[] getFile(int user_id, String filename) {

        FTPClient ftpClient = new FTPClient();
        byte[] fileData = null;

        try {
            // Connect to the FTP server
            ftpClient.connect(FTP_SERVER, FTP_PORT);
            ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

            // Check if login was successful
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return null;
            }

            // Enter passive mode
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Navigate to user directory
            String userDir = "/" + user_id;
            if (!ftpClient.changeWorkingDirectory(userDir)) {
                System.out.println("User directory does not exist.");
                return null;
            }

            // Download the file
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            boolean success = ftpClient.retrieveFile(filename, byteArrayOutputStream);
            if (success) {
                fileData = byteArrayOutputStream.toByteArray();
                System.out.println("File has been downloaded successfully.");
            } else {
                System.out.println("Failed to download file.");
            }
            byteArrayOutputStream.close();

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return fileData;
    }
}

