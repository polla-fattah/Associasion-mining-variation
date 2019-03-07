/**
*	File Name : NetBot.java
*	Class Purpose: This class stores itemsets and there frequencies in the database,
*		according to the length of the itemset it will save it in deferent tables for later use.
*	Last Modified: 4/11/2007
*	Author: Polla A. Fattah
*/

import java.util.*;
import java.io.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class NetBot extends Thread{

	private static NetBot netBot;	//the only instantiation at a time

	private ItemsetGenerator itemsetGenerator;	//to get subsets and their frequencies


	private Connection con= null;
	private Statement stmt = null;

	/** Creates a new instance of NetBot it is private to prevent out side class instantiation */
	private NetBot (){
		super("Netbot");
		setPriority(7);
 		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection ("jdbc:mysql://localhost/frequencybase?user=root&password=sanar");
			stmt = con.createStatement ();
		}
		catch(SQLException sqlEx ){
			sqlEx.printStackTrace ();
			JOptionPane.showMessageDialog (null,"Problem Occurs while trying to connect and open FrequencyBase");
			System.exit (1);
		}
		catch(Exception ex){
			ex.printStackTrace ();
			JOptionPane.showMessageDialog (null,"System Error Database Driver Missing !");
			System.exit (1);
		}

		setFrequencyBase();
		itemsetGenerator = ItemsetGenerator.getItemsetGenerator();
	}

	/**
	*	creates all tables for the frequency base
	*/
	private void setFrequencyBase(){
		try{
			for(int i = 1; i <= NetNavigator.MAX_TRANSACTION_ITEMS ; i++){
				stmt.executeUpdate ("CREATE TABLE IF NOT EXISTS itemset_"+ i +
									" (" +
										" subset VARCHAR(80) NOT NULL, "+
										" frequency INTEGER UNSIGNED  NOT NULL, "+
										" PRIMARY KEY(subset)" +
									" );"
									);

			}//end of for
		}
		catch(SQLException sqlEx ){
			sqlEx.printStackTrace ();
			JOptionPane.showMessageDialog (null,"Problem Occurs while trying to connect and open FrequencyBase");
			System.exit (1);
		}
	}


	/**
	*	This method initializes an starts an instant of NetBot then returns
	*	it if it called twice with out calling exit between them it will throw NullPointerException
	*	@throws NullPointerException
	*	@return NetBot
	*/
	public static NetBot getNetBot(){
		if(netBot == null){
			netBot = new NetBot();
			return netBot;
		}
		else{
			throw new NullPointerException("FBWriter cant be loaded more than once");
		}
	}
	/**
	*	clears all tables of frequencyBace
	*/
	public void reset(){
		for(int i = 1 ;i <= NetNavigator.MAX_TRANSACTION_ITEMS ; i++){
			try{
				stmt.executeUpdate ("DROP TABLE IF EXISTS itemset_"+i );
			}
			catch(SQLException sqlEx){
				sqlEx.printStackTrace ();
				JOptionPane.showMessageDialog (null,"Problem Occurs while trying to reset the NetBot");
			}
		}
		setFrequencyBase();
	}

	/**
	*	overrides run of the Tread class. This method gets subsets and write them to frequencybase
	*/
	public void run(){
		KItemset itemset = null;
		while(true){
			try{
				itemset = itemsetGenerator.getItemset();
			}
			catch(Exception e){break;}
			System.out.println(itemset.k);
			write(itemset.subsets, itemset.k);
		}//end of while(true)

		try{
			stmt.close ();
			con.close ();
			con = null;
		}
		catch(SQLException sqlEx){
			sqlEx.printStackTrace();
		}
		System.out.println((System.currentTimeMillis() - TestNetBot.start)/1000.0);
	}

	/**
	*	this method writes itemsets to the frequencybase with this method
	*	1- first queries k's table of the frequencybase for fetching old itemsets frequencies
	*	2- adds old frequencies to new one.
	*	3- deletes old frequencies at the k's table
	*	4-inserts new frequencies
	*
	*	@param itemSet : a set of all flushed subsets with the same order
	*	@param k: order (number of items) of the subsets
	*/
	private void write(TreeMap itemSet, int k){
		Long frequency;
		Map.Entry currentSet;
		String set = "";
		ResultSet rs = null;

		StringBuffer setsBuffer = new StringBuffer("(");

		for (Iterator i = itemSet.entrySet().iterator() ;  i.hasNext(); ) {
			currentSet = (Map.Entry) i.next ();
			set = (String)currentSet.getKey ();

			setsBuffer.append("'" + set + "', ");
		}
		setsBuffer.setCharAt(setsBuffer.lastIndexOf(","), ')');

		try{
			Long oldFreq;
			rs = stmt.executeQuery ("SELECT subset,frequency from itemset_" + k + " where subset IN " + setsBuffer + ";");
			while (rs.next() ) {
				set = rs.getString("subset");
				oldFreq = (Long)itemSet.get(set);
				itemSet.put(set,oldFreq + rs.getLong("frequency"));
			}
			stmt.executeUpdate("Delete from itemset_" + k + " where subset IN" + setsBuffer + ";");
		}
		catch(SQLException sqlex){
			sqlex.printStackTrace ();
		}

		setsBuffer = new StringBuffer("");

		for (Iterator i = itemSet.entrySet().iterator() ;  i.hasNext(); ) {
			currentSet = (Map.Entry) i.next ();
			set = (String)currentSet.getKey ();
			frequency = (Long)currentSet.getValue ();

			setsBuffer.append(" ('" + set + "', " + frequency + "),");

		}//end of for

		setsBuffer.setCharAt(setsBuffer.lastIndexOf(","), ';');

		try{
			//System.out.println(setsBuffer);
			stmt.executeUpdate ("INSERT INTO itemset_" + k + "  VALUES " + setsBuffer);
		}
		catch(SQLException sqlEx){
			sqlEx.printStackTrace ();
		}

	}//end of write

	protected void finalize(){
		try{
			stmt.close ();
			con.close ();
		}
		catch(SQLException sqlEx){
			sqlEx.printStackTrace();
		}
	}
}
