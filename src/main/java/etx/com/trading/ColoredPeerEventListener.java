package etx.com.trading;

import java.util.List;
import java.util.Map;



import java.util.concurrent.CountDownLatch;

import com.google.bitcoin.core.AbstractPeerEventListener;
import com.google.bitcoin.core.Block;
import com.google.bitcoin.core.GetDataMessage;
import com.google.bitcoin.core.Message;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerEventListener;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletTransaction.Pool;

public class ColoredPeerEventListener implements PeerEventListener {

	
	private final Map<String, Integer> colorMap;
	private boolean deepSearch = false;
	private Wallet workingWallet;
	final CountDownLatch latch = new CountDownLatch(1);
	
	public ColoredPeerEventListener(Map<String, Integer> map, Wallet w)
	{
		colorMap = map;
		workingWallet = w;
	}
	
	@Override
	public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {
		// TODO Auto-generated method stub
		//if (latch.getCount() == 0) return;
	try{
		
			for(Transaction t : block.getTransactions())
	        {					
				if(colorMap.containsKey(t.getHashAsString())){
					System.out.println("some transaction we will add to wallet: " + t.getHashAsString());
					// all outputs
					if(colorMap.get(t.getHashAsString()).intValue() == -1){
						System.out.println("all outputs");
						for(TransactionOutput outs : t.getOutputs())
						{
							if(outs.getSpentBy() != null){
								System.out.println("spent");
								colorMap.put(outs.getSpentBy().getOutpoint().getHash().toString(), -1);
							}
						}
					}
					else if(t.getOutput(colorMap.get(t.getHashAsString()).intValue()).getSpentBy() != null)
					{
						System.out.println("output index: " + colorMap.get(t.getHashAsString()).intValue());
						String addhash = t.getOutput(colorMap.get(t.getHashAsString()).intValue()).getSpentBy().getOutpoint().getHash().toString();
						System.out.println("add hash: " + addhash);
						colorMap.put(addhash, -1);
					}
					if(workingWallet.getTransaction(t.getHash()) == null) {
						System.err.println("AddingColorTx: " + t);
						workingWallet.addWalletTransaction(new com.google.bitcoin.core.WalletTransaction(Pool.PENDING, t));
					}
					deepSearch = true;
				}
				if(deepSearch){
					int insIndex = 0;
					boolean addToWallet = false;
					for( TransactionInput ins : t.getInputs()){
						
						if(colorMap.containsKey(ins.getOutpoint().getHash().toString()))
						{
	
						//	if(colorMap.get(ins.getOutpoint().getHash().toString()).intValue() == ins.getOutpoint().getIndex()){
								System.out.println("connecting transaction:\n" + t +"\nto:\n" + ins.getOutpoint().getHash().toString());
								colorMap.put(t.getHashAsString(), insIndex);
								addToWallet = true;
						//	}
						}							
						insIndex++;
					}
					if(addToWallet) {
						if(workingWallet.getTransaction(t.getHash()) == null) {
							System.err.println("AddingColorTx: " + t);
							workingWallet.addWalletTransaction(new com.google.bitcoin.core.WalletTransaction(Pool.PENDING, t));					
						}
					}
				}
				
	        }
		}
		catch(Exception ex) {
			System.out.println("ColoredPeerEventListener - onBlocksDownloaded exception:\n");
			ex.printStackTrace();
		}
		//latch.countDown();
	}

	@Override
	public void onChainDownloadStarted(Peer peer, int blocksLeft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerConnected(Peer peer, int peerCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerDisconnected(Peer peer, int peerCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Message onPreMessageReceived(Peer peer, Message m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onTransaction(Peer peer, Transaction t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Message> getData(Peer peer, GetDataMessage m) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
