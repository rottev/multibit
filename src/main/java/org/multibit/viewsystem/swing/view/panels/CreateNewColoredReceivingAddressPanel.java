package org.multibit.viewsystem.swing.view.panels;

import org.bitcoinj.wallet.Protos.Wallet.EncryptionType;
import org.multibit.controller.Controller;
import org.multibit.controller.bitcoin.BitcoinController;
import org.multibit.utils.ImageLoader;
import org.multibit.viewsystem.swing.action.CancelBackToParentAction;
import org.multibit.viewsystem.swing.action.CreateNewColoredReceivingAddressSubmitAction;
import org.multibit.viewsystem.swing.action.CreateNewReceivingAddressSubmitAction;
import org.multibit.viewsystem.swing.view.components.MultiBitButton;
import org.multibit.viewsystem.swing.view.components.MultiBitLabel;
import org.multibit.viewsystem.swing.view.components.MultiBitTitledPanel;
import org.multibit.viewsystem.swing.view.dialogs.CreateNewColoredReceivingAddressDialog;
import org.multibit.viewsystem.swing.view.dialogs.CreateNewReceivingAddressDialog;

import etx.com.trading.BaseTrading.Asset;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * JPanel for creating new receiving addresses.
 * (The JPanel is separated out of the JDialog to enable running tests headless on a server).
 * 
 * @author jim
 *
 */
public class CreateNewColoredReceivingAddressPanel extends JPanel {
    private static final long serialVersionUID = -1604698555807842464L;
    
    private final Controller controller;
    private final BitcoinController bitcoinController;
 
    private TradeingPanel tradeBitcoinPanel;
    private CreateNewColoredReceivingAddressDialog createNewColoredReceivingAddressDialog;

    private MultiBitLabel messageText;

    private CreateNewColoredReceivingAddressSubmitAction createNewColoredReceivingAddressSubmitAction;
    private MultiBitButton createNewReceivingAddressSubmitButton;
    private MultiBitButton cancelButton;
    
    private JPasswordField walletPasswordField;
    private MultiBitLabel walletPasswordPromptLabel;
    
    private JComboBox numberOfAddresses;
    
    private static final int STENT_WIDTH = 10;
    private Asset asset = null;
   
    public CreateNewColoredReceivingAddressPanel(BitcoinController bitcoinController, TradeingPanel tradePanel,
            CreateNewColoredReceivingAddressDialog createNewColoredReceivingAddressDialog) {
        super();

        this.bitcoinController = bitcoinController;
        this.controller = this.bitcoinController;
        
        this.tradeBitcoinPanel = tradePanel;
        this.createNewColoredReceivingAddressDialog = createNewColoredReceivingAddressDialog;
        this.asset = tradePanel.getSelectedAsset();

        setOpaque(false);
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.3;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(MultiBitTitledPanel.createStent(STENT_WIDTH), constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 7;
        constraints.gridy = 1;
        constraints.weightx = 0.3;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(MultiBitTitledPanel.createStent(STENT_WIDTH), constraints);

        MultiBitLabel explainLabel = new MultiBitLabel("");
        explainLabel.setText(controller.getLocaliser().getString("createNewColoredReceivingAddressDialog.message", new Object[] {asset.name}));
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.weightx = 0.8;
        constraints.weighty = 1.0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(explainLabel, constraints);

        
        numberOfAddresses = new JComboBox();
        numberOfAddresses.addItem(1);
        numberOfAddresses.addItem(5);
        numberOfAddresses.addItem(20);
        numberOfAddresses.addItem(100);
        //numberOfAddresses.addItem(new Integer(500));

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 5;
        constraints.gridy = 1;
        constraints.weightx = 0.3;
        constraints.weighty = 0.3;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        
        //mainPanel.add(numberOfAddresses, constraints);

        // Add wallet password field.
        walletPasswordPromptLabel = new MultiBitLabel(controller.getLocaliser().getString(
                "showExportPrivateKeysPanel.walletPasswordPrompt"));
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 3;
        constraints.gridy = 8;
        constraints.weightx = 0.3;
        constraints.weighty = 0.1;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(walletPasswordPromptLabel, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 4;
        constraints.gridy = 7;
        constraints.weightx = 0.05;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        mainPanel.add(MultiBitTitledPanel.createStent(MultiBitTitledPanel.SEPARATION_BETWEEN_NAME_VALUE_PAIRS), constraints);

        JPanel filler4 = new JPanel();
        filler4.setOpaque(false);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 3;
        constraints.gridy = 7;
        constraints.weightx = 0.3;
        constraints.weighty = 0.01;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(filler4, constraints);

        walletPasswordField = new JPasswordField(24);
        walletPasswordField.setMinimumSize(new Dimension(200, 20));
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 5;
        constraints.gridy = 8;
        constraints.weightx = 0.3;
        constraints.weighty = 0.1;
        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(walletPasswordField, constraints);

        JPanel filler5 = new JPanel();
        filler5.setOpaque(false);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 3;
        constraints.gridy = 9;
        constraints.weightx = 0.3;
        constraints.weighty = 0.01;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(filler5, constraints);

        if (this.bitcoinController.getModel().getActiveWallet() != null) {
            if (this.bitcoinController.getModel().getActiveWallet().getEncryptionType() == EncryptionType.ENCRYPTED_SCRYPT_AES) {
                // Need wallet password.
                walletPasswordField.setEnabled(true);
                walletPasswordPromptLabel.setEnabled(true);
            } else {
                // No wallet password required.
                walletPasswordField.setEnabled(false);
                walletPasswordPromptLabel.setEnabled(false);
            }
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 3;
        constraints.gridy = 10;
        constraints.weightx = 0.8;
        constraints.weighty = 0.1;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(buttonPanel, constraints);

        CancelBackToParentAction cancelAction = new CancelBackToParentAction(controller,
                ImageLoader.createImageIcon(ImageLoader.CROSS_ICON_FILE), createNewColoredReceivingAddressDialog);
        cancelButton = new MultiBitButton(cancelAction, controller);
        cancelButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Clear password.
                if (walletPasswordField != null) {
                    walletPasswordField.setText("");
                }
            }});
 
        if (createNewColoredReceivingAddressDialog != null) {
        	createNewColoredReceivingAddressDialog.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosed(WindowEvent arg0) {
                    if (walletPasswordField != null) {
                        walletPasswordField.setText("");
                    }
                }
            });
        }
        buttonPanel.add(cancelButton);

        createNewColoredReceivingAddressSubmitAction = new CreateNewColoredReceivingAddressSubmitAction(this.bitcoinController,
                createNewColoredReceivingAddressDialog, this, walletPasswordField, asset.name);
        createNewReceivingAddressSubmitButton = new MultiBitButton(createNewColoredReceivingAddressSubmitAction, controller);
        buttonPanel.add(createNewReceivingAddressSubmitButton);

        messageText = new MultiBitLabel("");
        messageText.setText(" ");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 3;
        constraints.gridy = 11;
        constraints.weightx = 0.8;
        constraints.weighty = 0.15;
        constraints.gridwidth = 4;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(messageText, constraints);

        JLabel filler3 = new JLabel();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 7;
        constraints.gridy = 11;
        constraints.weightx = 0.05;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(filler3, constraints);

        JLabel filler6 = new JLabel();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 7;
        constraints.gridy = 12;
        constraints.weightx = 0.05;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(filler6, constraints);
    }
 
    public void setMessageText(String message) {
        messageText.setText(message);
    }

    public TradeingPanel getTradeBitcoinPanel() {
        return tradeBitcoinPanel;
    }

    public int getNumberOfAddressesToCreate() {
        return 1;//((Integer) numberOfAddresses.getSelectedItem()).intValue();
    }

    public JComboBox getNumberOfAddresses() {
        return numberOfAddresses;
    }

    // Accessors used in testing.
    public CreateNewColoredReceivingAddressSubmitAction getCreateNewColoredReceivingAddressSubmitAction() {
        return createNewColoredReceivingAddressSubmitAction;
    }

    public String getMessageText() {
        return messageText.getText();
    }

    public void setWalletPassword(String walletPassword) {
        walletPasswordField.setText(walletPassword);
    }

    public MultiBitButton getCancelButton() {
        return cancelButton;
    }

    public boolean isWalletPasswordFieldEnabled() {
        return walletPasswordField.isEnabled();
    }
}
