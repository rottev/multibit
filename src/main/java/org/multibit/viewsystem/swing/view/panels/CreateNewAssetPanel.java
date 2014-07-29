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
import javax.swing.JTextPane;
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
import org.multibit.viewsystem.swing.view.dialogs.CreateAssetDialog;
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



public class CreateNewAssetPanel extends JPanel implements Viewable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2398664167122332450L;

	private Controller controller;
	private CreateNewColoredReceivingAddressAction createNewColoredReceivingAddressAction;
	protected MultiBitFrame mainFrame;
	private MultiBitButton createAssetButton;
	private MultiBitButton cancelCreateAssetButton;
	private TradeingPanel tradePanel;
	private CreateAssetDialog dialog;
    private MultiBitLabel assetNameLabel;
    private MultiBitLabel assetSymbolLabel;
    private MultiBitLabel assetMultipLabel;
    private JTextPane  assetKeyLabel;
    private MultiBitLabel assetKeyInfoLabel;
    private MultiBitLabel assetCreatorNameLabel;
    
    private JFormattedTextField assetName =  null;
    private JFormattedTextField assetSymbol =  null;
    private JFormattedTextField assetMultip =  null;
    private JFormattedTextField creatorName =  null;
    
    private static final byte[] EMPTY_BYTES = new byte[32];


	public CreateNewAssetPanel(BitcoinController bitcoinController,
			final TradeingPanel tradePanel, CreateAssetDialog newAssetDialog) {
		super();
		// TODO Auto-generated constructor stub
	//	this.mainFrame = mainFrame;
		this.controller = bitcoinController;
		this.tradePanel = tradePanel;
		this.dialog = newAssetDialog;
		
		NumberFormat f = NumberFormat.getNumberInstance();
		f.setMaximumFractionDigits(10);
		f.setMaximumIntegerDigits(10);
		assetName =  new JFormattedTextField();
		assetSymbol = new JFormattedTextField();
		creatorName = new JFormattedTextField();
	    assetMultip = new JFormattedTextField(f);
		
	  //  assetName.
	///	asssetSymbol = new JFormattedTextField();
	 //   assetMultip = new JFormattedTextField(f);
		
		GridBagLayout gridLayout = new GridBagLayout();
		 GridBagConstraints c = new GridBagConstraints();
		 c.insets = new Insets(5, 10, 5, 10);
		 c.fill = GridBagConstraints.BOTH;
		// Components
		    c.gridwidth = 1;
		    c.weightx = 1;
		    c.weighty = 1;
		    c.gridx = 0;
		    c.gridy = 0;
		
		this.setLayout(gridLayout);
		JPanel panel = new JPanel();
		panel.setLayout(gridLayout);
		
        CancelBackToParentAction cancelAction = new CancelBackToParentAction(controller,
                ImageLoader.createImageIcon(ImageLoader.CROSS_ICON_FILE), newAssetDialog);
        cancelCreateAssetButton = new MultiBitButton(cancelAction, controller);
        
        OkAction okAction = new OkAction(controller, ImageLoader.createImageIcon(ImageLoader.ACCEPT_ICON_FILE), newAssetDialog);
        createAssetButton = new MultiBitButton(okAction,  controller);
      //  createAssetButton.setEnabled(false);
      

        
		GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0.3;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
		
		
		
		
       
        // recvie wallet area
        constraints.gridx = 0;
        constraints.gridy = 0;
        assetNameLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "CreateAssetPanel.assetName"));
        
        panel.add(assetNameLabel, constraints);
        
      
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.7;
        panel.add(assetName, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;

        assetSymbolLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "CreateAssetPanel.assetSymbol"));
        
        panel.add(assetSymbolLabel, constraints);

        
    
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.7;
        panel.add(assetSymbol, constraints);
        
        
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;

        assetMultipLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "CreateAssetPanel.assetMultiplyer"));
        
        panel.add(assetMultipLabel, constraints);

        
    
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.7;
        panel.add(assetMultip, constraints);
        
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        assetCreatorNameLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "CreateAssetPanel.creatorName"));
        panel.add(assetCreatorNameLabel, constraints);
        
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.7;

        panel.add(creatorName, constraints);
        
        //
        
        
        constraints.gridx = 0;
        constraints.gridy = 4;
       // constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 1;

        assetKeyLabel  = new JTextPane();
        assetKeyLabel.setVisible(false);
        assetKeyLabel.setEditable(false); 
        assetKeyLabel.setBackground(null); 
        assetKeyLabel.setBorder(null);
        
        panel.add(assetKeyLabel, constraints);
      
        
        
        
        
        // add buttons
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
		 
        constraints.fill = GridBagConstraints.NONE;
   
        constraints.gridwidth = 1;
        constraints.weightx = .01;
        constraints.weighty = .2;
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.WEST;
	    
        constraints.gridx = 1;
        buttonsPanel.add(createAssetButton, constraints);
        constraints.gridx = 2;
        buttonsPanel.add(cancelCreateAssetButton, constraints);
        constraints.gridx = 0;
        panel.add(buttonsPanel, constraints);
	    
        createAssetButton.addActionListener(new ActionListener() {
			
			private BaseTrading bt = BaseTrading.getInstance(((BitcoinController) controller).getModel().isTestnet());
			boolean closeWindow = false;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//validate selection
				if (closeWindow)
					((OkAction)(createAssetButton.getAction())).close();
				try
				{
					final String key = Sha256Hash.create(assetName.getText().getBytes()).toString();
					String assetKey = bt.createNewUntrustedAsset(new Asset(){{ name= assetName.getText(); symbol = assetSymbol.getText(); satoshi_multiplyier = Double.parseDouble(assetMultip.getText()); id= key; creator= creatorName.getText(); }});
					if(assetKey != null) {
						assetKeyLabel.setText(controller.getLocaliser().getString(
				                "CreateAssetPanel.key") +  assetKey);
						assetKeyLabel.setVisible(true);
						cancelCreateAssetButton.setVisible(false);
						createAssetButton.setText(controller.getLocaliser().getString("CreateAssetPanel.close"));
						tradePanel.refreshAssets();
						closeWindow = true;
						
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
		createAssetButton.setEnabled(enable);
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
		return cancelCreateAssetButton;
	}
	


}
