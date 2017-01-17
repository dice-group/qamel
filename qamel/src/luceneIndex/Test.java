package luceneIndex;

import java.util.HashSet;

public class Test {
	public static void main(String[] args){
		Index IB = new Index();
		IB.buildIndex();
		String query = "Acqualana";
		HashSet<String> res = IB.search(query);
		System.out.println("Searched : " + query);
		System.out.println(res.toString());

	}
}
