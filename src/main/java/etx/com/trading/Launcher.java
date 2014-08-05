package etx.com.trading;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.multibit.platform.builder.OSUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import etx.com.trading.BaseTrading.Fufilment;
import etx.com.trading.BaseTrading.MsgRecive;
import etx.com.trading.BaseTrading.Proposal;

public class Launcher {
	//OSUtils.isWindows()
	
	public static class BitmessageFinder extends SimpleFileVisitor<Path> {

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				throws IOException {
			// TODO Auto-generated method stub
			if(file.endsWith("Bitmessage.exe"))
			{
				prefs.put("bitmessage_path", file.toString());
				return FileVisitResult.TERMINATE;
			}
			return FileVisitResult.CONTINUE;
		}
		
		 @Override
         public FileVisitResult visitFileFailed(Path file, IOException e)
             throws IOException {

             return FileVisitResult.SKIP_SUBTREE;
         }
	}
	
	private static Launcher instance = null;
	private static Preferences prefs = Preferences.userNodeForPackage(Launcher.class);
	private static List<DependencyLoadCompleteEvent> listeners = new ArrayList<DependencyLoadCompleteEvent>();
	
	public interface DependencyLoadCompleteEvent {
		public void OnDependencyLoadComplete(boolean Success);
	}
	
	static void RegisterForDependenciesReadyEvent(DependencyLoadCompleteEvent l)
	{
		listeners.add(l);
	}
	
	public static Launcher getInstance() {
		 if(instance == null) {
		     synchronized(Launcher.class) {
		       if(instance == null) {
		    	   instance = new Launcher();
		       }
		    }
		  }
		  return instance;
	  }
	
	private Launcher() 
	{
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			  public void run() {
				CheckDependencies();
				boolean success = IsBitmessageRunning() && IsMessagesRunning();	
				for(DependencyLoadCompleteEvent l : listeners){
					l.OnDependencyLoadComplete(success);
				}
			}
		});
		
	}

	private void CheckDependencies() {
		// TODO Auto-generated method stub
		if(!IsBitmessageRunning()) {
			if(StartBitmessage())
			{
				for(int i = 0; i < 30 ; i++){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(IsBitmessageRunning())
						break;
				}
			}
		}
		
		if(!IsMessagesRunning())
			StartMessagepy();
		
	}

	private boolean StartMessagepy() {
		// TODO Auto-generated method stub
		if(OSUtils.isWindows())
		{
			try {
				Process myProcess = new ProcessBuilder("./Messages/message.exe", "").start();
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
		return false;
	}

	private boolean StartBitmessage() {
		// TODO Auto-generated method stub
		if(OSUtils.isWindows())
		{
			String path = LocateBitMessage();
			try {
				Process myProcess = new ProcessBuilder(path, "").start();
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	private String LocateBitMessage() {
		// TODO Auto-generated method stub
		 String loc = prefs.get("bitmessage_path", null);
		 try
		 {
			 if(loc == null){
				 Path startDir = Paths.get("c:/"); //FileSystems.getDefault().getRootDirectories();
				 Files.walkFileTree(startDir, new BitmessageFinder());
				 loc = prefs.get("bitmessage_path", null);
			 }
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
		 }
		 return loc;
	}

	private boolean IsBitmessageRunning() {
		 XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		    try {
				config.setServerURL(new URL("http://127.0.0.1:6713"));
				XmlRpcClient clientxml = new XmlRpcClient();
				clientxml.setConfig(config);
			  //  Object[] params = {new Msg(){{ this.subject = "something"; this.message = "otherthing";}}};
				
				 Object data = clientxml.execute("add", new Object[]{ 1, 1});
				 return true;
		    }
		    catch(Exception ex)
		    {
		    	ex.printStackTrace();
		    }
		return false;
	}

	private boolean IsMessagesRunning() {
		try
		{
			JsonRpcHttpClient client = new JsonRpcHttpClient(new URL("http://127.0.0.1:" + BaseTrading.JSON_RPC_PORT));
			  
			  MsgRecive[] messages = client.invoke("receive", null,MsgRecive[].class);
			  return true;
		}
		catch(MalformedURLException ex) {
			ex.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
}


