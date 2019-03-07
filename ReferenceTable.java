/*
*	File Name: ReferenceTable.java
*	Class Purpose: Handles coding and decoding of the Items this is very useful for abstraction and minimize memory usage
*	Last Updated: 2/11/2007
*	Author: Polla A. Fatah
*/

import java.io.*;
import java.util.*;
import javax.swing.*;

public class ReferenceTable implements Serializable
{
	private TreeMap table;		//contains (item, code) values
	private TreeMap tableInv;	//contains (code, item(s)) valuse

	private short count;	//keeps track of code secuence

	private static ReferenceTable rt = null;	//the only instanciation for the class this is because it should be senchronized.
	private static final String REFERENCE_FILE = "refrencetable.dat";	//target file to save and restore this class information

	/**
		Prevent Creation of new instance of ReferenceTable
	*/
	private ReferenceTable (){
		table = new TreeMap();
		tableInv = new TreeMap();
		count = 0;
	}

	/**
		Loads Reference Table Once and returns Loaded one multiple times
	*/
	public static ReferenceTable getReferenceTable(){
		if(rt == null){
			try{
				FileInputStream refInStream= new FileInputStream(REFERENCE_FILE);
				ObjectInputStream refIn = new ObjectInputStream(refInStream);
				rt = (ReferenceTable)refIn.readObject ();
				refIn.close ();
			}
			catch(IOException ioex){
				JOptionPane.showMessageDialog (null, "Sorry! Can NOT Read from [ReferenceTable] file.\n");
			}
			catch(ClassNotFoundException cnfex){
				JOptionPane.showMessageDialog (null, "Sorry!  [ReferenceTable] file corrupted");
			}
			if(rt == null){
				rt = new ReferenceTable();
				rt.save();
			}
		}
		return rt ;
	}

	/**
	*	Saves this object to the file
	*/
	public void save(){
		try{
			FileOutputStream fos= new FileOutputStream(REFERENCE_FILE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject (this);
			out.close ();
		}
		catch (IOException ioe){
			JOptionPane.showMessageDialog (null, "Sorry! Can NOT write to [ReferenceTable] file");
		}
	}
	/**
	*	Inserting an item in the Refrence Table
	*	@param item : an item
	*/
	public short insert(String item){
		short currentCode = -1;
		if(! table.containsKey (item)){
			table.put (item, count);
			tableInv.put (count, new StringBuffer(item+",") );
			currentCode = count;
			count++;
		}
		return currentCode;
	}

	/**
	*	Inserting group of items under single code number
	*	@param items[] : list of items
	*/
	public void insert(String []items, String groupName){
		boolean hasNew = false;
		for(int i = 0; i < items.length; i++)
			if(! table.containsKey (items[i])){
				table.put (items[i], count);
				hasNew = true;
			}
		if(hasNew){
			tableInv.put (count, groupName);
			count++;
		}
	}

	/**
	*	Appends new item to an existing code
	*	@param item : new item to be appeded.
	*	@param code : existing code.
	*/
	public void append(String item, short code){
		if(!tableInv.containsKey(code) && !table.containsKey(item))
			return;
		table.put(item, code);
	}

	/**
	*	Appends new item to an existing code
	*	@param item : new item to be appeded.
	*	@param code : existing code.
	*/
	public void append(String item, short code, String groupName){
		if(!tableInv.containsKey(code) && !table.containsKey(item))
			return;
		table.put(item, code);
		tableInv.put (code, groupName);
	}

	public boolean renameGroup(String groupName, short code){
		if(!tableInv.containsKey(code))
			return false;

		tableInv.put (code, groupName);
		return true;
	}
	/**
	*	Returns code of an item. this method throus ItemNotFoundException if the item is not exist
	*	@param item : the item
	*/
	public short getCode(String item) throws ItemNotFoundException{
		short code = 0;
		try{
			code = (Short) table.get (item);
		}
		catch(Exception e){
			throw new ItemNotFoundException("Ther is no Such Item");
		}
		return code;
	}

	/**
	*	Returns provided item's code if it is exist else it will insert the item and returns new code for it.
	*	@param item : an item.
	*/
	public short getCleanCode(String item){
		short code = 0;
		try{
			code = (Short) table.get (item);
		}
		catch(Exception e){
			table.put (item, count);
			tableInv.put (count, new StringBuffer(item+",") );
			code = count;
			count++;
		}
		return code;
	}

	/**
	*	Returns Item name for an existing code .
	*	This method throus ItemNotFoundException it the code is not exist
	*	@param code : a code of an item
	*/
	public String getItem(short code) throws ItemNotFoundException{
		StringBuffer item = new StringBuffer();
		try{
			item = (StringBuffer)tableInv.get(code);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ItemNotFoundException("Ther is no Such Item");
		}
		return item.toString();
	}

	/**
	*	clears RefrenceTable variables and resets file too.
	*/
	public void clear(){
		table= new TreeMap();
		tableInv= new TreeMap();
		count = 0;
		save();
	}

	/**
	*	Returns list of existing items
	*	Note : this method under construction
	*/
	public Set listItems(){
		return table.keySet ();
	}
}