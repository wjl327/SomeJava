package com.server.hello;

import java.net.InetSocketAddress;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;

import com.service.hello.HelloService;
import com.service.hello.impl.HelloServiceImpl;

/**
 * 
 * thrift支持的数据传输协议
 *
 * TBinaryProtocol : 二进制格式. 
 * TCompactProtocol : 压缩格式. 
 * TJSONProtocol : JSON格式.
 * TSimpleJSONProtocol : 提供JSON只写协议, 生成的文件很容易通过脚本语言解析.
 * 
 * @author Jarvis.Wu
 *
 */
public class ThriftServer {

	public static final int PORT = 9090;

	private void start() {
		try {
			TProcessor processor = new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl());

			TServerSocket serverSocket = new TServerSocket(new InetSocketAddress(PORT));
			TServer.Args args = new TServer.Args(serverSocket);
			args.processor(processor);
			// 服务端指定TBinaryProtocol即二进制协议
			args.protocolFactory(new TBinaryProtocol.Factory());

			System.out.println("server started~");

			// 单线程服务模型，一般用于测试
			TServer server = new TSimpleServer(args);
			server.serve();
		} catch (Exception e) {
			System.out.println("start over!!");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new ThriftServer().start();
	}

}
