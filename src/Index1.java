import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import com.google.common.primitives.*;

class Index1 {
	public enum Setting{
		normal, pre, search 
	}
	
	static Setting setting = Setting.pre;
	static int numRuns = 1;
	static int numFiles =1;
	static int startFile = 0;
	
    String document;
    HashTable currentHashTable;
    
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
    
    
    
    private class HashTable{
    	Random r = new Random();
    	
    	private final int size;
    	private int n = 0;
    	int d,l;
    	UnsignedLong[] a = new UnsignedLong[256];
   	 

    	WikiItem[] table; 
    	HashTable(int s){
    		
       	 	for(int i = 0 ; i < 256-1;i++ ){
       	 		a[i] = UnsignedLong.fromLongBits(ThreadLocalRandom.current().nextLong());
       	 	}
        	d=0;
        	
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
    			table[Index1.hashCode(word,a,l) % size] = new WikiItem(word,new DocItem(document,null), null);			
    		} else {																// collision
    			
    			while(true){
    				d++;
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
    		return table[Index1.hashCode(word,a,l) % size];
    	}
    	
    	public WikiItem getIndex(int i){
    		return table[i];
    	}
    	
    }
    
    public Index1(String filename) {
        String word;

        
        try {
        	Scanner input = new Scanner(new File(filename), "UTF-8");    
            
            word = input.next();
            word = word.replaceAll("[^A-Za-z0-9]", "");
            document = word;
            
            currentHashTable = new HashTable(128);
            currentHashTable.l = 7;
            currentHashTable.insert(word);
            
            while (input.hasNext()) {  
                word = input.next();
                word = word.replaceAll("[^A-Za-z0-9]", "");
                
                if(word.equals("ENDOFDOCUMENT") && input.hasNext()){
                	word = input.next();
                	word = word.replaceAll("[^A-Za-z0-9]", "");
                	document = word;
                }
                
                if((double) currentHashTable.n / currentHashTable.size > 1.0){
                	//System.out.println("Making new Hash Table, "+ currentHashTable.n + " / " + currentHashTable.size * 2 );
                 	int currentHashCode;
                 	WikiItem currentWikiItem, nextWikiItem, currentWikiItem2;
                 	
                 	HashTable tmpHashTable = new HashTable(currentHashTable.size * 2);
                 	tmpHashTable.n = currentHashTable.n;
                 	tmpHashTable.l = currentHashTable.l + 1;
                 	
                 	for(int i = 0; i < currentHashTable.size; i++){									// loop igennem hashTable 
                 		currentWikiItem = currentHashTable.getIndex(i);

                 		while(currentWikiItem != null){													// loop igennem wikiItem Linked List
                 			nextWikiItem = currentWikiItem.next;
                 			currentHashCode = Index1.hashCode(currentWikiItem.str,tmpHashTable.a,tmpHashTable.l);
                 			if(tmpHashTable.table[currentHashCode % tmpHashTable.size] == null){			// no collision
                 				tmpHashTable.table[currentHashCode % tmpHashTable.size] = currentWikiItem;
                 				currentWikiItem.next = null;
                 			} else {																											// collision

                 				currentWikiItem2 = tmpHashTable.table[currentHashCode % tmpHashTable.size];
                 				while(currentWikiItem2.next != null){
                 					tmpHashTable.d++;
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
            	currentHashTable.insert(word);
            }

            System.out.print(currentHashTable.n + " / " + currentHashTable.size + " = ");
            System.out.println((double)currentHashTable.n / currentHashTable.size);
            input.close();
            
            WikiItem currentWikiItem;
            int[] bucketList = new int[20];
            int c,cIndex=0;
            int cmax = 0;
            for(int i = 0; i < currentHashTable.size; i++){
            	currentWikiItem = currentHashTable.table[i];
            	c = 0;
            	while(currentWikiItem != null){
            		c++;
            		currentWikiItem = currentWikiItem.next;
            	}
            	if(c > cmax) {
            		cmax = c;
            		cIndex = i;
            	}
            	
            	bucketList[c] ++;
            	
            }

            
            System.out.println(cmax);
            System.out.println();
            for(int i : bucketList){
            	System.out.println(i);
            }
            System.out.println();
            
            
            System.out.println();
            currentWikiItem = currentHashTable.table[cIndex];
            while(currentWikiItem != null){
            	System.out.println(currentWikiItem.str);
            	currentWikiItem = currentWikiItem.next;
            }
            System.out.println();
            
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
    

    public static int hashCode(String word,UnsignedLong[] a, int l){
    	// b,c er random seeds fra [0,...,p-1], hvor p = 2^31-1
    	// a fra [1,...,p-1]
    	UnsignedLong h = UnsignedLong.ZERO;
    	UnsignedLong x;
    	UnsignedLong y;
		for(int i = 0; i < word.length() / 2; i++){
			x = UnsignedLong.valueOf(word.charAt((i*2)+1));
			y = UnsignedLong.valueOf(word.charAt(i*2));
			h = h.plus( a[i*2].plus(x)).times( (a[(i*2)+1].plus(y) ) );
		}
		if(word.length() % 2 == 1){
			h = h.plus(UnsignedLong.valueOf(word.charAt(word.length()-1)).times(a[word.length()]));
		}

		h = h.plus(a[word.length()]);
		long j = h.longValue();
		j = j >>> (63 - l);
    	return (int) j ;
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
 
    public static void preprocessTest(String[] args){
    	long time, totalTime = 0;
    	int[] runTime = new int[numFiles];
    	
    	for(int h = startFile; h < numFiles; h++){
    		System.out.println("Preprocessing " + args[h]);
    		totalTime = 0;
    		
    		for(int j = 0; j<numRuns; j++){
    			time = System.currentTimeMillis();
                Index1 i = new Index1(args[h]);
                totalTime += System.currentTimeMillis() - time;
        	}
    		runTime[h] = ((int) totalTime)/numRuns;
    	}
    	
        System.out.println("Preprocessing time: " );
        for(int j = 0; j < numFiles; j++){
        	//System.out.println(j*j + " : " + runTime[j]);
        	System.out.println(runTime[j]);
        }
    }
    
    public static void searchTest(String[] args){
    	long time, totalTime = 0;
    	long[] runTime = new long[numFiles];
        
        
        for(int h = startFile; h < numFiles; h++){
        	Index1 i = new Index1(args[h]);
        	System.out.println("Searching: " + args[h]);
    		totalTime = 0;
    		for(int j = 0; j< numRuns; j++){
    			time = System.currentTimeMillis();
        		//i.search("%&/¤#%&¤/(%");
    			i.search("the");
                totalTime+= System.currentTimeMillis() - time;
        	}
    		runTime[h] = ( totalTime); 
        }
        for(int j = 0; j < numFiles; j++){
        	//System.out.println(j*j + " : " + runTime[j]);
        	System.out.println(runTime[j]);
        }
    }
    
    public static void main(String[] args) {
    	/*
    	 int l = (int) 20;
    	 for(int k=0; k<100;k++){
    	   	 UnsignedLong[] a = new UnsignedLong[256];
        	 
        	 for(int i = 0 ; i < 256-1;i++ ){
        		 a[i] = UnsignedLong.fromLongBits(ThreadLocalRandom.current().nextLong());
        	 }
        	 
        	 String s = "hea";
        	
        	 System.out.println(hashCode(s,a,l));
    	 }
    	 */
    			 
    	
    	switch(setting) {
    		case normal : 
    			normal(args);
    			break;
    		case pre :
    			preprocessTest(args);
    			break;
    		case search :
    			searchTest(args);
    			break;
    	}
    }
}