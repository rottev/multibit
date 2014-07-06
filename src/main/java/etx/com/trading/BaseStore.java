package etx.com.trading;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.multibit.ApplicationDataDirectoryLocator;
import org.multibit.message.Message;
import org.multibit.message.MessageManager;

import com.google.bitcoin.store.BlockStoreException;
// TODO: add db update proceduers and version
public class BaseStore {
	 private ThreadLocal<Connection> conn;
	 static final String driver = "org.h2.Driver";
	 private List<Connection> allConnections;
	 private String connectionURL;
	 static final String PROPOSALS_TABLE_NAME = "proposals";
	 static final String TX_TABLE_NAME = "transactions";
	 
	  static final String CREATE_PORPOSALS_TABLE = "CREATE TABLE " + PROPOSALS_TABLE_NAME +" ( "
		        + "hash VARCHAR(64) NOT NULL CONSTRAINT proposals_pk PRIMARY KEY,"
		        + "proposal BLOB,"
		        + "state INT NOT NULL DEFAULT 0"
		        + ")";
	  
	  static final String CREATE_VERSION_TABLE = "CREATE TABLE version ( "
		        + "version DECIMAL NOT NULL CONSTRAINT version_pk PRIMARY KEY"
		        + ")";
	 
	 
	 public BaseStore(String dbName) throws SQLException
	 {
		 ApplicationDataDirectoryLocator path = new ApplicationDataDirectoryLocator();
		 
		   connectionURL = "jdbc:h2:tcp://localhost/" + path.getApplicationDataDirectory() + "/" + dbName + ";create=true;LOCK_TIMEOUT=60000";
	        
	        conn = new ThreadLocal<Connection>();
	        allConnections = new LinkedList<Connection>();

	        try {
	            Class.forName(driver);
	            System.out.println(driver + " loaded. ");
	        } catch (java.lang.ClassNotFoundException e) {
	        	 System.err.println("check CLASSPATH for H2 jar ");
	        	 e.printStackTrace();
	        }
	        
	        maybeConnect();
	        if(!tableExists(PROPOSALS_TABLE_NAME))
	        {
	        	 Statement s = conn.get().createStatement();
	             s.executeUpdate(CREATE_PORPOSALS_TABLE);
	             s = conn.get().createStatement();
	             s.executeUpdate(CREATE_VERSION_TABLE);
	        }
	        else if(updateReqired()){
	        	
	        }
	        
	        
	 }
	 
	  private boolean updateReqired() {
		return false;
	}

	private synchronized void maybeConnect() throws SQLException {
	        try {
	            if (conn.get() != null)
	                return;
	            
	            conn.set(DriverManager.getConnection(connectionURL));
	            allConnections.add(conn.get());
	            System.out.println("Made a new connection to database " + connectionURL);
	            Message message = new Message("Connected to local db", 100);
	            MessageManager.INSTANCE.addMessage(message);
	        } catch (SQLException ex) {
	            Message message = new Message(ex.getMessage(), 100);
	            MessageManager.INSTANCE.addMessage(message);
	            throw new SQLException(ex);

	        }
	    }
	  
	  private boolean tableExists(String table) throws SQLException {
	        Statement s = conn.get().createStatement();
	        try {
	            ResultSet results = s.executeQuery("SELECT * FROM " + table + " WHERE 1 = 2");
	            results.close();
	            return true;
	        } catch (SQLException ex) {
	            return false;
	        } finally {
	            s.close();
	        }
	    }
	  
	  public boolean isMyProposal(String key) throws SQLException
	  {
		  PreparedStatement s = conn.get().prepareStatement("SELECT * FROM " + PROPOSALS_TABLE_NAME + " WHERE hash = ?");
		try {
			
            s.setString(1, key);
            ResultSet results = s.executeQuery();
			 if(results.next())
				 return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			s.close();
		}

		  return false;
	  }
	  
	  public boolean isMyProposalStillNotFufilled(String key) throws SQLException
	  {
		  PreparedStatement s = conn.get().prepareStatement("SELECT * FROM " + PROPOSALS_TABLE_NAME + " WHERE hash = ? AND state = 0");
		try {
			
            s.setString(1, key);
            ResultSet results = s.executeQuery();
			 if(results.next())
				 return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			s.close();
		}

		  return false;
	  }
	  
	  public boolean setProposalFufilled(String Key) throws SQLException
	  {
		  PreparedStatement s = conn.get().prepareStatement("UPDATE " + PROPOSALS_TABLE_NAME + " SET state = ? WHERE hash = ?");
			try {
				s.setInt(1, 1);
	            s.setString(2, Key);
	            s.executeUpdate();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				s.close();
			}
			return false;
	  }

	  public void addProposal(String key, String data) throws SQLException
	  {
		  PreparedStatement s = conn.get().prepareStatement("INSERT INTO proposals(hash, proposal) VALUES(?,?)");
		  try {
			 ByteArrayInputStream bais = new ByteArrayInputStream( data.getBytes() );
			  SerialBlob blob = new SerialBlob(data.getBytes());
			  s.setString(1, key);
			  s.setBlob(2, bais);
	          s.executeUpdate();
	          s.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				s.close();
			}

	  }
	  
	  public byte[] getProposal(String key) throws SQLException
	  {
		  Statement s = conn.get().createStatement();
		  try {
			  ResultSet results = s.executeQuery("SELECT * FROM proposals WHERE hash = '" + key + "'"  );
			  if(results.next()) {
				  Blob b =results.getBlob("proposal");
				  return b.getBytes(0, (int) b.length());
			  }

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				s.close();
			}
		return null;

	  }
}
