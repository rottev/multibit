package etx.com.trading;

import org.apache.commons.lang3.StringUtils;

public class ColorProperty {
	public final static String propertyName = "colors";

	
	public static String serilalize(String[] data)
	{
		if(data == null)
			return null;
		
		return StringUtils.join(data, "#");
	}
	
	public static String[] deserlizie(String data)
	{
		if(data == null)
			return null;
		
		return data.split("#");
	}

}
