/**
*	File name: KItemset.java
*	Class Purpose: This class just works like a data paket between ItemsetGenerator and NetBot to
*	hold and transfer subsets Map wich contain subsets and there frequencies and the length of sets.
*	Last Updated: 2/11/2007
*	Author: Polla A. Fatah
*/
import java.util.*;

class KItemset{
	public TreeMap subsets;	//contain (subset -> frequency) paires
	public int k;			//the length of current subset.

	/**
	*	the only constructor that has two paremeters:
	*	@param subset: Contains subsets
	*	@param k : the length of the subset.
	*/

	KItemset(TreeMap subsets, int k){
		this.subsets = subsets;
		this.k = k;
	}
}