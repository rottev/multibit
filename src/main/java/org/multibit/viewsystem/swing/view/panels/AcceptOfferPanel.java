package org.multibit.viewsystem.swing.view.panels;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.awt.ScrollPane;

import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;















//import org.bitcoinj.wallet.Protos.Transaction;
import com.google.bitcoin.core.Transaction;

import org.multibit.controller.Controller;
import org.multibit.controller.bitcoin.BitcoinController;
import org.multibit.model.bitcoin.WalletData;
import org.multibit.model.bitcoin.WalletInfoData;
import org.multibit.network.ReplayManager;
import org.multibit.network.ReplayTask;
import org.multibit.utils.ImageLoader;
import org.multibit.viewsystem.DisplayHint;
import org.multibit.viewsystem.View;
import org.multibit.viewsystem.Viewable;
import org.multibit.viewsystem.swing.MultiBitFrame;
import org.multibit.viewsystem.swing.action.CancelBackToParentAction;
import org.multibit.viewsystem.swing.action.CreateNewColoredReceivingAddressAction;
import org.multibit.viewsystem.swing.action.OkAction;
import org.multibit.viewsystem.swing.action.OkBackToParentAction;
import org.multibit.viewsystem.swing.view.components.MultiBitButton;
import org.multibit.viewsystem.swing.view.components.MultiBitLabel;
import org.multibit.viewsystem.swing.view.dialogs.AcceptOfferDialog;
import org.multibit.viewsystem.swing.view.dialogs.CreateOfferDialog;
import org.spongycastle.crypto.params.KeyParameter;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction.SigHash;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutPoint;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletTransaction;
import com.google.bitcoin.core.WalletTransaction.Pool;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.common.base.Preconditions;

import etx.com.trading.BaseTradesModel;
import etx.com.trading.BaseTrading;
import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTrading.ColorGenisis;
import etx.com.trading.BaseTrading.Issuance;
import etx.com.trading.BaseAssetModel;
import etx.com.trading.BaseTrading.Proposal;
import etx.com.trading.BaseTrading.ProposalInfo;
import etx.com.trading.ColorProperty;

import java.awt.ComponentOrientation;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;



public class AcceptOfferPanel extends JPanel implements Viewable {
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
	protected Action createNewColoredAddressAction;
	private CreateNewColoredReceivingAddressAction createNewColoredReceivingAddressAction;
	protected MultiBitFrame mainFrame;
	private MultiBitButton createOfferButton;
	private MultiBitButton cancelOfferButton;
	private TradeingPanel panel;
	private AcceptOfferDialog dialog;
    private MultiBitLabel selectRecivingWalletLabel;
    private MultiBitLabel selectSendingWalletLabel;
    private MultiBitLabel statusLabel = new MultiBitLabel("");
    private static final byte[] EMPTY_BYTES = new byte[32];

    private JComboBox reciveWalletCB;
    private JComboBox sendWalletCB;

    private Proposal proposal;
	
	
	

	public AcceptOfferPanel(BitcoinController bitcoinController,
			TradeingPanel tradePanel, AcceptOfferDialog acceptOfferDialog) {
		super();
		// TODO Auto-generated constructor stub
	//	this.mainFrame = mainFrame;
		this.controller = bitcoinController;
		this.panel = tradePanel;
		this.dialog = acceptOfferDialog;
		
		proposal = tradePanel.getSelectedProposal();
		
		GridBagLayout gridLayout = new GridBagLayout();
		 GridBagConstraints c = new GridBagConstraints();
		 c.insets = new Insets(5, 10, 5, 10);
		 c.fill = GridBagConstraints.BOTH;
		// Components
		    c.gridwidth = GridBagConstraints.HORIZONTAL;
		    c.weightx = 1;
		    c.weighty = 1;
		    c.gridx = 0;
		    c.gridy = 0;
		
		this.setLayout(gridLayout);
		JPanel panel = new JPanel();
		panel.setLayout(gridLayout);
		
        CancelBackToParentAction cancelAction = new CancelBackToParentAction(controller,
                ImageLoader.createImageIcon(ImageLoader.CROSS_ICON_FILE), acceptOfferDialog);
        cancelOfferButton = new MultiBitButton(cancelAction, controller);
        
        OkAction okAction = new OkAction(controller, ImageLoader.createImageIcon(ImageLoader.ACCEPT_ICON_FILE), acceptOfferDialog);
        createOfferButton = new MultiBitButton(okAction,  controller);
        createOfferButton.setEnabled(false);
      
		
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.NONE;
        c2.anchor = GridBagConstraints.WEST;
        
		GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.3;
        constraints.weighty = 0.3;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
		
		
		String activeWallet = ((BitcoinController) controller).getModel().getActiveWalletFilename();
		final WalletData wdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(activeWallet);
		String[] colors = ColorProperty.deserlizie(wdata.getWalletInfo().getProperty(ColorProperty.propertyName));
		
       
        // recvie wallet area
        constraints.gridx = 0;
        constraints.gridy = 0;
        selectRecivingWalletLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "AceeptOfferPanel.selectReciveWallet"));
        
        panel.add(selectRecivingWalletLabel, constraints);
        
        List<WalletData> wListData = ((BitcoinController) controller).getModel().getPerWalletModelDataList();
        reciveWalletCB = new JComboBox();
        reciveWalletCB.addItem("Please select a wallet");
        for(WalletData singleWalletData : wListData)
        	reciveWalletCB.addItem(singleWalletData.getWalletFilename());
        
        reciveWalletCB.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				maybeEnableOK();
				if(reciveWalletCB.getSelectedIndex() != 0)
				{
					WalletData selectedwdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(
							(String)reciveWalletCB.getItemAt(reciveWalletCB.getSelectedIndex()));
					String[] sectedcolors = ColorProperty.deserlizie(selectedwdata.getWalletInfo().getProperty(ColorProperty.propertyName));
				
				}
				
			}
		});

      //send wallet area
        constraints.gridx = 1;
        panel.add(reciveWalletCB, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        selectSendingWalletLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "AceeptOfferPanel.selectSendWallet"));
        
        panel.add(selectSendingWalletLabel, constraints);

        sendWalletCB = new JComboBox();
        sendWalletCB.addItem("Please select a wallet");
        for(WalletData singleWalletData : wListData)
        	sendWalletCB.addItem(singleWalletData.getWalletFilename());
        
        sendWalletCB.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				maybeEnableOK();
				if(sendWalletCB.getSelectedIndex() != 0)
				{
					WalletData selectedwdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(
							(String)sendWalletCB.getItemAt(sendWalletCB.getSelectedIndex()));
					String[] sectedcolors = ColorProperty.deserlizie(selectedwdata.getWalletInfo().getProperty(ColorProperty.propertyName));
				
				}
				
			}
		});
    
        constraints.gridx = 1;
        panel.add(sendWalletCB, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        
        panel.add(statusLabel, constraints);
        
      
        
        // add buttons
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
		 
        constraints.fill = GridBagConstraints.NONE;
   
        constraints.gridwidth = 1;
        constraints.weightx = .01;
        constraints.weighty = .2;
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.WEST;
	    
        constraints.gridx = 1;
        buttonsPanel.add(createOfferButton, constraints);
        constraints.gridx = 2;
        buttonsPanel.add(cancelOfferButton, constraints);
        constraints.gridx = 0;
        panel.add(buttonsPanel, constraints);
	    
        createOfferButton.addActionListener(new ActionListener() {
			
			private BaseTrading bt = BaseTrading.getInstance();
			

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//validate selection
				try
				{
					statusLabel.setText("");
					BigInteger totalgive = BigInteger.ZERO;
					boolean feePayed = false;
					ProposalInfo pinfo = ProposalInfo.parse(proposal);
					
					WalletData sendwdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(
							(String)sendWalletCB.getItemAt(sendWalletCB.getSelectedIndex()));				
					WalletData recivewdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(
							(String)reciveWalletCB.getItemAt(reciveWalletCB.getSelectedIndex()));
					
					Transaction tx = new Transaction(sendwdata.getWallet().getNetworkParameters());
					
					boolean finishTx = false;
					// first input has to be colored
					if(pinfo.giveBtc || (pinfo.giveBtc && pinfo.takeBtc)) {
						AddGiveOutputsToTransaction(tx, sendwdata.getWallet(), pinfo );
						finishTx = AddOutputsAndInputsToMyWallet(tx, pinfo);
					}
					else
					{
						AddOutputsAndInputsToMyWallet(tx, pinfo);
						finishTx = AddGiveOutputsToTransaction(tx, sendwdata.getWallet(), pinfo );
					}
					if(finishTx) {
						TakeFeeFromAnyBTCUnspent(tx);
						for(WalletData singleWalletData :((BitcoinController) controller).getModel().getPerWalletModelDataList()) {
							signInputs(tx,SigHash.ALL, singleWalletData.getWallet(),null);
						}
						StringBuilder sb = new StringBuilder();
						Formatter formatter = new Formatter(sb);
					    for (byte b : tx.bitcoinSerialize()) {
					        formatter.format("%02x", b);  
					    }
					    bt.createFufil(bt.GetColorScheme(), formatter.toString(), pinfo.hash.toString() );
					    ((OkAction)(createOfferButton.getAction())).close();
					    return; // donep
					}
					
					
					// lets create the transaction
					/*
					Set<Transaction> trans = sendwdata.getWallet().getTransactions(false);
					for (Iterator<Transaction> it = trans.iterator(); it.hasNext(); ) {
						Transaction t = it.next();
						if(t.isMine( sendwdata.getWallet()) && !t.isEveryOwnedOutputSpent(sendwdata.getWallet())) {
							List<TransactionOutput> outputs = t.getOutputs();
							for(int i = 0; i < outputs.size(); i++) {
								if(outputs.get(i).isMine(sendwdata.getWallet() ) && outputs.get(i).isAvailableForSpending() && outputs.get(i).getValue().compareTo(BigInteger.ZERO) > 0) {
									ColorGenisis cg = bt.GetColorTransactionSearchHistory(sendwdata.getWallet(), t.getHashAsString(), i);
									if(pinfo.takeBtc && cg == null){
										totalgive = totalgive.add(outputs.get(i).getValue());								
										tx.addInput(outputs.get(i));
										System.out.println("transaciont:\n" + tx.toString());
										
										// passed the mark now make sure to set the outputs accordingly
										System.out.println("totalgive: " + totalgive);
										System.out.println("outputs.get(i).getValue(): " + outputs.get(i).getValue());
										if(totalgive.compareTo(Utils.toNanoCoins(pinfo.takeQuantity)) >= 0){
											tx.addOutput(Utils.toNanoCoins(pinfo.takeQuantity), new Address(sendwdata.getWallet().getNetworkParameters(), pinfo.takeAddress));
											BigInteger backToWallet = totalgive.subtract(Utils.toNanoCoins(pinfo.takeQuantity).add(tx.calculateFee(sendwdata.getWallet())) );
											tx.addOutput(backToWallet,outputs.get(i).getScriptPubKey().getToAddress(sendwdata.getWallet().getNetworkParameters()));
											if(AddOutputsAndInputsToMyWallet(tx, pinfo))
											{
												TakeFeeFromAnyBTCUnspent(tx);
												System.out.println("transaciont before sign:\n" + tx.toString());
												for(WalletData singleWalletData :((BitcoinController) controller).getModel().getPerWalletModelDataList()) {
													signInputs(tx,SigHash.ALL, singleWalletData.getWallet(),null);
												}
												
												//signInputs(tx,SigHash.ALL, recivewdata.getWallet(),null);
												System.out.println("transaciont:\n" + tx.toString());
												System.out.println("\nhex:\n" + tx.bitcoinSerialize());
												StringBuilder sb = new StringBuilder();
												Formatter formatter = new Formatter(sb);
											    for (byte b : tx.bitcoinSerialize()) {
											        formatter.format("%02x", b);  
											    }
											    System.out.println("\nhex:\n" + formatter.toString());
											    bt.createFufil(bt.GetColorScheme(), formatter.toString(), pinfo.hash.toString() );
											    ((OkAction)(createOfferButton.getAction())).close();
											    return; // donep
											}
										}
									} 
									else if(cg != null && !pinfo.takeBtc){ //send a color to the offerer
										// find enough color
										Asset a = bt.getAssetForTransaction(cg.txout, cg.index);
										//Double multi = outputs.get(i).getValue().doubleValue() * a.satoshi_multiplyier;
										totalgive = totalgive.add(outputs.get(i).getValue());								
										tx.addInput(outputs.get(i));
										
										// passed the mark now make sure to set the outputs accordingly
										System.out.println("totalgive: " + totalgive);
										System.out.println("outputs.get(i).getValue(): " + outputs.get(i).getValue());
										
										if(totalgive.doubleValue() >= (Double.parseDouble(pinfo.takeQuantity) / a.satoshi_multiplyier)){
											tx.addOutput(new BigDecimal(Double.parseDouble(pinfo.takeQuantity) / a.satoshi_multiplyier).toBigInteger(), new Address(sendwdata.getWallet().getNetworkParameters(), pinfo.takeAddress));
											BigInteger backToWallet = totalgive.subtract(new BigDecimal(Double.parseDouble(pinfo.takeQuantity) / a.satoshi_multiplyier).toBigInteger());
											if(backToWallet.doubleValue() > 0)
												tx.addOutput(backToWallet,outputs.get(i).getScriptPubKey().getToAddress(sendwdata.getWallet().getNetworkParameters()));
											
											if(AddOutputsAndInputsToMyWallet(tx, pinfo))
											{
												TakeFeeFromAnyBTCUnspent(tx);
												System.out.println("transaciont before sign:\n" + tx.toString());
												for(WalletData singleWalletData :((BitcoinController) controller).getModel().getPerWalletModelDataList()) {
													signInputs(tx,SigHash.ALL, singleWalletData.getWallet(),null);
												}
												
												//signInputs(tx,SigHash.ALL, recivewdata.getWallet(),null);
												System.out.println("transaciont:\n" + tx.toString());
												System.out.println("\nhex:\n" + tx.bitcoinSerialize());
												StringBuilder sb = new StringBuilder();
												Formatter formatter = new Formatter(sb);
											    for (byte b : tx.bitcoinSerialize()) {
											        formatter.format("%02x", b);  
											    }
											    System.out.println("\nhex:\n" + formatter.toString());
											    bt.createFufil(bt.GetColorScheme(), formatter.toString(), pinfo.hash.toString() );
											    ((OkAction)(createOfferButton.getAction())).close();
											    return; // donep
												
											}
										}
											
										
									}
								}
							}
						}
					}
					*/
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				
				// error message
				System.err.println("Cant accept offer");
			}
		});
        
		add(panel, c);
		
		
	}

	private boolean TakeFeeFromAnyBTCUnspent(Transaction tx) {
		// TODO Auto-generated method stub
		try
		{
		for( WalletData wallet : ((BitcoinController) controller).getModel().getPerWalletModelDataList())
		{
			BaseTrading bt = BaseTrading.getInstance();
			
			
			for (WalletTransaction it : wallet.getWallet().getWalletTransactions()) {
				Transaction t = it.getTransaction();
				if(t.isMine( wallet.getWallet()) && !t.isEveryOwnedOutputSpent(wallet.getWallet()) && it.getPool() == Pool.UNSPENT) {
					List<TransactionOutput> outputs = t.getOutputs();
					for(int i = 0; i < outputs.size(); i++) {
						if(outputs.get(i).isAvailableForSpending() && outputs.get(i).getValue().compareTo(BigInteger.ZERO) > 0) {
							ColorGenisis cg = bt.GetColorTransactionSearchHistory(wallet.getWallet(), t.getHashAsString(), i);
							if(cg == null){
								double change = outputs.get(i).getValue().doubleValue() - Transaction.REFERENCE_DEFAULT_MIN_TX_FEE.doubleValue();
								if(change > 0 ) {
									if(!IsOutputInTx(tx, outputs.get(i))) {
										//just use this output to pay the fee.
										tx.addInput( outputs.get(i));
										System.out.println("transaciont:\n" + tx.toString());
										
										tx.addOutput( new BigDecimal(change).toBigInteger(), outputs.get(i).getScriptPubKey().getToAddress(wallet.getWallet().getNetworkParameters()));
										System.out.println("transaciont:\n" + tx.toString());
									//	tx.signInputs(SigHash.ALL, wallet.getWallet());
										return true;
										}
								}
								else
									System.out.println("IsOutputInTx: true: ");
								
							}
						}
					}
				}
			}
		}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
	
	private boolean AddGiveOutputsToTransaction(Transaction tx, Wallet from, ProposalInfo pinfo )
	{
		BigInteger totalgive = BigInteger.ZERO;
		boolean feePayed = false;
		
		try
		{
		//	List<Integer> alInputs = new ArrayList<Integer>();
		//	List<Integer> alOuputs = new ArrayList<Integer>();
			BaseTrading bt = BaseTrading.getInstance();

			for (WalletTransaction it : from.getWalletTransactions() ) {
				Transaction t = it.getTransaction();
				if(t.isMine( from) && !t.isEveryOwnedOutputSpent(from) && it.getPool() == Pool.UNSPENT) {
					List<TransactionOutput> outputs = t.getOutputs();
					for(int i = 0; i < outputs.size(); i++) {
						if(outputs.get(i).isMine(from ) && outputs.get(i).isAvailableForSpending() && outputs.get(i).getValue().compareTo(BigInteger.ZERO) > 0) {
							ColorGenisis cg = bt.GetColorTransactionSearchHistory(from, t.getHashAsString(), i);
							if(pinfo.takeBtc && cg == null){	
								totalgive = totalgive.add(outputs.get(i).getValue());								
								tx.addInput(outputs.get(i));
								
								System.out.println("transaciont:\n" + tx.toString());
								
								// passed the mark now make sure to set the outputs accordingly
								System.out.println("totalgive: " + totalgive);
								System.out.println("outputs.get(i).getValue(): " + outputs.get(i).getValue());
								if(totalgive.compareTo(Utils.toNanoCoins(pinfo.takeQuantity)) >= 0){
									tx.addOutput(Utils.toNanoCoins(pinfo.takeQuantity), new Address(from.getNetworkParameters(), pinfo.takeAddress));
									
									BigInteger backToWallet = totalgive.subtract(Utils.toNanoCoins(pinfo.takeQuantity) /*.add(tx.calculateFee(from)) */ );
									if(backToWallet.doubleValue() > 0)
										tx.addOutput(backToWallet,outputs.get(i).getScriptPubKey().getToAddress(from.getNetworkParameters()));			
									return true; // donep
									
								}
							} 
							else if(cg != null && !pinfo.takeBtc){ //send a color to the offerer
								// get asset and check its the one we should give
								Asset a = bt.getAssetForTransaction(cg.txout, cg.index);
								if (!a.id.equals(pinfo.takeAssetId))
									continue;
								
								totalgive = totalgive.add(outputs.get(i).getValue());								
								tx.addInput(outputs.get(i));
								tx.getInput(tx.getInputs().size() - 1).setSequenceNumber(tx.getOutputs().size() | Integer.MIN_VALUE);
								//alInputs.add(tx.getInputs().size() - 1);
								
								// passed the mark now make sure to set the outputs accordingly
								System.out.println("totalgive: " + totalgive);
								System.out.println("outputs.get(i).getValue(): " + outputs.get(i).getValue());
								
								if(totalgive.doubleValue() >= (Double.parseDouble(pinfo.takeQuantity) / a.satoshi_multiplyier)){
									tx.addOutput(new BigDecimal(Double.parseDouble(pinfo.takeQuantity) / a.satoshi_multiplyier).toBigInteger(), new Address(from.getNetworkParameters(), pinfo.takeAddress));
								//	alOuputs.add(tx.getOutputs().size() - 1);
									BigInteger backToWallet = totalgive.subtract(new BigDecimal(Double.parseDouble(pinfo.takeQuantity) / a.satoshi_multiplyier).toBigInteger());
									if(backToWallet.doubleValue() > 0) {
										tx.addOutput(backToWallet,outputs.get(i).getScriptPubKey().getToAddress(from.getNetworkParameters()));
										tx.getInput(tx.getInputs().size() - 1).setSequenceNumber((tx.getInputs().size() -1) | Integer.MIN_VALUE);
									}
									// since were color lets add the sequence for color
									//AddColorToTx(tx, alInputs ,alOuputs);
									
									return true;
								}																
							}
						}
					}
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void AddColorToTx(Transaction tx, List<Integer> inputs, List<Integer> ouputs) {
		if(inputs.size() <= ouputs.size())
		{
			Iterator<Integer> itero = ouputs.iterator();
			Iterator<Integer> iteri = inputs.iterator();
		
		}
		
	}

	private boolean IsOutputInTx(Transaction tx, TransactionOutput tout) {
		// TODO Auto-generated method stub
		for(TransactionInput o : tx.getInputs()){
			//if (o.getConnectedOutput() != null &&  o.getConnectedOutput().hashCode() == tout.hashCode())
			//	return true;
			//tout.getSpentBy();
			// same transas
			if(o.getConnectedOutput().getParentTransaction().getHashAsString().equals(tout.getParentTransaction().getHashAsString())) {
				if(tout.getParentTransaction().getOutput((int)o.getOutpoint().getIndex()).equals(tout)) {
					return true;
				}
			}
		}
		return false;
	}

	private void maybeEnableOK()
	{
		boolean enable = false;
		enable = (sendWalletCB.getSelectedIndex() != 0 && reciveWalletCB.getSelectedIndex() != 0);
		createOfferButton.setEnabled(enable);
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
	
	 protected Action getCreateNewColoredAddressAction() {
	     

		 return null;
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

	public Component getCancelButton() {
		// TODO Auto-generated method stub
		return cancelOfferButton;
	}
	
	private boolean AddOutputsAndInputsToMyWallet(Transaction tx, ProposalInfo prop)
	{
		try
		{
			BigInteger totalValue = BigInteger.ZERO;
			BaseTrading bt = BaseTrading.getInstance();
			WalletData recivewdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(
					(String)reciveWalletCB.getItemAt(reciveWalletCB.getSelectedIndex()));
			Address to = recivewdata.getWallet().getKeys().get(0).toAddress(recivewdata.getWallet().getNetworkParameters());
			Address from = null;
			Asset a = null;
			
			for(int i =0; i< prop.utxos.size(); i++) {
				int index =  Integer.parseInt(prop.utxosIndexes.get(i));
				// TODO: check tx exisits in the reciving wallet, since it might not be color aware,
				if(recivewdata.getWallet().getTransaction(new Sha256Hash(prop.utxos.get(i))) == null)
				{
					statusLabel.setText("Reciving wallet cannot verify proposal asset, perhaps its missing a color?");
					return false;
				}
				
				
				BigInteger value = bt.GetColorValue(recivewdata.getWallet(), prop.utxos.get(i),index);
				ColorGenisis isColor = bt.GetColorTransactionSearchHistory(recivewdata.getWallet(), prop.utxos.get(i),index);
				
				if(isColor != null && prop.giveBtc) // found color transaction requires normal btc
					continue;
				if(isColor == null && !prop.giveBtc) // found btc transaction requires color
					continue;
				
				if(isColor != null) {
					a = bt.getAssetForTransaction(isColor.txout, isColor.index);
					if(!a.id.equals(prop.giveAssetId)) // not the right asset
						continue;
				}
				
				Transaction prevTransaction = recivewdata.getWallet().getTransaction(new Sha256Hash(prop.utxos.get(i)));
				from = prevTransaction.getOutput(index).getScriptPubKey().getToAddress(recivewdata.getWallet().getNetworkParameters());
				tx.addInput(prevTransaction.getOutput(index));
			
				if(isColor != null) {
					tx.getInput(tx.getInputs().size() -1).setSequenceNumber(tx.getInputs().size() | Integer.MIN_VALUE);
					totalValue = totalValue.add(new BigDecimal(value.doubleValue() / a.satoshi_multiplyier).toBigInteger()); // total value needs to be in btc nano coins so we need to convert value before using it if this is a color.
				}
				else
				{
					totalValue = totalValue.add(value);
				}
				
				System.out.println("transaciont:\n" + tx.toString());
			}
			//TODO: cant use nanaocoins here since we dont use it in asset calculations for the wallet or adding new offer based on color	
			//BigInteger reciveValue = Utils.toNanoCoins(prop.giveQuantity);
			BigInteger reciveValue = BigInteger.ZERO;
			if(!prop.giveBtc && a != null)
			{
				System.out.println("AddOutputsAndInputsToMyWallet: Getting a color");
				// multiply for asset value.
				reciveValue = new BigDecimal(Double.parseDouble(prop.giveQuantity) / a.satoshi_multiplyier).toBigInteger();
			}
			else
			{
				reciveValue = Utils.toNanoCoins(prop.giveQuantity);
			}
			
			
			tx.addOutput(reciveValue, to);
			System.out.println("transaciont:\n" + tx.toString());
			BigInteger change = totalValue.subtract(reciveValue);
			if(change.compareTo(BigInteger.ZERO) > 0) {
				tx.addOutput(change, from);
				tx.getInput(tx.getInputs().size() -1).setSequenceNumber((tx.getOutputs().size() -1) | Integer.MIN_VALUE);
			}
			System.out.println("transaciont:\n" + tx.toString());
//			tx.signInputs(SigHash.ALL, recivewdata.getWallet());
			System.out.println("AddOutputsAndInputsToMyWallet: true");
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		System.out.println("AddOutputsAndInputsToMyWallet: false");
		return false;
	}
	
	public void trySignInputsWithAllWallets()
	{
		
	}
	
	public synchronized void signInputs(Transaction Tx, SigHash hashType, Wallet wallet, KeyParameter aesKey) throws ScriptException {
        // TODO: This should be a method of the TransactionInput that (possibly?) operates with a copy of this object.
        Preconditions.checkState(Tx.getInputs().size() > 0);
        Preconditions.checkState(Tx.getOutputs().size() > 0);

        // I don't currently have an easy way to test other modes work, as the official client does not use them.
        Preconditions.checkArgument(hashType == SigHash.ALL, "Only SIGHASH_ALL is currently supported");

        // The transaction is signed with the input scripts empty except for the input we are signing. In the case
        // where addInput has been used to set up a new transaction, they are already all empty. The input being signed
        // has to have the connected OUTPUT program in it when the hash is calculated!
        //
        // Note that each input may be claiming an output sent to a different key. So we have to look at the outputs
        // to figure out which key to sign with.

        TransactionSignature[] signatures = new TransactionSignature[Tx.getInputs().size()];
        ECKey[] signingKeys = new ECKey[Tx.getInputs().size()];
        for (int i = 0; i < Tx.getInputs().size(); i++) {
            TransactionInput input = Tx.getInputs().get(i);
            // We don't have the connected output, we assume it was signed already and move on
            if (input.getOutpoint().getConnectedOutput() == null) {
              //  log.warn("Missing connected output, assuming input {} is already signed.", i);
                continue;
            }
            try {
                // We assume if its already signed, its hopefully got a SIGHASH type that will not invalidate when
                // we sign missing pieces (to check this would require either assuming any signatures are signing
                // standard output types or a way to get processed signatures out of script execution)
                input.getScriptSig().correctlySpends(Tx, i, input.getOutpoint().getConnectedOutput().getScriptPubKey(), true);
               // log.warn("Input {} already correctly spends output, assuming SIGHASH type used will be safe and skipping signing.", i);
                continue;
            } catch (ScriptException e) {
                // Expected.
            }
        //    if (input.getScriptBytes().length != 0)
       //         log.warn("Re-signing an already signed transaction! Be sure this is what you want.");
            // Find the signing key we'll need to use.
            ECKey key = input.getOutpoint().getConnectedKey(wallet);
           
            if(key == null)
            	continue;
            // This assert should never fire. If it does, it means the wallet is inconsistent.
            Preconditions.checkNotNull(key, "Transaction exists in wallet that we cannot redeem: %s",
                                       input.getOutpoint().getHash());
            // Keep the key around for the script creation step below.
            signingKeys[i] = key;
            // The anyoneCanPay feature isn't used at the moment.
            boolean anyoneCanPay = false;
            byte[] connectedPubKeyScript = getConnectedPubKeyScript( input.getOutpoint());
            signatures[i] = Tx.calculateSignature(i, key, aesKey, connectedPubKeyScript, hashType, anyoneCanPay);
        }

        // Now we have calculated each signature, go through and create the scripts. Reminder: the script consists:
        // 1) For pay-to-address outputs: a signature (over a hash of the simplified transaction) and the complete
        //    public key needed to sign for the connected output. The output script checks the provided pubkey hashes
        //    to the address and then checks the signature.
        // 2) For pay-to-key outputs: just a signature.
        for (int i = 0; i < Tx.getInputs().size(); i++) {
            if (signatures[i] == null)
                continue;
            TransactionInput input = Tx.getInputs().get(i);
            Script scriptPubKey = input.getOutpoint().getConnectedOutput().getScriptPubKey();
            if (scriptPubKey.isSentToAddress()) {
                input.setScriptSig(ScriptBuilder.createInputScript(signatures[i], signingKeys[i]));
            } else if (scriptPubKey.isSentToRawPubKey()) {
                input.setScriptSig(ScriptBuilder.createInputScript(signatures[i]));
            } else {
                // Should be unreachable - if we don't recognize the type of script we're trying to sign for, we should
                // have failed above when fetching the key to sign with.
                throw new RuntimeException("Do not understand script type: " + scriptPubKey);
            }
        }

        // Every input is now complete.
    }
	
    byte[] getConnectedPubKeyScript(TransactionOutPoint top) {
        byte[] result = checkNotNull(top.getConnectedOutput().getScriptBytes());
        checkState(result.length > 0);
        return result;
    }

}
