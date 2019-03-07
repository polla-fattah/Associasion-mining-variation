/**
*	File Name :NetNavigator.java
*	Class Purpose: This Class has capability to connect to the servers, then fetching data from these servers and
*		 group them as transaction in arrays it has capability for connecting to multiple servers but one server at a time.
*		also it saves the last record fetched at the last scan this enables the program to start from this point at the next scan.
*	Author: Polla A. Fattah
*	Last Modified : 4/11/2007.
*/


import java.util.*;
import java.io.*;
import java.sql.*;
import javax.swing.*;

public class NetNavigator extends Thread
{
	private CircularBuffer buffer;	//for holding transactions

	private ReferenceTable refTable;
	private DBServers dbs;

	private static NetNavigator netNavigator;//the only instantiation at a time

	public static int MAX_TRANSACTION_ITEMS = 15;//the maximum elements of a transaction

	private Connection con= null;
	private Statement stmt = null;

	private long currentId;

	/**
	*	Creates a new instance of NetNavigator. It is private to prevent out side class instantiation
	*/
	private NetNavigator(){
		setPriority(7);
		final int BUFFER_SIZE = 100;
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch(Exception ex){
			ex.printStackTrace ();
			JOptionPane.showMessageDialog (null,"System Error Database Driver Missing !");
			System.exit (1);
		}
		buffer = new CircularBuffer(BUFFER_SIZE);
		refTable = ReferenceTable.getReferenceTable();
		dbs = DBServers.getDBServers();
	}

	/**
	*	This method initializes an starts an instant of NetNavigator then returns
	*	it if it called twice with out calling exit between them it will throw NullPointerException
	*	@throws NullPointerException
	*	@return NetNavigator
	*/
	public static NetNavigator getNetNavigator(){
		if(netNavigator == null){
			netNavigator = new NetNavigator();
			netNavigator.start();
			return netNavigator;
		}
		else{
			throw new NullPointerException("NetNavigator cant be loaded more than once");
		}
	}

	/**
	*	overrides run method of the Thread class. It scans database servers
	*	from last stopped location, groups every transaction in an array of short
	*/
	public void run(){
		ResultSet rs = null;
		TreeSet transaction = new TreeSet();
		final int  FETCH_SIZE = 5000;

		int loop =dbs.size();

		long lastStop = 0,lastBolck = -1 ;
		for(int i = 0; i < loop; i++){
			currentId = -1;
			lastBolck = -1;
			try{
				con = DriverManager.getConnection (dbs.getServerUrl(i));
				stmt = con.createStatement ();
				String tableName = dbs.getTable(i);
				lastStop = dbs.getCurser(i);
				while(lastBolck != lastStop){
					rs = stmt.executeQuery ("SELECT tid,items FROM " + tableName + " ORDER BY tid LIMIT " + lastStop +", "+ FETCH_SIZE);
					lastBolck = lastStop;
					while(rs.next ()){
						lastStop++;
						addItem(transaction, rs.getString ("items"), rs.getLong ("TID"));

					}//end of while(rs.next ())

				}
			}
			catch(SQLException sqlEx){
				sqlEx.printStackTrace ();
				JOptionPane.showMessageDialog (null,"Fetching data can not continue in ["+dbs.getServerName(i)+"]\n NetNavigator Proceeds to the next Database.");
			}
			buffer.insert(transaction.toArray());


			dbs.setCurser(i, lastStop);
			refTable.save();

		}//end of for (i)

		buffer.insert("End");
		System.gc();

    }//End of Run

	private void addItem(TreeSet transaction, String item, long id){
		if(id == currentId){
			transaction.add(refTable.getCleanCode(item) );
		}
		else{

			buffer.insert(transaction.toArray());
			transaction.clear();
			currentId = id;
			transaction.add(refTable.getCleanCode(item) );
		}
	}

	/**
	*	@return every transaction which its elements < MAX_TRANSACTION_ITEMS
	*/
	public short[] getTransaction(){
		Object temp [] = null;
		do{
			temp =(Object[])buffer.remove();
		}while(temp.length >MAX_TRANSACTION_ITEMS || temp.length == 0);
		dbs.incItemset();
		short transaction [] =new short[temp.length];

		for(int i = 0 ; i < transaction.length ; i++)
			transaction [i] = ((Short)temp[i]).shortValue();

		return transaction;
	}

	protected void finalize(){
		try{
			stmt.close();
			con.close();
		}
		catch(SQLException sqlex){sqlex.printStackTrace();}
	}
}//end of class
