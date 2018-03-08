import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
 
class Index1 {
	public enum Setting{
		normal, pre, search 
	}
	
	static Setting setting = Setting.pre;
	static int numRuns = 1;
	static int numFiles = 8;
	static int start = 0;
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
        int n = 0;
        
        try {
        	Scanner input = new Scanner(new File(filename), "UTF-8");    
            
            word = input.next();
            word = word.replaceAll("[^A-Za-z0-9]", "");
            document = word;
            startW = new WikiItem(word,new DocItem(document,null), null);
            current = startW;
            while (input.hasNext()) {   // Read all words in input
            	// Der er to muligheder:
            	// 1: den er i den første liste
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
                		currentD = current2.docs; 
                		while(true){
                			if(currentD.str.equals(document))
                				break;
                			
                			if(currentD.next == null){
                				currentD.next = new DocItem(document, null);
                			}
                			
                			currentD = currentD.next;
                		}
                		
                		break;
                	}
                	
                	
                	// 2: Hvis ordet ikke er i den første liste endnu
                	if(current2.next ==null){
                		n++;
                		current.next = new WikiItem(word,new DocItem(document,null),null);
                		current = current.next;
                		break;
                	}
                	current2 = current2.next;
                }
            }
            System.out.println(n);
            input.close();
            
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
 
    public ArrayList search(String searchstr) {
        WikiItem current = startW;
        ArrayList<String> list = new ArrayList<String>();
        while (current != null) {
            if (current.str.equals(searchstr)) {
                for(DocItem doc = current.docs ; doc != null; doc = doc.next){
                	list.add(doc.str);
                }
            	break;
            }
            current = current.next;
        }
        return list;
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
            System.out.println(i.search(searchstr));
            
        }
        console.close();
    }
 
    public static void preprocessTest(String[] args){
    	long time, totalTime = 0;
    	int[] runTime = new int[numFiles];
    	
    	for(int h = start; h < numFiles; h++){
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
        
        
        for(int h = start; h < numFiles; h++){
        	System.out.println("Preprocessing " + args[h]);
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