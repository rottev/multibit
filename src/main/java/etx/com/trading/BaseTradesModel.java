package etx.com.trading;

import java.util.List;
import java.util.concurrent.Executors;

import javax.swing.table.AbstractTableModel;

import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTrading.Proposal;

public class BaseTradesModel extends AbstractTableModel {
	
	private BaseTrading model = BaseTrading.getInstance();
	private List<Proposal> dataSource = null;
	
	public BaseTradesModel()
	{
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			  public void run() {
				dataSource = model.getProposals();
				BaseTradesModel.this.fireTableStructureChanged();
				BaseTradesModel.this.fireTableDataChanged();				
			  };
		});
			
		
		
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return dataSource == null ? 0 : dataSource.get(0).getColumnNames().length;
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
	
	public Proposal getAssetById(final String Id){
		int index = dataSource.indexOf(new Proposal() { {this.id.equals(Id); } } );
		if(index >= 0)
			return dataSource.get(index);
		return null;
	}
	
	public Proposal getAssetAtRow(int row){		
		if(row >= 0)
			return dataSource.get(row);
		return null;
	}

}
