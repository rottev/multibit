package etx.com.trading;

import etx.com.trading.BaseTrading.Fufilment;

public interface FufilEventListener {
	public void OnNewFufilment(Fufilment f);
}
