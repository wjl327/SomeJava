package com.wjl.simple;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;

/**
 * 只依赖 activemq-all-5.7.0一个jar包
 * 
 * 启动Bootstrap2即可
 * 
 * @author WJL
 *
 */
public class Bootstrap2 {
	
	final String mqurl = "tcp://localhost:61616";
	final String queue = "mq-simple2";
	
	private void service() {
		start(); //启动mq 主线程
		consumer();//启动消费者线程
		producer(); //启动生产者线程
	}
	
	private void consumer() {
		new Thread(new Runnable() {
			public void run() {
				try {
					ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(mqurl);
					Connection connection = connectionFactory.createConnection();
					connection.start();
					// 创建Session
					final Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
					// 创建通道
					Destination destination = session.createQueue(queue);
					// 创建消费者
					MessageConsumer consumer = session.createConsumer(destination);
					while (true) {
						MapMessage message = (MapMessage)consumer.receive(1000);
						if (message != null) {
							System.out.println("收到了一个消息:" + message.getLong("current-time"));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void producer(){
		new Thread(new Runnable() {
			public void run() {
				try {
					ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(mqurl);      
				    Connection connection = connectionFactory.createConnection();      
				    connection.start();      
				    //创建Session
				    Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);      
				    //创建通道
				    Destination destination = session.createQueue(queue);      
				    //创建生产者
				    MessageProducer producer = session.createProducer(destination);
				    
				    while(true){
				    	//可以创建很多种不同的消息类型 但推荐一个通道就一种消息类型 
				    	MapMessage message = session.createMapMessage();      
						message.setLong("current-time", new Date().getTime());    
						producer.send(message);
						session.commit();
						TimeUnit.SECONDS.sleep(1);
					}      
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}).start();
	}
	
	public void start() {
		// **************************启动MQ服务******************************
		try {
			BrokerService broker = new BrokerService();
			broker.setBrokerName("doc");

			TransportConnector connector = new TransportConnector();
			connector.setUri(new URI(mqurl));
			broker.addConnector(connector);

			String kahaDBdir = "kahaDB";

			KahaDBPersistenceAdapter adapter = new KahaDBPersistenceAdapter();
			adapter.setDirectory(new File(kahaDBdir));
			broker.setPersistenceAdapter(adapter);

			broker.start();
			System.out.println("MQ启动成功");
		} catch (Exception e) {
			throw new RuntimeException("MQ启动异常");
		}
	}

	public static void main(String[] args) {
		new Bootstrap2().service();
	}

}
