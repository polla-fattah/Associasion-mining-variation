/**
*	File Name :CircularBuffer.java
*	Class Purpos : This class creates a synchronized and variable size circular buffer,
*		for holding objects between threads
*	Last Modified :2/11/2006
*	Author : Polla A. Fattah
*/
import javax.swing.JOptionPane;

public class CircularBuffer{
	private int SIZE;		  // The size of the buffer
	private Object []buffer;	//buffer for holding Object [Parent Object], so that any type of Objects

	//for controlling access to the buffer
	private boolean writeable = true;
	private boolean readable  = false;

	//Indicating Locations of read and write opperation
	private int writeLocation;
	private int readLocation ;

	/**
	*	Default constructor
	*	Creates a new instance of CircularBuffer whit size = 50;
	*/
	public CircularBuffer (){
		writeLocation = 0;
		readLocation = 0;
		SIZE = 50;
		buffer = new Object[SIZE];
	}

	/**
	*	Creates a new instance of CircularBuffer with selected size
	*	@param size the size of the buffer
	*/

	public CircularBuffer (int size){
		writeLocation = 0;
		readLocation = 0;
		SIZE = size;
		buffer = new Object[SIZE];
	}

	/**
	* This Method for inserting elements to the circular buffer.
	* it is synchronized so that one thread at a time can reach it.
	* @see #removeElement
	* @param element the element to be insearted
	*/
	public synchronized void insert(Object element){
		while(!writeable){
			try{wait();	}
			catch(InterruptedException irEx){irEx.printStackTrace();}
		}//end of while

		buffer[writeLocation] = element;
		readable = true;

		writeLocation = (writeLocation + 1) % SIZE;

		if(writeLocation == readLocation)
			writeable = false;

		notifyAll ();
    }//end of insert(Object element)

	/**
	*This Method removes elements from the circular buffer.
	* it is synchronized so that one thread at a time can reach it.
	* @see #insertElement
	* @return an <code>Object</code> which is an element in the circular buffer
	*/
	public synchronized Object remove(){
		Object element;
		while( !readable ){
			try{
			wait ();
			}
			catch(InterruptedException irex){}
		}//end of  while(!readable)

		element = buffer[readLocation];
		writeable = true;

		readLocation = (readLocation + 1) % SIZE;

		if(writeLocation == readLocation)
			readable = false;

		notifyAll ();
		return element;
	}//end of remove()
}
