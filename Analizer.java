import java.util.*;
import java.io.*;
import java.sql.*;
import javax.swing.*;

public class Analizer
{
	private Connection con;
	private Statement stmt;

	private RuleGenerator rg;
	private final int BUFFER_SIZE = 100;
	private double allItemsCount;
	private ReferenceTable rt = ReferenceTable.getReferenceTable();

	public Analizer(){
		DBServers dbs = DBServers.getDBServers();
		rg = new RuleGenerator();
		allItemsCount = dbs.getItemSetCount();
		ItemSet.allItemsets = allItemsCount;
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
	}
	public int itemsetCount(int k, double minSup){
		int count = 0;
		try{
			double minCount = minSup * allItemsCount;
			ResultSet rs = stmt.executeQuery("Select count(frequency) as itemCount from itemset_" + k +"  Where frequency >= "+minCount);
			rs.next();
			count = rs.getInt("itemCount");
		}
		catch(SQLException sqlEx ){
			sqlEx.printStackTrace ();
			JOptionPane.showMessageDialog (null,"Problem Occurs while trying to connect and open FrequencyBase");
			System.exit (1);
		}
			return count;
	}
/*	public void simulateApriory(double minSup,double minConf){
		double minCount = minSup * allItemsCount;
		for(int k = NetNavigator.MAX_TRANSACTION_ITEMS; k >1 ; k--)
			if(itemsetCount(k,minSup) >= minCount){
				strongAssociationRules(k,minSup, minConf);
				break;
			}
	}
*/
	public void simulateApriory(double minSup,double minConf){
		double minCount = minSup * allItemsCount;
		for(int k = 2; k <= NetNavigator.MAX_TRANSACTION_ITEMS ; k++)
			if(itemsetCount(k,minSup) > 0)
				strongAssociationRules(k ,minSup, minConf);
			else
				break;
	}

	public void selectiveGroup(String[] items){
		StringBuffer strItems = new StringBuffer("");
		TreeSet itemset = new TreeSet();
		try{
			for(int i = 0 ; i < items.length ; i++)
				itemset.add(rt.getCode(items[i]));
			for(Iterator i = itemset.iterator(); i.hasNext();)
				strItems.append((Short)i.next()+",");
		}
		catch(ItemNotFoundException infex)
		{
			System.out.println("one of the provided items not exist.");
		}
		System.out.println("Select subset,frequency from itemset_" + items.length +"  Where subset = "+ strItems );
		try{
			ResultSet rs = stmt.executeQuery ("Select subset,frequency from itemset_" + items.length +"  Where subset = '"+ strItems +"'");

			if(rs.next())
			{
				TreeSet link = new TreeSet();
				link.add(new ItemSet(rs.getString("subset"), rs.getLong("frequency")));
				TreeMap buffer = new TreeMap();
				buffer.put(rs.getString("subset"), link);

				rg.getRules(buffer, items.length,0 );
			}
			else {
				System.out.println("This group is not exist.");
			}
		}
		catch(SQLException e){e.printStackTrace();}

	}
	public void strongAssociationRules(int k, double minSup,double minConf){

		ResultSet rs = null;

		int elementNumer = 0;

		TreeSet link;
		TreeMap buffer = new TreeMap();
		double minCount = minSup * allItemsCount;
		System.out.println("=============================="+minCount);
		String sql = "Select subset,frequency from itemset_" + k +"  Where frequency >= "+minCount;
		long freq = 0;
		String temp;
		try{
			rs = stmt.executeQuery (sql);

			while(rs.next()){
				temp = rs.getString("subset");
				freq = rs.getLong("frequency");
		//		System.out.println(temp +" : " +freq);
				link = new TreeSet();
				link.add(new ItemSet(temp, freq));
				buffer.put(temp, link);
				//System.out.println(its);
				elementNumer++;
				if(elementNumer == BUFFER_SIZE){
					elementNumer = 0;
					rg.getRules(buffer,k,minConf);
					buffer.clear();
					//break;
				}
			}//end of while(rs)
			if(elementNumer !=0)
				rg.getRules(buffer,k,minConf);
		}
		catch(SQLException e){e.printStackTrace();}
	}

	public static void main(String str[])throws IOException{
		Analizer an = new Analizer();
		long start = System.currentTimeMillis();
	//	an.strongAssociationRules(2, 0.05,0.4);
	//	an.selectiveGroup(new String[]{ "1933", "1179", "1367"});
		an.simulateApriory(0.005,0.50);
		System.out.println((System.currentTimeMillis() - start)/1000.0);
	}
}