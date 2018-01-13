import java.awt.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
 
class Index1 {
 
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
            start = new WikiItem(word, null);
            current = start;
            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                System.out.println(word);
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
        	if (current.str.equals("---END.OF.DOCUMENT---") && current.next!=null){
        		doc = current.next.str;
        	}
            
        	if (current.str.equals(searchstr) && !listOfDocs.contains(doc)) {
            	listOfDocs.add(doc);
            	while(current != null){
            		if((current.next.str.equals("---END.OF.DOCUMENT---"))){
            			break;
            		}
            		current = current.next;
            	}
            }
            current = current.next;
        }
        return listOfDocs;
    }
 
    public static void main(String[] args) {
        System.out.println("Preprocessing " + args[0]);
        Index1 i = new Index1(args[0]);
        Scanner console = new Scanner(System.in);
        ArrayList<String> searchString = new ArrayList<String>();
        for (;;) {
            System.out.println("Input search string or type exit to stop");
            String searchstr = console.nextLine();
            if (searchstr.equals("exit")) {
                break;
            }
            searchString = i.search(searchstr);
            if (!searchString.isEmpty()) {
                System.out.println(searchstr + " exists in documents: " + searchString);
                
            } else {
                System.out.println(searchstr + " does not exist");
            }
        }
        console.close();
    }
}