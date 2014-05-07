package org.multibit.viewsystem.swing.view.panels;

import java.awt.BorderLayout;
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

import org.apache.commons.lang3.StringUtils;
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
import org.multibit.viewsystem.swing.action.CreateNewColoredReceivingAddressAction;
import org.multibit.viewsystem.swing.action.CreateNewOfferAction;
import org.multibit.viewsystem.swing.view.components.MultiBitButton;

import com.google.bitcoin.core.Wallet;

import etx.com.trading.BaseTradesModel;
import etx.com.trading.BaseTrading;
import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTrading.Issuance;
import etx.com.trading.BaseAssetModel;
import etx.com.trading.ColorProperty;

import java.awt.ComponentOrientation;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



public class TradeingPanel extends JPanel implements Viewable {
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
	private Action createNewOfferAction;
	protected MultiBitFrame mainFrame;
	private MultiBitButton createNewButton;
	private MultiBitButton createOfferButton;
	private MultiBitButton acceptOfferButton  = new MultiBitButton("Accept Offer");
	
	
	public TradeingPanel(BitcoinController bitcoinController,
			MultiBitFrame mainFrame) {
		
		this.mainFrame = mainFrame;
		this.controller = bitcoinController;
		
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
				
			}
		 });
		    
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
       createNewColoredAddressAction = getCreateNewColoredAddressAction();
       createNewButton = new MultiBitButton(createNewColoredAddressAction, controller);
       createNewButton.setText(controller.getLocaliser().getString("crudButton.new"));
       
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
       
       	c2.fill = GridBagConstraints.NONE;
  
       
	     c2.gridwidth = 1;
	     c2.weightx = .01;
	     c2.weighty = .2;
	     c2.gridx = 0;
	     c2.gridy = 1;
	     c2.anchor = GridBagConstraints.WEST;
	     buttonsPanel.add(createNewButton,   c2);
	     
	     buttonAddColor.setEnabled(false);
	     buttonAddColor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Asset a = ((BaseAssetModel) tableAssets.getModel()).getAssetAtRow( tableAssets.getSelectedRow());
					if (((BitcoinController) controller).getModel().getActiveWallet() != null) {
						
						WalletInfoData info = ((BitcoinController) controller).getModel().getActiveWalletWalletInfo();
						String colors = info.getProperty(ColorProperty.propertyName);
						if(colors != null){
							if(!colors.contains(a.id)){
								colors += "#" + a.id;
							}
							else
								return;
							
						}
						else {
							colors = a.id; 
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
	     
	     c2.gridx = 1;
		
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
	
	 protected Action getCreateNewColoredAddressAction() {
	        createNewColoredReceivingAddressAction = new CreateNewColoredReceivingAddressAction(((BitcoinController)(controller)), mainFrame, this);
	        return createNewColoredReceivingAddressAction;
		 //return null;
	    }
	 
	 protected Action getCreateNewOfferActoin()
	 {
		 createNewOfferAction = new CreateNewOfferAction(((BitcoinController)(controller)), mainFrame, this);
		 return createNewOfferAction;
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

}
