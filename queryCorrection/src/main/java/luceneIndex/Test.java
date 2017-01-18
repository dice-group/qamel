package luceneIndex;

import java.util.HashSet;

public class Test {
	public static void main(String[] args){
		Index I = new Index();
		String query = "aBrak Obama";
		HashSet<String> res = I.search(query);
		System.out.println("Searched : " + query);
		for(String r: res){
			System.out.println(r);
		}
	}
}
