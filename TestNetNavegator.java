import java.io.*;
public class TestNetNavegator{
	private static ReferenceTable rt;
	public static void main(String []args)throws Exception {
		DBServers dbs = DBServers.getDBServers();
		dbs.clear();
		dbs.insert("localhost", "supermarket", "transaction", "root", "sanar");
		dbs.insert("localhost", "supermarket2", "transaction", "root", "sanar");
/*		int loop = dbs.size();
		for(int i = 0; i < loop ; i++){
			System.out.println(dbs.getServerName(i));
		}
*/
		rt = ReferenceTable.getReferenceTable();
		rt.clear();
		NetNavigator nn = NetNavigator.getNetNavigator();


		int i = 0;
		while(true){
			i++;
			try{
			//	System.out.println(i +" - "+str(nn.getTransaction()));
				String s = str(nn.getTransaction());
				if(i>100)nn.exit();
			}catch(Exception e){break;}
		}
	}

	public static String str(short a[]){
		String s = "";
		try{
			for(int i = 0 ; i < a.length ; i++){
				s += rt.getItem(a[i]);
			}
		}catch(Exception e){}
		return s;
	}
}

