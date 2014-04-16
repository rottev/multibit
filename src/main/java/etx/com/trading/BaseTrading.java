package etx.com.trading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;



public class BaseTrading {
	
	//private static HttpClient httpclient = HttpClientBuilder.create().build();
	private static List<Asset> assetsList;
	private static Map<String,Issuance> issuancesList;
	private static Object lock = new Object();
	
	private static BaseTrading instance = null;
	
	public static class MsgSend
	{
		public String subject;
		public String body;
	}
	
	public static class MsgRecive
	{
		public String subject;
		public String body;
		public String fromaddress;
	}
	
	private JsonRpcHttpClient client;
	
	
	private BaseTrading()
	{
		synchronized (lock) {
			assetsList = fetchAssetList();
			issuancesList = fetchIssuane();
		}
		
	}
	
	public static BaseTrading getInstance() {
		 if(instance == null) {
		     synchronized(BaseTrading.class) {
		       if(instance == null) {
		    	   instance = new BaseTrading();
		       }
		    }
		  }
		  return instance;
	   }
	
	
	
	
	
	public static class Proposal
	{
		//offeredOutputs -- output that traces back to a genisis block with a color
		//quantityOffered -- quantity of the colored asset
		//requestedAsset -- output of a genisis block for a diffrent asset you want in return 
       // price -- ratio for how many of theier for one of mine.
		public String[] offeredOutputs;
		public double quantityOffered;
		public String requestedAsset;
		public double price;
		
		
		
		
		public String[] getColumnNames()
		{
			return new String[] {"Offerd", "Quantity", "Requesting", "Ratio"};
		}


		public String getColumnByInedx(int index) {
			// TODO Auto-generated method stub
			switch(index)
			{
			case 0:
				return offeredOutputs[0];
			case 1:
				return Double.toString(quantityOffered);
			case 2:
				return requestedAsset;
			case 3:
				return Double.toString(price);
			default:
				return null;
			}
		}
	}
	
	public static class Asset {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Asset))
				return false;
			Asset other = (Asset) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		public String name;
		public String id;
		public String symbol;
		public String icon_url;
		public String value_sign;
		public double value_multiplyer;
		public double satoshi_multiplyier;
		
		
		public int getColumnCount()
		{
			return 2;
		}
		
		public String getColumnByInedx(int index)
		{
			switch(index)
			{
			case 0:
				return name;
			case 1:
				return id;
			case 2:
				return symbol;
			case 3:
				return Double.toString(satoshi_multiplyier);
			default:
				return null;
			}
		}
		
		public String[] getColumnNames()
		{
			return new String[] {"Asset Name", "Asset Key", "Asset Symbol", "Signle Satoshi Multiplyer"};
		}
		
		
		
		
	}
	
	public static class Issuance
	{
		public String geneisistransaction;
		public int outputindex;
		public String asssetId;
		public String units;
	}
	
	
	public List<Asset> getAssetList()
	{
		synchronized (lock) {
			return assetsList;
		}
	}
	private List<Asset> fetchAssetList()  {
		
		List<Asset> result = null;
		try {
			result = Request.Get("https://dl.dropboxusercontent.com/u/13369450/assets.json")
			        .execute().handleResponse(new ResponseHandler<List<Asset>>() {

						@Override
						public List<Asset> handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							// TODO Auto-generated method stub
							BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

							StringBuffer result = new StringBuffer();
							String line = "";
							while ((line = rd.readLine()) != null) {
							    result.append(line);
							}

							Asset[] json =  new Gson().fromJson(result.toString(),Asset[].class);
							//Map<String, String> map = new Gson().fromJson(result.toString(), new TypeToken<Map<String, String>>() {}.getType());
							//System.out.println(map.get("name"));
							

							
							List<Asset> assets = new ArrayList<Asset>(Arrays.asList(json));
							//System.out.println(assets.get(0).name);
							return assets;
						}
					});
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	
	//	HttpGet httpGet = new HttpGet("https://dl.dropboxusercontent.com/u/13369450/assets.json");
	//	try {
			//CloseableHttpResponse response = httpclient.execute(httpGet);
	//	} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	} catch (IOException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();		
			
			
		////////////
			return result;
		
	}
	
	public Map<String, Issuance> getIssuane(){
		synchronized (lock) {
			return issuancesList;
		}
	}
	
	public Map<String, Issuance> fetchIssuane(){
		Map<String, Issuance> result = null;
		try {
			result = Request.Get("https://dl.dropboxusercontent.com/u/13369450/issuance.json")
			        .execute().handleResponse(new ResponseHandler<Map<String,Issuance>>() {

						@Override
						public Map<String,Issuance> handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							// TODO Auto-generated method stub
							BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

							StringBuffer result = new StringBuffer();
							String line = "";
							while ((line = rd.readLine()) != null) {
							    result.append(line);
							}

							Issuance[] json =  new Gson().fromJson(result.toString(),Issuance[].class);
							//Map<String, String> map = new Gson().fromJson(result.toString(), new TypeToken<Map<String, String>>() {}.getType());
							//System.out.println(map.get("name"));
							

							
							Map<String, Issuance> imap = new HashMap<String,Issuance>();
							for(Issuance i : json)
							{
								imap.put(i.geneisistransaction, i);
							}
							//System.out.println(assets.get(0).name);
							return imap;
						}
					});
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return result;
	}
	
  // color parts
	public boolean IsColorTransaction(String txhash, int outIndex)
	{
		return getIssuane().containsKey(txhash) && getIssuane().get(txhash).outputindex == outIndex;
	}

	public Asset getAssetForTransaction(final String txhash) {
		// TODO Auto-generated method stub
		//Asset a = new Asset();
		//a.id = getIssuane().get(txhash).asssetId;
		return getAssetList().get(getAssetList().indexOf(new Asset(){{ this.id= getIssuane().get(txhash).asssetId;}} ));
	}
	
	public List<Proposal> getProposals()
	{
		List<Proposal> proposals = null;
		try
		{
			 client = new JsonRpcHttpClient(new URL("http://127.0.0.1:6712"));
			 
			  Map<String,String> map = client.getHeaders();
			  ObjectMapper om = client.getObjectMapper();
			  
			  MsgRecive[] messages = client.invoke("receive", null,MsgRecive[].class);
			  for (MsgRecive msg : messages)
			  {
				  if(msg.subject.equals("proposal"))
				  {
					  Proposal p = new Gson().fromJson(msg.body,Proposal.class);
					  proposals.add(p);
				  }
			  }
			  
			// client.invoke("send", new Msg(){{ this.subject = "something"; this.message = "otherthing";}});
			  
			  
			  System.err.format("client: %s", client);
		}
		catch(MalformedURLException ex) {
			ex.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return proposals;
	}
}
