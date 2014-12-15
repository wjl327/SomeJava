package com.client.st;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import com.service.st.Student;
import com.service.st.StudentService;


public class StudentClient {
	
	public static final String HOST = "localhost";
	public static final int PORT = 9090;
	public static final int TIMEOUT = 10000;
	
	private void start() {
		TSocket socket = null;
		try {
			socket = new TSocket(HOST, PORT, TIMEOUT);
			//客户端协议要和服务端一致，因此也是二进要协议
			TProtocol protocol = new TBinaryProtocol(socket);
			StudentService.Client client = new StudentService.Client(protocol);
			socket.open();
			Student s = new Student();
			s.setId(1).setName("xiaoMi");
			System.out.println(client.addStudent(s));
		} catch (Exception e) {
			System.out.println("client start fail!!");
			e.printStackTrace();
		} finally{
			if(null != socket)
				socket.close();
		}
		
	}
	
	public static void main(String[] args) {
		new StudentClient().start();
	}

}
