/*
*	File Name: DBServers.java
*	Class Purpose: Saves Database Servers Information
*	Last Updated: 4/11/2007
*	Author: Polla A. Fattah
*/

import java.io.*;
import java.util.*;
import javax.swing.*;

public class DBServers implements Serializable
{
	private static final String SERVERS_FILE = "servers.dat";	//The file that contains information about database servers and tables
	private static DBServers dbServers;			//The only instantiation at a time

	private Server servers[] ;	//array of Server class @see Server class
	private int size;			//The number of servers exists
	private final int MAX_SIZE = 8;		//Maximum number allowed of the servers
	private long itemsetCount;
	/** Creates a new instance of DBServers it is private to prevent out side class instantiation*/
	private DBServers() {
		servers = new Server[MAX_SIZE];
		size = 0;
		itemsetCount = 0;
	}
	public void incItemset(){itemsetCount++;}
	public long getItemSetCount(){return itemsetCount;}
	/**
	*	This method Loads an instantiation of DBServers form a file and returns it if there is an instantiation it will return this one
	*	and do not create new one. If there is no file it will create it and return a new empty instance with warning.
	*	@return DBServers:
	*/
	public static DBServers getDBServers(){
		if(dbServers == null){
			try{
				FileInputStream inStream = new FileInputStream(SERVERS_FILE);
				ObjectInputStream in = new ObjectInputStream(inStream);
				dbServers = (DBServers)in.readObject ();
				in.close ();
			}
			catch(IOException ioex){
				ioex.printStackTrace();
				JOptionPane.showMessageDialog (null, "Sorry! Can NOT Read from [SERVERS_FILE] file.\n");
			}
			catch(ClassNotFoundException cnfex){
				JOptionPane.showMessageDialog (null, "Sorry!  [SERVERS_FILE] file is corrupted");
			}
			if(dbServers == null){
				dbServers = new DBServers();
				dbServers.save();
			}
		}
		return dbServers ;
	}

	/**
	*	this method saves current instantiation in Target file
	*/
	public void save(){
		try{
			FileOutputStream fos = new FileOutputStream(SERVERS_FILE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject (this);
			out.close ();
		}
		catch (IOException ioe){
			JOptionPane.showMessageDialog (null, "Sorry! Can NOT write to [SERVERS_FILE] file");
		}
	}

	/**
	*	this method returns true if the given server is exist other wise returns false.
	*/
	public boolean containsServer(Server s){
		for(int i = 0 ; i < size ; i++)
			if(s.equal(servers[i]))
				return true;
		return false;
	}

	/**
	*	this method inserts new server to the DBServers and returns true if it succeed other wise returns false
	*	@param server: the computer server name.
	*	@param database: database name.
	*	@param table: Table name.
	*	@param user: a user of this database.
	*	@param password: password of the given user.
	*
	*	@return false if inserted server is exists. It checks server, database,
	*			and table (it uses Server class equal method for comparison)
	*/
	public boolean insert(String server, String database, String table, String user, String password){

		Server temp = new Server(server, database, table, user, password);
		if(containsServer(temp))
			return false;

		if(size ==MAX_SIZE)
			throw new ArrayIndexOutOfBoundsException();

		servers[size] = temp;
		size++;
		save();
		return true;
	}

	/**
	*	deletes server at the given location
	*	@param index: location of the server in DBServers.
	*	@throw ArrayIndexOutOfBoundsException if index is not correct.
	*/
	public void delete(int index){
		if(index >= size || index < 0)
			throw new ArrayIndexOutOfBoundsException();
		for(int i = index; i < size - 1 ; i++)
			servers[i] = servers[i+1];
		size--;
		save();
	}

	/**
	*	Returns the number of existing servers.
	*/
	public int size(){
		return size;
	}

	/**
	*	@return String table name.
	*	@param index: location of the server in DBServers.
	*	@throw ArrayIndexOutOfBoundsException if index is not correct.
	*/
	public String getTable(int index){
		if(index >= size || index < 0)
			throw new ArrayIndexOutOfBoundsException();

		return servers[index].table;
	}

	/**
	*	@return specified server path.
	*	@param index: location of the server in DBServers.
	*	@throw ArrayIndexOutOfBoundsException if index is not correct.
	*/
	public String getServerUrl (int index){
		if(index >= size || index < 0)
			throw new ArrayIndexOutOfBoundsException();
		return servers[index].toString();
	}

	/**
	*	@return String server name.
	*	@param index: location of the server in DBServers.
	*	@throw ArrayIndexOutOfBoundsException if index is not correct.
	*/
	public String getServerName (int index){
		if(index >= size || index < 0)
			throw new ArrayIndexOutOfBoundsException();
		return servers[index].name();
	}

	/**
	*	@return location of the last stop of the specified server
	*	@param index: location of the server in DBServers.
	*	@throw ArrayIndexOutOfBoundsException if index is not correct.
	*/
	public long getCurser (int index){
		if(index >= size || index < 0)
			throw new ArrayIndexOutOfBoundsException();
		return servers[index].curser;
	}

	/**
	*	sets new location of a given server .
	*	@param index: location of the server in DBServers.
	*	@param value: new cursor location.
	*	@throw ArrayIndexOutOfBoundsException if index is not correct.
	*/
	public void setCurser (int index, long value){
		if(index >= size || index < 0)
			throw new ArrayIndexOutOfBoundsException();

		servers[index].curser = value;
		save();
	}

	/**
	*	deletes all serve in the DBServers
	*/
	public void clear(){
		servers = new Server[MAX_SIZE];
		itemsetCount =0;
		size = 0;
		save();

	}
	/**
	*	sets all cursors to zero.
	*/
	public void reset(){
		itemsetCount =0;
		for (int i = 0 ; i < size ; i++)
			servers[i].curser = 0;
		save();
	}

	/*
	*	This is private inline class is used by DBServer for storing Servers information
	*/
	private class Server implements Serializable
	{
		public String server;
		public String database;
		public String table;
		public String user;
		public String password;
		public long curser;
		/*
		*	the only constructor
		*/
		public Server(String server, String database, String table, String user, String password){
			this.server = server;
			this.database = database;
			this.table = table;
			this.user = user;
			this.password = password;
			this.curser = 0;
		}

		/*
		*	returns serve information in the form of valid URL for MySql driver
		*/
		public String toString(){
			return "jdbc:mysql://" + server + "/" + database + "?user=" + user + "&password="+ password + "&table=" + table;
		}

		/*
		*	Overrides Object's equal method. two servers are equal if thy are equal in server, database and table
		*/
		public boolean equal(Server s){
			return (s.server.equalsIgnoreCase(this.server) && s.database.equalsIgnoreCase(this.database) && s.table.equalsIgnoreCase(this.table) );
		}


		public String name(){
			return server+"/"+database+"/"+table;
		}
	}
}
