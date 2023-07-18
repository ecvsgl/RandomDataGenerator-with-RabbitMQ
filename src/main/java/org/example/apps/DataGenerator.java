package org.example.apps;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class DataGenerator {

    public static void main(String[] args) {
        dataSender();
    }

    public static void dataSender(){

        //pointers created outside try-catch for finally block to be able to close them.
        ServerSocket serverSocket = null;
        Socket socket = null;
        OutputStreamWriter outputStreamWriter =null;
        BufferedWriter bufferedWriter = null;

        try{
            //Creating socket arrangements.
            serverSocket = new ServerSocket(1234);
            socket = serverSocket.accept();
            //Create a bufferedWriter object to write on socket.
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            while(true){
                //Generate message & write on socket continuously.
                String preparedMessage = messageGenerator();

                bufferedWriter.write(preparedMessage);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                //Loop requires to be triggered 5 times a sec, thus 200 milisec sleep introduced.
                Thread.sleep(200);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            //Close resources.
            try{
                if(bufferedWriter != null){
                    bufferedWriter.close();
                }
                if(outputStreamWriter != null){
                    outputStreamWriter.close();
                }
                if(socket != null){
                    socket.close();
                }
                if(serverSocket != null){
                    serverSocket.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //Method to parse byte array to hexadecimal string.
    private static String byteToHexaString (byte[] arr){
        StringBuilder sb = new StringBuilder();
        for (byte a : arr){
            sb.append(String.format("%02X",a));
        }
        return sb.toString();
    }

    //Method to generate messages as per case directives.
    private static String messageGenerator() throws NoSuchAlgorithmException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd--HH:mm:ss");
        String timestamp = now.format(formatter);

        Random random = new Random();
        int randomInteger = random.nextInt(101);
        MessageDigest md = MessageDigest.getInstance("MD5");
        String toBeHashed = String.valueOf(timestamp)+String.valueOf(randomInteger);
        byte[] hash = md.digest(toBeHashed.getBytes());
        String hashHexString = byteToHexaString(hash);

        return timestamp + "@@" + randomInteger + "@@" +
                hashHexString.substring(hashHexString.length()-2);
    }
}
