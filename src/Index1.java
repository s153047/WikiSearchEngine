import java.awt.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
 
class Index1 {
	public enum Setting{
		normal, pre, search 
	}
	
	static Setting setting = Setting.pre;
    WikiItem start;
 
    private class WikiItem {
        String str;
        WikiItem next;
 
        WikiItem(String s, WikiItem n) {
            str = s;
            next = n;
        }
    }
 
    public Index1(String filename) {
        String word;
        WikiItem current, tmp;
        
        try {
        	Scanner input = new Scanner(new File(filename), "UTF-8");    
            
            word = input.next();
            word = word.replace(".", "");
            start = new WikiItem(word, null);
            current = start;
            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                word = word.replace(".", "");
                tmp = new WikiItem(word, null);
                current.next = tmp;
                current = tmp;
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
 
    public ArrayList<String> search(String searchstr) {
        WikiItem current = start;
        String doc = start.str;
        ArrayList<String> listOfDocs = new ArrayList<String>();
        
        while (current != null) {
        	if (current.str.equals("---ENDOFDOCUMENT---") && current.next!=null){
        		doc = current.next.str;
        	}
            
        	if (current.str.equals(searchstr) && !listOfDocs.contains(doc)) {
        		
            	listOfDocs.add(doc);
            	while(current.next != null){
            		if((current.next.str.equals("---ENDOFDOCUMENT---"))){
            			break;
            		}
            		current = current.next;
            		
            	}
            }
            current = current.next;
        }
        
        return listOfDocs;
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
            i.search(searchstr);
          
        }
        console.close();
    }
 
    public static void preprocessTest(String[] args){
    	System.out.println("Preprocessing " + args[0] + " 1000 times");
    	long time, totalTime = 0;
    	for(int j = 0; j<1000; j++){
    		time = System.currentTimeMillis();
            Index1 i = new Index1(args[0]);
            totalTime += System.currentTimeMillis() - time;
    	}
        System.out.println("Preprocessing time: " + totalTime);
    }
    
    public static void searchTest(String[] args){
    	long time, totalTime = 0;
    	Index1 i = new Index1(args[0]);
        totalTime = 0;
        System.out.println("Searching 10000 times");
        for(int j = 0; j<10000; j++){
    		time = System.currentTimeMillis();
    		i.search("%&/");
            totalTime += System.currentTimeMillis() - time;
    	}
        System.out.println("Search time: " + totalTime);
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