import java.io.*;
import java.util.*;

public class TestIG{
	public static void main(String []args)throws Exception {
		DBServers dbs = DBServers.getDBServers();
		dbs.clear();
		dbs.insert("localhost", "supermarket", "transaction", "root", "sanar");
		dbs.insert("localhost", "supermarket2", "transaction", "root", "sanar");
	//	dbs.reset();
		String set = "";
		Integer frequency;
		ItemsetGenerator ig = ItemsetGenerator.getItemsetGenerator();
		Map.Entry currentSet = null;
		int v = 1;
		while(true){
			try{
				KItemset ki = (KItemset)ig.getItemset();
				System.out.println("--------------------------------------------------------------");
				System.out.println(ki.k);
				v=1;
				for (Iterator i = ki.subsets.entrySet().iterator() ;  i.hasNext(); ) {

					currentSet = (Map.Entry) i.next ();
					set = (String)currentSet.getKey ();
					frequency = (Integer)currentSet.getValue ();
					if(frequency > 5 && ki.k > 4)
						System.out.println(v + "- " +set +" --> "+frequency);
					v++;
				}

			}
			catch(Exception e){break;}
		}

	}
}