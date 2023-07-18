package org.example.apps;

import com.rabbitmq.client.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.model.Message;

import java.nio.charset.StandardCharsets;

public class QueueListener {
    public static void main(String[] args) {
        handleMessages();
    }
    private static void handleMessages(){

        //Using JPA's emf for data persistance to MySQL db.
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysqlPU");
        final EntityManager entityManager = emf.createEntityManager();
        Connection connection = null;
        Channel channel = null;

        try{

            //Creating channel to declare what queue to listen in RabbitMQ.
            ConnectionFactory factory = new ConnectionFactory();
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare("message_queue",false,false,false,null);

            while (true) {
                channel.basicConsume("message_queue", true, new DeliverCallback() {
                    @Override
                    public void handle(String consumerTag, Delivery message) {
                        String rabbitMessage = new String (message.getBody(), StandardCharsets.UTF_8);
                        //Wrapping the rabbitMessage with Message object to persist.
                        Message messageToPersist =new Message(rabbitMessage);
                        try{
                            //Persisting to MySQL db.
                            entityManager.getTransaction().begin();
                            entityManager.persist(messageToPersist);
                            entityManager.getTransaction().commit();
                        } catch (Exception e){
                            entityManager.getTransaction().rollback();
                        }
                    }
                }, new CancelCallback() {
                    //We will not be cancelled, thus empty.
                    @Override
                    public void handle(String s) {

                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //Resource closure.
            try{
                emf.close();
                if(entityManager != null){
                    entityManager.clear();
                    entityManager.close();
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

}
