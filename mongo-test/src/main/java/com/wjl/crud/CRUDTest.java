package com.wjl.crud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class CRUDTest {
	
	private final static String Test_DB = "test";
	private final static String Test_COLL = "message";
	private final static String MSG_ID = "_id";
	private final static String MSG_NAME = "name";
	private final static String MSG_CONTENT = "content";
	private MongoClient mongoClient;
	private DBCollection message;
	
	@Before
	public void before(){
		try {
			this.mongoClient = new MongoClient("192.168.1.104", 27017);
			DB db = this.mongoClient.getDB(Test_DB);
			this.message = db.getCollection(Test_COLL);
			if(!db.collectionExists(Test_COLL)){
				init();
			}
		} catch (UnknownHostException e) {
			err("连接不上Mongo服务器：");
			e.printStackTrace();
		} catch (Exception e) {
			err("其它错误：");
			e.printStackTrace();
		}
	}
	
	private void init() throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader("message.txt"));
		String line = null;
		while((line = reader.readLine()) != null){
			if(StringUtils.isEmpty(line))
				continue;
			String[] msgString = line.split(":");
			BasicDBObject msg = new BasicDBObject(
					MSG_NAME, StringUtils.trim(msgString[0])).append(
					MSG_CONTENT, StringUtils.trim(msgString[1]));
			message.insert(msg);
		}
		reader.close();
		print("data init success!!");
	}
	
	private void print(String msg){
		if(StringUtils.isNotBlank(msg))
			System.out.println(msg);
	}
	
	private void err(String msg){
		if(StringUtils.isNotBlank(msg))
			System.err.println(msg);
	}
	
	@Test
	public void query_all_1(){
		//数据结果集游标
		DBCursor cur = message.find();
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	@Test
	public void query_all_2(){
		//数据结果集游标
		DBCursor cur = message.find();
		while(cur.hasNext()){
			DBObject message = cur.next();
			print((String)message.get("content"));
		}
	}
	
	@Test
	public void query_by_name(){
		DBObject condition = new BasicDBObject();
		condition.put(MSG_NAME, "Mom");
		//数据结果集游标
		DBCursor cur = message.find(condition);
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	@Test
	public void query_by_id(){
		DBObject condition = new BasicDBObject();
		condition.put(MSG_ID, new ObjectId("545dfea32b3ba519ad9965f9"));
		DBObject msg = message.findOne(condition);
		System.out.println(msg);
	}
	
	/**
	 * $gte/$lte/$gt/$lt, 还有$in操作也类似。
	 * 
	 * 另外，多条件查询的时候可以用 $all $and $or
	 */
	@Test
	public void query_gt_lt(){
		//单条件查询
//		DBObject condition = new BasicDBObject("age", new BasicDBObject("$gte", 18));
//		DBCursor cur = message.find(condition);
		
		//多条件查询
		DBObject condition1 = new BasicDBObject("age", new BasicDBObject("$gte", 18));
		DBObject condition2 = new BasicDBObject("age", new BasicDBObject("$lte", 20));
		BasicDBList conditions = new BasicDBList();
		conditions.add(condition1);
		conditions.add(condition2);
		DBCursor cur = message.find(new BasicDBObject("$and", conditions));
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	/**
	 * 正则匹配  -- 模糊
	 */
	@Test
	public void query_pat(){
		Pattern pattern = Pattern.compile("^.*Bo-Bo.*$", Pattern.CASE_INSENSITIVE);
		DBObject condition = new BasicDBObject(
				"content", new BasicDBObject("$regex", pattern));
		DBCursor cur = message.find(condition);
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	/**
	 * 查询并更新。
	 * 返回的是更新前的文档。
	 * 同理还有一个findAndRemove()!!
	 */
	@Test
	public void findAndModify(){
		System.out.println(message.findAndModify(
				new BasicDBObject(MSG_NAME, "Jl.Wu"),
				new BasicDBObject(MSG_NAME, "Jl.Wu").append(MSG_CONTENT, "God is Girl")));
	}
	
	/**
	 * 默认“对象级”更新，如果不指定其他字段，那只会剩下被更新的字段.
	 * 默认upsert为false:匹配不到时，不插入。
	 * 默认muli为false：匹配多条，也只更新第一条记录。
	 */
	@Test
	public void update(){
		DBObject condition = new BasicDBObject(MSG_ID, new ObjectId("545dfea32b3ba519ad9965f9"));
		DBObject rst = new BasicDBObject(MSG_CONTENT, "wa ha ha...");
		message.update(condition, rst);
		query_by_id();
	}
	
	@Test
	public void count(){
		print(String.valueOf(message.count()));
	}
	
	/**
	 * 条件匹配不到的时候，插入数据。
	 */
	@Test
	public void update_upsert(){
		DBObject condition = new BasicDBObject(MSG_NAME, "Jl.Wu");
		DBObject rst = new BasicDBObject(MSG_NAME, "Jl.Wu").append(
				MSG_CONTENT, "So Good!!");
		message.update(condition, rst, true, false);
	}
	
	/**
	 * 只更新某个字段的办法！！
	 */
	@Test
	public void update_field(){
		DBObject obj = message.findOne(
				new BasicDBObject(MSG_NAME, "Jl.Wu"));
		DBObject newObj = new BasicDBObject(obj.toMap());
		newObj.put(MSG_CONTENT, "So so Good!!!");
		message.update(obj, newObj);
	}
	
	@Test
	public void add(){
		DBObject msg = new BasicDBObject(MSG_NAME, "Jl.Wu")
			.append(MSG_CONTENT, "Let' go, guys!");
		message.insert(msg);
	}
	
	@Test
	public void delete_by_id(){
		message.remove(new BasicDBObject(MSG_ID, new ObjectId("545dfea32b3ba519ad9965f9")));
	}

	@After
	public void after(){
		if(null != mongoClient)
			mongoClient.close();
	}

}