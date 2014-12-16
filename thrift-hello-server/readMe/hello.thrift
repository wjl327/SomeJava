namespace java com.service.hello

service HelloService{
	string sayHello(1:string username);
}

service UserService{
	i32 getAge(1:i32 id);
}