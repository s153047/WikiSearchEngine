import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
 
class Index1 {
	public enum Setting{
		normal, pre, search 
	}
	
	static Setting setting = Setting.search;
	static int numRuns = 100;
	static int numFiles = 7;
	static int startFile = 0;
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
        int n = 1;
        
        try {
        	Scanner input = new Scanner(new File(filename), "UTF-8");    
            
            word = input.next();
            if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
            	  word = word.substring(0, word.length() - 1);
            }
            document = word;
            startW = new WikiItem(word,new DocItem(document,null), null);
            current = startW;
            while (input.hasNext()) {   // Read all words in input
            	// Der er to muligheder:
            	// 1: den er i den første liste
            	// 2: den er ikke i listen
                word = input.next();
                if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
              	  word = word.substring(0, word.length() - 1);
                }
                current2 = startW;
               
                
                if(word.equals("ENDOFDOCUMENT") && input.hasNext()){
                	word = input.next();
                	if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
                  	  word = word.substring(0, word.length() - 1);
                	}
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
    		for(int k = 0; k < 100; k++){
    			i.search("%&/¤#%&¤/(%");
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