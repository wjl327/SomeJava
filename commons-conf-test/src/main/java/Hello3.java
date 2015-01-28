import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


public class Hello3 {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		PropertiesConfiguration config = null;
		try {
			config = new PropertiesConfiguration("test.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		//支持一组配置一起读取
		Configuration wjls = config.subset("wjl");
		Iterator<String> it = wjls.getKeys();
		while(it.hasNext())
			System.out.println(wjls.getString(it.next()));
	}

}
