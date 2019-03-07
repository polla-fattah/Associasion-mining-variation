/**
*	File Name :ItemsetGenarator.java
*	Class Purpose : This class generates all itemsets of a transaction and counting their frequencies
*	Last Modified : 4/11/2007
*	Author : Polla A. Fattah
*/
import java.util.*;

public class ItemsetGenerator extends Thread
{
	private final int MAX_FREQUENT_BEFOR_FLUSH = 10000;	//Maximum limit for any K itemset befor sending them to the Database
	private static ItemsetGenerator ig;	//the only instantiation at a time

	private NetNavigator netNav;	//to get transactional data
	private CircularBuffer buffer;	//analyazed data will send to the CircularBuffer as KItemset Object
									//which contains a TreeMap and k

	private TreeMap kItemsetFreq[];	//contains all subsets as key and there frequencies as value before flushing
									//subsets are inserts to the element of TreeMap according to there k value
									//wich is used as index for TreeMap array.

	private short []curentTransaction;//every transaction will placed here for analyzing

	/**
	*	the constructor is private for preventing outside class instantiation
	*/
	private ItemsetGenerator(){
		super("ItemsetGenerator");
		setPriority(9);
		netNav = NetNavigator.getNetNavigator();
		buffer = new CircularBuffer(10);

		kItemsetFreq = new TreeMap[NetNavigator.MAX_TRANSACTION_ITEMS + 1 ];
		for(int i = 1; i < kItemsetFreq.length; i++)
			kItemsetFreq[i] = new TreeMap();
	}

	/**
	*	This method initializes an starts an instant of ItemsetGenerator then returns
	*	it if it called twicewith out calling exit between them it will throw NullPointerException
	*	@throws NullPointerException
	*	@return ItemsetGenerator
	*/
	public static ItemsetGenerator getItemsetGenerator(){
		if(ig == null){
			ig = new ItemsetGenerator();
			ig.start();
			return ig;
		}
		else{
			throw new NullPointerException("ItemsetGenerator cant be loaded more than once");
		}
	}//end of getItemsetGenerator

	/**
	*	overrides  run method of Thread class
	*	Fetches transaction data from netNavigator, finds this transaction power set, calculates there frequency.
	* 	when MAX_FREQUENT_BEFOR_FLUSH or end of transactions is reached it flushes it to CircularBuffer as KItemset object
	*/
	public void run(){
		int elements = 0;
		while(true){
			try{
				curentTransaction = netNav.getTransaction();
			}
			catch(Exception e){break;}//stopping while(true)

			elements = (int)Math.pow(2.0, curentTransaction.length);
			for(int i = 1 ; i < elements ; i++){
				increment(getSubset(i), Integer.bitCount(i));
			}
			for(int k = 1; k < kItemsetFreq.length ; k++)
				if (kItemsetFreq[k].size() > MAX_FREQUENT_BEFOR_FLUSH){
					//System.out.println("ItemsetGenerator Flushes : " + k);
					buffer.insert(new KItemset(kItemsetFreq[k], k) );
					kItemsetFreq[k] = new TreeMap();
				}
		}//end of while(true)

		for(int k = 1; k < kItemsetFreq.length ; k++)
			if (!kItemsetFreq[k].isEmpty())
				buffer.insert(new KItemset(kItemsetFreq[k], k) );

		buffer.insert("End");
	}

	/**
	*	this method increments sebsetfrequency if it exists other wise
	*	it will insert the new subset to the TreeMap
	*	@param subset : the subset to be incremented
	*	@param k : the order of the subset
	*/
	private void increment(String subset, int k) {
		//System.out.println(k);
		Long freq =(Long) kItemsetFreq[k].put(subset, new Long(1));
		if(freq != null)
			kItemsetFreq[k].put(subset, freq + 1);
	}

	/**
	*	this method returns a subset of the current set acoring to the binary ones in the index.
	*/
	private String getSubset(int index){
		String binary = Integer.toBinaryString(index);
		StringBuffer subset = new StringBuffer("");
		for(int j = 0,k = binary.length() - 1; k > -1; j++,k--){
			if(binary.charAt(k) != '0')
				subset.append(curentTransaction[j] + ",");
		}
		return subset.toString();
	}
	/**
	*	returns subset in the circularbuffer
	*/
	public KItemset getItemset(){
		return (KItemset)buffer.remove();
	}
}
