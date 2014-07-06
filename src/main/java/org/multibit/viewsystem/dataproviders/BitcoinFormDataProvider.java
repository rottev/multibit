package org.multibit.viewsystem.dataproviders;

import etx.com.trading.BaseTrading.Asset;


/**
 * DataProvider for send bitcoin and send bitcoin confirm action
 * @author jim
 *
 */
public interface BitcoinFormDataProvider extends DataProvider { 
    /**
     * Get the address
     */
    public String getAddress();
    
    /**
     * Get the label
     */
    public String getLabel();
    
    /**
     * Get the amount (denominated in BTC).
     */
    public String getAmount();
    
    /**
     * Get the amount (denominated in fiat)
     */
    public String getAmountFiat();
    
    public boolean isBTC();
    
    public Asset getColorAsset();
}
