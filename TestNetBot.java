import java.io.*;
import java.util.*;
public class TestNetBot{
	public static long start;
	public static void main(String []args)throws Exception {
		DBServers dbs =DBServers.getDBServers();
		dbs.clear();
		ReferenceTable rt = ReferenceTable.getReferenceTable();
		rt.clear();
		NetNavigator.MAX_TRANSACTION_ITEMS = 15;

		dbs.insert("localhost", "supermarket1", "transaction",  "root", "sanar");
	//	dbs.insert("localhost", "supermarket2", "transaction", "root", "sanar");
	//	dbs.insert("localhost", "supermarket3", "transaction", "root", "sanar");
		NetBot netBot = NetBot.getNetBot();
		netBot.reset();
		start = System.currentTimeMillis();
		netBot.start();
	//	netBot.exit();
	}
}