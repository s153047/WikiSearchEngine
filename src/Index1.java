import java.io.*;
import java.util.Arrays;
import java.util.Scanner;


class Index1 {
	public enum Setting{
		normal, pre, search,correct 
	}
	
	static Setting setting = Setting.normal;
	static int numRuns = 50;
	static int numFiles = 8;
	static int startFile = 6;
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
            if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
            	  word = word.substring(0, word.length() - 1);
            }
            start = new WikiItem(word, null);
            current = start;
            int n = 1;
            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
              	  word = word.substring(0, word.length() - 1);
                }
                tmp = new WikiItem(word, null);
                current.next = tmp;
                current = tmp;
                n++;
            }
            System.out.println("n : "+n);
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
        System.out.println("Preprocessing " + args[startFile]);
        Index1 i = new Index1(args[startFile]);
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
    		i.search("%&/¤#%&¤/(%");
    		timeList[j] = (int) (System.currentTimeMillis() - time);
    	}

    	Arrays.sort(timeList);

    	if(numRuns % 2 == 0){
    		return (timeList[numRuns/2] + timeList[(numRuns/2)-1]) /2;
    	} else {
    		return timeList[numRuns/2];
    	}
    }
    
    public static void main(String[] args) {
    	
    	double w = 0;
    	double d = 0;
    	
    	  try {
          	Scanner input = new Scanner(new File(args[startFile]), "UTF-8");    
            while(input.hasNext()){
            	w += input.next().length();
              	d++;
            }
          	System.out.println(w);
          	System.out.println(d);
          	System.out.println((w/d));
          	
             input.close();
          } catch (FileNotFoundException e) {
              System.out.println("Error reading file ");
          }
    	
    	/*
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
    		case correct :
    			Index1 i = new Index1(args[startFile]);
    			 try {
    		        	Scanner input = new Scanner(new File(args[startFile]), "UTF-8");    
    		            boolean check = true;
    		        	String word;
    		            while (input.hasNext()) {   // Read all words in input
    		            	word = input.next();
    		            	word = word.replaceAll("[^A-Za-z0-9]", "");
    		            	if( ! i.search(word)){
    		            		check = false;
    		            	}
    		            }
    		            input.close();
    		        	System.out.println("Did all words exists? " + check);
    		        	System.out.println("Nonsense word search: " + i.search("%&/¤#%&¤/(%"));
    		     } catch (FileNotFoundException e) {
    		            System.out.println("Error reading file " + args[startFile]);
    		     }
    			break;
    		default:
    			break;
    	}*/
    }
}