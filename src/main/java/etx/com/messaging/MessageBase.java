package etx.com.messaging;


import java.net.MalformedURLException;
import java.net.URL;

import com.googlecode.jsonrpc4j.*;

public class MessageBase {
	private JsonRpcHttpClient client;
	public MessageBase() {
		try
		{
			 client = new JsonRpcHttpClient(new URL("http://127.0.0.1:6712"));
		}
		catch(MalformedURLException ex) {
			
		}
	}
	//public MessageBase() throws MalformedURLException {}

}
