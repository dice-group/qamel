package luceneIndex;

import java.util.HashSet;

public class Test {
	public static void main(String[] args){
		Index I = new Index();
		I.buildIndex();
		String query = "aBrack_bOama";
		HashSet<String> res = I.search(query);
		System.out.println("Searched : " + query);
		System.out.println(res.toString());

	}
}
