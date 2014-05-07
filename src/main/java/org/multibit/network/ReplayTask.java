package org.multibit.network;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.multibit.message.Message;
import org.multibit.model.bitcoin.WalletData;

import com.google.bitcoin.core.Transaction;

import etx.com.trading.ColoredPeerEventListener;

/**
 * A class encapsulating a request on one or more wallets to perform a
 * blockchain replay
 */
public class ReplayTask {
    
    public static final int UNKNOWN_START_HEIGHT = -1;

    private final List<WalletData> perWalletModelDataToReplay;
    
    /**
     * The start date of the replay task.
     */
    private final Date startDate;
    
    /**
     * The height the blockchain needs to be truncated to.
     */
    private int startHeight;
  
    /**
     * A UUID identifying this replay task.
     */
    private final UUID uuid;
    
    /**
     * The percent complete as reported by the downloadlistener.
     */
    private long percentComplete;
    
    private String followTx;
    private int outIndex;
    
  //  private List<Transaction> reAddList;
    
    private ColoredPeerEventListener listener = null;
         
    private final Map<String, Integer> colorTransMap = new HashMap<String, Integer>();
    
    public ReplayTask( List<WalletData> perWalletModelDataToReplay, Date startDate, int startHeight) {
        this.perWalletModelDataToReplay = perWalletModelDataToReplay;
        this.startDate = startDate;
        this.startHeight = startHeight;
        this.percentComplete = Message.NOT_RELEVANT_PERCENTAGE_COMPLETE;
        this.uuid = UUID.randomUUID();
    }
    
    public ReplayTask( List<WalletData> perWalletModelDataToReplay, Date startDate, int startHeight, String txToFollow, int Index) {
      this(perWalletModelDataToReplay, startDate, startHeight);
      this.followTx = txToFollow;
      this.outIndex = Index;
      colorTransMap.put(this.followTx, this.outIndex);
    }

    public List<WalletData> getPerWalletModelDataToReplay() {
        return perWalletModelDataToReplay;
    }

    public Date getStartDate() {
        return startDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((perWalletModelDataToReplay == null) ? 0 : perWalletModelDataToReplay.hashCode());
        result = prime * result + (int) (percentComplete ^ (percentComplete >>> 32));
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + startHeight;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ReplayTask))
            return false;
        ReplayTask other = (ReplayTask) obj;
        if (perWalletModelDataToReplay == null) {
            if (other.perWalletModelDataToReplay != null)
                return false;
        } else if (!perWalletModelDataToReplay.equals(other.perWalletModelDataToReplay))
            return false;
        if (percentComplete != other.percentComplete)
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (startHeight != other.startHeight)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ReplayTask [perWalletModelDataToReplay=" + perWalletModelDataToReplay + ", startDate=" + startDate
                + ", startHeight=" + startHeight + ", uuid=" + uuid
                + ", percentComplete=" + percentComplete + "]";
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(long percentComplete) {
        this.percentComplete = percentComplete;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public String getFollowingTx(){
     return followTx;
    }
    
    public int getFolloingTxIndex()
    {
    	return outIndex;
    }
    
    public Map<String, Integer> getColorMap()
    {
    	return colorTransMap;
    }
    
    
    public void setListener(ColoredPeerEventListener l)
    {
    	listener= l;
    }
    
    public ColoredPeerEventListener getListener()
    {
    	return listener;
    }
    
    public void setStartHeight(int startHeight) {
        this.startHeight = startHeight;
    }
}
