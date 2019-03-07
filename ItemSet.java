public class ItemSet implements Comparable
{
	private String itemset ;
	private long supportCount;
	public static double allItemsets;
	private ReferenceTable rt = ReferenceTable.getReferenceTable();
	public ItemSet(String itemset, long supportCount){
		this.itemset = itemset;
		this.supportCount = supportCount;
	}
	public ItemSet(String itemset){
		this.itemset = itemset;
	}

	public void setSupportCount(long supportCount){
		this.supportCount = supportCount;
	}

	public int compareTo(Object anotherItemSet){
		ItemSet i = (ItemSet)anotherItemSet;
		return itemset.compareTo(i.getSet());
	}

	public static String[] firstSubitemsets(String itemset){
		String [] itemsets = itemset.split(",");
		String subitemsets [] = new String[itemsets.length];
		StringBuffer subitemset = new StringBuffer();
		for (int i = 0 ; i < itemsets.length ; i++){
			for (int j = 0 ; j < itemsets.length ; j++){
				if(i != j)
					subitemset.append(itemsets[j]+",");
			}
			subitemsets[i] = subitemset.toString();
			subitemset = new StringBuffer();
		}
		return subitemsets;
	}

	public String rule(String subset, double sup, double conf){
		return "";
	}

	public String toString(){
		return itemset;
	}

	public String getSet(){
		return itemset;
	}
	public void print(){
		System.out.println(itemset+"["+supportCount+"]"+" ---"+"["+allItemsets+"]");
	}
	public long getSupportCount(){
		return supportCount;
	}

	public String creatRule(String imp, long conf){
		StringBuffer cnsiquence = new StringBuffer("");
		StringBuffer impedence = new StringBuffer("");
		String impedenceItems[] =  imp.split(",");
		String [] itemsets = itemset.split(",");
		int j = 0;
		try{
			for(int i = 0 ; i< itemsets.length;i++){
				if(!itemsets[i].equals(impedenceItems[j]) ){
					cnsiquence.append(rt.getItem(Short.parseShort(itemsets[i])));
				}
				else{
					impedence.append(rt.getItem(Short.parseShort(impedenceItems[j])));
					j++;
					if(j ==impedenceItems.length)
						for(i++; i<itemsets.length;i++)
							cnsiquence.append(rt.getItem(Short.parseShort(itemsets[i])));
				}
			}
		}
		catch(ItemNotFoundException infex){infex.printStackTrace();}
		return impedence + "--->" + cnsiquence + "  sup = ["+(((int)(10000 * supportCount/allItemsets))/100.0)+"]"+" ---"+" Conf = ["+(conf/100.0)+"]" ;
	}

	public static void main(String arge[]){
		ItemSet.allItemsets = 8;
		ItemSet it = new ItemSet("0,1,3,5,6,",5);
		System.out.println(it.creatRule("0,3,5,",5550));
	}
}