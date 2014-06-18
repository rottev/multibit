package org.multibit.viewsystem.swing.view.panels;

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
import org.bitcoinj.wallet.Protos.Transaction;
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
import org.multibit.viewsystem.swing.view.dialogs.CreateOfferDialog;

import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Wallet;

import etx.com.trading.BaseTradesModel;
import etx.com.trading.BaseTrading;
import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTrading.ColorGenisis;
import etx.com.trading.BaseTrading.Issuance;
import etx.com.trading.BaseAssetModel;
import etx.com.trading.ColorProperty;

import java.awt.ComponentOrientation;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;



public class CreateNewOfferPanel extends JPanel implements Viewable {
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
	private CreateOfferDialog dialog;
    private MultiBitLabel selectAssetLabel;
    private MultiBitLabel selectRecivingWalletLabel;
    private MultiBitLabel sendQuantityLabel;
    private MultiBitLabel recviveAssetLabel;
    private MultiBitLabel quantityLabel = new MultiBitLabel("");
    
    private JFormattedTextField sendQuantity =  null;
    private JFormattedTextField reciveQuantity = null;
    
    private JComboBox selectAssetCB;
    private JComboBox selectWalletCB;
    private JComboBox reciveAssetCB;
	
	
	

	public CreateNewOfferPanel(BitcoinController bitcoinController,
			TradeingPanel tradePanel, CreateOfferDialog createOfferDialog) {
		super();
		// TODO Auto-generated constructor stub
	//	this.mainFrame = mainFrame;
		this.controller = bitcoinController;
		this.panel = tradePanel;
		this.dialog = createOfferDialog;
		NumberFormat f = NumberFormat.getNumberInstance();
		f.setMaximumFractionDigits(10);
		f.setMaximumIntegerDigits(10);
	    sendQuantity =  new JFormattedTextField(f);
	    reciveQuantity = new JFormattedTextField(f);
		
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
                ImageLoader.createImageIcon(ImageLoader.CROSS_ICON_FILE), createOfferDialog);
        cancelOfferButton = new MultiBitButton(cancelAction, controller);
        
        OkAction okAction = new OkAction(controller, ImageLoader.createImageIcon(ImageLoader.ACCEPT_ICON_FILE), createOfferDialog);
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
		
		selectAssetLabel = new MultiBitLabel(controller.getLocaliser().getString(
                "createNewOfferPanel.selectAsset"));
		
		panel.add(selectAssetLabel, constraints);
		
		String activeWallet = ((BitcoinController) controller).getModel().getActiveWalletFilename();
		final WalletData wdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(activeWallet);
		String[] colors = ColorProperty.deserlizie(wdata.getWalletInfo().getProperty(ColorProperty.propertyName));
		
        selectAssetCB = new JComboBox();
    	selectAssetCB.addItem("Please Select a color");
    	selectAssetCB.addItem("BTC");
        if(colors != null)
        {

        	for(String color : colors)
        		selectAssetCB.addItem(color);
        }
     //   else
     //   {
     //   	selectAssetCB.addItem(controller.getLocaliser().getString("createNewOfferPanel.noAssetInWallet"));
     //   }
        
        selectAssetCB.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendQuantity.setEnabled(selectAssetCB.getSelectedIndex() != 0);
				if(selectAssetCB.getSelectedIndex() != 0)
				{
					quantityLabel.setText((String)selectAssetCB.getItemAt(selectAssetCB.getSelectedIndex()));
				}
				else
					quantityLabel.setText("");
				
			}
		});

        constraints.gridx = 1;
        panel.add(selectAssetCB, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        sendQuantityLabel = new MultiBitLabel(controller.getLocaliser().getString(
                "createNewOfferPanel.sendQuantity"));
        
        panel.add(sendQuantityLabel, constraints);
        constraints.gridx = 1;
        
        JPanel quantityPanel = new JPanel(new GridBagLayout());
        
        sendQuantity.setEnabled(false);
        sendQuantity.setColumns(10);
        c2.gridx = 0;
        quantityPanel.add(sendQuantity, c2);
        c2.gridx = 1;
        quantityPanel.add(quantityLabel, c2);
        panel.add(quantityPanel, constraints);
        
        
        constraints.gridx = 0;
        constraints.gridy = 2;
        selectRecivingWalletLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "createNewOfferPanel.selectReciveWallet"));
        
        panel.add(selectRecivingWalletLabel, constraints);
        
        List<WalletData> wListData = ((BitcoinController) controller).getModel().getPerWalletModelDataList();
        selectWalletCB = new JComboBox();
        selectWalletCB.addItem("Please select a wallet");
        for(WalletData singleWalletData : wListData)
        	selectWalletCB.addItem(singleWalletData.getWalletFilename());
        
        selectWalletCB.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				createOfferButton.setEnabled(selectWalletCB.getSelectedIndex() != 0);
				if(selectWalletCB.getSelectedIndex() != 0)
				{
					WalletData selectedwdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(
							(String)selectWalletCB.getItemAt(selectWalletCB.getSelectedIndex()));
					String[] sectedcolors = ColorProperty.deserlizie(selectedwdata.getWalletInfo().getProperty(ColorProperty.propertyName));
					reciveAssetCB.setEnabled(true);
					reciveAssetCB.removeAllItems();
					reciveAssetCB.addItem("BTC");
					if(sectedcolors != null){
						for(String item : sectedcolors)
							reciveAssetCB.addItem(item);
					}
				}
				
			}
		});
    
        constraints.gridx = 1;
        panel.add(selectWalletCB, constraints);
        
        
        recviveAssetLabel  = new MultiBitLabel(controller.getLocaliser().getString(
                "createNewOfferPanel.selectReciveAsset"));
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(recviveAssetLabel, constraints);
        
        reciveAssetCB = new JComboBox();
        reciveAssetCB.addItem("BTC");
        
        reciveAssetCB.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(reciveAssetCB.getSelectedIndex() != 0)
				{
					
				}
				
			}
		});
        
        JPanel quantityPanel2 = new JPanel(new GridBagLayout());
        constraints.gridx = 1;
        quantityPanel2.add(reciveAssetCB, constraints);
        
        constraints.gridx = 2;
        quantityPanel2.add(new MultiBitLabel(controller.getLocaliser().getString(
                "createNewOfferPanel.Quantity")),   constraints);

        constraints.gridx = 3;
        
        reciveQuantity.setColumns(10);
        constraints.gridx = 4;
        quantityPanel2.add(reciveQuantity,   constraints);
        
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(quantityPanel2, constraints);        
        constraints.fill = GridBagConstraints.NONE;
        
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
				//validate offer
				
				//create offer
				//find what we need for the give
				double available = 0;
				NumberFormat nf = NumberFormat.getInstance();
				Set<com.google.bitcoin.core.Transaction> trans = wdata.getWallet().getTransactions(false);
				for (Iterator<com.google.bitcoin.core.Transaction> it = trans.iterator(); it.hasNext(); ) {
					com.google.bitcoin.core.Transaction t = it.next();
					if(t.isMine( wdata.getWallet()) && !t.isEveryOwnedOutputSpent(wdata.getWallet())) {
						List<TransactionOutput> outputs = t.getOutputs();
						for(int i = 0; i < outputs.size(); i++)
						{
							if(outputs.get(i).isAvailableForSpending() && outputs.get(i).isMine(wdata.getWallet())) {
								ColorGenisis cg = bt.GetColorTransactionSearchHistory(wdata.getWallet(), t.getHashAsString(), i);
				    			if( cg != null) {
				    				// we can spend this!!!!!!!
				    				System.out.println("We can spend this!!!!!!");
				    				Asset ast = bt.getAssetForTransaction(cg.txout, cg.index);
				    				// are we talking about the asset we want to trade?
				    				if(ast.symbol.equals(selectAssetCB.getItemAt(selectAssetCB.getSelectedIndex()))) {
				    					available += outputs.get(i).getValue().doubleValue() * ast.satoshi_multiplyier;
				    					// do we have enough
				    					
				    					try {
											if(available >= nf.parse(sendQuantity.getText()).doubleValue()){
												String address;
												WalletData selectedwdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(
														(String)selectWalletCB.getItemAt(selectWalletCB.getSelectedIndex()));
												address = selectedwdata.getWallet().getChangeAddress().toString();

												bt.addOffer(bt.GetColorScheme(), 
														ast.id, 
														nf.parse(sendQuantity.getText()).doubleValue(),
														new String[] { t.getHashAsString(), "" + i } ,
														nf.parse(reciveQuantity.getText()).doubleValue(),
														address,
														bt.getAssetBySymbol((String)reciveAssetCB.getItemAt(reciveAssetCB.getSelectedIndex())).id);
												
												((OkAction)(createOfferButton.getAction())).close();
												return;
												
											}
										} catch (NumberFormatException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
				    				}
				    				//TODO ISSUE: make sure wallet also has enough satoshi to pay the fee for this transaction.
				    				
				    			}
				    			else {
				    				if(selectAssetCB.getItemAt(selectAssetCB.getSelectedIndex()).equals("BTC")) {
				    					available += outputs.get(i).getValue().doubleValue();
				    					// do we have enough
				    					try {
											if(available >= nf.parse(sendQuantity.getText()).doubleValue()){
												String address;
												WalletData selectedwdata = ((BitcoinController) controller).getModel().getPerWalletModelDataByWalletFilename(
														(String)selectWalletCB.getItemAt(selectWalletCB.getSelectedIndex()));
												address = selectedwdata.getWallet().getChangeAddress().toString();

												bt.addOffer(bt.GetColorScheme(), 
														"BTC", 
														nf.parse(sendQuantity.getText()).doubleValue(),
														new String[] { t.getHashAsString(), "" + i } ,
														nf.parse(reciveQuantity.getText()).doubleValue(),
														address,
														(String)reciveAssetCB.getItemAt(reciveAssetCB.getSelectedIndex()));
												
												((OkAction)(createOfferButton.getAction())).close();
												return;
											}
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
				    				}
				    			}
				    					
							}
						}
					}
				}
				//if we got here it might be because we dont have enough unspent of the asset we wanted to give in the selected wallet.
				
				//selectWalletCB.getItemAt(selectWalletCB.getSelectedIndex());
				
				//((OkAction)(createOfferButton.getAction())).close();
				
			}
		});
        
		add(panel, c);
		
		
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

}
