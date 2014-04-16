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

import org.multibit.controller.Controller;
import org.multibit.controller.bitcoin.BitcoinController;
import org.multibit.utils.ImageLoader;
import org.multibit.viewsystem.DisplayHint;
import org.multibit.viewsystem.View;
import org.multibit.viewsystem.Viewable;
import org.multibit.viewsystem.swing.MultiBitFrame;
import org.multibit.viewsystem.swing.action.CreateNewColoredReceivingAddressAction;
import org.multibit.viewsystem.swing.view.components.MultiBitButton;

import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTradingModel;

import java.awt.ComponentOrientation;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;



public class TradeingPanel extends JPanel implements Viewable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2398664167122332450L;
	private final JTable table = new JTable();
	private final JTable oferingsTable = new JTable();
	private Controller controller;
	private final Button button = new Button("New button");
	protected Action createNewColoredAddressAction;
	private CreateNewColoredReceivingAddressAction createNewColoredReceivingAddressAction;
	protected MultiBitFrame mainFrame;
	private MultiBitButton createNewButton;
	
	
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
		table.setModel(new BaseTradingModel());		
		scrollPane_1.setViewportView(table);
		
		//set table selection
		 ListSelectionModel listSelectionModel = table.getSelectionModel();
		 listSelectionModel.addListSelectionListener(new ListSelectionListener() {										
			@Override
			public void valueChanged(ListSelectionEvent e) {
				table.getSelectedRow();
				
			}
		 });
		    
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
	
       createNewColoredAddressAction = getCreateNewColoredAddressAction();
       createNewButton = new MultiBitButton(createNewColoredAddressAction, controller);
       createNewButton.setText(controller.getLocaliser().getString("crudButton.new"));
       c2.fill = GridBagConstraints.NONE;
       
	     c2.gridwidth = 1;
	     c2.weightx = .01;
	     c2.weighty = .2;
	     c2.gridx = 0;
	     c2.gridy = 2;
	     c2.anchor = GridBagConstraints.WEST;
		add(createNewButton,   c2);

		
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

	public Object getLabelTextArea() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Asset getSelectedAsset()
	{
		int selectedrow = table.getSelectedRow();
		if(selectedrow >= 0)
			return ((BaseTradingModel) table.getModel()).getAssetAtRow(selectedrow);
		return null;
	}

}
