package etx.com.trading;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.table.AbstractTableModel;

import etx.com.trading.BaseTrading.Asset;
import etx.com.trading.BaseTrading.Proposal;

public class BaseTradesModel extends AbstractTableModel {
	
	private BaseTrading model = BaseTrading.getInstance();
	private List<Proposal> dataSource = null;
	private Object lock = new Object();
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
  	final Runnable updateTrading = new Runnable() {
       public void run() { 
    	// TODO Auto-generated method stub
			boolean updated = false;
			boolean datachanged = dataSource.size() == 0;
			System.out.println("checking for new proposals ");
			model.getMessages();
			synchronized (lock) {
				
				System.out.println("model has: " + model.getProposals().size() + " dataSource has: " + dataSource.size());
				for(Proposal p : model.getProposals()) {
					if(!dataSource.contains(p)) {
						System.out.println("Adding proposal " + p.toString());
						dataSource.add(p);
						updated = true;
					}
				}
				if(updated && datachanged)
					BaseTradesModel.this.fireTableStructureChanged();
				if(updated) {
					BaseTradesModel.this.fireTableDataChanged();
					System.out.println("fireTableDataChanged fo new proposal ");
				}
				
			}
       }
     };
     
    private ScheduledFuture<?> handler;
	
	public BaseTradesModel()
	{
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			  public void run() {
				model.getMessages();
				synchronized (lock) {
					dataSource = new ArrayList<Proposal>();
					dataSource.addAll(model.getProposals());
					BaseTradesModel.this.fireTableStructureChanged();
					BaseTradesModel.this.fireTableDataChanged();
					System.out.println("BaseTradesModel done loading");
					handler =  scheduler.scheduleWithFixedDelay(updateTrading, 1, 1, SECONDS);
				}
			  };
		});
		/*
		ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
		ex.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean updated = false;
				boolean datachanged = dataSource.size() == 0;
				System.out.println("checking for new proposals ");
				model.getMessages();
				synchronized (lock) {
					
					System.out.println("model has: " + model.getProposals().size() + " dataSource has: " + dataSource.size());
					for(Proposal p : model.getProposals()) {
						if(!dataSource.contains(p)) {
							System.out.println("Adding proposal " + p.toString());
							dataSource.add(p);
							updated = true;
						}
					}
					if(updated && datachanged)
						BaseTradesModel.this.fireTableStructureChanged();
					if(updated) {
						BaseTradesModel.this.fireTableDataChanged();
						System.out.println("fireTableDataChanged fo new proposal ");
					}
					
				}
			}
		}, 10, 1, TimeUnit.SECONDS);
			
		*/
		
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		synchronized (lock) {
			return dataSource == null ? 0 : (dataSource.size() == 0 ? 0 : dataSource.get(0).getColumnNames().length);
		}
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		synchronized (lock) {
			return dataSource == null ? 0 : dataSource.size();
		}
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		synchronized (lock) {
			Object o = null;
			try{
				o = dataSource.get(arg0).getColumnByInedx(arg1);
			}
			catch(Exception ex)
			{
				
			}
			return o;
		}
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		return dataSource == null ? null : dataSource.get(0).getColumnNames()[columnIndex];
	}
	/*
	public Proposal getProposalById(final String Id){
		int index = dataSource.indexOf(new Proposal() { {this.id.equals(Id); } } );
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
