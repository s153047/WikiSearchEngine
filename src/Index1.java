import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
 
class Index1 {
	public enum Setting{
		normal, pre, search 
	}
	
	static Setting setting = Setting.pre;
	static int numRuns = 1;
	static int numFiles = 6;
	static int startFile = 5;
	
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
    		
    		
    		if(currentWikiItem == null){ 								// den's key er ikke i hashTable
    			n++;
    			table[(word.hashCode() & 0x7fffffff) % size] = new WikiItem(word,new DocItem(document,null), null);			
    		} else {
    			while(true){
    				if(currentWikiItem.str.equals(word)){					// den er i hashTable
    					DocItem currentDocItem = currentWikiItem.docs; 
    					while(currentDocItem != null){
    						if(currentDocItem.str.equals(document)){
    							return;
    						}
    						if(currentDocItem.next == null){
    							currentDocItem.next = new DocItem(document,null);
    						}
    						currentDocItem = currentDocItem.next;
    					}
    				}
    				if(currentWikiItem.next == null) {				// den er ikke i hashTable
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
            
            word = input.next();
            word = word.replaceAll("[^A-Za-z0-9]", "");
            document = word;
            
            currentHashTable = new HashTable(128);
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
                 	System.out.println("Making new Hash Table, size = " + currentHashTable.size * 2 );
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
                 	 System.out.println("Done doubling");
                 }
            	currentHashTable.insert(word);
            }

            System.out.print(currentHashTable.n + " / " + currentHashTable.size + " = ");
            System.out.println((double)currentHashTable.n / currentHashTable.size);
            
            WikiItem currentWikiItem;
            
            for(int i = 0; i < 100; i++){
            	currentWikiItem = currentHashTable.getIndex(i+1000);
            	System.out.println();
            	System.out.println("i: ");
            	while(currentWikiItem != null){
            		System.out.print(currentWikiItem.str + ", ");
            		currentWikiItem = currentWikiItem.next;
            	}
            }
            
            input.close();
            
            
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
				System.out.println(currentWikiItem.str);
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
        		i.search("%&/¤#%&¤/(%");
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