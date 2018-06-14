import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.openjdk.jol.info.ClassLayout;
 
class Index1 {
	public enum Setting{
		normal, pre, search, col, correct
	}
	
	static Setting setting = Setting.correct;
	static int numRuns = 1;
	static int numFiles =4;
	static int startFile = 2;
	
    String document;
    HashTable currentHashTable;
    int cmax;
    
    private class WikiItem {
        String str;
        DocItem docs;
        WikiItem next;
 
        WikiItem(String s,DocItem d, WikiItem n) {
            str = s;
            docs = d;
            next = n;
        }
    }
    
    private class DocItem{
    	String str;
    	DocItem next;
    	
    	DocItem(String s, DocItem n){
    		str = s;
    		next = n;
    	}
    }
    
    private static long binomial(int n, int k)
    {
        if (k>n-k)
            k=n-k;

        long b=1;
        for (int i=1, m=n; i<=k; i++, m--)
            b=b*m/i;
        return b;
    }
    
    
    private class HashTable{
    	private final int size;
    	private int n = 0;
    	long a,b,c;
    	WikiItem[] table; 
    	HashTable(int s){
        	
    		a = ThreadLocalRandom.current().nextLong(2305843009213693951L-1)+1;
    		b = ThreadLocalRandom.current().nextLong(2305843009213693951L);
    		c = ThreadLocalRandom.current().nextLong(2305843009213693951L-1)+1;

    		size = s;
    		table = new WikiItem[size];
    		for(int i = 0; i < size; i++){
    			table[i] = null;
    		}
    	}
    	
    	public void insert(String word){								// 3 situationer:
    		
    		WikiItem currentWikiItem = getBucket(word); 
    		DocItem tmpDocItem; 
    		
    		if(currentWikiItem == null){ 								// no collision
    			n++;
    			table[(int)(Index1.hashCode(word,a,b,c) % size)] = new WikiItem(word,new DocItem(document,null), null);			
    		} else {																// collision
    			
    			while(true){
    				if(currentWikiItem.str.equals(word)){					// den er i linked list
    					
    					if(currentWikiItem.docs.str.equals(document)){
    						return;
    					}
    					tmpDocItem = new DocItem(document,currentWikiItem.docs);
    					currentWikiItem.docs = tmpDocItem;
    					return;
    					
    				} else if(currentWikiItem.next == null) {				// den er ikke i linked list
    					n++;
    					currentWikiItem.next = new WikiItem(word,new DocItem(document,null), null);
    					return;
    				}
    				currentWikiItem = currentWikiItem.next;
    			}
    		}
    	}
    	
    	public WikiItem getBucket(String word){
    		return table[(int)(Index1.hashCode(word,a,b,c) % size)];
    	}
    	
    	public WikiItem getIndex(int i){
    		return table[i];
    	}
    	
    }
    
    public Index1(String filename) {
        String word;
        String[] wordArr;
    	int d=1;
        try {
        	Scanner input = new Scanner(new File(filename), "UTF-8");    
            
            word = input.next().toLowerCase();
            if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
            	  word = word.substring(0, word.length() - 1);
            }

            document = word;
            
            currentHashTable = new HashTable(128);
            currentHashTable.insert(word);
            
            while (input.hasNext()) {  
                word = input.next().toLowerCase();
                if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
              	  word = word.substring(0, word.length() - 1);
                }

                
                if(word.equals("---end.of.document---") && input.hasNext()){

					d++;
                	input.nextLine();
                	input.nextLine();
                	word = input.nextLine().toLowerCase();
                    if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
                  	  word = word.substring(0, word.length() - 1);
                    }

                	document = word;
                	wordArr = word.split(" ");
                	for(String w : wordArr){
                		currentHashTable.insert(w);
                	}
                }
                
                if((double) currentHashTable.n / currentHashTable.size > 1.0){
                	//System.out.println("Making new Hash Table, "+ currentHashTable.n + " / " + currentHashTable.size * 2 );
                 	long currentHashCode;
                 	WikiItem currentWikiItem, nextWikiItem, currentWikiItem2;
                 	
                 	HashTable tmpHashTable = new HashTable(currentHashTable.size * 2);
                 	tmpHashTable.n = currentHashTable.n;
                 	
                 	for(int i = 0; i < currentHashTable.size; i++){									// loop igennem hashTable 
                 		currentWikiItem = currentHashTable.getIndex(i);

                 		while(currentWikiItem != null){													// loop igennem wikiItem Linked List
                 			nextWikiItem = currentWikiItem.next;
                 			currentHashCode = Index1.hashCode(currentWikiItem.str,tmpHashTable.a,tmpHashTable.b,tmpHashTable.c);
                 			if(tmpHashTable.table[(int)(currentHashCode % tmpHashTable.size)] == null){			// no collision
                 				tmpHashTable.table[(int)(currentHashCode % tmpHashTable.size)] = currentWikiItem;
                 				currentWikiItem.next = null;
                 			} else {																											// collision

                 				currentWikiItem2 = tmpHashTable.table[(int)(currentHashCode % tmpHashTable.size)];
                 				while(currentWikiItem2.next != null){
                 					currentWikiItem2 = currentWikiItem2.next;
                 				}
                 				currentWikiItem2.next = currentWikiItem;
                 				currentWikiItem.next = null;
                 			}
                 			
                 			currentWikiItem = nextWikiItem;
                 		}
                 	}
                 	
                 	currentHashTable = tmpHashTable;
                 	//System.out.println("Done doubling");
                 	
                 	
                      
                    
                 }
                if(input.hasNext()) 	currentHashTable.insert(word);
            }
            
           System.out.print(currentHashTable.n + " / " + currentHashTable.size + " = ");
            System.out.println((double)currentHashTable.n / currentHashTable.size);
            input.close();
            
            System.out.println(d);
            
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
    

    public static long hashCode(String word,long a,long b, long c){
    	// b,c er random seeds fra [0,...,p-1], hvor p = 2^61-1
    	// a fra [1,...,p-1]
    	long h = 1;
		long x;
		long p = (1<<61)-1;
		
		
    	for(int i = 0; i<word.length(); i++ ){
    		x = word.charAt(word.length()-1-i);
    		h = h * c + x;
			h = (h & p) + (h >>> 61);
			h = (h == p) ? 0 : h;
		}
    	
    	h = a*h+b;
		h = (h & p) + (h >>> 61);
		h = (h == p) ? 0 : h;
		
    	return h ;
    }
 
    public ArrayList search(String searchstr) {
    	WikiItem currentWikiItem = currentHashTable.getBucket(searchstr);
    	 ArrayList<String> list = new ArrayList<String>();
    	while(currentWikiItem != null){
			if(currentWikiItem.str.equals(searchstr)){
				for(DocItem doc = currentWikiItem.docs ; doc != null; doc = doc.next){
                	list.add(doc.str);
                }
				break;
			}
			currentWikiItem = currentWikiItem.next;
		}
        return list;
    }
 
    public static void normal(String[] args) {
        System.out.println("Preprocessing " + args[startFile]);
        Index1 i = new Index1(args[startFile]);
        Scanner console = new Scanner(System.in);
        for (;;) {
            System.out.println("Input search string or type exit to stop");
            String searchstr = console.nextLine();
            if (searchstr.equals("exit")) {
                break;
            }
            System.out.println(i.search(searchstr));
            
        }
        console.close();
    }
 
    public static int preprocessTest(String[] args, int fileNumber){
		System.out.println("Preprocessing " + args[fileNumber]);
    	long time = 0;
    	int[] timeList = new int[numRuns];
    	
    	time = 0;

    	for(int j = 0; j<numRuns; j++){
    		time = System.currentTimeMillis();
    		Index1 i = new Index1(args[fileNumber]);
    		timeList[j] = (int) (System.currentTimeMillis() - time);
    	}

    	Arrays.sort(timeList);

    	if(numRuns % 2 == 0){
    		return (timeList[numRuns/2] + timeList[(numRuns/2)-1]) /2;
    	} else {
    		return timeList[numRuns/2];
    	}
    	
    }
    
    public static int searchTest(String[] args, int fileNumber){
		System.out.println("Preprocessing " + args[fileNumber]);
    	long time = 0;
    	int[] timeList = new int[numRuns];
    	
    	time = 0;
    	Index1 i = new Index1(args[fileNumber]);
    	
    	for(int j = 0; j<numRuns; j++){
    		time = System.currentTimeMillis();
    		i.search("the");
    		timeList[j] = (int) (System.currentTimeMillis() - time);
    	}

    	Arrays.sort(timeList);

    	if(numRuns % 2 == 0){
    		return (timeList[numRuns/2] + timeList[(numRuns/2)-1]) /2;
    	} else {
    		return timeList[numRuns/2];
    	}
    }

    public static int collisionTest(String[] args, int fileNumber){
    	
    	int[] collisions = new int[numRuns];
    	int d;
		System.out.println("Preprocessing " + args[fileNumber]);
		
		for(int j = 0; j<numRuns; j++){
			d = 0;
            Index1 i = new Index1(args[fileNumber]);
            int s;
            for(int k = 0; k < i.currentHashTable.size; k++){
            	s=0;
            	WikiItem currentWikiItem = i.currentHashTable.table[k];
            	while(currentWikiItem != null){
            		s++;
            		currentWikiItem = currentWikiItem.next;
            	}
            	s--;
            	if(s>1) d += binomial(s, 2);
            }
            collisions[j] = d;
    	}
		Arrays.sort(collisions);
		
    	if(numRuns % 2 == 0){
    		return (collisions[numRuns/2] + collisions[(numRuns/2)-1]) /2;
    	} else {
    		return collisions[numRuns/2];
    	}
		
    }
    
    public static void main(String[] args) {
		int[] list = new int[numFiles];
    	switch(setting) {
    		case normal : 
    			normal(args);
    			break;
    		case pre :
    			for(int i = startFile; i < numFiles; i++){
    				list[i]=preprocessTest(args, i);
    			}
    			for(int i : list){
    				System.out.println(i);
    			}
    			break;
    		case search :
    			for(int i = startFile; i < numFiles; i++){
    				list[i]=searchTest(args,i);
    			}
    			for(int i : list){
    				System.out.println(i);
    			}
    			break;
    		case col :
    			for(int i = startFile; i < numFiles; i++){
    				list[i]=collisionTest(args,i);
    			}
    			for(int i : list){
    				System.out.println(i);
    			}
    			break;
    		case correct :
    			Index1 i = new Index1(args[startFile]);
    			 try {
    		        	Scanner input = new Scanner(new File(args[startFile]), "UTF-8");    
    		            boolean check = true;
    		        	String word;
    		            while (input.hasNext()) {   // Read all words in input
    		            	word = input.next().toLowerCase();
    		            	if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
    		                	  word = word.substring(0, word.length() - 1);
    		                }
    		            	if( i.search(word).size() == 0 && !word.equals("---end.of.document---")){
    		            		System.out.println(word);
    		            		check = false;
    		            	}
    		            }
    		            input.close();
    		        	System.out.println("Did all words exists? " + check);
    		        	System.out.println("Nonsense word search: " + (i.search("%&/¤#%&¤/(%").size() == 0));
    		     } catch (FileNotFoundException e) {
    		            System.out.println("Error reading file " + args[startFile]);
    		     }
    		break;
    	}
    }
}