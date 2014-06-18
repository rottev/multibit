package org.multibit.viewsystem.swing.view.panels;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;

import java.awt.ScrollPane;

import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;

























//import org.bitcoinj.wallet.Protos.Transaction;
import com.google.bitcoin.core.Transaction;

import org.multibit.controller.Controller;
import org.multibit.controller.bitcoin.BitcoinController;
import org.multibit.model.bitcoin.BitcoinModel;
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
import org.multibit.viewsystem.swing.view.dialogs.CreateAssetDialog;
import org.multibit.viewsystem.swing.view.dialogs.CreateOfferDialog;
import org.multibit.viewsystem.swing.view.dialogs.IssueAssetDialog;
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
import com.google.bitcoin.core.Wallet.SendRequest;
import com.google.bitcoin.core.Wallet.SendResult;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Component;
import java.math.BigDecimal;
import java.math.BigInteger;
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



public class IssueAssetPanel extends JPanel implements Viewable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2398664167122332450L;

	private Controller controller;
	private CreateNewColoredReceivingAddressAction createNewColoredReceivingAddressAction;
	protected MultiBitFrame mainFrame;
	private MultiBitButton issueAssetButton;
	private MultiBitButton cancelIssueAssetButton;
	private TradeingPanel tradePanel;
	private IssueAssetDialog dialog;
    private MultiBitLabel assetIssueToWalletLabel;
    private MultiBitLabel btcAmountLabel;
    private MultiBitLabel assetIssuerLabel;
    private MultiBitLabel assetDescriptionLabel;
    private MultiBitLabel isssueGenisisblockLabel;
    private MultiBitLabel isssueOutupIndexLabel;
    
    private JScrollPane sc;
   // private JTextPane  assetKeyLabel;
    private MultiBitLabel assetKeyInfoLabel;
    private JComboBox toWalletCB;
    
    private JTextField assetValueLabel = new JTextField();
    
    private JTextField assetKey = new JTextField();
    private JTextField btcValue = new JTextField();
    private JTextField genTx = new JTextField();
    private JTextField outIndex = new JTextField();
    private JFormattedTextField selectWalletLabele =  null;
    private JTextArea assetDescription =  null;
    private JFormattedTextField assetIssuer =  null;
    private JPanel cbPanel = new JPanel();
    private JCheckBox withTx = new JCheckBox();
    
    private static final byte[] EMPTY_BYTES = new byte[32];


	public IssueAssetPanel(BitcoinController bitcoinController,
			final TradeingPanel tradePanel, IssueAssetDialog issueAssetDialog) {
		super();
		// TODO Auto-generated constructor stub
	//	this.mainFrame = mainFrame;
		this.controller = bitcoinController;
		this.tradePanel = tradePanel;
		this.dialog = issueAssetDialog;
		
		NumberFormat f = NumberFormat.getNumberInstance();
		f.setMaximumFractionDigits(10);
		f.setMaximumIntegerDigits(10);
		selectWalletLabele =  new JFormattedTextField();
		assetDescription = new JTextArea(5, 20);
		//assetDescription.setBorder(selectWalletLabele.getBorder());
		assetDescription.setLineWrap(true);
		sc = new JScrollPane(assetDescription);
	    assetIssuer = new JFormattedTextField();
	    assetValueLabel.setEditable(false);
	    assetValueLabel.setBackground(null);
	    assetValueLabel.setBorder(null);
		
	    cbPanel.setLayout(new GridLayout(1,1));
	  //  assetName.
	///	asssetSymbol = new JFormattedTextField();
	 //   assetMultip = new JFormattedTextField(f);
		
		GridBagLayout gridLayout = new GridBagLayout();

		 GridBagConstraints c = new GridBagConstraints();
		 c.insets = new Insets(5, 10, 5, 10);
		 c.fill = GridBagConstraints.HORIZONTAL;
		// Components
		    c.gridwidth = 1;
		    c.weightx = 1;
		    c.weighty = 1;
		    c.gridx = 0;
		    c.gridy = 0;
		    c.anchor = GridBagConstraints.NORTHWEST;
		    
		
		this.setLayout(gridLayout);
		JPanel panel = new JPanel();
		panel.setLayout(gridLayout);
		
        CancelBackToParentAction cancelAction = new CancelBackToParentAction(controller,
                ImageLoader.createImageIcon(ImageLoader.CROSS_ICON_FILE), issueAssetDialog);
        cancelIssueAssetButton = new MultiBitButton(cancelAction, controller);
        
        OkAction okAction = new OkAction(controller, ImageLoader.createImageIcon(ImageLoader.ACCEPT_ICON_FILE), issueAssetDialog);
        issueAssetButton = new MultiBitButton(okAction,  controller);
      //  createAssetButton.setEnabled(false);
      

        
		GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0.7;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(2,0,2,0);
		
		
		
		
       
        // recvie wallet area
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.weightx = 0.1;
        assetIssueToWalletLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "IssueAssetPanel.selectWalletLabel"));
        
        panel.add(assetIssueToWalletLabel, constraints);
        
      
        
         constraints.gridx = 1;
        constraints.fill = GridBagConstraints.NONE;
       // constraints.anchor = GridBagConstraints.EAST;
        constraints.gridwidth = 2;
        List<WalletData> wListData = ((BitcoinController) controller).getModel().getPerWalletModelDataList();
        toWalletCB = new JComboBox();
        toWalletCB.addItem("Please select a wallet");
        for(WalletData singleWalletData : wListData)
          toWalletCB.addItem(singleWalletData.getWalletFilename());
        
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 0;
       // constraints.anchor = GridBagConstraints.EAST;
        constraints.gridwidth = 1;
        
        cbPanel.add(toWalletCB, constraints);
        constraints.gridx = 1;
        constraints.gridy = 7;
        constraints.fill = GridBagConstraints.NONE;
       // constraints.anchor = GridBagConstraints.EAST;
        constraints.gridwidth = 2;
        panel.add(cbPanel, constraints);
        
        
        assetIssueToWalletLabel.setVisible(false);
        toWalletCB.setVisible(false);
       // cbPanel.setBackground(Color.RED);;
        

        //isssueOutupIndexLabel;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.weightx = 0.1;
        isssueGenisisblockLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "IssueAssetPanel.genBlock"));
        
        panel.add(isssueGenisisblockLabel, constraints);
        
      
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 2;
       
        
        panel.add(genTx, constraints);
        //
        
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.weightx = 0.1;
        constraints.gridwidth = 1;
        isssueOutupIndexLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "IssueAssetPanel.outputIndex"));
        
        panel.add(isssueOutupIndexLabel, constraints);
        
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.5;
        constraints.gridwidth = 2;
        outIndex.setColumns(3);
        panel.add(outIndex, constraints);
        
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.1;

        btcAmountLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "IssueAssetPanel.assetBtcAmount"));
        
        
        btcValue.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				setText();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				setText();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void setText() {
				if(btcValue.getText().length() > 0 ) {
					Double val = Double.parseDouble(btcValue.getText()) * tradePanel.getSelectedAsset().satoshi_multiplyier;
					assetValueLabel.setText(val + tradePanel.getSelectedAsset().symbol);
				}
				else
					assetValueLabel.setText("");
					
			}
		});
        
        btcValue.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				handleKey(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				handleKey(e);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
				handleKey(e);
			}
			
			public void handleKey(KeyEvent e)
			{
				  char c = e.getKeyChar(); 
				    if (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {  
				      if (!(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' ||  
				            c == '5' || c == '6' || c == '7' || c == '8' || c == '9')) {  
				    	  if ((c == '.')) { 
				    		if (btcValue.getText().indexOf(".") >= 0) {
				    			e.consume(); 
				    			return; 
				    		}
				    	  }
				    	  else { 
				    		e.consume(); 
				    	 } 
				      }  
				    }  
			}
		});
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        panel.add(btcAmountLabel, constraints);
        
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        panel.add(btcValue, constraints);  
        constraints.gridx = 2;
        constraints.weightx = 0.4;
        panel.add(assetValueLabel, constraints);

        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        
        assetIssuerLabel = new MultiBitLabel(controller.getLocaliser().getString(
                "IssueAssetPanel.issuerName"));
        panel.add(assetIssuerLabel, constraints);

    
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        panel.add(assetIssuer, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0.1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        
        assetDescriptionLabel = new MultiBitLabel(controller.getLocaliser().getString(
                "IssueAssetPanel.issueDescription"));
        panel.add(assetDescriptionLabel, constraints);
        
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(sc, constraints);
        
        
        constraints.gridx = 0;
        constraints.gridy = 3;
       // constraints.fill = GridBagConstraints.BOTH;
       // constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 0.1;
        assetKeyInfoLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "IssueAssetPanel.assetKey")); 
        panel.add(assetKeyInfoLabel, constraints);
        
        
        constraints.gridx = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 0.5;
        panel.add(assetKey, constraints);
/*
        assetKeyLabel  = new JTextPane();
        assetKeyLabel.setVisible(false);
        assetKeyLabel.setEditable(false); 
        assetKeyLabel.setBackground(null); 
        assetKeyLabel.setBorder(null);
        
        panel.add(assetKeyLabel, constraints);
  */    
       
        constraints.gridx = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 0.5;
        constraints.gridy = 4;
        withTx.setText(controller.getLocaliser().getString("IssueAssetPanel.createTransaction"));
        panel.add(withTx, constraints);
        withTx.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	if(withTx.isSelected())
            	{
                    genTx.setVisible(false);
                    outIndex.setVisible(false);
                    isssueGenisisblockLabel.setVisible(false);
                    isssueOutupIndexLabel.setVisible(false);
                    assetIssueToWalletLabel.setVisible(true);
                    toWalletCB.setVisible(true);
            	}
            	else
            	{
            	     assetIssueToWalletLabel.setVisible(false);
                     toWalletCB.setVisible(false);
                     genTx.setVisible(true);
                     outIndex.setVisible(true);
                     isssueGenisisblockLabel.setVisible(true);
                     isssueOutupIndexLabel.setVisible(true);
            	}
            }
        });
        
        
        // add buttons
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
		 
        constraints.fill = GridBagConstraints.NONE;
   
        constraints.gridwidth = 1;
        constraints.weightx = .01;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 8;
        constraints.anchor = GridBagConstraints.SOUTHWEST;
	    
        constraints.gridx = 1;
        buttonsPanel.add(issueAssetButton, constraints);
        constraints.gridx = 2;
        buttonsPanel.add(cancelIssueAssetButton, constraints);
        constraints.gridx = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.NONE;
        this.add(buttonsPanel, constraints);
	    
        issueAssetButton.addActionListener(new ActionListener() {
			
			private BaseTrading bt = BaseTrading.getInstance();
			boolean closeWindow = false;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//validate selection
				if (closeWindow)
					((OkAction)(issueAssetButton.getAction())).close();
				try
				{
					// create actual transaction
					if(withTx.isSelected()){
						WalletData w = ((BitcoinController) controller).getModel().getActivePerWalletModelData();
						WalletData to = w;
						if(toWalletCB.getSelectedIndex() != 0)
							to = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename((String)toWalletCB.getSelectedItem());
						BigInteger value =  new BigDecimal(btcValue.getText()).multiply(new BigDecimal(Utils.COIN)).toBigInteger();
						SendRequest sr = SendRequest.to(to.getWallet().getChangeAddress(),value);
						sr.ensureMinRequiredFee = true;
						sr.fee = BigInteger.ZERO;
						sr.feePerKb = BitcoinModel.SEND_FEE_PER_KB_DEFAULT;


		                boolean completedOk = ((BitcoinController) controller).getModel().getActiveWallet().completeTx(sr, false);
						Transaction genTx = ((BitcoinController) controller).getMultiBitService().sendCoins(w, sr, null);
						//SendResult res = w.sendCoins(sr);
						// TODO: we might want to set the label in the walletinfo class of multibit for this issuance attempt
						int outIndex = 0;
						for(int i = 0; i < genTx.getOutputs().size(); i++){
							if(genTx.getOutput(i).getValue() == value) {
								outIndex = i;
								break;
							}
						}
						if(bt.createIssueanceForUntrustedAsset(assetKey.getText(), tradePanel.getSelectedAsset(), genTx.getHashAsString(), outIndex, assetIssuer.getText(), assetDescription.getText()))
						{
							tradePanel.refreshIssueance();
							String colors = to.getWalletInfo().getProperty(ColorProperty.propertyName);
							if(colors != null){
								if(!colors.contains(tradePanel.getSelectedAsset().symbol)){
									colors += "#" + tradePanel.getSelectedAsset().symbol;
								}
							}
							else {
								colors = tradePanel.getSelectedAsset().symbol; 
							}
							
							to.getWalletInfo().put(ColorProperty.propertyName, colors);
							
							((OkAction)(issueAssetButton.getAction())).close();
						}
					}
					else
					{
						if(bt.createIssueanceForUntrustedAsset(assetKey.getText(), tradePanel.getSelectedAsset(), genTx.getText(), Integer.parseInt(outIndex.getText()), assetIssuer.getText(), assetDescription.getText()))
						{
							tradePanel.refreshIssueance();
							((OkAction)(issueAssetButton.getAction())).close();
						}
					}
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				
			}
		});
        
		add(panel, c);
		
		
	}

	
	
	

	private void maybeEnableOK()
	{
		boolean enable = false;
		issueAssetButton.setEnabled(enable);
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
	

	public Component getCancelButton() {
		// TODO Auto-generated method stub
		return cancelIssueAssetButton;
	}
	


}
