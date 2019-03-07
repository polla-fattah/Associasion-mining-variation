import java.util.*;
import java.io.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class RuleGenerator{

	private StringBuffer bufferItemsets;
	private int currentK;

	private Connection con= null;
	private Statement stmt = null;
	private PrintWriter out = null;

	public RuleGenerator(){
 		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection ("jdbc:mysql://localhost/frequencybase?user=root&password=sanar");
			stmt = con.createStatement ();
			FileWriter writer = new FileWriter( "outfile.txt" );
			out=new PrintWriter(writer, true);
		}
		catch(SQLException sqlEx ){
			sqlEx.printStackTrace ();
			JOptionPane.showMessageDialog (null,"Problem Occurs while trying to connect and open FrequencyBase");
			System.exit (1);
		}
		catch(Exception ex){
			ex.printStackTrace ();
			JOptionPane.showMessageDialog (null,"System Error Database Driver Missing!");
			System.exit (1);
		}
	}

	private void putNewItemSet(TreeMap newItemSet, String subsets[], ItemSet superSet){
		TreeSet link = null;

		for(int index = 0 ; index < subsets.length ; index++){
			if(!newItemSet.containsKey(subsets[index])){
				bufferItemsets.append( "'" + subsets[index] + "', ");
				link = new TreeSet();
				link.add(superSet);

				newItemSet.put(subsets[index], link );
			}
			else{
				link = (TreeSet)newItemSet.get(subsets[index]);
				link.add(superSet);
			}
		}
	}

	private TreeMap firstSubsets(TreeMap itemSet){
		String set = null;
		TreeMap newItemSet = new TreeMap();
		Map.Entry currentSet = null;
		String []subsets;
		TreeSet superSets;
		bufferItemsets = new StringBuffer();
		for (Iterator i = itemSet.entrySet().iterator() ;  i.hasNext(); ) {
			currentSet = (Map.Entry) i.next ();
			set = (String)currentSet.getKey ();
			superSets = (TreeSet)currentSet.getValue();
			subsets = ItemSet.firstSubitemsets(set);

			for (Iterator j = superSets.iterator(); j.hasNext();)
				putNewItemSet(newItemSet, subsets, (ItemSet)j.next());
		}//end for(Iterator
		bufferItemsets.deleteCharAt(bufferItemsets.lastIndexOf(","));
		itemSet.clear();
		return newItemSet;
	}

	private void generateRule(TreeMap itemSet,double minConf ){
		if(--currentK == 0){
			out.flush();
			return;
		}

		TreeMap subsets = firstSubsets(itemSet);

		ItemSet temp = null;
		TreeSet link = null;
		String str = "";
		double conf = 0;
		long fre = 0 ;

		try{
			ResultSet rs = stmt.executeQuery("Select subset,frequency from itemset_" + currentK + " Where subset IN ("+bufferItemsets+");");
			while(rs.next()){
				str =rs.getString("subset");
				fre = rs.getLong("frequency");
				link = (TreeSet)subsets.get(str);
				for(Iterator i = link.iterator(); i.hasNext();){
					temp = (ItemSet)i.next();
					conf = (double)temp.getSupportCount()/fre;
					if(conf >= minConf){
						out.println(temp.creatRule(str, (long)(conf * 10000)));
					}
					else{
						i.remove();
						if(link.size() == 0)
							subsets.remove(str);
					}
				}
			}
		}
		catch(Exception sqlEx ){
			sqlEx.printStackTrace ();
			JOptionPane.showMessageDialog (null,"Sudden Peoblem");
			System.exit (1);
		}
		itemSet.clear();
		if(subsets.size()>0)
			generateRule(subsets,minConf);
		else
			out.flush();
	}
	protected void finalize(){
		out.close();
	}


	public void getRules(TreeMap itemSet, int k, double minConf){
		currentK = k;
		generateRule(itemSet,minConf);
	}
}