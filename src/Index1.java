import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
 
class Index1 {
	public enum Setting{
		normal, pre, search 
	}
	
	static Setting setting = Setting.pre;
	static int numRuns = 10;
	static int numFiles = 8;
	static int startFile = 0;
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
    		i.search("%&/�#%&�/(%");
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
    	}
    }
}