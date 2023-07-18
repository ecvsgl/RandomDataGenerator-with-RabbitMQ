package org.example.apps;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketListener {

    public static void main(String[] args) {
        dataReceiver();
    }

    public static void dataReceiver() {
        //pointers created outside try-catch for finally block to be able to close them.

        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        Connection connection = null;
        Channel channel = null;

        try{
            //Creating bufferedReader to listen the generator's socket
            socket = new Socket("localhost",1234);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            //Creating a queue in channel of RabbitMQ to publish messages.
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();
            //Queue name is message_queue, 3 false bools, messages not needed
            // to be durable/exclusive/autoDeleteable for our scope.
            //Arguments null, we only need to send string message in bytes.
            channel.queueDeclare("message_queue",false,false,false,null);

            while(true){
                String receivedMessage = bufferedReader.readLine();
                String[] arr = receivedMessage.split("@@");
                if(Integer.parseInt(arr[1])>90){
                    channel.basicPublish("", "message_queue", null, receivedMessage.getBytes(StandardCharsets.UTF_8));
                }else {
                    fileMessageWriter(receivedMessage);
                }
                if(receivedMessage.equals("-1")){
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            //Resource closure.
            try{
                if(bufferedReader != null){
                    bufferedReader.close();
                }
                if(inputStreamReader != null){
                    inputStreamReader.close();
                }
                if(socket != null){
                    socket.close();
                }
                if(channel != null){
                    channel.close();
                }
                if(connection != null){
                    connection.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //Filewriting method.
    private static void fileMessageWriter(String s) throws IOException {
        FileWriter fileWriter = new FileWriter("lessThanNinety.txt",true);
        fileWriter.write(s+"\n");
        fileWriter.close();
    }
}
