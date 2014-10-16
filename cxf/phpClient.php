<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="zh-CN">
<head>
<title>新建网页</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="description" content="布尔教育 http://www.itbool.com" />
</head>
    <body>
    	<?php
    	 	header('Content-Type: text/html; charset=UTF-8');
		$client = new SoapClient('http://localhost:8888/hello?wsdl');
		$param = array('arg1'=>'xiaoMi')
		$result=$client->sayHello($param);
		$result=get_object_vars($result);  
		print_r($result);
	?>
    </body>
</html>