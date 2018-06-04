import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
 
class Index1 {
	public enum Setting{
		normal, pre, search,col 
	}
	
	static Setting setting = Setting.col;
	static int numRuns = 1;
	static int numFiles =11;
	static int startFile = 2;
	
    String document;
    HashTable currentHashTable;
    
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
    	
    	public DocItem getLast(){
    		DocItem current = this;
    		while(current.next != null){
    			current = current.next;
    		}
    		return current;
    	}
    }
    
    private class HashTable{
    	private final int size;
    	private int n = 0;
    	WikiItem[] table; 
    	HashTable(int s){
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
    			table[(word.hashCode() & 0x7fffffff) % size] = new WikiItem(word,new DocItem(document,null), null);			
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
    		return table[(word.hashCode() & 0x7fffffff) % size];
    	}
    	
    	public WikiItem getIndex(int i){
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
            document = word;
            
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
                	document = word;
                }
                
                if((double) currentHashTable.n / currentHashTable.size > 0.75){
                	//System.out.println("Making new Hash Table, "+ currentHashTable.n + " / " + currentHashTable.size * 2 +" ("+currentHashTable.d+")");
                 	int currentHashCode;
                 	WikiItem currentWikiItem, nextWikiItem, currentWikiItem2;
                 	
                 	HashTable tmpHashTable = new HashTable(currentHashTable.size * 2);
                 	tmpHashTable.n = currentHashTable.n;
                 	
                 	for(int i = 0; i < currentHashTable.size; i++){									// loop igennem hashTable 
                 		currentWikiItem = currentHashTable.getIndex(i);

                 		while(currentWikiItem != null){													// loop igennem wikiItem Linked List
                 			nextWikiItem = currentWikiItem.next;
                 			currentHashCode = currentWikiItem.str.hashCode() & 0x7fffffff;
                 			if(tmpHashTable.table[currentHashCode % tmpHashTable.size] == null){			// no collision
                 				tmpHashTable.table[currentHashCode % tmpHashTable.size] = currentWikiItem;
                 				currentWikiItem.next = null;
                 			} else {																											// collision
                 				
                 				currentWikiItem2 = tmpHashTable.table[currentHashCode % tmpHashTable.size];
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
            	currentHashTable.insert(word);
            }

            System.out.print(currentHashTable.n + " / " + currentHashTable.size + " = ");
            System.out.println((double)currentHashTable.n / currentHashTable.size);
            input.close();

            
            
            
            /*
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
            System.out.println();

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
            */
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
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
    
    public int maxCollisions(){
    	WikiItem currentWikiItem;
        int c,cmax,ci;
        cmax = 0;
        ci = 0;
        for(int i = 0; i < currentHashTable.size; i++){
        	currentWikiItem = currentHashTable.table[i];
        	c = 0;
        	
        	while(currentWikiItem != null){
        		c++;
        		currentWikiItem = currentWikiItem.next;
        	}
        	if(c > cmax){
        		cmax = c;
        		ci = i;
        	}
        }
        System.out.println("cmax: "+cmax);
        System.out.println("ci: "+ci);
        currentWikiItem = currentHashTable.getIndex(ci);
        while(currentWikiItem.next != null){
        	System.out.print(currentWikiItem.str + " , ");
        	currentWikiItem = currentWikiItem.next;
        }
        return cmax;
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
    		for(int k = 0; k < 10000; k++){
    			i.search("the");
    		}
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
    	}
    }
}