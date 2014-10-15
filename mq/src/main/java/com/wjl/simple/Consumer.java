package com.wjl.simple;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer {

	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();
		// 创建Session
		final Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
		// 创建通道
		Destination destination = session.createQueue("mq-simple");
		// 创建消费者
		MessageConsumer consumer = session.createConsumer(destination);
		System.out.println("启动了消费者~ ");
		while (true) {
			Message message = consumer.receive(1000);
			if (message != null) {
				if (message instanceof TextMessage) {
					TextMessage txt = (TextMessage) message;
					System.out.println("收到了一个消息:" + txt.getText());
				}
			}
		}

		//session.close();
		//connection.close();
	}

}
