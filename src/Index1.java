import java.io.*;
import java.util.Scanner;
 
class Index1 {
	public enum Setting{
		normal, pre, search 
	}
	
	static Setting setting = Setting.normal;
	static int numRuns = 100;
	static int numFiles = 7;
    WikiItem startW;
    
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
 
    public Index1(String filename) {
        String word,document;
        WikiItem current,current2, tmp;
        DocItem currentD;
        
        try {
        	Scanner input = new Scanner(new File(filename), "UTF-8");    
            
            word = input.next();
            word = word.replaceAll("[^A-Za-z0-9]", "");
            document = word;
            startW = new WikiItem(word,new DocItem(document,null), null);
            current = startW;
            while (input.hasNext()) {   // Read all words in input
            	// 1: den er i listen
            	// 2: den er ikke i listen
                word = input.next();
                word = word.replaceAll("[^A-Za-z0-9]", "");
                current2 = startW;
               
                
                if(word.equals("ENDOFDOCUMENT") && input.hasNext()){
                	word = input.next();
                	word = word.replaceAll("[^A-Za-z0-9]", "");
                	document = word;
                }
                	
                
                while(current2 != null){
                	if(current2.str.equals(word)){ // 1: den er i listen
                		// add til docList, hvis den ikke er der
                		currentD = current2.docs; // TODO: fix hvis currentD er null
                		while(currentD.next != null){
                			if(currentD.str.contains(document))
                				break;
                			
                			currentD = currentD.next;
                		}
                		if(currentD.next == null)
                			currentD.next = new DocItem(document, null);
                		
                		break;
                	}
                	
                	
                	// 2: Hvis ordet ikke er i den første liste endnu
                	if(current2.next ==null){
                		current.next = new WikiItem(word,new DocItem(document,null),null);
                		current = current.next;
                		break;
                	}
                	current2 = current2.next;
                }
            }
            currentD = startW.next.next.next.docs;
            
            while(currentD != null){
            	System.out.print(currentD.str+ ", ");
            	currentD = currentD.next;
            }
            System.out.println();
            
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
 
    public boolean search(String searchstr) {
        WikiItem current = startW;
        while (current != null) {
            if (current.str.equals(searchstr)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
 
    public static void normal(String[] args) {
        System.out.println("Preprocessing " + args[0]);
        Index1 i = new Index1(args[0]);
        Scanner console = new Scanner(System.in);
        for (;;) {
            System.out.println("Input search string or type exit to stop");
            String searchstr = console.nextLine();
            if (searchstr.equals("exit")) {
                break;
            }
            if (i.search(searchstr)) {
                System.out.println(searchstr + " exists");
            } else {
                System.out.println(searchstr + " does not exist");
            }
        }
        console.close();
    }
 
    public static void preprocessTest(String[] args){
    	long time, totalTime = 0;
    	int[] runTime = new int[numFiles];
    	
    	for(int h = 0; h < numFiles; h++){
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
        
        
        for(int h = 0; h < numFiles; h++){
        	Index1 i = new Index1(args[h]);
        	System.out.println("Searching: " + args[h]);
    		totalTime = 0;
    		for(int j = 0; j< numRuns; j++){
    			time = System.currentTimeMillis();
        		i.search("%&/¤#%&¤/(%");
                totalTime+= System.currentTimeMillis() - time;
        	}
    		runTime[h] = ( totalTime) / (long) numRuns; 
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