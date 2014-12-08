package com.wjl.serialize.pb;

import java.net.ServerSocket;
import java.net.Socket;

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
//		CodedInputStreams.getInputStream();
	}
	
}
