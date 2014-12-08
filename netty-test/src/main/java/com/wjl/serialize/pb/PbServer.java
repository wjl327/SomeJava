package com.wjl.serialize.pb;

import java.net.ServerSocket;
import java.net.Socket;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

/**
 * 展示原生的ServerSocket如何使用Google Protobuf来序列化。
 * 生产还是推荐Netty。ServerSocket高并发并不行，而编写高性能NIO又太费劲。
 * 所以，服务端的写法了解即可。
 * 
 * @author jl.wu
 *
 */
public class PbServer {

	public static void main(String[] args) throws Exception{
		ServerSocket ss = new ServerSocket(8090);
		Socket s = ss.accept();
		CodedInputStream cis = CodedInputStream.newInstance(s.getInputStream());
		int len = cis.readInt32();
		HelloProto.HelloReq req = HelloProto.HelloReq.parseFrom(cis.readRawBytes(len));
		System.out.println("Name is " + req.getName());
		
		HelloProto.HelloRes.Builder builder = HelloProto.HelloRes.newBuilder();
		builder.setRst("Hello," + req.getName());
		byte[] reqData = builder.build().toByteArray();
		
		CodedOutputStream cos = CodedOutputStream.newInstance(s.getOutputStream());
		cos.writeInt32NoTag(reqData.length);
		cos.writeRawBytes(reqData);
		cos.flush();
		
		s.close();
		ss.close();
	}
	
}
