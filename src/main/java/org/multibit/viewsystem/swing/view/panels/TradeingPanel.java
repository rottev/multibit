package org.multibit.viewsystem.swing.view.panels;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import java.awt.ScrollPane;

import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.multibit.controller.Controller;
import org.multibit.controller.bitcoin.BitcoinController;
import org.multibit.file.WalletSaveException;
import org.multibit.message.Message;
import org.multibit.message.MessageManager;
import org.multibit.model.bitcoin.WalletAddressBookData;
import org.multibit.model.bitcoin.WalletData;
import org.multibit.model.bitcoin.WalletInfoData;
import org.multibit.network.ReplayManager;
import org.multibit.network.ReplayTask;
import org.multibit.store.WalletVersionException;
import org.multibit.utils.ImageLoader;
import org.multibit.viewsystem.DisplayHint;
import org.multibit.viewsystem.View;
import org.multibit.viewsystem.Viewable;
import org.multibit.viewsystem.swing.MultiBitFrame;
import org.multibit.viewsystem.swing.action.AcceptOfferAction;
import org.multibit.viewsystem.swing.action.CreateAssetAction;
import org.multibit.viewsystem.swing.action.CreateNewColoredReceivingAddressAction;
import org.multibit.viewsystem.swing.action.CreateNewOfferAction;
import org.multibit.viewsystem.swing.action.IssueAssetAction;
import org.multibit.viewsystem.swing.view.components.MultiBitButton;

import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.core.Transaction.SigHash;
import com.google.bitcoin.core.Wallet.SendRequest;
import com.google.bitcoin.core.Wallet;
import com.google.common.util.concurrent.ListenableFuture;

import etx.com.trading.BaseProposalModel;
import etx.com.trading.BaseStore;
import etx.com.trading.BaseTradesModel;
import etx.com.trading.BaseTrading;
import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTrading.Fufilment;
import etx.com.trading.BaseTrading.Issuance;
import etx.com.trading.BaseAssetModel;
import etx.com.trading.BaseTrading.Proposal;
import etx.com.trading.BaseTrading.ProposalInfo;
import etx.com.trading.ColorProperty;
import etx.com.trading.FufilEventListener;

import java.awt.ComponentOrientation;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.*;


public class TradeingPanel extends JPanel implements Viewable, FufilEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2398664167122332450L;
	private final JTable tableAssets = new JTable();
	private final JTable tableTrades = new JTable();
	private final JTable oferingsTable = new JTable();
	private Controller controller;
	private final Button button = new Button("New button");
	private final MultiBitButton buttonAddColor = new MultiBitButton("Add color to wallet");
	protected Action createNewAssetAction;
	private CreateAssetAction createAssetAction;
	private Action createNewOfferAction;
	private Action IssueAssetAction;
	private Action acceptOfferAction;
	protected MultiBitFrame mainFrame;
	private MultiBitButton createNewButton;
	private MultiBitButton issueAssetButton;
	private MultiBitButton createOfferButton;
	private MultiBitButton acceptOfferButton  = new MultiBitButton("Accept Offer");
	private BaseStore database = null; 

	     
		 
	
	
	public TradeingPanel(BitcoinController bitcoinController,
			MultiBitFrame mainFrame) {
		
		this.mainFrame = mainFrame;
		this.controller = bitcoinController;
		
		try {
			database = new BaseStore("ccdb2");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		 BaseTrading.getInstance().AddFufilmentEventListener(this);
		
		// TODO Auto-generated constructor stub
		GridBagLayout gridLayout = new GridBagLayout();
		 GridBagConstraints c = new GridBagConstraints();
		 c.fill = GridBagConstraints.BOTH;
		 GridBagConstraints c2 = new GridBagConstraints();
		 GridBagConstraints c3 = new GridBagConstraints();
		// Components
		    c.gridwidth = GridBagConstraints.HORIZONTAL;
		    c.weightx = 1;
		    c.weighty = 1;
		    c.gridx = 0;
		    c.gridy = 0;
		
		this.setLayout(gridLayout);
		JScrollPane scrollPane_1 = new JScrollPane();
		add(scrollPane_1, c);
		tableAssets.setModel(new BaseAssetModel());		
		scrollPane_1.setViewportView(tableAssets);
		
		//set table selection
		 ListSelectionModel listSelectionModel = tableAssets.getSelectionModel();
		 listSelectionModel.addListSelectionListener(new ListSelectionListener() {										
			@Override
			public void valueChanged(ListSelectionEvent e) {
				tableAssets.getSelectedRow();
				buttonAddColor.setEnabled(true);
				issueAssetButton.setEnabled(true);
				
			}
		 });
		    
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
       createNewAssetAction = getCreateNewAssetAction();
       createNewButton = new MultiBitButton(createNewAssetAction, controller);
       createNewButton.setText(controller.getLocaliser().getString("tradingPanel.newAssetButton"));
       
       IssueAssetAction = getIssueAssetAction();
       issueAssetButton = new MultiBitButton(IssueAssetAction, controller);
       issueAssetButton.setText(controller.getLocaliser().getString("tradingPanel.issueAssetButton"));
       issueAssetButton.setEnabled(false);
       
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
       
       	c2.fill = GridBagConstraints.NONE;
  
       
	     c2.gridwidth = 1;
	     c2.weightx = .01;
	     c2.weighty = .2;
	     c2.gridx = 0;
	     c2.gridy = 1;
	     c2.anchor = GridBagConstraints.WEST;
	     buttonsPanel.add(createNewButton,   c2);
	     
	     c2.gridx = 1;
	     buttonsPanel.add(issueAssetButton,   c2);
	     
	     
	     buttonAddColor.setEnabled(false);
	     buttonAddColor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Asset a = ((BaseAssetModel) tableAssets.getModel()).getAssetAtRow( tableAssets.getSelectedRow());
					if (((BitcoinController) controller).getModel().getActiveWallet() != null) {
						
						WalletInfoData info = ((BitcoinController) controller).getModel().getActiveWalletWalletInfo();
						String colors = info.getProperty(ColorProperty.propertyName);
						if(colors != null){
							if(!colors.contains(a.symbol)){
								colors += "#" + a.symbol;
							}
						//	else
						//		return;
							
						}
						else {
							colors = a.symbol; 
						}
						
						//mark wallet for color
						info.put(ColorProperty.propertyName, colors);
						
						//
						//((BitcoinController) controller).getModel().getActiveWallet();
						String activeWallet = ((BitcoinController) controller).getModel().getActiveWalletFilename();
						 WalletData wdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(activeWallet);
						 List<WalletData> wlist = new ArrayList<WalletData>();
						 wlist.add(wdata);
						 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						 ReplayTask replayTask = null;
						 Map<String, Issuance> isuuancesMap  = BaseTrading.getInstance().getIssuane(a.id);
						 //for now just use the first issuance instace for replay
						 String firstKey=null;
						 int firstIndex = 0;
						 String date = null;
						 for(Entry<String, Issuance> es : isuuancesMap.entrySet())
						 {
							 firstKey = es.getValue().geneisistransaction;
							 firstIndex = es.getValue().outputindex;
							 date =  es.getValue().date;
						 }
						 
						 try {
							replayTask = new ReplayTask(wlist, sdf.parse(date), ReplayTask.UNKNOWN_START_HEIGHT,firstKey, firstIndex);
						} catch (ParseException e) {
							e.printStackTrace();
						}

						 ReplayManager.INSTANCE.offerReplayTask(replayTask);
					}
				}
			});
	     
	     c2.gridx = 2;
		
		buttonsPanel.add(buttonAddColor,   c2);
		c2.gridx = 0;
		
		add(buttonsPanel, c2);
		
		
		 c.fill = GridBagConstraints.BOTH;
		// Components
		    c.gridwidth = GridBagConstraints.HORIZONTAL;
		    c.weightx = 1;
		    c.weighty = 1;
		    c.gridx = 0;
		    c.gridy = 2;
		

	   //  buttonsPanel.add(createNewButton,   c2);
		
		// setup asset table
		JScrollPane scrollPane2 = new JScrollPane();
		add(scrollPane2, c);
		tableTrades.setModel(new BaseTradesModel());		
		scrollPane2.setViewportView(tableTrades);
		
		
		//set table selection
		 ListSelectionModel listSelectionModel2 = tableTrades.getSelectionModel();
		 listSelectionModel2.addListSelectionListener(new ListSelectionListener() {										
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//tableAssets.getSelectedRow();
				acceptOfferButton.setEnabled(true);
				/*
				Proposal p = ((BaseProposalModel)(tableTrades.getModel())).getProposalAtRow(tableTrades.getSelectedRow());
				String activeWallet = ((BitcoinController) controller).getModel().getActiveWalletFilename();
				WalletData wdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(activeWallet);
				String colors = wdata.getWalletInfo().getProperty(ColorProperty.propertyName);
				ProposalInfo info = ProposalInfo.parse(p);
				// does current wallet have the asset requested in the proposal?
				if(colors.contains(info.takeAssetId)){
					
				}
				*/
				
			}
		 });
		 

		 
		    JPanel buttonsPanel2 = new JPanel(new GridBagLayout());
			 
	       	c2.fill = GridBagConstraints.NONE;
       
		     c2.gridwidth = 1;
		     c2.weightx = .01;
		     c2.weighty = .2;
		     c2.gridx = 0;
		     c2.gridy = 4;
		     c2.anchor = GridBagConstraints.WEST;
		     
			 
			 acceptOfferAction = getAcceptOfferAction();
			 acceptOfferButton = new MultiBitButton(acceptOfferAction, controller);
			 acceptOfferButton.setText(controller.getLocaliser().getString("offers.acceptoffer"));
			 acceptOfferButton.setEnabled(false);
			 buttonsPanel2.add(acceptOfferButton, c2);
			 c2.gridx = 1;
			 
				
			createNewOfferAction = getCreateNewOfferActoin();
			createOfferButton = new MultiBitButton(createNewOfferAction, controller);
			createOfferButton.setText(controller.getLocaliser().getString("offers.addoffer"));

		     buttonsPanel2.add(createOfferButton,   c2);
		     c2.gridx = 0;
		     add(buttonsPanel2, c2);
		     /*
		     createOfferButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String activeWallet = ((BitcoinController) controller).getModel().getActiveWalletFilename();
					WalletData wdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(activeWallet);
					String[] colors = ColorProperty.deserlizie(wdata.getWalletInfo().getProperty(ColorProperty.propertyName));
					
					
				}
			});
			*/
		
	}

	@Override
	public void displayView(DisplayHint displayHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Icon getViewIcon() {
		// TODO Auto-generated method stub
		return ImageLoader.createImageIcon(ImageLoader.TRANSACTIONS_ICON_FILE);
	}

	@Override
	public View getViewId() {
		// TODO Auto-generated method stub
		 return View.TRADING_VIEW;
	}

	@Override
	public String getViewTitle() {
		// TODO Auto-generated method stub
		return controller.getLocaliser().getString("showTradingAction.text");
	}

	@Override
	public String getViewTooltip() {
		// TODO Auto-generated method stub
		return controller.getLocaliser().getString("showTradingAction.tooltip");
	}

	@Override
	public void navigateAwayFromView() {
		// TODO Auto-generated method stub
		
	}
	
	 protected Action getIssueAssetAction() {
		 IssueAssetAction = new IssueAssetAction(((BitcoinController)(controller)), mainFrame, this);
	        return IssueAssetAction;
		 //return null;
	    }
	
	 protected Action getCreateNewAssetAction() {
		 createAssetAction = new CreateAssetAction(((BitcoinController)(controller)), mainFrame, this);
	        return createAssetAction;
		 //return null;
	    }
	 
	 protected Action getCreateNewOfferActoin()
	 {
		 createNewOfferAction = new CreateNewOfferAction(((BitcoinController)(controller)), mainFrame, this);
		 return createNewOfferAction;
	 }
	 
	 protected Action getAcceptOfferAction()
	 {
		 acceptOfferAction = new AcceptOfferAction(((BitcoinController)(controller)), mainFrame, this);
		 return acceptOfferAction;
	 }

	public Object getLabelTextArea() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Asset getSelectedAsset()
	{
		int selectedrow = tableAssets.getSelectedRow();
		if(selectedrow >= 0)
			return ((BaseAssetModel) tableAssets.getModel()).getAssetAtRow(selectedrow);
		return null;
	}
	
	public Proposal getSelectedProposal()
	{
		int selectedrow = tableTrades.getSelectedRow();
		if(selectedrow >= 0)
			return ((BaseTradesModel) tableTrades.getModel()).getProposalAtRow(selectedrow);
		return null;
	}

	@Override
	public void OnNewFufilment(Fufilment f) {
		
		//is this a fulfilment to my proposal?
		try {
			try {
				database = new BaseStore("ccdb2");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// check if this is a proposal i remember sending and i havent broadcast a tx for it yet
			// maybe we can check on other fufillment requests that happend to see what offers we can remove or mark or do things with.
			if(database.isMyProposalStillNotFufilled(f.proposalHash)) {
				byte[] txBytes = DatatypeConverter.parseHexBinary(f.txHex);
				
			    String toAddress = null;
			    Wallet walletUsed = null;
			    WalletData walletData = null;
			    byte[] data = database.getProposal(f.proposalHash);
			    
			    if(data == null) {
			    	// either no
			    }
			    
			    Proposal p = Proposal.parse(new String(data));
			    ProposalInfo pinfo = ProposalInfo.parse(p);
			    toAddress = pinfo.takeAddress;
			    
			    //check proposal to see what the wallet we wanteded to get trasaction to and then get network params
			    for (WalletData wdata  :((BitcoinController) controller).getModel().getPerWalletModelDataList())
			    {
			    	for( WalletAddressBookData adder : wdata.getWalletInfo().getReceivingAddresses()) {
			    		if(adder.getAddress().compareTo(toAddress) == 0) {
			    			walletUsed = wdata.getWallet();
			    			walletData = wdata;
			    		}
			    	}
			    }
			    
			  
			    // from it, also sue that wallet to sign the transaction then save it then broadcast it
			    Transaction t = new Transaction(walletUsed.getNetworkParameters(), txBytes);
			    if(t != null) {
			    	// did we already commit the tx and send it?
			    	if(walletUsed.getTransaction(t.getHash()) != null) {
			    		database.setProposalFufilled(f.proposalHash);
				    	return;
			    	}
			    	
			    	if(tryPingPeer()) {
			    		SendRequest sr = SendRequest.forTx(t);
			    		sr.ensureMinRequiredFee = false;
			    		sr.fee = BigInteger.ZERO;
			    		boolean completedOk = walletUsed.completeTx(sr, false);
			    		if(completedOk) {
					    	walletUsed.commitTx(t);
					    	database.setProposalFufilled(f.proposalHash);
					    	t.signInputs(SigHash.ALL, walletUsed);
					    	PeerGroup peerGroup = ((BitcoinController) controller).getMultiBitService().getPeerGroup();
					    	ListenableFuture<Transaction> lf = peerGroup.broadcastTransaction(t);
					    	Transaction senttx = lf.get();
					    	if (senttx.getConfidence() != null) {     
					    		 senttx.getConfidence().addEventListener((BitcoinController) controller);
					    	}
					    	trySaveWallet(walletData);
					    	notifyAllWalletsOfTx(walletData, senttx);
			    		}
			    		 else {
			    			 //TODO: figure out a way to notify the ui here.
			    			 // do it anyway since wallet might not have enough for fee and cannot verify we are actually have the fee in the transaction
			    			  	walletUsed.commitTx(t);
						    	database.setProposalFufilled(f.proposalHash);
						    	t.signInputs(SigHash.ALL, walletUsed);
						    	PeerGroup peerGroup = ((BitcoinController) controller).getMultiBitService().getPeerGroup();
						    	ListenableFuture<Transaction> lf = peerGroup.broadcastTransaction(t);
						    	Transaction senttx = lf.get();
						    	if (senttx.getConfidence() != null) {     
						    		 senttx.getConfidence().addEventListener((BitcoinController) controller);
						    	}
						    	trySaveWallet(walletData);
						    	notifyAllWalletsOfTx(walletData, senttx);
			    		 }
				    	 
			    	}
			    }
			    
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	
		// TODO Auto-generated method stub
		 

		
		
		/*
		EventQueue.invokeLater(new Runnable() { 
			  @Override
			  public void run() {
			   
			  }
			});
			*/
	}
	
	private boolean tryPingPeer() throws IOException {
		   List<Peer> connectedPeers = ((BitcoinController) controller).getMultiBitService().getPeerGroup().getConnectedPeers();
		    boolean atLeastOnePingWorked = false;
		    if (connectedPeers != null) {
		      for (Peer peer : connectedPeers) {
		        try {

		          ListenableFuture<Long> result = peer.ping();
		          result.get(4, TimeUnit.SECONDS);
		          atLeastOnePingWorked = true;
		          break;
		        } catch (ProtocolException e) {
		          System.out.println("Peer '" + peer.getAddress().toString() + "' failed ping test. Message was " + e.getMessage());
		        } catch (InterruptedException e) {
		        	System.out.println("Peer '" + peer.getAddress().toString() + "' failed ping test. Message was " + e.getMessage());
		        } catch (ExecutionException e) {
		        	System.out.println("Peer '" + peer.getAddress().toString() + "' failed ping test. Message was " + e.getMessage());
		        } catch (TimeoutException e) {
		        	System.out.println("Peer '" + peer.getAddress().toString() + "' failed ping test. Message was " + e.getMessage());
		        }
		      }
		    }

		    if (!atLeastOnePingWorked) {
		      throw new IllegalStateException("All peers failed ping test (check network)");
		    }
		    return true;

	}
	
	private void notifyAllWalletsOfTx(WalletData perWalletModelData, Transaction sendTransaction)
	{
	
		 try {
		        // Notify other wallets of the send (it might be a send to or from them).
		        List<WalletData> perWalletModelDataList = ((BitcoinController) controller).getModel().getPerWalletModelDataList();

		        if (perWalletModelDataList != null) {
		          for (WalletData loopPerWalletModelData : perWalletModelDataList) {
		            if (!perWalletModelData.getWalletFilename().equals(loopPerWalletModelData.getWalletFilename())) {
		              Wallet loopWallet = loopPerWalletModelData.getWallet();
		              if (loopWallet.isPendingTransactionRelevant(sendTransaction)) {
		                // The loopPerWalletModelData is marked as dirty.
		                if (loopPerWalletModelData.getWalletInfo() != null) {
		                  synchronized (loopPerWalletModelData.getWalletInfo()) {
		                    loopPerWalletModelData.setDirty(true);
		                  }
		                } else {
		                  loopPerWalletModelData.setDirty(true);
		                }
		                if (loopWallet.getTransaction(sendTransaction.getHash()) == null) {
		                  System.err.println("MultiBit adding a new pending transaction for the wallet '"
		                          + loopPerWalletModelData.getWalletDescription() + "'\n" + sendTransaction.toString());
		                  loopWallet.receivePending(sendTransaction, null);
		                }
		              }
		            }
		          }
		        }
		      } catch (ScriptException e) {
		        e.printStackTrace();
		      } catch (VerificationException e) {
		        e.printStackTrace();
		      }
	}
	
	private void trySaveWallet(WalletData perWalletModelData)
	{
		  try {
			  ((BitcoinController) controller).getFileHandler().savePerWalletModelData(perWalletModelData, false);
		      } catch (WalletSaveException wse) {
		    	  System.err.println(wse.getClass().getCanonicalName() + " " + wse.getMessage());
		        MessageManager.INSTANCE.addMessage(new Message(wse.getClass().getCanonicalName() + " " + wse.getMessage()));
		      } catch (WalletVersionException wse) {
		    	  System.err.println(wse.getClass().getCanonicalName() + " " + wse.getMessage());
		        MessageManager.INSTANCE.addMessage(new Message(wse.getClass().getCanonicalName() + " " + wse.getMessage()));
		      }
	}
	
	public void refreshAssets()
	{
		((BaseAssetModel)tableAssets.getModel()).Refresh();
	}
	
	public void refreshIssueance()
	{
		BaseTrading.getInstance().getIssuane(true);
	}

}
