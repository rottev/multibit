package etx.com.trading;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.table.AbstractTableModel;

import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTrading.Proposal;

public class BaseProposalModel extends AbstractTableModel {
	
	private BaseTrading model = BaseTrading.getInstance();
	private List<Proposal> dataSource = null;
	
	public BaseProposalModel()
	{
		/*
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			  public void run() {
				dataSource = model.getAssetList();
				BaseProposalModel.this.fireTableStructureChanged();
				BaseProposalModel.this.fireTableDataChanged();				
			  };
		});
			*/
		ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
		ex.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				dataSource = model.getProposals();
				BaseProposalModel.this.fireTableStructureChanged();
				BaseProposalModel.this.fireTableDataChanged();				
			}
		}, 1, 1, TimeUnit.SECONDS);
		
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
	/*
	public Proposal getProposalById(final String Id){
		int index = dataSource.indexOf(new Asset() { {this.id = Id; } } );
		if(index >= 0)
			return dataSource.get(index);
		return null;
	}
	*/
	public Proposal getProposalAtRow(int row){		
		if(row >= 0)
			return dataSource.get(row);
		return null;
	}

}
