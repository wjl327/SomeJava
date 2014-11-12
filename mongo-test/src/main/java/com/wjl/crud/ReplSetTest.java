package com.wjl.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class ReplSetTest {
	
	public static void main(String[] args) throws Exception {
		List<ServerAddress> seeds = new ArrayList<ServerAddress>();
		seeds.add(new ServerAddress("192.168.1.104" , 27017)); //SECONDARY
		seeds.add(new ServerAddress("192.168.1.107" , 27017)); //SECONDARY
		seeds.add(new ServerAddress("192.168.1.108" , 27017)); //PRIMARY
		
		MongoClient client = new MongoClient(seeds);
		DB db = client.getDB("test");
		DBCollection users = db.getCollection("users");
		
		users.insert(new BasicDBObject("id", 1).append("name", "xiaoMi"));
		
		DBCursor cursor = users.find();
		while(cursor.hasNext())
			System.out.println(cursor.next());
		
		TimeUnit.SECONDS.sleep(40);  
		//停顿40秒，用这段时间去停掉主节点。测试副本集节点挂掉，是否对程序完全透明。
		//108是我的主节点，我停掉之后，副本集会选举另外一个成为主节点。之后，数据照样可以正常插入和读取。
		
		System.out.println("ready go..");
		
		users.insert(new BasicDBObject("id", 2).append("name", "xiaoMi"));
		
		cursor = users.find();
		while(cursor.hasNext())
			System.out.println(cursor.next());
		
		/**
		 * { "_id" : { "$oid" : "5463721c2b3b5e5affc04538"} , "id" : 1 , "name" : "xiaoMi"}
		   ready go..
		   { "_id" : { "$oid" : "5463721c2b3b5e5affc04538"} , "id" : 1 , "name" : "xiaoMi"}
		   { "_id" : { "$oid" : "546372302b3b5e5affc04539"} , "id" : 2 , "name" : "xiaoMi"}
		 * 
		 */
	}

}
