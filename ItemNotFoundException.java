
/*
*	File Name: ItemNotFound.java
*	Class Purpose: An Exception occures when requesting an element from refrence table but it is not exist
*	Last Updated: 2/10/2007
*	Author: Polla A. Fatah
*/

public class ItemNotFoundException extends java.lang.Exception{

	/**
	* Creates a new instance of <code>ItemNotFound</code> without detail message.
	*/
	public ItemNotFoundException (){
	}

	/**
	* Constructs an instance of <code>ItemNotFound</code> with the specified detail message.
	* @param msg the detail message.
	*/
	public ItemNotFoundException (String msg){
		super (msg);
	}
}
