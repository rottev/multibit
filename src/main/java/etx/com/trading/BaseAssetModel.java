package etx.com.trading;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.*;
import javax.swing.table.AbstractTableModel;

import etx.com.trading.BaseTrading.Asset;

public class BaseAssetModel extends AbstractTableModel {
	
	private BaseTrading model = BaseTrading.getInstance();
	private List<Asset> dataSource = null;

     


	public BaseAssetModel()
	{
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			  public void run() {
				dataSource = model.getAssetList();
				if(dataSource != null) {
					BaseAssetModel.this.fireTableStructureChanged();
					BaseAssetModel.this.fireTableDataChanged();				
				}
			  };
		});
			
		
		
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return dataSource == null ? 0 : (dataSource.size() == 0 ? 0 : dataSource.get(0).getColumnNames().length);
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return dataSource == null ? 0 : dataSource.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return dataSource.get(arg0).getColumnByInedx(arg1);
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		return dataSource == null ? null : dataSource.get(0).getColumnNames()[columnIndex];
	}
	
	public Asset getAssetById(final String Id){
		int index = dataSource.indexOf(new Asset() { {this.id = Id; } } );
		if(index >= 0)
			return dataSource.get(index);
		return null;
	}
	
	public Asset getAssetAtRow(int row){		
		if(row >= 0)
			return dataSource.get(row);
		return null;
	}
	
	public void Refresh()
	{
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			  public void run() {
				dataSource = model.getAssetList(true);
				if(dataSource != null) {
					//BaseAssetModel.this.fireTableStructureChanged();
					BaseAssetModel.this.fireTableDataChanged();				
				}
			  };
		});
	}

}
