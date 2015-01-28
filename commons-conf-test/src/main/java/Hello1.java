import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


public class Hello1 {
	
	public static void main(String[] args) {
		PropertiesConfiguration config = null;
		try {
			config = new PropertiesConfiguration("test.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
//		config.setThrowExceptionOnMissing(true); 设置查找不到的机制为抛异常,而不是默认值NULL.
		System.out.println(config.getString("noExist"));
		System.out.println(config.getString("user.name"));
		System.out.println(config.getInt("user.age"));
	}

}
