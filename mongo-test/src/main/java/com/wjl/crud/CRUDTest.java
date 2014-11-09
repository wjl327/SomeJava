package com.wjl.crud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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
import com.mongodb.GroupCommand;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MongoClient;

/**
 * 从普通的CRUD到Group和MapReduce等基本使用。
 * 
 * @author jl.wu
 *
 */
public class CRUDTest {
	
	private final static String DB = "test";
	private final static String COLL_MSG = "message";
	private final static String COLL_TEST = "test";
	private final static String COLL_OUT = "out";
	private final static String MSG_ID = "_id";
	private final static String MSG_NAME = "name";
	private final static String MSG_CONTENT = "content";
	private MongoClient mongoClient;
	private DBCollection message;
	private DBCollection test;
	
	@Before
	public void before(){
		try {
			this.mongoClient = new MongoClient("192.168.1.104", 27017);
			DB db = this.mongoClient.getDB(DB);
			this.test = db.getCollection(COLL_TEST);
			this.message = db.getCollection(COLL_MSG);
			if(!db.collectionExists(COLL_MSG)){
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
	
	/**
	 * 无查询条件、无限制字段
	 */
	@Test
	public void query_all_1(){
		//数据结果集游标
		DBCursor cur = message.find();
		while(cur.hasNext()){
			print(cur.next().toString());
			//print((String)message.get("content"));
		}
	}
	
	/**
	 * 无查询条件、但只查content字段
	 */
	@Test
	public void query_all_2(){
		BasicDBObject fields = new BasicDBObject(MSG_CONTENT, 1);
		fields.put(MSG_ID, 0);
		DBCursor cur = message.find(new BasicDBObject(), fields);
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	/**
	 * 查询限制 ： limit 、 skip 、 sort 指定字段排序（1是升序 -1降序）
	 * 
	 * skip 可以用来做分页，但是当skip的数值较大，跳过太多数据的话，会比较慢。
	 * 
	 * var page1 = db.foo.find(cond).limit(15);
	 * var page2 = db.foo.find(cond).skip(15).limit(15);
	 * 
	 * 想不用skip分页，除非查询有类似date之类可以排序的，然后再配合limit。。
	 * 例如：
	 * var page = db.food.find({"date":{"$gt": 上一页最后时间}})
	 *   .sort({"date":-1}).limit(15);
	 * 
	 */
	@Test
	public void query_all_limit(){
		DBCursor cur = message.find().limit(5); //只查前5条
//		DBCursor cur = message.find().skip(5); //跳过5条
//		DBCursor cur = message.find().limit(10).sort(new BasicDBObject("content", 1));
		while(cur.hasNext()){
			print(cur.next().toString());
			//print((String)message.get("content"));
		}
	}
	
	/**
	 * 单条件查询   去掉该方法里面的注释即为多条件and查询
	 */
	@Test
	public void query_by_name(){
		DBObject condition = new BasicDBObject();
		condition.put(MSG_NAME, "Mom");
		//condition.put(MSG_CONTENT, "See you.");
		
		DBCursor cur = message.find(condition);
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	/**
	 * $in 查询
	 */
	@Test
	public void query_by_in(){
		DBObject condition = new BasicDBObject("$in", 
				new String[]{"How do you do?","All right."});
		DBCursor cur = message.find(new BasicDBObject("content", condition));
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	/**
	 * $操作符规则： 
	 *    条件符  $lt $gt 是放在内层的 <br/>
	 *    更新器  $inc $set 是放在外层的 <br/>
	 * 
	 * $gte/$lte/$gt/$lt/$in 都是内层
	 */
	@Test
	public void query_gt_lt(){
		DBObject condition = new BasicDBObject();
		condition.put("$gte", 18);
		condition.put("$lte", 20);
		DBCursor cur = message.find(new BasicDBObject("age", condition));
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	/**
	 * 多条件组合$and $or 
	 */
	@Test
	public void query_and_or(){
		DBObject condition1 = new BasicDBObject("age", 17);
		DBObject condition2 = new BasicDBObject("age", 18);
		BasicDBList conditions = new BasicDBList();
		conditions.add(condition1);
		conditions.add(condition2);
		DBCursor cur = message.find(new BasicDBObject("$or", conditions));
		//DBCursor cur = message.find(new BasicDBObject("$and", conditions));
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	/**
	 * 特定查询系：  正则匹配  -- 模糊
	 *         
	 * 其他：
	 * 内嵌数组查询：$all $size $slice 。 内嵌文档查询等等。
	 * 
	 * 
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
	 * 特定查询系：数组查询。
	 *         
	 * 测试例子：
	 * db.message.save({name:"BoBo", content:"haha", games:["cf","lol","tx"]})
	 * 
	 * 其他：
	 * 内嵌数组查询：$all $size    $slice（范围查询） 。 
	 * 
	 * 内嵌文档查询 ： $elemMatch 参考《Mongodb权威指南》
	 * 
	 */
	@Test
	public void query_$all(){
		DBObject condition = new BasicDBObject("games", 
				new BasicDBObject("$all", new String[]{"lol", "tx"}));
//		DBObject condition = new BasicDBObject("games", new BasicDBObject("$size", 3));
		DBCursor cur = message.find(condition);
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	/**
	 *  where 直接写javascript来查询。 超级强大
	 *  
	 *  测试例子：
	 *  db.test.save([{x:4 ,y:3, z:5}, {x:3 ,y:5, z:4}, {x:3 ,y:3, z:4}])
	 */
	@Test
	public void query_$where(){
//		DBObject condition = new BasicDBObject("$where", 
//				"function(){ return this.x == this.y}");
		DBObject condition = new BasicDBObject("$where", 
				"function(){ return this.x + this.y + this.z == 12}");
		DBCursor cur = test.find(condition);
		while(cur.hasNext()){
			print(cur.next().toString());
		}
	}
	
	/**
	 * 查询并更新。  相比update，执行还是比较慢的，但它是原子操作！！！！
	 * 返回的是更新前的文档。
	 * 同理还有一个findAndRemove()!!底层shell命令都是"findAndModify"
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
	}
	
	/**
	 * upsert为true的情况。
	 * 条件匹配不到的时候，插入数据。
	 * 等同于先find，然后save/update。 
	 * 不同的是，upsert是原子的。
	 */
	@Test
	public void update_upsert(){
		DBObject condition = new BasicDBObject(MSG_NAME, "Jl.Wu");
		DBObject rst = new BasicDBObject(MSG_NAME, "Jl.Wu").append(
				MSG_CONTENT, "So Good!!");
		message.update(condition, rst, true, false);
	}
	
	/**
	 * 只更新某个字段的办法！！ 可以用于整个对象比较多的更新。
	 * 真正只更新一个字段的方法还是 update_field_2()。
	 */
	@Test
	public void update_field_1(){
		DBObject obj = message.findOne(
				new BasicDBObject(MSG_NAME, "Jl.Wu"));
		DBObject newObj = new BasicDBObject(obj.toMap());
		newObj.put(MSG_CONTENT, "So so Good!!!");
		message.update(obj, newObj);
	}
	
	/**
	 * $操作符规则： 
	 *    条件符  $lt $gt 是放在内层的 <br/>
	 *    更新器  $inc $set 是放在外层的 <br/>
	 * 
	 * 真正只更新一个字段：使用更新器 $set。
	 * 
	 * 同类使用的还有　：　　$unset可以删除字段。 
	 * $inc 数值增加和修改 ； 运用场景：计数器。 
	 * $push 增加数据到数组； 运用场景：往某个内容增加一条评论。  
	 *      【注意：但数组很大的时候，$push修改器的性能就直线下降，那种情况下的设计最好是将内嵌数据抽出来做单独集合】
	 * $addToSet 也用于数据；用法类似$push，但是可以避免重复。
	 * $pop 数组"位置删除",可以从头和尾像出队一样删除
	 * $pull 数组"匹配删除",删除指定的值
	 *   
	 */
	@Test
	public void update_field_2(){
		DBObject finder = message.findOne(
				new BasicDBObject("_id", new ObjectId("545dfea32b3ba519ad9965e0")));
		DBObject updater = new BasicDBObject(
				"$set", new BasicDBObject("content", "This good!!"));
		message.update(finder, updater);
	}
	
	/**
	 * 驱动会将这个插入数据转换成BSON，数据库解析之后直接保存，是不会执行代码的。
	 * 因此，Mongo没有注入攻击的危险。
	 */
	@Test
	public void add(){
		DBObject msg = new BasicDBObject(MSG_NAME, "Jl.Wu")
			.append(MSG_CONTENT, "Let' go, guys!");
		message.insert(msg);
	}
	
	/**
	 * 多条数据这样批量插入：只是单个TCP请求，减少了网络开销、每次请求头处理等开销，所以性能会更好。
	 */
	@Test
	public void add_list(){
		DBObject msg1 = new BasicDBObject(MSG_NAME, "Jl.Wu")
			.append(MSG_CONTENT, "Let' go, guys! 1111");
		DBObject msg2 = new BasicDBObject(MSG_NAME, "Jl.Wu")
		.append(MSG_CONTENT, "Let' go, guys 2222!");
		DBObject msg3 = new BasicDBObject(MSG_NAME, "Jl.Wu")
		.append(MSG_CONTENT, "Let' go, guys! 3333");
		List<DBObject> list = new ArrayList<DBObject>();
		list.add(msg1); list.add(msg2); list.add(msg3);
		message.insert(list);
	}
	
	/**
	 * 单个删除的时候，用remove就可以。 一般常见业务remove就可以了。。。。
	 * 当要删除整个集合的时候，remove速度就有点慢了，可以直接drop，但是连索引都会drop掉。如果只是要删除表数据，那就要重建索引。
	 */
	@Test
	public void delete_by_id(){
		message.remove(new BasicDBObject(MSG_ID, new ObjectId("545dfea32b3ba519ad9965f9")));
	}

	/**
	 * 聚合 count
	 */
	@Test
	public void count(){
		System.out.println(message.count());
	}
	
	/**
	 * 聚合 distinct
	 */
	@Test
	public void distinct(){
		System.out.println(message.distinct(MSG_NAME));
	}
	
	/**
	 * 聚合 group
	 * 
	 * 每个文档都会对应调用一次：$reduce = "function(doc,prev){...}
	 * doc为每次执行的当前文档。
	 * prev则为累加器文档也就是为最后的结果。
	 * 分组的每一组都会有自己的累加器，所以结果是不会混淆的。
	 * 
	 * 所以这里下面的函数执行，其实就很简单：
	 * 每次执行一个文档的时候，并没做一些复杂操作，而是累加器做了一次记录数+1.
	 * 
	 * condition 可以作条件限制，只处理满足条件的文档集。
	 * 这里只统计必须有name这个字段的文档。
	 * 
	 * finalize 字段是完成器，可以对统计的结果再做计算和修修改改。
	 * 比如本例，即通过完成器来判断，如果对话记录超过15条，标记为活跃。
	 * 
	 * group足以应付普通一般分组，更复杂的可以使用$keyf。
	 * 或者MapReduce。
	 * 
	 */
	@Test
	public void group(){
		BasicDBObject key = new BasicDBObject(MSG_NAME, true);
		BasicDBObject condition = new BasicDBObject(
				MSG_NAME, new BasicDBObject("$exists", true));
		BasicDBObject initial = new BasicDBObject("num", 0);
		String $reduce = "function(doc,prev){prev.num++; }";
		String finalize = null;
//		String finalize = "function(prev){"
//				              + "if(prev.num > 15)"
//				              + "     prev.status = 'active';"
//				              + "else"
//				              + "     prev.status = 'general';"
//				           + "}";
		GroupCommand groupCommand = 
				new GroupCommand(
						message, 
						key, 
						condition, 
						initial, 
						$reduce, 
						finalize);
		BasicDBList rst = (BasicDBList)message.group(groupCommand);
		for(Object obj : rst){
			System.out.println(obj);
		}
	}
	
	/**
	 * 超级强大的聚合工具。  
	 * 也有点复杂，它执行步骤分为：map、shuffle、reduce。
	 * 
	 * MapReduce 它可以轻松并行化	到多个服务器的聚合方法，每个服务器都完成一部分，最后再汇总。
	 * 使用MapReduce的代价就是速度：group本身就不是很快，MapReduce更慢，绝对不合适"实时"的环境。
	 * 它的应用场景是后台任务运行MapReduce，然后创建一个保存结果的集合，再对这个集合进行实时查询。
	 * 
	 * 例子1：找出集合中所有键，不包含内嵌文档的。
	 * 输出类型：测试才用OutputType.INLINE ，真正使用建议OutputType.REPLACE输出到集合。
	 * 
	 */
	@Test
	public void mapReduce_1(){
		String map = "function(){ for(var key in this){ emit(key, {count:1}); }}";
		String reduce = "function(key, emits){"
				       + "  total = 0;" 
				       + "  for(var i in emits){"
				          + "   total += emits[i].count;"
				          + "}"
				       + "  return {'count': total};"
				       + "}";
		MapReduceCommand command = new MapReduceCommand(
				message, map, reduce, "out_temp", OutputType.INLINE, null);
		System.out.println(message.mapReduce(command));
	}
	
	@Test
	public void mapReduce_2(){
		String map = "function(){ "
				+ "     if(this.name != 'Jl.Wu') "
				+ "         return;"
	            + "     var ws = this.content.split(' ');"
				+ "     for(var w in ws){ "
				+ "         emit('words', 1); "
				+ "     }"
				+ "}";
		String reduce = "function(key, emits){"
				       + "  return emits.length;"
				       + "}";
		MapReduceCommand command = new MapReduceCommand(
				message, map, reduce, "out_temp", OutputType.INLINE, null);
		System.out.println(message.mapReduce(command));
	}
	
	
	@After
	public void after(){
		if(null != mongoClient)
			mongoClient.close();
	}

}
