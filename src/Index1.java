import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import com.google.common.primitives.*;

class Index1 {
	public enum Setting{
		normal, pre, search, col
	}
	
	static Setting setting = Setting.normal;
	static int numRuns = 10;
	static int numFiles =11;
	static int startFile = 2;
	
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
    	Random r = new Random();
    	
    	private final int size;
    	private int n = 0;
    	int l;
    	UnsignedLong[] a = new UnsignedLong[256];
   	 	long a2,b2,c2;
   	 	long[] longArray = new long[32];
   	 	
    	WikiItem[] table; 
    	HashTable(int s){
    		
       	 	for(int i = 0 ; i < 256-1;i++ ){
       	 		a[i] = UnsignedLong.fromLongBits(ThreadLocalRandom.current().nextLong());
       	 	}
        	
    		a2 = ThreadLocalRandom.current().nextLong(2305843009213693951L-1)+1;
    		b2 = ThreadLocalRandom.current().nextLong(2305843009213693951L);
    		c2 = ThreadLocalRandom.current().nextLong(2305843009213693951L-1)+1;
       	 	
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
    			table[(int)(Index1.hashCode(word,a,l,a2,b2,c2,longArray) % size)] = new WikiItem(word,new DocItem(document,null), null);			
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
    		return table[(int)(Index1.hashCode(word,a,l,a2,b2,c2,longArray) % size)];
    	}
    	
    	public WikiItem getIndex(int i){
    		return table[i];
    	}
    	
    }
    
    public Index1(String filename) {
        String word;
        String[] wordArr;
        
        try {
        	Scanner input = new Scanner(new File(filename), "UTF-8");    
            
            word = input.next().toLowerCase();
            if ( (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) && word.length()>1) {
            	  word = word.substring(0, word.length() - 1);
              }
            document = word;
            
            currentHashTable = new HashTable(128);
            currentHashTable.l = 7;
            currentHashTable.insert(word);
            
            while (input.hasNext()) {  
                word = input.next().toLowerCase();
                if ( (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) && word.length()>1) {
                	  word = word.substring(0, word.length() - 1);
                  }
                
                if(word.equals("---end.of.document---") && input.hasNext()){
                	input.nextLine();
                	input.nextLine();
                	word = input.nextLine().toLowerCase();
                    if ( (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) && word.length()>1) {
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
                 	tmpHashTable.l = currentHashTable.l + 1;
                 	
                 	for(int i = 0; i < currentHashTable.size; i++){									// loop igennem hashTable 
                 		currentWikiItem = currentHashTable.getIndex(i);

                 		while(currentWikiItem != null){													// loop igennem wikiItem Linked List
                 			nextWikiItem = currentWikiItem.next;
                 			currentHashCode = Index1.hashCode(currentWikiItem.str,tmpHashTable.a,tmpHashTable.l,tmpHashTable.a2,tmpHashTable.b2,tmpHashTable.c2,tmpHashTable.longArray);
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
            	currentHashTable.insert(word);
            }

            System.out.print(currentHashTable.n + " / " + currentHashTable.size + " = ");
            System.out.println((double)currentHashTable.n / currentHashTable.size);
            input.close();   
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
    

    public static long hashCode(String word,UnsignedLong[] a, int l, long a2, long b2, long c2, long[] longArray){
    	// UnsignedLong primitiven er fra Guava biblioteket lavet af Google.
    	// a består af 256 UnsignedLong mellem 0 og 2^64-1.
    	// 2^l er størrelsen af hash tabellen.
    	UnsignedLong h = UnsignedLong.ZERO;
    	UnsignedLong x;
    	UnsignedLong y;
    	
    	if(word.length() > 256) return hashCode2(word, a2, b2, c2);
    	
    	byte[] byteArray;
		try {
			byteArray = word.getBytes("UTF-8");
	    	if( word.length() == byteArray.length){
	    		
	    		int d = (word.length() +7 ) >>3;
                //memory copy
                for(int i = 0; i < d; i++){
                	int k = byteArray.length-i*8;
                	int j=0;
                	longArray[i] = 0;
                	while(j < k && j < 8){
                		longArray[i] = ((longArray[i]<< 8) | (byteArray[i*8+j] & 0xFF));
                		j++;
                	}
                }
                for(int i = 0; i < d/2; i++){
        			x = UnsignedLong.valueOf(longArray[(i*2)+1]);
        			y = UnsignedLong.valueOf(longArray[i*2]);
        			h = h.plus( a[i*2].plus(x)).times( (a[(i*2)+1].plus(y) ) );
        		}
        		if(d % 2 == 1){
        			h = h.plus(UnsignedLong.valueOf(longArray[d-1]).times(a[d]));
        		}

        		h = h.plus(a[d]);
        		long j = h.longValue();						//ingen bitshift for UnsignedLong
        		j = j >>> (64 - l);
            	return ( (int) j) ;

	    	}
		} catch (UnsupportedEncodingException e) {
			System.out.println("UTF-8 error");
		} 
    	
		for(int i = 0; i < word.length() / 2; i++){
			x = UnsignedLong.valueOf(word.charAt((i*2)+1));
			y = UnsignedLong.valueOf(word.charAt(i*2));
			h = h.plus( a[i*2].plus(x)).times( (a[(i*2)+1].plus(y)) );
		}
		if(word.length() % 2 == 1){
			h = h.plus(UnsignedLong.valueOf(word.charAt(word.length()-1)).times(a[word.length()]));
		}

		h = h.plus(a[word.length()]);
		long j = h.longValue();						//ingen bitshift for UnsignedLong
		j = j >>> (64 - l);
    	return j ;
    }
 
    public static long hashCode2(String word,long a,long b, long c){
    	// b,c er random seeds fra [0,...,p-1], hvor p = 2^61-1
    	// a fra [1,...,p-1]
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
    	}
    }
}