package com.wjl.simple;

import java.io.File;
import java.net.URI;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;

/**
 * 只依赖 activemq-all-5.7.0一个jar包
 * 
 * 先启动 Bootstrap ， 在分别启动Concumer 和 Producter
 * 
 * @author WJL
 *
 */
public class Bootstrap {

	public static void main(String[] args) {
		//**************************启动MQ服务******************************
		try {
			BrokerService broker = new BrokerService();
			broker.setBrokerName("doc");
			
			TransportConnector connector = new TransportConnector();
			connector.setUri(new URI("tcp://localhost:61616"));
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
	
}
