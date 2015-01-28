import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


public class Hello2 {
	
	public static void main(String[] args) {
		PropertiesConfiguration config = null;
		try {
			config = new PropertiesConfiguration("test.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		//支持字符串分隔|数组
		config.setListDelimiter('/');
		
		config.addProperty("num", 123);
		config.addProperty("colors", new String[]{"Red", "Yellow", "Blue"});
		config.addProperty("interest", "CF/LOL/XL");
		
		System.out.println(config.getInt("num"));
		System.out.println(config.getString("colors"));
		System.out.println(config.getList("colors"));
		System.out.println(config.getList("interest"));
		//支持配置变量
		System.out.println(config.getString("application.title"));
		
	}

}
