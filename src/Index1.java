import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.openjdk.jol.info.ClassLayout;
 
class Index1 {
	public enum Setting{
		normal, pre, search, col, correct
	}
	
	static Setting setting = Setting.correct;
	static int numRuns = 10;
	static int numFiles =12;
	static int startFile =0;
	

    int docIndex = 1;
    String[] docList;
    HashTable currentHashTable;
    
    private class WikiItem {
        int[] docs;
        int docsIndex=0;
        String str;
 
        WikiItem(String s,int d) {
        	str = s;
        	docs = new int[1];
        	docs[docsIndex]=d;
        }
        
        public void insertDoc(int doc){
        	if(docs.length-1 <= docsIndex){
        		int[] docsTmp = new int[docs.length << 1];
        		
        		for(int i = 0; i < docs.length; i++){
        			docsTmp[i] = docs[i];
        		}
        		docs = docsTmp;
        	}
        	docsIndex++;
        	docs[docsIndex] = doc;
        }
    }
    
    private static long binomial(int n, int k){
        if (k>n-k){
        	k=n-k;
        }
        long b=1;
        for (int i=1, m=n; i<=k; i++, m--){
        	b=b*m/i;
        }
        return b;
    }
    
    
    private class HashTable{
    	private final int size;
    	private int n = 0;
    	long a,b,c;
    	WikiItem[][] table; 
    	HashTable(int s){
        	
    		a = ThreadLocalRandom.current().nextLong(2305843009213693951L-1)+1;
    		b = ThreadLocalRandom.current().nextLong(2305843009213693951L);
    		c = ThreadLocalRandom.current().nextLong(2305843009213693951L-1)+1;

    		size = s;
    		table = new WikiItem[size][];
    		for(int i = 0; i < size; i++){
    			table[i] = null;
    		}
    	}
    	
    	public void insert(String word){								// 3 situationer:
    		
    		int hashCode=(int)(Index1.hashCode(word,a,b,c) % size);
    		if(hashCode < 0) System.out.println(word);
    		if(table[hashCode] == null){ 								// no collision
    			n++;
    			table[hashCode] = new WikiItem[1];
    			table[hashCode][0] = new WikiItem(word,docIndex);
    		} else {																// collision
    			int k = 0;
    			for(WikiItem currentWikiItem : table[hashCode]){
    				k++;
    				if(currentWikiItem == null) break;
    				if(currentWikiItem.str.equals(word)){					// den er i linked list
    					if(currentWikiItem.docs[currentWikiItem.docsIndex] == docIndex){
    						return;
    					}
    					currentWikiItem.insertDoc(docIndex);
    					return;
    				} 
    			}
    			n++;
    			int length = table[hashCode].length;
    			if(k >= length){
    				WikiItem[] tmpBucket = new WikiItem[length<<1];
    				for(int i = 0; i < length; i++){
    					tmpBucket[i] = table[hashCode][i];
    				}
    				table[hashCode] =tmpBucket;
    			}

    			table[hashCode][k-1] = new WikiItem(word,docIndex);
				return;	
    		}
    	}
    	
    	public WikiItem[] getBucket(String word){
    		return table[(int)(Index1.hashCode(word,a,b,c) % size)];
    	}
    	
    	public WikiItem[] getIndex(int i){
    		return table[i];
    	}
    	
    }
    
    public Index1(String filename) {
        String word;
        
        try {
        	Scanner input = new Scanner(new File(filename), "UTF-8");    
            
            word = input.next().toLowerCase();
            if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
            	  word = word.substring(0, word.length() - 1);
            }

            docList = new String[4];
            docList[docIndex] = word;
            
            currentHashTable = new HashTable(128);
            currentHashTable.insert(word);
            
            while (input.hasNext()) {  
                word = input.next().toLowerCase();
                if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
              	  word = word.substring(0, word.length() - 1);
                }

                
                if(word.equals("---end.of.document---") && input.hasNext()){
                	word = input.next().toLowerCase();
                    if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
                  	  word = word.substring(0, word.length() - 1);
                    }
                    docIndex++;
                    
                    if( docList.length <= docIndex){
                		String[] docListTmp = new String[docList.length<<1];
                		
                		for(int i = 0; i < docList.length; i++){
                			docListTmp[i] = docList[i];
                		}
                		docList = docListTmp;
                    }
                    
                	docList[docIndex] = word;
                }
                
                if(currentHashTable.n > currentHashTable.size){
                	//System.out.println("Making new Hash Table, "+ currentHashTable.n + " / " + currentHashTable.size * 2 );
                 	int hashCode;
                 	//WikiItem currentWikiItem, nextWikiItem, currentWikiItem2;
                 	
                 	HashTable tmpHashTable = new HashTable(currentHashTable.size << 1);
                 	tmpHashTable.n = currentHashTable.n;
                 	
                 	for(int i = 0; i < currentHashTable.size; i++){									// loop igennem hashTable 
                 		//currentWikiItem = currentHashTable.getIndex(i);
                 		WikiItem[] bucket = currentHashTable.table[i];
                 		if(bucket == null) continue;
                 		
                 		for(WikiItem currentWikiItem : bucket){
                 			if(currentWikiItem == null) continue;
                 			hashCode =(int) (Index1.hashCode(currentWikiItem.str,tmpHashTable.a,tmpHashTable.b,tmpHashTable.c) % tmpHashTable.size);
                 			if(hashCode < 0) System.out.println(currentWikiItem.str);
                 			if(tmpHashTable.table[hashCode] == null){			// no collision
                    			tmpHashTable.table[hashCode] = new WikiItem[2];
                    			tmpHashTable.table[hashCode][0] = currentWikiItem;
                    			
                 			} else {																											// collision
                 				int k=0;
                 				for(WikiItem currentWikiItem2 : tmpHashTable.table[hashCode]){
                 					k++;
                 					if(currentWikiItem2 == null) break;
                 				}
                 				int length = tmpHashTable.table[hashCode].length;
                    			if(k == length){
                    				WikiItem[] tmpBucket = new WikiItem[length<<1];
                    				for(int j = 0; j < length; j++){
                    					tmpBucket[j] = tmpHashTable.table[hashCode][j];
                    				}
                    				tmpHashTable.table[hashCode] =tmpBucket;
                    			}

                    			tmpHashTable.table[hashCode][k-1] =currentWikiItem;
                 			}
                 		}
                 	}

                 	currentHashTable = tmpHashTable;
                 	//System.out.println("Done doubling");
                 	
                      
                    
                 }
            	currentHashTable.insert(word);
            }
            
           System.out.print(currentHashTable.n + " / " + currentHashTable.size + " = ");
            System.out.println((double)currentHashTable.n / currentHashTable.size);
            input.close();
            
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
    

    public static long hashCode(String word,long a,long b, long c){
    	// b,c er random seeds fra [0,...,p-1], hvor p = 2^61-1
    	// a fra [1,...,p-1]
    	return word.hashCode();
    	/*
    	long h = 1;
		long x;
		long p = (1<<61)-1;
		
		
    	for(int i = 0; i<word.length(); i++ ){
    		x = word.charAt(word.length()-1-i);
    		h = h * c + x;
			h = (h & p) + (h >> 61);
			h = (h == p) ? 0 : h;
		}
    	
    	h = a*h+b;
		h = (h & p) + (h >> 61);
		h = (h == p) ? 0 : h;
		
    	return h ;*/
    }
 
    public int[] search(String searchstr) {
    	searchstr = searchstr.toLowerCase();
    	WikiItem[] bucket = currentHashTable.getBucket(searchstr);
    	if(bucket != null){
    		for(WikiItem currentWikiItem : bucket){
        		if(currentWikiItem == null) break;
    			if(currentWikiItem.str.equals(searchstr)){
    				return currentWikiItem.docs;
    			}
    		}
    	}
        return new int[0];
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
            System.out.print("Found in : \t");
            for(int d : i.search(searchstr)){
            	String s = i.docList[d];
            	if(s != null)
            		System.out.print(s + ", ");
            }
            System.out.println();
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
            	if(i.currentHashTable.table[k] == null) continue;
            	for(WikiItem currentWikiItem : i.currentHashTable.table[k]){
            		if(currentWikiItem == null) break;
            		s++;
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
    		            	if( i.search(word).length == 0){
    		            		//System.out.println(word);
    		            		//System.out.println(i.hashCode(word, i.currentHashTable.a, i.currentHashTable.b, i.currentHashTable.c) % i.currentHashTable.size);
    		            		check = false;
    		            	}
    		            }
    		            input.close();
    		        	System.out.println("Did all words exists? " + check);
    		        	System.out.println("Nonsense word search: " + (i.search("%&/¤#%&¤/(%").length == 0));
    		     } catch (FileNotFoundException e) {
    		            System.out.println("Error reading file " + args[startFile]);
    		     }
    			break;
    	}
    }
}