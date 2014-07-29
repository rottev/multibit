/**
 * Copyright 2012 multibit.org
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
package org.multibit.viewsystem.swing.action;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.WalletTransaction;
import com.google.bitcoin.core.Transaction.SigHash;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.Wallet.SendRequest;
import com.google.bitcoin.core.WalletTransaction.Pool;
import com.google.bitcoin.core.WrongNetworkException;
import com.google.bitcoin.crypto.KeyCrypterException;

import etx.com.trading.BaseTrading;
import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTrading.ColorGenisis;

import org.multibit.controller.bitcoin.BitcoinController;
import org.multibit.message.Message;
import org.multibit.message.MessageManager;
import org.multibit.model.bitcoin.BitcoinModel;
import org.multibit.model.bitcoin.WalletData;
import org.multibit.utils.ImageLoader;
import org.multibit.viewsystem.dataproviders.BitcoinFormDataProvider;
import org.multibit.viewsystem.swing.MultiBitFrame;
import org.multibit.viewsystem.swing.view.dialogs.SendBitcoinConfirmDialog;
import org.multibit.viewsystem.swing.view.dialogs.ValidationErrorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.List;

/**
 * This {@link Action} shows the send bitcoin confirm dialog or validation dialog on an attempted spend.
 */
public class SendBitcoinConfirmAction extends MultiBitSubmitAction {

    private static final long serialVersionUID = 1913592460523457765L;

    private static final Logger log = LoggerFactory.getLogger(SendBitcoinConfirmAction.class);

    private MultiBitFrame mainFrame;
    private BitcoinFormDataProvider dataProvider;
    private BitcoinController bitcoinController;

    /**
     * Creates a new {@link SendBitcoinConfirmAction}.
     */
    public SendBitcoinConfirmAction(BitcoinController bitcoinController, MultiBitFrame mainFrame, BitcoinFormDataProvider dataProvider) {
        super(bitcoinController, "sendBitcoinConfirmAction.text", "sendBitcoinConfirmAction.tooltip", "sendBitcoinConfirmAction.mnemonicKey", ImageLoader.createImageIcon(ImageLoader.SEND_BITCOIN_ICON_FILE));
        this.mainFrame = mainFrame;
        this.dataProvider = dataProvider;
        this.bitcoinController = bitcoinController;
    }

    /**
     * Complete the transaction to work out the fee) and then show the send bitcoin confirm dialog.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (abort()) {
            return;
        }

        SendBitcoinConfirmDialog sendBitcoinConfirmDialog = null;
        ValidationErrorDialog validationErrorDialog = null;

        try {
            String sendAddress = dataProvider.getAddress();
            String sendAmount = dataProvider.getAmount();

            Validator validator = new Validator(super.bitcoinController);
        	
            // check if we want to send color
        	Asset asset = dataProvider.getColorAsset();
        	if(asset != null) {
        		SendRequest sendRequest = sendColordCoins(sendAddress, sendAmount, asset);
        		  if (sendRequest != null) {
                      // There is enough money.

                      sendBitcoinConfirmDialog = new SendBitcoinConfirmDialog(super.bitcoinController, mainFrame, sendRequest, dataProvider);
                      sendBitcoinConfirmDialog.setVisible(true);
                  } else {
                      // There is not enough money.
                      // TODO setup validation parameters accordingly so that it displays ok.
                      validationErrorDialog = new ValidationErrorDialog(super.bitcoinController, mainFrame, sendRequest, true);
                      validationErrorDialog.setVisible(true);
                  }
        		 return;
        	}
        	
            if (validator.validate(sendAddress, sendAmount)) {

            	SendRequest sendRequest = sendBTCCoins(sendAddress, sendAmount);
      		  if (sendRequest != null) {
                    // There is enough money.

                    sendBitcoinConfirmDialog = new SendBitcoinConfirmDialog(super.bitcoinController, mainFrame, sendRequest, dataProvider);
                    sendBitcoinConfirmDialog.setVisible(true);
                } else {
                    // There is not enough money.
                    // TODO setup validation parameters accordingly so that it displays ok.
                    validationErrorDialog = new ValidationErrorDialog(super.bitcoinController, mainFrame, sendRequest, true);
                    validationErrorDialog.setVisible(true);
                }
      		
                // Note - Request is populated with the AES key in the SendBitcoinNowAction after the user has entered it on the SendBitcoinConfirm form.

                // Complete it (which works out the fee) but do not sign it yet.
                log.debug("Just about to complete the tx (and calculate the fee)...");
                boolean completedOk = bitcoinController.getModel().getActiveWallet().completeTx(sendRequest, false);
                log.debug("The fee after completing the transaction was " + sendRequest.fee);
                if (completedOk) {
                    // There is enough money.

                    sendBitcoinConfirmDialog = new SendBitcoinConfirmDialog(super.bitcoinController, mainFrame, sendRequest, dataProvider);
                    sendBitcoinConfirmDialog.setVisible(true);
                } else {
                    // There is not enough money.
                    // TODO setup validation parameters accordingly so that it displays ok.
                    validationErrorDialog = new ValidationErrorDialog(super.bitcoinController, mainFrame, sendRequest, true);
                    validationErrorDialog.setVisible(true);
                }

            } else {
                validationErrorDialog = new ValidationErrorDialog(super.bitcoinController, mainFrame, null, false);
                validationErrorDialog.setVisible(true);
            }
        } catch (WrongNetworkException e1) {
            logMessage(e1);
        } catch (AddressFormatException e1) {
            logMessage(e1);
        } catch (KeyCrypterException e1) {
            logMessage(e1);
        } catch (Exception e1) {
            logMessage(e1);
        }
    }

    private SendRequest sendBTCCoins(String sendAddress, String sendAmount) throws WrongNetworkException, AddressFormatException {
		// TODO Auto-generated method stub
    	 Address sendAddressObject = new Address(bitcoinController.getModel().getNetworkParameters(), sendAddress);
    	 Transaction tx = new Transaction(bitcoinController.getModel().getActiveWallet().getNetworkParameters());
			
			boolean finishTx = false;
			// first input has to be colored
			if(AddGiveOutputsToTransaction(tx, bitcoinController.getModel().getActiveWallet(), null, sendAmount, sendAddressObject ))
			{
				if(tx.calculateFee(bitcoinController.getModel().getActiveWallet()) == BigInteger.ZERO)
					TakeFeeFromAnyBTCUnspent(tx, bitcoinController.getModel().getActiveWallet());
				SendRequest sendRequest = SendRequest.forTx(tx);
	            sendRequest.ensureMinRequiredFee = true;
	            sendRequest.fee = BigInteger.ZERO;
	            sendRequest.feePerKb = BigInteger.ZERO;
	            return sendRequest;
			}
			return null;	
	}

	private SendRequest sendColordCoins(String sendAddress, String sendAmount, Asset asset) throws WrongNetworkException, AddressFormatException {
		// TODO Auto-generated method stub
    	 Address sendAddressObject = new Address(bitcoinController.getModel().getNetworkParameters(), sendAddress);
    	 Transaction tx = new Transaction(bitcoinController.getModel().getActiveWallet().getNetworkParameters());
			
			boolean finishTx = false;
			// first input has to be colored
			if(AddGiveOutputsToTransaction(tx, bitcoinController.getModel().getActiveWallet(), asset, sendAmount, sendAddressObject ))
			{
				TakeFeeFromAnyBTCUnspent(tx, bitcoinController.getModel().getActiveWallet());
				SendRequest sendRequest = SendRequest.forTx(tx);
	            sendRequest.ensureMinRequiredFee = true;
	            sendRequest.fee = BigInteger.ZERO;
	            sendRequest.feePerKb = BigInteger.ZERO;
	            return sendRequest;
			}
			return null;	
	}

	

	private boolean TakeFeeFromAnyBTCUnspent(Transaction tx, Wallet activeWallet) {
		// TODO Auto-generated method stub
		try
		{
			BaseTrading bt = BaseTrading.getInstance(((BitcoinController) controller).getModel().isTestnet());
			
			
			for (WalletTransaction it : activeWallet.getWalletTransactions()) {
				Transaction t = it.getTransaction();
				if(t.isMine( activeWallet) && !t.isEveryOwnedOutputSpent(activeWallet) && it.getPool() == Pool.UNSPENT) {
					List<TransactionOutput> outputs = t.getOutputs();
					for(int i = 0; i < outputs.size(); i++) {
						if(outputs.get(i).isAvailableForSpending() && outputs.get(i).isMine(activeWallet) && outputs.get(i).getValue().compareTo(BigInteger.ZERO) > 0) {
							ColorGenisis cg = bt.GetColorTransactionSearchHistory(activeWallet, t.getHashAsString(), i);
							if(cg == null){
								double change = outputs.get(i).getValue().doubleValue() - Transaction.REFERENCE_DEFAULT_MIN_TX_FEE.doubleValue();
								if(change > 0 ) {									
										//just use this output to pay the fee.
										tx.addInput( outputs.get(i));
										System.out.println("transaciont:\n" + tx.toString());
										
										tx.addOutput( new BigDecimal(change).toBigInteger(), outputs.get(i).getScriptPubKey().getToAddress(activeWallet.getNetworkParameters()));
										System.out.println("transaciont:\n" + tx.toString());
									//	tx.signInputs(SigHash.ALL, wallet.getWallet());
										return true;									
								}
								else
									System.out.println("IsOutputInTx: true: ");
								
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

	private boolean AddGiveOutputsToTransaction(Transaction tx,
			Wallet activeWallet, Asset asset, String sendAmount, Address toAdress) {
		// TODO Auto-generated method stub
		BigInteger totalgive = BigInteger.ZERO;
		boolean feePayed = false;
		
		try
		{
		//	List<Integer> alInputs = new ArrayList<Integer>();
		//	List<Integer> alOuputs = new ArrayList<Integer>();
			BaseTrading bt = BaseTrading.getInstance(((BitcoinController) controller).getModel().isTestnet());

			NumberFormat nf = NumberFormat.getInstance();
			Double amount = nf.parse(sendAmount).doubleValue();
			String amountString = nf.parse(sendAmount).toString();
			for (WalletTransaction it : activeWallet.getWalletTransactions() ) {
				Transaction t = it.getTransaction();
				if(t.isMine( activeWallet) && !t.isEveryOwnedOutputSpent(activeWallet) && it.getPool() == Pool.UNSPENT) {
					List<TransactionOutput> outputs = t.getOutputs();
					for(int i = 0; i < outputs.size(); i++) {
						if(outputs.get(i).isMine(activeWallet ) && outputs.get(i).isAvailableForSpending() && outputs.get(i).getValue().compareTo(BigInteger.ZERO) > 0) {
							ColorGenisis cg = bt.GetColorTransactionSearchHistory(activeWallet, t.getHashAsString(), i);
							
							 if(cg != null){ //send a color to the offerer is output colord at all?
								// get asset and check its the one we should give
								Asset a = bt.getAssetForTransaction(cg.txout, cg.index);
								if (!a.id.equals(asset.id))
									continue;
								
								totalgive = totalgive.add(outputs.get(i).getValue());								
								tx.addInput(outputs.get(i));
								tx.getInput(tx.getInputs().size() - 1).setSequenceNumber(tx.getOutputs().size() | Integer.MIN_VALUE);
								//alInputs.add(tx.getInputs().size() - 1);
								
								// passed the mark now make sure to set the outputs accordingly
								System.out.println("totalgive: " + totalgive);
								System.out.println("outputs.get(i).getValue(): " + outputs.get(i).getValue());
								
								if(totalgive.doubleValue() >= (amount / a.satoshi_multiplyier)){
									tx.addOutput(new BigDecimal(amount / a.satoshi_multiplyier).toBigInteger(), toAdress);
								//	alOuputs.add(tx.getOutputs().size() - 1);
									BigInteger backToWallet = totalgive.subtract(new BigDecimal(amount / a.satoshi_multiplyier).toBigInteger());
									if(backToWallet.doubleValue() > 0) {
										tx.addOutput(backToWallet,outputs.get(i).getScriptPubKey().getToAddress(activeWallet.getNetworkParameters()));
										tx.getInput(tx.getInputs().size() - 1).setSequenceNumber((tx.getOutputs().size() -1) | Integer.MIN_VALUE);
									}
									// since were color lets add the sequence for color
									//AddColorToTx(tx, alInputs ,alOuputs);
									
									return true;
								}																
							}
							 else if(asset == null) { // none colored asset maybe this is what we want?
								 totalgive = totalgive.add(outputs.get(i).getValue());								
								 tx.addInput(outputs.get(i));
									
									System.out.println("transaciont:\n" + tx.toString());
									
									// passed the mark now make sure to set the outputs accordingly
									System.out.println("totalgive: " + totalgive);
									System.out.println("outputs.get(i).getValue(): " + outputs.get(i).getValue());
									if(totalgive.compareTo(Utils.toNanoCoins(amountString)) >= 0){
										tx.addOutput(Utils.toNanoCoins(amountString), toAdress);
										
										BigInteger backToWallet = totalgive.subtract(Utils.toNanoCoins(amountString) /*.add(tx.calculateFee(from)) */ );
										if(backToWallet.doubleValue() > 0) {
											if(backToWallet.compareTo(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE) > 0)
												backToWallet.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
											tx.addOutput(backToWallet,outputs.get(i).getScriptPubKey().getToAddress(activeWallet.getNetworkParameters()));
										}
										return true; // donep
										
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

	private void logMessage(Exception e) {
        e.printStackTrace();
        String errorMessage = controller.getLocaliser().getString("sendBitcoinNowAction.bitcoinSendFailed");
        String detailMessage = controller.getLocaliser().getString("deleteWalletConfirmDialog.walletDeleteError2", new String[]{e.getClass().getCanonicalName() + " " + e.getMessage()});
        MessageManager.INSTANCE.addMessage(new Message(errorMessage + " " + detailMessage));
    }
}