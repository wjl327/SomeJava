package com.wjl.serialize.pb;

import java.net.Socket;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

/**
 * 展示原生的Socket如何使用Google Protobuf来序列化。
 * 客户端可以直接Socket来请求pb服务，可以封装成pb接口的单元测试。
 * 
 * @author 吴嘉林
 * @since 1.0
 * @date 2014年11月30日
 *
 */
public class PbClient {
	
	public static void main(String[] args) throws Exception{
		HelloProto.HelloReq.Builder builder = HelloProto.HelloReq.newBuilder();
		builder.setName("Jarvis.Wu").setAge(18); //age为可选字段 非必填
		byte[] reqData = builder.build().toByteArray();
		
		Socket socket = new Socket("localhost", 8090);
		CodedOutputStream cos = CodedOutputStream.newInstance(socket.getOutputStream());
		cos.writeInt32NoTag(reqData.length);
		cos.writeRawBytes(reqData);
		cos.flush();
		
		CodedInputStream cis = CodedInputStream.newInstance(socket.getInputStream());
		int len = cis.readInt32();
		HelloProto.HelloRes res = HelloProto.HelloRes.parseFrom(cis.readRawBytes(len));
		System.out.println(res.getRst());
		
		socket.close();
	}

}
