package etx.com.trading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Block;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionConfidence;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.TransactionConfidence.ConfidenceType;
import com.google.bitcoin.core.Wallet.CoinSelection;
import com.google.bitcoin.core.Wallet.CoinSelector;
import com.google.bitcoin.core.Wallet.DefaultCoinSelector;
import com.google.bitcoin.script.Script;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTrading.Proposal;
import etx.com.trading.BaseTrading.ProposalInfo;



public class BaseTrading {
	
	//private static HttpClient httpclient = HttpClientBuilder.create().build();
	private static List<Asset> assetsList;
	private static Map<String,Issuance> issuancesList;
	private static Object lock = new Object();
	public static int ColorSchemeId = 2;
	public static final String TRADE_MESSAGE_PROTOCOL_VERSION = "1.0";
	private static List<Proposal> proposals = new ArrayList<Proposal>();
	private static List<Fufilment> fufillments = new ArrayList<Fufilment>();
	private static List<FufilEventListener> fuflemntEventListeners = new ArrayList<FufilEventListener>();
	
	public static enum ColoringMode {
		MODE_ORDER_BASED,
		MODE_SEQUENCE_NUMBER
	}
	//private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	/*
	 * 	final Runnable refresh = new Runnable() {
	       public void run() { 
	    	   
	       }
	     };
	     
	     final ScheduledFuture<?> beeperHandle =  scheduler.scheduleWithFixedDelay(beeper, 10, 10, SECONDS);
     
	 */
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
		public String giveAssetId;
		public String takeAssetId;
		public boolean giveBtc = false;
		public boolean takeBtc = false;
		public String giveQuantity;
		public String takeQuantity;
		public String takeAddress;
		public List<String> utxos;
		public List<String> utxosIndexes;
		public Sha256Hash hash = null;
		

		public static ProposalInfo parse(Proposal in) {
			// TODO Auto-generated method stub
			ProposalInfo p = new ProposalInfo();
			p.giveAssetId = in.data[1];
			p.takeAssetId = in.data[3];
			p.giveBtc = p.giveAssetId.equals("BTC");
			p.takeBtc = p.takeAssetId.equals("BTC");
			
			p.giveQuantity = in.data[2];
			p.takeQuantity = in.data[4];
			p.takeAddress = in.data[5];
			p.utxos = new ArrayList<String>();
			p.utxosIndexes = new ArrayList<String>();
			
			 JsonArray utxosArray = in.raw.get("give").getAsJsonObject().get("utxos").getAsJsonArray();
			 for(JsonElement o : utxosArray)
			 {
				 p.utxos.add(o.getAsJsonArray().get(0).getAsString());
				 p.utxosIndexes.add(o.getAsJsonArray().get(1).getAsString());
			 }
			 p.hash = Sha256Hash.create(in.raw.toString().getBytes(Charset.forName("UTF-8")));

			
			return p;
		}
	}
	
	public static class Proposal
	{
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((hash == null) ? 0 : hash.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Proposal other = (Proposal) obj;
			if (hash == null) {
				if (other.hash != null)
					return false;
			} else if (!hash.equals(other.hash))
				return false;
			return true;
		}


		public String[] data;
		public String id;
		public JsonObject raw;
		private Sha256Hash hash = null;
		
		public Proposal(String data)
		{
			 hash = Sha256Hash.create(data.getBytes(Charset.forName("UTF-8")));
		}
		
		public String getHashAsString()
		{
			return hash == null? null : hash.toString();
		}
		
		public String[] getColumnNames()
		{
			return new String[] {"Scheme", "Offerd", "Quantity", "Requesting", "Ratio"};
		}


		public String getColumnByInedx(int index) {
			// TODO Auto-generated method stub
				return data[index];
		}
		
		public static Proposal parse(String proposal)
		{
			
			 Proposal p = null;
			 JsonParser parser = new JsonParser();
			 JsonObject o = (JsonObject)parser.parse(proposal);
			 if(IsValid(o) ) { // version 1.0 {
				 
				 
				 
				 p = new Proposal(o.toString());
				 String Scheme = o.get("scheme").toString();
				 String asset = o.get("give").getAsJsonObject().get("asset").getAsString();
				 String quantity = o.get("give").getAsJsonObject().get("quantity").toString();
				 String assestre = o.get("take").getAsJsonObject().get("asset").getAsString();
				 String ratio = o.get("take").getAsJsonObject().get("quantity").toString();
				 String toaddress =  o.get("take").getAsJsonObject().get("address").getAsString();

				 
				 p.data = new String[] {Scheme, asset, quantity, assestre, ratio, toaddress };
				 p.id = UUID.randomUUID().toString();
				 p.raw = o;
			 }
			return p;
		}
		
		private static boolean IsValid(JsonObject o) {
			// TODO Auto-generated method stub
			if(!o.has("scheme"))
				return false;
			if(o.has("version") && o.get("version").getAsString().equals(TRADE_MESSAGE_PROTOCOL_VERSION))
				return true;
			if(!o.has("version"))
				return true;
			
			return false;
		}
		
	}
	
	public static class Fufilment
	{
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((proposalHash == null) ? 0 : proposalHash.hashCode());
			result = prime * result + ((txHex == null) ? 0 : txHex.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Fufilment other = (Fufilment) obj;
			if (proposalHash == null) {
				if (other.proposalHash != null)
					return false;
			} else if (!proposalHash.equals(other.proposalHash))
				return false;
			if (txHex == null) {
				if (other.txHex != null)
					return false;
			} else if (!txHex.equals(other.txHex))
				return false;
			return true;
		}

		public String proposalHash;
		public String txHex;
		
		private static boolean IsValid(JsonObject o) {
			// TODO Auto-generated method stub
			if(!o.has("scheme"))
				return false;
			if(!o.has("txHex"))
				return false;
			if(!o.has("proposalHash"))
				return false;
			if(o.has("version") && o.get("version").getAsString().equals(TRADE_MESSAGE_PROTOCOL_VERSION))
				return true;
			if(!o.has("version"))
				return true;
			
			return false;
		}
		
		public static Fufilment parse(String fufillmentString)
		{
			Fufilment f = null;
			try{
			 JsonParser parser = new JsonParser();
			 JsonObject o = (JsonObject)parser.parse(fufillmentString);
			 if(IsValid(o) ) { // version 1.0 {
				 f = new Fufilment();
				 f.proposalHash = o.get("proposalHash").getAsString();
				 f.txHex = o.get("txHex").getAsString();
			 }
			}
			catch(Exception ex){
				
			}
			return f;
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
		public boolean trusted;
		
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
		return getAssetList(false);
	}
	
	public List<Asset> getAssetList(boolean forceUpdate)
	{
		synchronized (lock) {
			if(forceUpdate) {
				assetsList = fetchAssetList();
			}
			return assetsList;
		}
	}
	
	public Asset getAssetBySymbol(String sym)
	{
		if(sym.equals("BTC"))
			return new Asset() {{ id = "BTC";  }};
		
		synchronized (lock) {
			for(Asset a : assetsList)
			{
				if(a.symbol.equals(sym))
					return a;
			}
		}		
		return null;
	}
	
	
	
	private List<Asset> fetchAssetList()  {
		
		List<Asset> result = null;
		try {
			result = Request.Get("http://localhost:8080/asset"/*"https://dl.dropboxusercontent.com/u/13369450/assets.json" */)
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
		return getIssuane(false);
	}
	
	public Map<String, Issuance> getIssuane(boolean forceUpdate){
		synchronized (lock) {
			if(forceUpdate) {
				issuancesList = fetchIssuane();
			}
		}
		return issuancesList;
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
			result = Request.Get("http://localhost:8080/issue"/*"https://dl.dropboxusercontent.com/u/13369450/issuance.json"*/)
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
	
	
	public BigInteger GetColorValue(Wallet w, final String txhash, final int outIndex)
	{
		double value =0;
		try
		{
			ColorGenisis gc = GetColorTransactionSearchHistory(w, txhash, outIndex);
			Transaction t = w.getTransaction(new Sha256Hash(txhash));
			TransactionOutput out = t.getOutput(outIndex);
			value =out.getValue().doubleValue();
			if(gc != null){
				Asset a = getAssetForTransaction(gc.txout, gc.index);
				return BigDecimal.valueOf(value * a.satoshi_multiplyier).toBigInteger(); 	
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return BigInteger.ZERO;
	}
	
	public ColorGenisis DoesTransactionHaveAColor(Set<Transaction> trans, final String txhash, final int outIndex, Wallet w) {
		if(IsColorTransaction(txhash,outIndex ))
			return new ColorGenisis(){{ index = outIndex; txout = txhash; }};
			
		for (Iterator<Transaction> it = trans.iterator(); it.hasNext(); ) {
			Transaction t = it.next();
			if(t.getHashAsString().equals(txhash)){
		//
				// simple case we only have one input.
				if(t.getInputs().size() == 1) {
					trans.remove(t);
					return DoesTransactionHaveAColorAnyWhere(trans, t.getInput(0).getOutpoint().getHash().toString(), (int)t.getInput(0).getOutpoint().getIndex(), w);
			
				}
				else
				{
					
					// does the tx live in the color rules?
					// 1. last input has to be fee. must contain a single output with change back to same adress
					// 2. last output has to be fee.
					// 3. color inputs come before non color inputs.
					int index = getPerviousColorOutIndex(ColoringMode.MODE_SEQUENCE_NUMBER, trans, t, outIndex, w);
					
					if(index >= 0)
						return DoesTransactionHaveAColorAnyWhere(trans, t.getInput(index).getOutpoint().getHash().toString(), (int)t.getInput(index).getOutpoint().getIndex(), w);
					
				}
			}
		}
		
		
		return null;
	}
	
	public ColorGenisis GetColorTransactionSearchHistory(Wallet w, final String txhash, final int outIndex)
	{
		if(IsColorTransaction(txhash,outIndex ))
			return new ColorGenisis(){{ index = outIndex; txout = txhash; }};
		
		Set<Transaction> trans = w.getTransactions(true);
		for (Iterator<Transaction> it = trans.iterator(); it.hasNext(); ) {
			Transaction t = it.next();
			if(t.getHashAsString().equals(txhash)){
		//
				// simple case we only have one input.
				if(t.getInputs().size() == 1) {
					trans.remove(t);
					return DoesTransactionHaveAColor(trans, t.getInput(0).getOutpoint().getHash().toString(), (int)t.getInput(0).getOutpoint().getIndex(), w);
			
				}
				else
				{
					
					// does the tx live in the color rules?
					// 1. last input has to be fee. must contain a single output with change back to same adress
					// 2. last output has to be fee.
					// 3. color inputs come before non color inputs.
					int index = getPerviousColorOutIndex(ColoringMode.MODE_SEQUENCE_NUMBER, trans, t, outIndex, w);
					
					if(index >= 0)
						return DoesTransactionHaveAColor(trans, t.getInput(index).getOutpoint().getHash().toString(), (int)t.getInput(index).getOutpoint().getIndex(), w);
					
				}
			}
		}
		
		
		return null;
	}
	
	private int getPerviousColorOutIndex(ColoringMode mode, Set<Transaction> trans, Transaction t, final int outIndex, Wallet w) {
		if(mode == ColoringMode.MODE_ORDER_BASED) {
			return VerifyColorThroughOrdering(trans, t, outIndex, w);
		}
		else if(mode == ColoringMode.MODE_SEQUENCE_NUMBER) {
			return VerifyColorThroughSequenceNumbers(trans, t, outIndex, w);
		}
		return -1;
	}
	
	private int VerifyColorThroughSequenceNumbers(Set<Transaction> trans, Transaction t, final int outIndex, Wallet w) {
		try
		{
			for(int i = 0; i< t.getInputs().size(); i++) {
				if(t.getInput(i).getConnectedOutput() != null) {
					if( t.getInput(i).hasSequence() && ((t.getInput(i).getSequenceNumber() & Integer.MIN_VALUE) != 0 )) {
						
						if((int)(t.getInput(i).getSequenceNumber() & ~Integer.MIN_VALUE) >= outIndex) {
							return i;
						}	
					}
				}
				else
					break;
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	private int VerifyColorThroughOrdering(Set<Transaction> trans, Transaction t, final int outIndex, Wallet w) {
		// TODO Auto-generated method stub
		int numberOfInputs = t.getInputs().size();
		int numberOfOutputs = t.getOutputs().size();
		try {
			if(t.getInput(numberOfInputs -1).getFromAddress().equals(t.getOutput(numberOfOutputs -1).getScriptPubKey().getToAddress(w.getNetworkParameters()))){
				for(int i = 0; i< t.getInputs().size(); i++) {
				// check if first input is color. id it is then it has to have the prvious tx output
					if(t.getInput(i).getConnectedOutput() != null) {
						if(outIndex == 0 && i == 0) // we order must be kept. first output will match first input
						{
							return 0;
						}
						else 
						{
							BigInteger value = t.getInput(0).getConnectedOutput().getValue();
							if(value.equals(t.getOutput(outIndex).getValue()) || value.equals(t.getOutput(outIndex).getValue().add(t.getOutput(outIndex +1).getValue()))) {
								return i;
							}
						}
						
					}
					else
						break;
				}
			}
			else
			{
				System.out.println("Failed color rule 1");
				
			}
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}



	public int GetColorScheme()
	{
		return 2;
	}
	
	private ColorGenisis DoesTransactionHaveAColorAnyWhere(Set<Transaction> trans, final String txhash, final int outIndex, Wallet w)
	{
		if(IsColorTransaction(txhash,outIndex ))
			return new ColorGenisis(){{ index = outIndex; txout = txhash; }};
		
		for (Iterator<Transaction> it = trans.iterator(); it.hasNext(); ) {
			Transaction t = it.next();
			if(t.getHashAsString().equals(txhash)){
				for(TransactionInput ins : t.getInputs()){
					//if(ins.getConnectedOutput() != null && ins.getConnectedOutput().equals(t.getOutput(outIndex))){
						//trans.remove(t);
						return DoesTransactionHaveAColor(trans, ins.getOutpoint().getHash().toString(), (int)ins.getOutpoint().getIndex(), w);
					//}
				}
			}
		}
		return null;
	}

	public Asset getAssetForTransaction(final String txhash, final int index) {
		return getAssetList().get(getAssetList().indexOf(new Asset(){{ this.id= getIssuane().get(txhash + ":" + index).asssetId;}} ));
	}
	
	public List<Fufilment> getFulfilments()
	{
		synchronized (fufillments) {
			return fufillments;
		}
	}
	
	public void getMessages()
	{
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
					  Proposal p = Proposal.parse(msg.body);
					  if(!proposals.contains(p) && p != null)
						  proposals.add(p);
				  }
				  else if(msg.subject.equals("fulfil")){
					  Fufilment f = Fufilment.parse(msg.body);
					  if(f != null && !fufillments.contains(f)){
						fufillments.add(f);
						if(!fuflemntEventListeners.isEmpty()) {
							for( FufilEventListener listener : fuflemntEventListeners)
								listener.OnNewFufilment(f);
						}
						  
					  }
				  }
			  }
		}
		catch(MalformedURLException ex) {
			ex.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public List<Proposal> getProposals()
	{
		synchronized (proposals) {
			return proposals;
		}
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
			JsonGive.add("utxos", utxos);
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
			 	BaseStore database = new BaseStore("ccdb2") ; 
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
				JsonGive.add("utxos", utxos);
				JsonGive.addProperty("quantity", giveQuantty);
				
				o.add("give", JsonGive);
				
				JsonObject JsonTake = new JsonObject();
				JsonTake.addProperty("quantity", takeQuantity);
				JsonTake.addProperty("asset", takeAsset);
				JsonTake.addProperty("address", takeAddress);
				
				o.add("take", JsonTake);
				o.addProperty("version", TRADE_MESSAGE_PROTOCOL_VERSION);

				client.invoke("send", new MsgSend(){{ this.subject = "proposal"; this.body = o.toString();}});
				String hhh = Sha256Hash.create(o.toString().getBytes(Charset.forName("UTF-8"))).toString();
				// persist out proposal
				database.addProposal(Sha256Hash.create(o.toString().getBytes(Charset.forName("UTF-8"))).toString(), o.toString());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		
	}
	
	public boolean createFufil(int Scheme, String txHex, String proposalHash)
	{
		try {
			client = new JsonRpcHttpClient(new URL("http://127.0.0.1:6712"));	        
		    
			Map<String,String> map = client.getHeaders();
			ObjectMapper om = client.getObjectMapper();
			final JsonObject o = new JsonObject();

			
			o.addProperty("txHex", txHex);
			o.addProperty("proposalHash", proposalHash);
			o.addProperty("scheme", Scheme);
			

			o.addProperty("version", TRADE_MESSAGE_PROTOCOL_VERSION);
			
			//o.add("scheme", Scheme);
						
			client.invoke("send", new MsgSend(){{ this.subject = "fulfil"; this.body = o.toString();}});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void AddFufilmentEventListener(FufilEventListener listener)
	{
		synchronized (fuflemntEventListeners) {
			fuflemntEventListeners.add(listener);
		}
	}
	
	public void RewmoveFufilmentEventListener(FufilEventListener listener)
	{
		synchronized (fuflemntEventListeners) {
			fuflemntEventListeners.remove(listener);
		}
	}
	
	public String createNewUntrustedAsset(Asset asset)
	{
		String a = null;
		try {

			a = Request.Post("http://localhost:8080/asset").bodyString(new Gson().toJson(asset, Asset.class), ContentType.APPLICATION_JSON )
			        .execute().handleResponse(new ResponseHandler<String>() {

						@Override
						public String handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							// TODO Auto-generated method stub
							BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

							if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
								StringBuffer result = new StringBuffer();
								String line = "";
								while ((line = rd.readLine()) != null) {
								    result.append(line);
								}
	
								JsonObject o = new JsonParser().parse(result.toString()).getAsJsonObject();
								
								//System.out.println(assets.get(0).name);
								return o.get("key").getAsString();
							}
							return null;
							
						}
					});
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return a;
	}
	
	
	 public static class ColorAwareDefaultCoinSelector implements CoinSelector {
	        public CoinSelection select(BigInteger biTarget, LinkedList<TransactionOutput> candidates) {
	            long target = biTarget.longValue();
	            HashSet<TransactionOutput> selected = new HashSet<TransactionOutput>();
	            // Sort the inputs by age*value so we get the highest "coindays" spent.
	            // TODO: Consider changing the wallets internal format to track just outputs and keep them ordered.
	            ArrayList<TransactionOutput> sortedOutputs = new ArrayList<TransactionOutput>(candidates);
	            // When calculating the wallet balance, we may be asked to select all possible coins, if so, avoid sorting
	            // them in order to improve performance.
	            if (!biTarget.equals(NetworkParameters.MAX_MONEY)) {
	                Collections.sort(sortedOutputs, new Comparator<TransactionOutput>() {
	                    public int compare(TransactionOutput a, TransactionOutput b) {
	                        int depth1 = 0;
	                        int depth2 = 0;
	                        TransactionConfidence conf1 = a.getParentTransaction().getConfidence();
	                        TransactionConfidence conf2 = b.getParentTransaction().getConfidence();
	                        if (conf1.getConfidenceType() == ConfidenceType.BUILDING) depth1 = conf1.getDepthInBlocks();
	                        if (conf2.getConfidenceType() == ConfidenceType.BUILDING) depth2 = conf2.getDepthInBlocks();
	                        BigInteger aValue = a.getValue();
	                        BigInteger bValue = b.getValue();
	                        BigInteger aCoinDepth = aValue.multiply(BigInteger.valueOf(depth1));
	                        BigInteger bCoinDepth = bValue.multiply(BigInteger.valueOf(depth2));
	                        int c1 = bCoinDepth.compareTo(aCoinDepth);
	                        if (c1 != 0) return c1;
	                        // The "coin*days" destroyed are equal, sort by value alone to get the lowest transaction size.
	                        int c2 = bValue.compareTo(aValue);
	                        if (c2 != 0) return c2;
	                        // They are entirely equivalent (possibly pending) so sort by hash to ensure a total ordering.
	                        BigInteger aHash = a.getParentTransaction().getHash().toBigInteger();
	                        BigInteger bHash = b.getParentTransaction().getHash().toBigInteger();
	                        return aHash.compareTo(bHash);
	                    }
	                });
	            }
	            // Now iterate over the sorted outputs until we have got as close to the target as possible or a little
	            // bit over (excessive value will be change).
	            long total = 0;
	            for (TransactionOutput output : sortedOutputs) {
	                if (total >= target) break;
	                // Only pick chain-included transactions, or transactions that are ours and pending.
	                Transaction t = output.getParentTransaction();
	                if (!shouldSelect(output)) continue;
	                selected.add(output);
	                total += output.getValue().longValue();
	            }
	            // Total may be lower than target here, if the given candidates were insufficient to create to requested
	            // transaction.
	            return new CoinSelection(BigInteger.valueOf(total), selected);
	        }
	        
	        protected int getOutputIndexInTx(Transaction tx, TransactionOutput tout) {
	    		// TODO Auto-generated method stub
	    		for(TransactionInput o : tx.getInputs()){
	    			if(o.getConnectedOutput().getParentTransaction().getHashAsString().equals(tout.getParentTransaction().getHashAsString())) {
	    				if(tout.getParentTransaction().getOutput((int)o.getOutpoint().getIndex()).equals(tout)) {
	    					return (int)o.getOutpoint().getIndex();
	    				}
	    			}
	    		}
	    		return -1;
	    	}

	        /** Sub-classes can override this to just customize whether transactions are usable, but keep age sorting. */
	        protected boolean shouldSelect(TransactionOutput out) {
	            return isSelectable(out);
	        }

	        public static boolean isSelectable(TransactionOutput out) {
	            // Only pick chain-included transactions, or transactions that are ours and pending.
	        	Transaction tx = out.getParentTransaction();
	            TransactionConfidence confidence = tx.getConfidence();
	            ConfidenceType type = confidence.getConfidenceType();
	            if (type.equals(ConfidenceType.BUILDING)) return true;
	            return type.equals(ConfidenceType.PENDING) &&
	                   confidence.getSource().equals(TransactionConfidence.Source.SELF) &&
	                   confidence.numBroadcastPeers() > 1;
	        }
	    }
	public class NoColorCoinSelector extends ColorAwareDefaultCoinSelector {

		private Wallet searchWallet = null;
		@Override
		protected boolean shouldSelect(TransactionOutput out) {
			// TODO Auto-generated method stub
			// if its a color we know about don't select it.
			ColorGenisis cg = GetColorTransactionSearchHistory(searchWallet, out.getParentTransaction().getHashAsString(),  getOutputIndexInTx( out.getParentTransaction(), out));
			if(cg != null)
				return false;
			
			return super.shouldSelect(out);
		}

		public NoColorCoinSelector(Wallet searchInWallet) {
			super();
			this.searchWallet = searchInWallet;
			// TODO Auto-generated constructor stub
		}
	}


	public boolean createIssueanceForUntrustedAsset(String key, Asset selectedAsset, 
				String genTransactionHash, int outIndex, String name, String desc) {
		boolean ret = false;
		try {
			
			
			Issuance is = new Issuance();
			is.asssetId = selectedAsset.id;
			is.geneisistransaction = genTransactionHash;
			is.outputindex = outIndex;
		

			ret = Request.Post("http://localhost:8080/issue/" + key).bodyString(new Gson().toJson(is, Issuance.class), ContentType.APPLICATION_JSON )
			        .execute().handleResponse(new ResponseHandler<Boolean>() {

						@Override
						public Boolean handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							// TODO Auto-generated method stub
							BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

							if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
								StringBuffer result = new StringBuffer();
								String line = "";
								while ((line = rd.readLine()) != null) {
								    result.append(line);
								}

								//JsonObject o = new JsonParser().parse(result.toString()).getAsJsonObject();
								
								//System.out.println(assets.get(0).name);
								return true;
							}
							return false;
							
						}
					});
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return ret;
		
	}
}
