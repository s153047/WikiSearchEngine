import java.io.*;
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
            word = word.replaceAll("[^A-Za-z0-9]", "");
            start = new WikiItem(word, null);
            current = start;
            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                word = word.replaceAll("[^A-Za-z0-9]", "");
                tmp = new WikiItem(word, null);
                current.next = tmp;
                current = tmp;
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }
 
    public boolean search(String searchstr) {
        WikiItem current = start;
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
    	int numRuns = 50;
    	int[] runTime = new int[numRuns];
    	
    	System.out.println("Preprocessing " + args[0]);
    	
    	for(int h = 1; h<numRuns; h++){
    		totalTime = 0;
    		for(int j = 1; j<h*h; j++){
        		time = System.currentTimeMillis();
                Index1 i = new Index1(args[0]);
                runTime[h] += System.currentTimeMillis() - time;
        	}
    	}
    	
        System.out.println("Preprocessing time: " );
        for(int j = 1; j < numRuns; j++){
        	//System.out.println(j*j + " : " + runTime[j]);
        	System.out.println(runTime[j]);
        }
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