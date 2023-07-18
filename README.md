## Random Data Generator with RabbitMQ

This is a Java code trial with three different executable java classes. 

Class-1 (Data Generator) is generating random data. Then, it will write the data to a Socket which is listened by another class for handling.

Class-2 (Socket Listener) is listening the data generated by Class-1. Then, it will assess the data and came into two conclusions; either the data will be sent to a RabbitMQ queue, or it will be written down to a .txt log file.

Class-3 (Queue Listener) is listening the data sent to RabbitMQ. The data will be consumed and then will be mapped into a Message object which in the next step will be persisted into a MySQL database.

This trial is performed to learn how to use sockets, hash mechanisms and RabbitMQ without any framework. 
