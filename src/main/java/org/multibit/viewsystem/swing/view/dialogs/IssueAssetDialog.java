/**
 * Copyright 2011 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.multibit.viewsystem.swing.view.dialogs;

import org.multibit.controller.Controller;
import org.multibit.controller.bitcoin.BitcoinController;
import org.multibit.viewsystem.swing.MultiBitFrame;
import org.multibit.viewsystem.swing.view.components.FontSizer;
import org.multibit.viewsystem.swing.view.components.MultiBitDialog;
import org.multibit.viewsystem.swing.view.panels.AcceptOfferPanel;
import org.multibit.viewsystem.swing.view.panels.CreateNewAssetPanel;
import org.multibit.viewsystem.swing.view.panels.CreateNewOfferPanel;
import org.multibit.viewsystem.swing.view.panels.CreateNewReceivingAddressPanel;
import org.multibit.viewsystem.swing.view.panels.IssueAssetPanel;
import org.multibit.viewsystem.swing.view.panels.ReceiveBitcoinPanel;
import org.multibit.viewsystem.swing.view.panels.TradeingPanel;

import javax.swing.*;

import java.awt.*;

/**
 * The dialog for creating new receiving addresses.
 */
public class IssueAssetDialog extends MultiBitDialog {

    private static final long serialVersionUID = 191439652345057706L;

    private static final int HEIGHT_DELTA = 240;
    private static final int WIDTH_DELTA = 420;
 
    private TradeingPanel tradePanel;

    private final Controller controller;
    private final BitcoinController bitcoinController;
    
    private IssueAssetPanel issueAssetPanel; 

    /**
     * Creates a new {@link IssueAssetDialog}.
     */
    public IssueAssetDialog(BitcoinController bitcoinController, MultiBitFrame mainFrame, TradeingPanel tradePanel) {
        super(mainFrame, bitcoinController.getLocaliser().getString("IssueAssetDialog.title"));
        this.bitcoinController = bitcoinController;
        this.controller = this.bitcoinController;
        this.tradePanel = tradePanel;
      
        initUI();
        
        issueAssetPanel.getCancelButton().requestFocusInWindow();
        applyComponentOrientation(ComponentOrientation.getOrientation(controller.getLocaliser().getLocale()));
    }

    /**
     * Initialise dialog.
     */
    public void initUI() {
        try {
            FontMetrics fontMetrics = getFontMetrics(FontSizer.INSTANCE.getAdjustedDefaultFont());
        
            int minimumHeight = fontMetrics.getHeight() * 8 + HEIGHT_DELTA;
            int minimumWidth = Math.max(
                fontMetrics.stringWidth(
                    controller.getLocaliser().getString("IssueAssetDialog.message")),
                fontMetrics.stringWidth(
                    controller.getLocaliser().getString("IssueAssetDialog.createdSuccessfullyShort",
                        new Object[] {100}))) + WIDTH_DELTA;
            setMinimumSize(new Dimension(minimumWidth, minimumHeight));
            positionDialogRelativeToParent(this, 0.5D, 0.47D);
        } catch (NullPointerException npe) {
            // FontSizer fail - probably headless in test - carry on.
        }

        issueAssetPanel = new IssueAssetPanel(this.bitcoinController, tradePanel, this);
        
        setLayout(new BorderLayout());
        add(issueAssetPanel, BorderLayout.CENTER);
    }

    public IssueAssetPanel getNewAssetPanel() {
        return issueAssetPanel;
    }
    /*
    public int getNumberOfAddressesToCreate() {
        return createNewReceivingAddressPanel.getNumberOfAddressesToCreate();
    }

    public JComboBox getNumberOfAddresses() {
        return createNewReceivingAddressPanel.getNumberOfAddresses();
    }
    */
}