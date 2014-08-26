package com.gh.service;

import org.springframework.stereotype.Service;
import com.gh.remote.HelloServiceRemote;

@Service("helloService")
public class HelloService implements HelloServiceRemote {

	//public static final Logger logger = LoggerFactory.getLogger(HelloService.class);

	@Autowired
	private LeaguerDao leaguerDao;
	
	@Override
	public String sayHello(String name) {
		return "hello : " + name;
	}

}
