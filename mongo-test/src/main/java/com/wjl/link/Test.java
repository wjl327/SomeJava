package com.wjl.link;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

/**
 * 连接Mongodb测试!!
 * @author jl.wu
 *
 */
public class Test {

	public static void main(String[] args) throws Exception {
		MongoClient mongoClient = new MongoClient("192.168.1.104", 27017);
		DB db = mongoClient.getDB("test");
		
		for(String name : db.getCollectionNames())
			System.out.printf("Mongodb的集合:%s\n", name);
		
		DBCollection coll = db.getCollection("test");
		System.out.printf("Mongodb的集合test一共%d条记录！\n" , coll.count());
		
		int i = 1;
		DBCursor cursor = coll.find();
		while(cursor.hasNext() && i++ <= 10){
			System.out.println(cursor.next());
		}
	}
}
