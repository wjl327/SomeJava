package com.wjl.simple;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {
	
	public static void main(String[] args) throws Exception {
		
	    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");      
	    Connection connection = connectionFactory.createConnection();      
	    connection.start();      
	    //创建Session
	    Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);      
	    //创建通道
	    Destination destination = session.createQueue("mq-simple");      
	    //创建生产者
	    MessageProducer producer = session.createProducer(destination);
	    
	    for(int i=0; i<10; i++) {  
	    	//可以创建很多种不同的消息类型 但推荐一个通道就一种消息类型 
	    	Message message = session.createTextMessage("msg-" + i);
	        //MapMessage message = session.createMapMessage();      
	        //message.setLong("current time ", new Date().getTime());    
	        producer.send(message);      
	    }      
	    session.commit();      
	    session.close();      
	    connection.close();
	    
	    System.out.println("消息已经发送到目标MQ的通道");
	}   

}
