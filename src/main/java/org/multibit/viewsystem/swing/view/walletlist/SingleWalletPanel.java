/**
 * Copyright 2013 multibit.org
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
package org.multibit.viewsystem.swing.view.walletlist;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Wallet.BalanceType;

import etx.com.trading.BaseTrading;
import etx.com.trading.BaseTrading.Asset;

import org.bitcoinj.wallet.Protos.Wallet.EncryptionType;
import org.joda.money.Money;
import org.multibit.controller.Controller;
import org.multibit.controller.bitcoin.BitcoinController;
import org.multibit.exchange.CurrencyConverter;
import org.multibit.message.Message;
import org.multibit.model.bitcoin.WalletBusyListener;
import org.multibit.model.bitcoin.WalletData;
import org.multibit.network.MultiBitDownloadListener;
import org.multibit.network.ReplayManager;
import org.multibit.network.ReplayTask;
import org.multibit.utils.ImageLoader;
import org.multibit.viewsystem.swing.ColorAndFontConstants;
import org.multibit.viewsystem.swing.MultiBitFrame;
import org.multibit.viewsystem.swing.action.HelpContextAction;
import org.multibit.viewsystem.swing.view.components.BlinkLabel;
import org.multibit.viewsystem.swing.view.components.FontSizer;
import org.multibit.viewsystem.swing.view.components.MultiBitTextField;
import org.multibit.viewsystem.swing.view.components.MultiBitTitledPanel;
import org.multibit.viewsystem.swing.view.panels.HelpContentsPanel;

import javax.swing.*;
import javax.swing.border.Border;







import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SingleWalletPanel extends JPanel implements ActionListener, FocusListener, WalletBusyListener {

  private static final int WIDTH_OF_TEXT_FIELD = 12;

  private static final long serialVersionUID = -7110340338285836548L;

  private static final Dimension ABOVE_BASELINE_LEADING_CORNER_PADDING = new Dimension(5, 5);
  private static final Dimension BELOW_BASELINE_TRAILING_CORNER_PADDING = new Dimension(5, 5);

  private WalletData perWalletModelData;

  private static final int COLOR_DELTA = 24;

  private static final int HEIGHT_DELTA = 26;
  private static final int WIDTH_DELTA = 4;
  private static final int MIN_WIDTH_SCROLLBAR_DELTA = 20;
  private static final double MINIMUM_WIDTH_SCALE_FACTOR = 0.5;

  private static Color inactiveBackGroundColor;
  private MultiBitTextField walletDescriptionTextField;
  private Border walletDescriptionTextFieldBorder;

  private BlinkLabel amountLabelBTC;
  private BlinkLabel amountLabelFiat;
  
  private class TransLabel extends BlinkLabel
  {

	public java.util.List<String> transactionHashes = new ArrayList<String>();
	
	public TransLabel(Controller controller, boolean isLarge) {
		super(controller, isLarge);
		// TODO Auto-generated constructor stub
	}
	
	  
  }
  
  private Map<Asset,TransLabel> coloredValuesLabel = new HashMap<Asset,TransLabel>();


  private final Controller controller;
  private final BitcoinController bitcoinController;

  private MultiBitFrame mainFrame;

  private int normalHeight;
  private int normalWidth;

  private RoundedPanel myRoundedPanel;

  public static int DESCRIPTION_HEIGHT_DELTA = 4;

  private boolean selected = false;

  private static final int WALLET_TYPE_LEFT_BORDER = 6;
  private static final int WALLET_TYPE_TOP_BORDER = 3;

  private static final int TWISTY_LEFT_OR_RIGHT_BORDER = 1;
  private static final int TWISTY_TOP_BORDER = 1;

  private JPanel twistyStent;

  private String unencryptedTooltip = "";
  private String encryptedTooltip = "";

  private JButton walletTypeButton;

  private JLabel hourglassLabel;

  private FontMetrics fontMetrics;
  
  private int NUMBER_OF_ROWS_IN_SUMMARY_PANEL = 2;
  private int AMOUNT_HEIGHT_DELTA = 4;
 // private JPanel addressValuesPanel;

  private double lastSyncPercent;

  public SingleWalletPanel(WalletData perWalletModelData, final BitcoinController bitcoinController, MultiBitFrame mainFrame, final WalletListPanel walletListPanel) {
    this.perWalletModelData = perWalletModelData;
    this.bitcoinController = bitcoinController;
    this.controller = this.bitcoinController;
    this.mainFrame = mainFrame;
    perWalletModelData.setSingleWalletDownloadListener(new SingleWalletPanelDownloadListener(this.bitcoinController, this));

    Font font = FontSizer.INSTANCE.getAdjustedDefaultFont();
    fontMetrics = getFontMetrics(font);

    // By default not selected.
    selected = false;

    unencryptedTooltip = HelpContentsPanel.createMultilineTooltipText(new String[]{controller.getLocaliser().getString("singleWalletPanel.unencrypted.tooltip"),
            " ", controller.getLocaliser().getString("multiBitFrame.helpMenuTooltip")});
    encryptedTooltip = HelpContentsPanel.createMultilineTooltipText(new String[]{controller.getLocaliser().getString("singleWalletPanel.encrypted.tooltip"),
            " ", controller.getLocaliser().getString("multiBitFrame.helpMenuTooltip")});


    normalHeight = NUMBER_OF_ROWS_IN_SUMMARY_PANEL * fontMetrics.getHeight() + DESCRIPTION_HEIGHT_DELTA + AMOUNT_HEIGHT_DELTA + HEIGHT_DELTA;
    normalWidth = calculateNormalWidth(this);

    // Add contents to myRoundedPanel.
    myRoundedPanel = new RoundedPanel(controller.getLocaliser().getLocale());
    myRoundedPanel.setLayout(new GridBagLayout());
    myRoundedPanel.setOpaque(true);
    myRoundedPanel.setPreferredSize(new Dimension(normalWidth, normalHeight));
    if (ComponentOrientation.LEFT_TO_RIGHT == ComponentOrientation.getOrientation(controller.getLocaliser().getLocale())) {
      myRoundedPanel.setMinimumSize(new Dimension(calculateMinimumWidth(normalWidth), normalHeight));
    } else {
      myRoundedPanel.setMinimumSize(new Dimension(normalWidth, normalHeight));
    }

    myRoundedPanel.setMaximumSize(new Dimension(normalWidth * 2, 10000));

    setOpaque(true);
    setFocusable(true);
    setBackground(ColorAndFontConstants.VERY_LIGHT_BACKGROUND_COLOR);

    if (ColorAndFontConstants.isInverse()) {
      inactiveBackGroundColor = new Color(Math.min(255, ColorAndFontConstants.BACKGROUND_COLOR.getRed() + 2 * COLOR_DELTA), Math.min(255,
              ColorAndFontConstants.BACKGROUND_COLOR.getBlue() + 2 * COLOR_DELTA), Math.min(255, ColorAndFontConstants.BACKGROUND_COLOR.getGreen() + 2 * COLOR_DELTA));
    } else {
      inactiveBackGroundColor = new Color(Math.max(0, ColorAndFontConstants.BACKGROUND_COLOR.getRed() - COLOR_DELTA), Math.max(0,
              ColorAndFontConstants.BACKGROUND_COLOR.getBlue() - COLOR_DELTA), Math.max(0, ColorAndFontConstants.BACKGROUND_COLOR.getGreen() - COLOR_DELTA));
    }
    GridBagConstraints constraints = new GridBagConstraints();
    GridBagConstraints constraints1 = new GridBagConstraints();
    GridBagConstraints constraints2 = new GridBagConstraints();
    GridBagConstraints constraints3 = new GridBagConstraints();
    GridBagConstraints constraints4 = new GridBagConstraints();
    GridBagConstraints constraints5 = new GridBagConstraints();
    GridBagConstraints constraints6 = new GridBagConstraints();
    GridBagConstraints constraints7 = new GridBagConstraints();
    GridBagConstraints constraints8 = new GridBagConstraints();
    GridBagConstraints constraints9 = new GridBagConstraints();
    GridBagConstraints constraints10 = new GridBagConstraints();
    GridBagConstraints constraints11 = new GridBagConstraints();
    GridBagConstraints constraints12 = new GridBagConstraints();
    GridBagConstraints constraints13 = new GridBagConstraints();
    

    JLabel filler1 = new JLabel();
    filler1.setMinimumSize(ABOVE_BASELINE_LEADING_CORNER_PADDING);
    filler1.setPreferredSize(ABOVE_BASELINE_LEADING_CORNER_PADDING);
    filler1.setMaximumSize(ABOVE_BASELINE_LEADING_CORNER_PADDING);

    filler1.setOpaque(false);
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.weightx = 0.04;
    constraints.weighty = 0.04;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
    myRoundedPanel.add(filler1, constraints);

    walletDescriptionTextField = new MultiBitTextField(perWalletModelData.getWalletDescription(), WIDTH_OF_TEXT_FIELD,
            controller);
    walletDescriptionTextField.setFocusable(true);
    walletDescriptionTextField.addActionListener(this);
    walletDescriptionTextField.addFocusListener(this);
    walletDescriptionTextField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (walletListPanel != null) {
          walletListPanel.selectAdjacentWallet(e, "SingleWalletPanel#WalletDescriptionTextField");
        }
        super.keyTyped(e);
      }
    });
    walletDescriptionTextFieldBorder = walletDescriptionTextField.getBorder();
    walletDescriptionTextField.setOpaque(false);

    constraints1.fill = GridBagConstraints.BOTH;
    constraints1.gridx = 1;
    constraints1.gridy = 1;
    constraints1.weightx = 100;
    constraints1.weighty = 8;
    constraints1.gridwidth = 8;
    constraints1.gridheight = 1;
    constraints1.anchor = GridBagConstraints.LINE_START;
    myRoundedPanel.add(walletDescriptionTextField, constraints1);

    JLabel filler2 = new JLabel();
    filler2.setMinimumSize(ABOVE_BASELINE_LEADING_CORNER_PADDING);
    filler2.setPreferredSize(ABOVE_BASELINE_LEADING_CORNER_PADDING);
    filler2.setMaximumSize(ABOVE_BASELINE_LEADING_CORNER_PADDING);
    filler2.setOpaque(false);
    constraints2.fill = GridBagConstraints.NONE;
    constraints2.gridx = 7;
    constraints2.gridy = 0;
    constraints2.weightx = 0.04;
    constraints2.weighty = 0.04;
    constraints2.gridwidth = 1;
    constraints2.gridheight = 1;
    constraints2.anchor = GridBagConstraints.ABOVE_BASELINE_TRAILING;
    myRoundedPanel.add(filler2, constraints2);

    constraints3.fill = GridBagConstraints.BOTH;
    constraints3.gridx = 1;
    constraints3.gridy = 2;
    constraints3.weightx = 0.1;
    constraints3.weighty = 0.1;
    constraints3.gridwidth = 1;
    constraints3.gridheight = 1;
    constraints3.anchor = GridBagConstraints.LINE_END;
    myRoundedPanel.add(MultiBitTitledPanel.createStent(2, 2), constraints3);

    constraints4.fill = GridBagConstraints.BOTH;
    constraints4.gridx = 1;
    constraints4.gridy = 3;
    constraints4.weightx = 0.1;
    constraints4.weighty = 0.1;
    constraints4.gridwidth = 1;
    constraints4.gridheight = 1;
    constraints4.anchor = GridBagConstraints.LINE_END;
    JPanel iconRowStent = MultiBitTitledPanel.createStent((int) (walletDescriptionTextFieldBorder.getBorderInsets(this).left * 0.5));
    myRoundedPanel.add(iconRowStent, constraints4);

    // Wallet type icon.
    walletTypeButton = new JButton();
    walletTypeButton.setOpaque(false);
    walletTypeButton.setVisible(true);
    walletTypeButton.setHorizontalAlignment(SwingConstants.CENTER);
    walletTypeButton.setVerticalAlignment(SwingConstants.CENTER);

    walletTypeButton.setBorder(BorderFactory.createEmptyBorder(WALLET_TYPE_TOP_BORDER, WALLET_TYPE_LEFT_BORDER, 0, WALLET_TYPE_LEFT_BORDER));
    if (perWalletModelData.getWallet() != null) {
      setIconForWalletType(perWalletModelData.getWallet().getEncryptionType(), walletTypeButton);
    }

    constraints5.fill = GridBagConstraints.NONE;
    constraints5.gridx = 2;
    constraints5.gridy = 3;
    constraints5.weightx = 0.1;
    constraints5.weighty = 0.1;
    constraints5.gridwidth = 1;
    constraints5.gridheight = 1;
    constraints5.anchor = GridBagConstraints.CENTER;
    myRoundedPanel.add(walletTypeButton, constraints5);

    // Hourglass icon.
    hourglassLabel = new JLabel(ImageLoader.createImageIcon(ImageLoader.HOURGLASS_ICON_FILE));
    hourglassLabel.setOpaque(false);
    hourglassLabel.setVisible(perWalletModelData.isBusy());
    hourglassLabel.setBorder(BorderFactory.createEmptyBorder(WALLET_TYPE_TOP_BORDER, WALLET_TYPE_LEFT_BORDER, 0, 0));

    constraints6.fill = GridBagConstraints.NONE;
    constraints6.gridx = 3;
    constraints6.gridy = 3;
    constraints6.weightx = 0.1;
    constraints6.weighty = 0.1;
    constraints6.gridwidth = 1;
    constraints6.gridheight = 1;
    constraints6.anchor = GridBagConstraints.LINE_START;
    myRoundedPanel.add(hourglassLabel, constraints6);

    twistyStent = MultiBitTitledPanel.createStent(1, 1);
    twistyStent.setBorder(BorderFactory.createEmptyBorder(TWISTY_TOP_BORDER, TWISTY_LEFT_OR_RIGHT_BORDER, 0, TWISTY_LEFT_OR_RIGHT_BORDER));
    twistyStent.setOpaque(false);
    myRoundedPanel.add(twistyStent, constraints7);

    amountLabelBTC = new BlinkLabel(controller, false);
    amountLabelBTC.setBackground(ColorAndFontConstants.BACKGROUND_COLOR);
    amountLabelBTC.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
    amountLabelBTC.setBlinkEnabled(true);
    amountLabelBTC.setHorizontalAlignment(JLabel.TRAILING);

    amountLabelFiat = new BlinkLabel(controller, false);
    amountLabelFiat.setBackground(ColorAndFontConstants.BACKGROUND_COLOR);
    amountLabelFiat.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
    amountLabelFiat.setBlinkEnabled(true);
    amountLabelFiat.setHorizontalAlignment(JLabel.TRAILING);

    JPanel stent = new JPanel();
    stent.setOpaque(false);
    constraints8.fill = GridBagConstraints.HORIZONTAL;
    constraints8.gridx = 5;
    constraints8.gridy = 3;
    constraints8.weightx = 10000;
    constraints8.weighty = 0.1;
    constraints8.gridwidth = 1;
    constraints8.anchor = GridBagConstraints.LINE_END;
    myRoundedPanel.add(stent, constraints8);

    constraints9.fill = GridBagConstraints.BOTH;
    constraints9.gridx = 6;
    constraints9.gridy = 3;
    constraints9.weightx = 0.1;
    constraints9.weighty = 0.1;
    constraints9.gridwidth = 2;
    constraints9.gridheight = 1;
    constraints9.anchor = GridBagConstraints.LINE_END;
    myRoundedPanel.add(amountLabelBTC, constraints9);

    constraints10.fill = GridBagConstraints.VERTICAL;
    constraints10.gridx = 8;
    constraints10.gridy = 3;
    constraints10.weightx = 0.1;
    constraints10.weighty = 0.1;
    constraints10.gridwidth = 1;
    constraints10.gridheight = 1;
    constraints.anchor = GridBagConstraints.LINE_END;
    myRoundedPanel.add(amountLabelFiat, constraints10);

    JPanel filler5 = new JPanel();
    filler5.setMinimumSize(BELOW_BASELINE_TRAILING_CORNER_PADDING);
    filler5.setPreferredSize(BELOW_BASELINE_TRAILING_CORNER_PADDING);
    filler5.setMaximumSize(BELOW_BASELINE_TRAILING_CORNER_PADDING);

    filler5.setOpaque(false);
    constraints11.fill = GridBagConstraints.NONE;
    constraints11.gridx = 9;
    constraints11.gridy = 5;
    constraints11.weightx = 0.02;
    constraints11.weighty = 0.02;
    constraints11.gridwidth = 1;
    constraints.anchor = GridBagConstraints.BELOW_BASELINE_TRAILING;
    myRoundedPanel.add(filler5, constraints11);

    // Add myRoundedPanel to myself.
    setLayout(new GridBagLayout());
  //  GridBagConstraints constraints2 = new GridBagConstraints();
    constraints12.fill = GridBagConstraints.HORIZONTAL;
    constraints12.gridx = 0;
    constraints12.gridy = 0;
    constraints12.weightx = 1;
    constraints12.weighty = 1;
    constraints12.gridwidth = 1;
    constraints12.gridheight = 1;
    constraints12.anchor = GridBagConstraints.CENTER;
    add(myRoundedPanel, constraints12);

    // Add bottom filler.
    JPanel filler = new JPanel();
    filler.setOpaque(false);
    constraints13.fill = GridBagConstraints.BOTH;
    constraints13.gridx = 0;
    constraints13.gridy = 2;
    constraints13.weightx = 1.0;
    constraints13.weighty = 1000;
    constraints13.gridwidth = 1;
    constraints13.gridheight = 1;
    constraints13.anchor = GridBagConstraints.CENTER;
    add(filler, constraints13);
    /*
    GridBagConstraints constraints14 = new GridBagConstraints();
    // Add bottom filler.
    addressValuesPanel = new JPanel();
    filler.setOpaque(false);
    constraints14.fill = GridBagConstraints.BOTH;
    constraints14.gridx = 0;
    constraints14.gridy = 6;
    constraints14.weightx = 1.0;
    constraints14.weighty = 1000;
    constraints14.gridwidth = 1;
    constraints14.gridheight = 1;
    constraints14.anchor = GridBagConstraints.CENTER;
    myRoundedPanel.add(addressValuesPanel, constraints14);
    */

    applyComponentOrientation(ComponentOrientation.getOrientation(controller.getLocaliser().getLocale()));

    setSelected(false);

    updateFromModel(false, true);

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent arg0) {
        if (walletListPanel != null) {
          walletListPanel.selectAdjacentWallet(arg0, "SingleWalletPanel");
        }
        super.keyTyped(arg0);
      }
    });

    // See if there is a waiting ReplayTask for this SingleWalletPanel and set up UI accordingly.
    ReplayTask replayTask = ReplayManager.INSTANCE.getWaitingReplayTask(perWalletModelData);
    if (replayTask != null) {
      String waitingText = controller.getLocaliser().getString("singleWalletPanel.waiting.verb");

      // When busy occasionally the localiser fails to localise.
      if (!(waitingText.indexOf("singleWalletPanel.waiting.verb") > -1)) {
        setSyncMessage(waitingText, Message.NOT_RELEVANT_PERCENTAGE_COMPLETE);
      }

    }
  }

  private void setIconForWalletType(EncryptionType walletType, JButton button) {
    button.setHorizontalAlignment(SwingConstants.CENTER);
    button.setContentAreaFilled(false);

    if (selected) {
      button.setOpaque(true);
      button.setBackground(ColorAndFontConstants.VERY_LIGHT_BACKGROUND_COLOR);
      button.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createMatteBorder(1, 1, 1, 1, ColorAndFontConstants.SELECTION_BACKGROUND_COLOR.darker()),
              BorderFactory.createMatteBorder(2, 2, 2, 2, ColorAndFontConstants.VERY_LIGHT_BACKGROUND_COLOR)));
      button.setBorderPainted(true);
    } else {
      button.setOpaque(false);
      button.setBackground(inactiveBackGroundColor);
      button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
      button.setBorderPainted(false);
    }

    if (walletType == EncryptionType.ENCRYPTED_SCRYPT_AES) {
      Action helpAction = new HelpContextAction(controller, ImageLoader.LOCK_ICON_FILE,
              "multiBitFrame.helpMenuText", "multiBitFrame.helpMenuTooltip", "multiBitFrame.helpMenuText",
              HelpContentsPanel.HELP_WALLET_TYPES_URL);
      button.setAction(helpAction);
      button.setText("");
      button.setToolTipText(encryptedTooltip);
    } else {
      Action helpAction = new HelpContextAction(controller, ImageLoader.SINGLE_WALLET_ICON_FILE,
              "multiBitFrame.helpMenuText", "multiBitFrame.helpMenuTooltip", "multiBitFrame.helpMenuText",
              HelpContentsPanel.HELP_WALLET_TYPES_URL);
      button.setAction(helpAction);
      button.setText("");
      button.setToolTipText(unencryptedTooltip);
    }
  }

  public static int calculateNormalWidth(JComponent component) {
    Font font = FontSizer.INSTANCE.getAdjustedDefaultFont();
    FontMetrics fontMetrics = component.getFontMetrics(font);
    return (int) (fontMetrics.stringWidth(MultiBitFrame.EXAMPLE_MEDIUM_FIELD_TEXT) + WIDTH_DELTA);
  }

  private int calculateMinimumWidth(int normalWidth) {
    if (ComponentOrientation.LEFT_TO_RIGHT == ComponentOrientation.getOrientation(controller.getLocaliser().getLocale())) {
      return (int) Math.max(0, normalWidth * MINIMUM_WIDTH_SCALE_FACTOR - MIN_WIDTH_SCROLLBAR_DELTA);
    } else {
      return normalWidth;
    }
  }

  @Override
  public void addMouseListener(MouseListener mouseListener) {
    super.addMouseListener(mouseListener);
    walletDescriptionTextField.addMouseListener(mouseListener);
    amountLabelBTC.addMouseListener(mouseListener);
    amountLabelFiat.addMouseListener(mouseListener);
    myRoundedPanel.addMouseListener(mouseListener);

    if (hourglassLabel != null) {
      hourglassLabel.addMouseListener(mouseListener);
    }
  }

  public void setBusyIconStatus(boolean isBusy) {
    hourglassLabel.setVisible(isBusy);

    // Update the tooltip.
    if (this.bitcoinController.getModel().getActivePerWalletModelData().isBusy()) {
      // Wallet is busy with another operation that may change the private keys - Action is disabled.
      if (perWalletModelData != null) {
        String toolTipText = HelpContentsPanel.createTooltipText(controller.getLocaliser().getString("multiBitSubmitAction.walletIsBusy",
                new Object[]{controller.getLocaliser().getString(perWalletModelData.getBusyTaskKey())}));
        hourglassLabel.setToolTipText(toolTipText);
      } else {
        hourglassLabel.setToolTipText(null);
      }
    } else {
      hourglassLabel.setToolTipText(null);
    }
  }

  public void setSelected(boolean selected) {
    this.selected = selected;

    myRoundedPanel.setSelected(selected);
    if (!perWalletModelData.isFilesHaveBeenChangedByAnotherProcess()) {
    	int actual_hieght = normalHeight;
        if(selected)
        {
        	actual_hieght += coloredValuesLabel.size() *  fontMetrics.getHeight();
        }

    	Iterator it = coloredValuesLabel.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ((TransLabel)pairs.getValue()).setVisible(selected);
          //  ((TransLabel)pairs.getValue()).setText(selected ? "visible" : "hidden");
            //it.remove(); // avoids a ConcurrentModificationException
        
        }
    	myRoundedPanel.setPreferredSize(new Dimension(normalWidth, actual_hieght));
         if (ComponentOrientation.LEFT_TO_RIGHT == ComponentOrientation.getOrientation(controller.getLocaliser().getLocale())) {
        	 myRoundedPanel.setMinimumSize(new Dimension(calculateMinimumWidth(normalWidth), actual_hieght));
         } else {
        	 myRoundedPanel.setMinimumSize(new Dimension(normalWidth, actual_hieght));
         }
         myRoundedPanel.setMaximumSize(new Dimension(normalWidth * 2, actual_hieght));


        
      setPreferredSize(new Dimension(normalWidth, actual_hieght));
      if (ComponentOrientation.LEFT_TO_RIGHT == ComponentOrientation.getOrientation(controller.getLocaliser().getLocale())) {
        setMinimumSize(new Dimension(calculateMinimumWidth(normalWidth), actual_hieght));
      } else {
        setMinimumSize(new Dimension(normalWidth, actual_hieght));
      }
      setMaximumSize(new Dimension(normalWidth * 2, actual_hieght));

      if (selected) {
        walletDescriptionTextField.setForeground(ColorAndFontConstants.TEXT_COLOR);
        if (!walletDescriptionTextField.isEditable()) {
          walletDescriptionTextField.setEditable(true);
        }
        requestFocusInWindow();

        walletDescriptionTextField.setBorder(walletDescriptionTextFieldBorder);
        walletDescriptionTextField.setBackground(ColorAndFontConstants.BACKGROUND_COLOR);
        myRoundedPanel.setBackground(ColorAndFontConstants.BACKGROUND_COLOR);

        walletTypeButton.setOpaque(true);
        walletTypeButton.setBackground(ColorAndFontConstants.VERY_LIGHT_BACKGROUND_COLOR);
        Color selectionBackGroundColor = ColorAndFontConstants.SELECTION_BACKGROUND_COLOR;
        selectionBackGroundColor = ColorAndFontConstants.isInverse() ? selectionBackGroundColor.brighter() : selectionBackGroundColor.darker();
        walletTypeButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, selectionBackGroundColor),
                BorderFactory.createMatteBorder(2, 2, 2, 2, ColorAndFontConstants.VERY_LIGHT_BACKGROUND_COLOR)));
        walletTypeButton.setBorderPainted(true);

        myRoundedPanel.repaint();
        twistyStent.setVisible(true);
      } else {
        walletDescriptionTextField.setForeground(ColorAndFontConstants.TEXT_COLOR);
        walletDescriptionTextField.setEditable(false);
        Insets insets = walletDescriptionTextFieldBorder.getBorderInsets(this);
        Border border = BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right);
        walletDescriptionTextField.setBorder(border);
        walletDescriptionTextField.setBackground(inactiveBackGroundColor);
        myRoundedPanel.setBackground(inactiveBackGroundColor);

        walletTypeButton.setOpaque(false);
        walletTypeButton.setBackground(inactiveBackGroundColor);
        walletTypeButton.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        walletTypeButton.setBorderPainted(false);

        myRoundedPanel.repaint();
        twistyStent.setVisible(true);
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    saveChanges();
    if (!perWalletModelData.isFilesHaveBeenChangedByAnotherProcess()) {
      walletDescriptionTextField.setBackground(ColorAndFontConstants.BACKGROUND_COLOR);
    }
    requestFocusInWindow();
  }

  public WalletData getPerWalletModelData() {
    return perWalletModelData;
  }

  @Override
  public void focusGained(FocusEvent arg0) {
    if (!perWalletModelData.isFilesHaveBeenChangedByAnotherProcess()) {
      walletDescriptionTextField.setSelectedTextColor(ColorAndFontConstants.SELECTION_FOREGROUND_COLOR);
      walletDescriptionTextField.setSelectionColor(ColorAndFontConstants.SELECTION_BACKGROUND_COLOR);
      String text = walletDescriptionTextField.getText();
      walletDescriptionTextField.setCaretPosition(text == null ? 0 : text.length());
      perWalletModelData.setWalletDescription(text);

      if (!(arg0.getSource() instanceof JTextField)) {
        // Panel selection.
        requestFocusInWindow();
      }
    }
  }

  @Override
  public void focusLost(FocusEvent arg0) {
    saveChanges();
  }

  private void saveChanges() {
    if (!perWalletModelData.isFilesHaveBeenChangedByAnotherProcess()) {
      walletDescriptionTextField.setForeground(ColorAndFontConstants.TEXT_COLOR);
      walletDescriptionTextField.select(0, 0);
      String text = walletDescriptionTextField.getText();
      perWalletModelData.setWalletDescription(text);

      String titleText = controller.getLocaliser().getString("multiBitFrame.title");
      if (this.bitcoinController.getModel().getActiveWallet() != null) {
        titleText = titleText + MultiBitFrame.SEPARATOR
                + this.bitcoinController.getModel().getActivePerWalletModelData().getWalletDescription() + MultiBitFrame.SEPARATOR
                + this.bitcoinController.getModel().getActivePerWalletModelData().getWalletFilename();
      }
      mainFrame.setTitle(titleText);
    }
  }

  /**
   * Update any UI elements from the model (hint that data has changed).
   */
  public void updateFromModel(boolean blinkEnabled, boolean useBusyStatus) {
    if (ColorAndFontConstants.isInverse()) {
      inactiveBackGroundColor = new Color(Math.min(255, ColorAndFontConstants.BACKGROUND_COLOR.getRed() + 2 * COLOR_DELTA), Math.min(255,
              ColorAndFontConstants.BACKGROUND_COLOR.getBlue() + 2 * COLOR_DELTA), Math.min(255, ColorAndFontConstants.BACKGROUND_COLOR.getGreen() + 2 * COLOR_DELTA));
    } else {
      inactiveBackGroundColor = new Color(Math.max(0, ColorAndFontConstants.BACKGROUND_COLOR.getRed() - COLOR_DELTA), Math.max(0,
              ColorAndFontConstants.BACKGROUND_COLOR.getBlue() - COLOR_DELTA), Math.max(0, ColorAndFontConstants.BACKGROUND_COLOR.getGreen() - COLOR_DELTA));
    }
    //etx
    BaseTrading bt = BaseTrading.getInstance();
    LinkedList<TransactionOutput> all = perWalletModelData.getWallet().calculateAllSpendCandidates(false);
    System.out.println("transactions count: " + all.size());
    BigInteger value = BigInteger.ZERO;
    for (TransactionOutput out : all) 
    {
    	List<TransactionOutput> outi = out.getParentTransaction().getOutputs();
    	System.out.println("output count: " + outi.size());
    	for(int outindex =0;  outindex < outi.size(); outindex ++) {
    		TransactionOutput singleOutput = outi.get(outindex);
    		if(singleOutput.isMine(perWalletModelData.getWallet()) && singleOutput.isAvailableForSpending()) {
		    	if(bt.IsColorTransaction(  singleOutput.getParentTransaction().getHashAsString(), outindex))
		    	{
		    		Asset ast = bt.getAssetForTransaction(singleOutput.getParentTransaction().getHashAsString());
		    		if(!coloredValuesLabel.containsKey(ast)) {
		    			TransLabel bl = new TransLabel(controller, false);
			    		bl.setBackground(ColorAndFontConstants.BACKGROUND_COLOR);
			    		bl.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
			    		bl.setBlinkEnabled(true);
			    		bl.setHorizontalAlignment(JLabel.TRAILING);
			    		bl.setText((singleOutput.getValue().doubleValue() * ast.satoshi_multiplyier) + " " + ast.symbol);
			    		
			    		GridBagConstraints con = new GridBagConstraints();
			    		con.fill = GridBagConstraints.BOTH;
			    		con.gridx = 6;
			    		con.gridy = 4 + coloredValuesLabel.size();
			    		con.weightx = 0.1;
			    		con.weighty = 0.1;
			    		con.gridwidth = 2;
			    		con.gridheight = 1;
			    	//	con.anchor = GridBagConstraints.LINE_END;
			    	    
			    		Dimension d = myRoundedPanel.getSize(); 
			    		d.height += 1 * fontMetrics.getHeight();
		
			    		
			    		coloredValuesLabel.put(ast,bl);
			    		//System.out.println(" we found a color tranaction in our wallet" );
			    		myRoundedPanel.add(bl, con);
			    		myRoundedPanel.setPreferredSize(new Dimension(1000, 1000));
		    		}
		    	}
		    	else
		    	{
		    		System.out.println("value is " + value + " before adding");
		    		System.out.println("adding: " + singleOutput.hashCode() + " value: " + singleOutput.getValue() + " for wallet " + perWalletModelData.getWallet().toString());
		    		value = value.add(singleOutput.getValue());
		    	}
    		}
	    }
    	
    }
   
    
    System.out.println("compare value " + value +  " to wallet " + perWalletModelData.getWallet().getBalance(BalanceType.ESTIMATED));
    //etx
    
    BigInteger estimatedBalance = value; // != BigInteger.ZERO ? value : perWalletModelData.getWallet().getBalance(BalanceType.ESTIMATED);
    String balanceTextToShowBTC = controller.getLocaliser().bitcoinValueToString(estimatedBalance, true, false);
    String balanceTextToShowFiat = "";
    if (CurrencyConverter.INSTANCE.getRate() != null && CurrencyConverter.INSTANCE.isShowingFiat()) {
      Money fiat = CurrencyConverter.INSTANCE.convertFromBTCToFiat(estimatedBalance);
      balanceTextToShowFiat = "(" + CurrencyConverter.INSTANCE.getFiatAsLocalisedString(fiat) + ")";
    }

    if (useBusyStatus && perWalletModelData.isBusy()) {
      if (lastSyncPercent > 0) {
        setSyncMessage(controller.getLocaliser().getString(perWalletModelData.getBusyTaskVerbKey()), lastSyncPercent);
      } else {
        setSyncMessage(controller.getLocaliser().getString(perWalletModelData.getBusyTaskVerbKey()), Message.NOT_RELEVANT_PERCENTAGE_COMPLETE);
      }
    } else {
      if (amountLabelBTC != null && amountLabelBTC.getText() != null && !"".equals(amountLabelBTC.getText())
              && !balanceTextToShowBTC.equals(amountLabelBTC.getText()) && blinkEnabled) {
        amountLabelBTC.blink(balanceTextToShowBTC);
        amountLabelFiat.blink(balanceTextToShowFiat);
      } else {
        amountLabelBTC.setText(balanceTextToShowBTC);
        amountLabelFiat.setText(balanceTextToShowFiat);
      }
    }

    if (perWalletModelData.getWallet() != null) {
      setIconForWalletType(perWalletModelData.getWallet().getEncryptionType(), walletTypeButton);
    }

    if (perWalletModelData.isFilesHaveBeenChangedByAnotherProcess()) {
      myRoundedPanel.setOpaque(true);
      myRoundedPanel.setBackground(ColorAndFontConstants.VERY_LIGHT_BACKGROUND_COLOR);
      walletDescriptionTextField.setBackground(ColorAndFontConstants.VERY_LIGHT_BACKGROUND_COLOR);
      walletDescriptionTextField.setText(controller.getLocaliser().getString("singleWalletPanel.dataHasChanged.text"));
      walletDescriptionTextField.setForeground(ColorAndFontConstants.DATA_HAS_CHANGED_TEXT_COLOR);
      walletDescriptionTextField.setDisabledTextColor(ColorAndFontConstants.DATA_HAS_CHANGED_TEXT_COLOR);

      mainFrame.setUpdatesStoppedTooltip(walletDescriptionTextField);
      walletDescriptionTextField.setEnabled(false);
      walletDescriptionTextField.setEditable(false);
      amountLabelBTC.setText("");
      amountLabelBTC.setEnabled(false);
      amountLabelFiat.setText("");
      amountLabelFiat.setEnabled(false);
      walletTypeButton.setEnabled(false);
    }

    invalidate();
    revalidate();
    repaint();
  }

  public void setSyncMessage(String message, double syncPercent) {
    if (message == null) {
      return;
    }

    lastSyncPercent = syncPercent;

    if (syncPercent > MultiBitDownloadListener.DONE_FOR_DOUBLES) {
      updateFromModel(false, false);
    } else {
      if (perWalletModelData.isBusy()) {
        // It shoud always be whilst a download is occurring.
        setBusyIconStatus(true);
      }

      String percentText = " ";
      if (syncPercent > Message.NOT_RELEVANT_PERCENTAGE_COMPLETE) {
        percentText = "(" + (int) syncPercent + "%)";
      }
      amountLabelBTC.setBlinkEnabled(false);
      amountLabelFiat.setBlinkEnabled(false);

      amountLabelBTC.setText(message);
      amountLabelFiat.setText(percentText);

      amountLabelBTC.setBlinkEnabled(true);
      amountLabelFiat.setBlinkEnabled(true);

      if (perWalletModelData.getWalletFilename().equals(this.bitcoinController.getModel().getActiveWalletFilename())) {
        mainFrame.updateHeader(message, syncPercent);
      }
    }
  }

  public int getFiatLabelWidth() {
    return fontMetrics.stringWidth(amountLabelFiat.getText());
  }

  public void setFiatLabelWidth(int fiatLabelMinimumWidth) {
    amountLabelFiat.setMinimumSize(new Dimension(fiatLabelMinimumWidth, amountLabelFiat.getMinimumSize().height));
    amountLabelFiat.setPreferredSize(new Dimension(fiatLabelMinimumWidth, amountLabelFiat.getPreferredSize().height));
    amountLabelFiat.setMaximumSize(new Dimension(fiatLabelMinimumWidth, amountLabelFiat.getMaximumSize().height));
  }

  public boolean isSelectedInternal() {
    return selected;
  }

  @Override
  public void walletBusyChange(boolean newWalletIsBusy) {
    if (perWalletModelData.isBusy()) {
      setSyncMessage(controller.getLocaliser().getString(perWalletModelData.getBusyTaskVerbKey()), Message.NOT_RELEVANT_PERCENTAGE_COMPLETE);
    }
  }
}