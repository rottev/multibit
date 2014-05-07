package etx.com.trading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Block;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.script.Script;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;



public class BaseTrading {
	
	//private static HttpClient httpclient = HttpClientBuilder.create().build();
	private static List<Asset> assetsList;
	private static Map<String,Issuance> issuancesList;
	private static Object lock = new Object();
	public static int ColorSchemeId = 2;
	public static final String TRADE_MESSAGE_PROTOCOL_VERSION = "1.0";
	
	public static class ColorGenisis
	{
		public int index;
		public String txout;
	}
	
	
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
	
	
	
	
	
	public static class ProposalInfo
	{
		public ProposalInfo parseProposal()
		{
			ProposalInfo p = new ProposalInfo();
			return p;
		}
	}
	
	public static class Proposal
	{
		public String[] data;
		public String id;
		
		
		
		public String[] getColumnNames()
		{
			return new String[] {"Scheme", "Offerd", "Quantity", "Requesting", "Ratio"};
		}


		public String getColumnByInedx(int index) {
			// TODO Auto-generated method stub
				return data[index];
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
		public String first_issuance_date;
		
		
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
		public String date;
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
		return getIssuane(null);
	}
	
	public Map<String, Issuance> getIssuane(String AssetId ){
		synchronized (lock) {
			if(AssetId == null)
				return issuancesList;
			else
			{
				Map<String, Issuance> hmap = new HashMap<String, BaseTrading.Issuance>();
				for(Entry<String, Issuance> sd : issuancesList.entrySet()) {
					if(AssetId.equals(sd.getValue().asssetId)){
						hmap.put(sd.getKey(), sd.getValue());
					}
				}
				return hmap;
			}
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
								imap.put(i.geneisistransaction + ":" + i.outputindex, i);
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
		
		return getIssuane().containsKey(txhash + ":" + outIndex);
	}
	
	
	public ColorGenisis GetColorTransactionSearchHistory(Wallet w, final String txhash, final int outIndex)
	{
		if(IsColorTransaction(txhash,outIndex ))
			return new ColorGenisis(){{ index = outIndex; txout = txhash; }};
		
		Set<Transaction> trans = w.getTransactions(true);
		for (Iterator<Transaction> it = trans.iterator(); it.hasNext(); ) {
			Transaction t = it.next();
			if(t.getHashAsString().equals(txhash)){
				for(TransactionInput ins : t.getInputs()){
				//	try {

						
						//if(t.getOutput(outIndex).getScriptPubKey().getToAddress(w.getNetworkParameters()).equals(ins.getFromAddress())){
							trans.remove(t);
							return DoesTransactionHaveAColor(trans, ins.getOutpoint().getHash().toString(), (int)ins.getOutpoint().getIndex());
					//	}
				//	} catch (ScriptException e) {
						// TODO Auto-generated catch block
				//		e.printStackTrace();
				//	}
					
				}
			}
		}
		
		
		return null;
	}
	
	public int GetColorScheme()
	{
		return 2;
	}
	
	private ColorGenisis DoesTransactionHaveAColor(Set<Transaction> trans, final String txhash, final int outIndex)
	{
		if(IsColorTransaction(txhash,outIndex ))
			return new ColorGenisis(){{ index = outIndex; txout = txhash; }};
		
		for (Iterator<Transaction> it = trans.iterator(); it.hasNext(); ) {
			Transaction t = it.next();
			if(t.getHashAsString().equals(txhash)){
				for(TransactionInput ins : t.getInputs()){
					//if(ins.getConnectedOutput() != null && ins.getConnectedOutput().equals(t.getOutput(outIndex))){
						trans.remove(t);
						return DoesTransactionHaveAColor(trans, ins.getOutpoint().getHash().toString(), (int)ins.getOutpoint().getIndex());
					//}
				}
			}
		}
		return null;
	}

	public Asset getAssetForTransaction(final String txhash, final int index) {
		return getAssetList().get(getAssetList().indexOf(new Asset(){{ this.id= getIssuane().get(txhash + ":" + index).asssetId;}} ));
	}
	
	public List<Proposal> getProposals()
	{
		List<Proposal> proposals = new ArrayList<Proposal>();
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
						 JsonParser parser = new JsonParser();
						 JsonObject o = (JsonObject)parser.parse(msg.body);
						 if(IsValidProposal(o) ) { // version 1.0 {
							 
							 
							 
							 Proposal p = new Proposal();
							 String Scheme = o.get("scheme").toString();
							 String asset = o.get("give").getAsJsonObject().get("asset").toString();
							 String quantity = o.get("take").getAsJsonObject().get("quantity").toString();
							 String assestre = o.get("give").getAsJsonObject().get("asset").toString();
							 String ratio = o.get("give").getAsJsonObject().get("quantity").toString();
							 
							 p.data = new String[] {Scheme, asset, quantity, assestre, ratio };
							 p.id = UUID.randomUUID().toString();
							 proposals.add(p);
							 //String firstHash = o.get("give").getAsJsonObject().get("utxos").getAsJsonArray().get(0).getAsJsonArray().get(0).getAsString();
							// String txHash = o.get("give").getAsJsonObject().get("utxos").getAsJsonArray().get(0).getAsJsonArray().get(1).getAsString();
						 }
					//  Proposal p = new Gson().fromJson(msg.body,Proposal.class);
					  //proposals.add(p);
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
	
	private boolean IsValidProposal(JsonObject o) {
		// TODO Auto-generated method stub
		if(!o.has("scheme"))
			return false;
		if(o.has("version") && o.get("version").getAsString().equals(TRADE_MESSAGE_PROTOCOL_VERSION))
			return true;
		if(!o.has("version"))
			return true;
		
		return false;
	}

	public boolean addOffer(int Scheme ,String giveAsset, int giveQuantty, String[] giveUtxos, int takeQuantity,
			String takeAddress, String takeAsset)
	{
		 try {
			client = new JsonRpcHttpClient(new URL("http://127.0.0.1:6712"));	        
		    
			Map<String,String> map = client.getHeaders();
			ObjectMapper om = client.getObjectMapper();
			final JsonObject o = new JsonObject();
			JsonObject item = new JsonObject();
			JsonArray utx = new JsonArray();
			JsonArray utxos = new JsonArray();
			item.addProperty(null, giveUtxos[0]);
			utx.add(item );
			item = new JsonObject();
			item.addProperty(null, giveUtxos[0]);
			utx.add(item );
			utxos.add(utx);
			
			o.addProperty("scheme", Scheme);
			
			
			JsonObject JsonGive = new JsonObject();
			JsonGive.addProperty("quantity", giveQuantty);
			JsonGive.addProperty("asset", giveAsset);
			JsonGive.add("giveUtxos", utxos);
			JsonGive.addProperty("quantity", giveQuantty);
			
			o.add("give", JsonGive);
			
			JsonObject JsonTake = new JsonObject();
			JsonTake.addProperty("quantity", takeQuantity);
			JsonTake.addProperty("asset", takeAsset);
			JsonTake.addProperty("address", takeAddress);
			
			o.add("take", JsonTake);
			o.addProperty("version", TRADE_MESSAGE_PROTOCOL_VERSION);
			
			//o.add("scheme", Scheme);
						
			client.invoke("send", new MsgSend(){{ this.subject = "proposal"; this.body = o.toString();}});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public boolean addOffer(int Scheme ,String giveAsset, double giveQuantty, String[] giveUtxos, double takeQuantity,
			String takeAddress, String takeAsset) {
		 try {
				client = new JsonRpcHttpClient(new URL("http://127.0.0.1:6712"));	        
			    
				Map<String,String> map = client.getHeaders();
				ObjectMapper om = client.getObjectMapper();
				final JsonObject o = new JsonObject();
				JsonArray utx = new JsonArray();
				JsonArray utxos = new JsonArray();
				utx.add(new JsonPrimitive(giveUtxos[0]));
				utx.add(new JsonPrimitive(giveUtxos[1]));
				utxos.add(utx);
				
				o.addProperty("scheme", Scheme);
				
				
				JsonObject JsonGive = new JsonObject();
				JsonGive.addProperty("quantity", giveQuantty);
				JsonGive.addProperty("asset", giveAsset);
				JsonGive.add("giveUtxos", utxos);
				JsonGive.addProperty("quantity", giveQuantty);
				
				o.add("give", JsonGive);
				
				JsonObject JsonTake = new JsonObject();
				JsonTake.addProperty("quantity", takeQuantity);
				JsonTake.addProperty("asset", takeAsset);
				JsonTake.addProperty("address", takeAddress);
				
				o.add("take", JsonTake);
				o.addProperty("version", TRADE_MESSAGE_PROTOCOL_VERSION);
				
				//o.add("scheme", Scheme);
							
				client.invoke("send", new MsgSend(){{ this.subject = "proposal"; this.body = o.toString();}});
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		
	}
}
