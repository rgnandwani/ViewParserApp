import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * Main "Views" Parsing Application
 */
public class ViewsParsingApp {
	// Constants: Leading characters in the user-entered selector.
	private static final char HASH = '#';
	private static final char DOT = '.';
	
	// Name of the JSON file to be parsed.
	private static String fileName = null;
	
	// Number of view matches.
	private static ArrayList<String> matchingViews = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException {
		// 1. Get the JSON file name from the command-line argument.
		// 2. Read the JSON file.
		// 3. Parse the file's contents.
		// 4. Store the parsed data hierarchically in ViewNode objects.
		// 5. Close the FileReader object.
		
		if (args.length == 1) {
			fileName = args[0];
		}
		else {
			System.out.println("Usage: java -classpath <CLASSPATH> ViewParsingApp <FILENAME>");
			System.exit(0);
		}
		
		Reader reader = new FileReader(fileName);
		ViewsParser parser = new ViewsParser(reader);
		ViewNode rootNode = parser.parseViews();
		reader.close();
		
		System.out.println("The JSON document was successfully parsed.");
		System.out.println("Enter a JSON selector to see its matching \"Views\".");
		System.out.println("Type quit to exit this program.");
		
		// Process standard input provided by the user.
		// Each line entered is assumed to be a JSON selector.
		// Processing will stop when the user enters quit.
        Scanner scanner = new Scanner(System.in);
        String inputSelector = null;
        while (inputSelector == null || !inputSelector.equalsIgnoreCase("quit")) {
        	try {
        		// User-entered JSON selector
        		inputSelector = scanner.nextLine();
        		
        		// Process the selector as long as the user didn't wish to quit.
        		if (!inputSelector.equalsIgnoreCase("quit") || !inputSelector.matches("quit")) {
        			
        			// Get the input selectors as an ArrayList, to provide support for selector chaining. 
        			ArrayList<String> inputSelectors = createInputSelectorArray(inputSelector);

        			// Find matching views for the entered selector.
        			printMatchingViews(rootNode, inputSelectors);
        			
        			// No views were found for the entered JSON selector.
        			if (matchingViews.size() == 0) {
        				System.out.println("Invalid selector entered. No match found!");
        			}
        			// Print all the obtained matching Views.
        			else {
        				for (int i = 0; i < matchingViews.size(); i++) {
        					System.out.println((i + 1) + " -> " + matchingViews.get(i));
        				}
        				matchingViews = new ArrayList<String>();
        			}
        		}
        	}
	        catch (NoSuchElementException nsee) {
	       		scanner.close();
	        	nsee.printStackTrace();
	        }
        }
        // Close the scanner object.
   		scanner.close();
   		
   		System.out.println("Program exited.");
    }
	
	private static void printMatchingViews(ViewNode node, ArrayList<String> selectors) {
		// Check to see whether chaining needs to be supported.
		boolean chaining = (selectors.size() == 1 ? false : true);
		
		if (node != null && !selectors.isEmpty()) {
			// Start from the end by getting the last selector substring in the list
			String selector = selectors.get(selectors.size() - 1);
			
			// Compare the current View's node attribute values with the user-entered JSON selector.
			if (selector.charAt(0) == HASH) {
				// Selector with the leading HASH character stripped off. 
				String strippedSelector = selector.substring(1);
				
				// Check if there is a match with the view's identifier.
				if (node.getIdentifier() != null && node.getIdentifier().matches(strippedSelector)) {
					// Handle chaining.
					if (chaining) {
						handleChaining (node, selectors);
					}
					// Else add the current View string to the matchingViews ArrayList.
					else {
						matchingViews.add(node.getView());
					}
				}
				// Check if there is a match with the view's control identifier.
				else if (node.getControlIdentifier() != null && node.getControlIdentifier().matches(strippedSelector)) {
					// Handle chaining.
					if (chaining) {
						handleChaining (node, selectors);
					}
					// Else add the current View string to the matchingViews ArrayList.
					else {
						matchingViews.add(node.getView());
					}
				}
			}
			else if (selector.charAt(0) == DOT) {
				// Selector with the leading HASH character stripped off. 
				String strippedSelector = selector.substring(1);
				
				// Check if there is a match with any of the view's CSS class names.
				if (node.isClassName(strippedSelector)) {
					// Handle chaining.
					if (chaining) {
						handleChaining (node, selectors);
					}
					// Else add the current View string to the matchingViews ArrayList.
					else {
						matchingViews.add(node.getView());
					}
				}
			}
			else {
				// Check if there is a match with the view's class name.
				if (node.getClazz() != null && node.getClazz().matches(selector)) {
					// Handle chaining.
					if (chaining) {
						handleChaining (node, selectors);
					}
					// Else add the current View string to the matchingViews ArrayList.
					else {
						matchingViews.add(node.getView());
					}
				}
				// Check if there is a match with the view's control class name.
				else if (node.getControlClazz() != null && node.getControlClazz().matches(selector)) {
					// Handle chaining.
					if (chaining) {
						handleChaining (node, selectors);
					}
					// Else add the current View string to the matchingViews ArrayList.
					else {
						matchingViews.add(node.getView());
					}
				}
			}
			
			// If the view's node object has children, compare their node objects' values (with the selector), recursively.
			if (node.getChildNodes() != null) {
				for (int i = 0; i < node.getChildNodes().size(); i++) {
					ViewNode childnode = node.getChildNodes().get(i);
					// Recursive call
					printMatchingViews(childnode, selectors);
				}
			}
		}
	}
	
	private static void handleChaining (ViewNode node, ArrayList<String> sels) {
		@SuppressWarnings("unchecked")
		ArrayList<String> selectors = (ArrayList<String>) sels.clone();
		// Since the last selector substring was already matched, remove it from the ArrayList.
		selectors.remove(selectors.size() - 1);
		// Since the current ViewNode contained the matching selector substring, now get its parent ViewNode.
		ViewNode parentNode = node.getParentNode();
		
		for (; parentNode != null; parentNode = parentNode.getParentNode()) {
			// Start from the end by getting the last selector substring in the list.
			String selector = selectors.get(selectors.size() - 1);
			if (selector.charAt(0) == HASH) {
				// Selector with the leading HASH character stripped off. 
				String strippedSelector = selector.substring(1);
				
				// Check if there is a match with the view's identifier.
				if (parentNode.getIdentifier() != null && parentNode.getIdentifier().matches(strippedSelector)) {
					// If there's a match, remove the matched selector substring, which is the last item in the list.
					selectors.remove(selectors.size() - 1);
					// If there are no more selector substrings in the list, then we have a full match and the node's View string can be added.
					if (selectors.isEmpty()) {
						matchingViews.add(node.getView());
						break;
					}
				}
				// Check if there is a match with the view's control identifier.
				else if (parentNode.getControlIdentifier() != null && parentNode.getControlIdentifier().matches(strippedSelector)) {
					// If there's a match, remove the matched selector substring, which is the last item in the list.
					selectors.remove(selectors.size() - 1);
					// If there are no more selector substrings in the list, then we have a full match and the node's View string can be added.
					if (selectors.isEmpty()) {
						matchingViews.add(node.getView());
						break;
					}
				}
			}
			else if (selector.charAt(0) == DOT) {
				// Selector with the leading HASH character stripped off. 
				String strippedSelector = selector.substring(1);
				
				// Check if there is a match with any of the view's CSS class names.
				if (parentNode.isClassName(strippedSelector)) {
					// If there's a match, remove the matched selector substring, which is the last item in the list.
					selectors.remove(selectors.size() - 1);
					// If there are no more selector substrings in the list, then we have a full match and the node's View string can be added.
					if (selectors.isEmpty()) {
						matchingViews.add(node.getView());
						break;
					}
				}
			}
			else {
				// Check if there is a match with the view's class name.
				if (parentNode.getClazz() != null && parentNode.getClazz().matches(selector)) {
					// If there's a match, remove the matched selector substring, which is the last item in the list.
					selectors.remove(selectors.size() - 1);
					// If there are no more selector substrings in the list, then we have a full match and the node's View string can be added.
					if (selectors.isEmpty()) {
						matchingViews.add(node.getView());
						break;
					}
				}
				// Check if there is a match with the view's control class name.
				else if (parentNode.getControlClazz() != null && parentNode.getControlClazz().matches(selector)) {
					// If there's a match, remove the matched selector substring, which is the last item in the list.
					selectors.remove(selectors.size() - 1);
					// If there are no more selector substrings in the list, then we have a full match and the node's View string can be added.
					if (selectors.isEmpty()) {
						matchingViews.add(node.getView());
						break;
					}
				}
			}
		}
	}
	
	private static ArrayList<String> createInputSelectorArray(String selector) {
		ArrayList<String> selectors = new ArrayList<String>();
		
		// Parse the user-provided input selector
		while (selector.length() != 0) {
			// If there are no classes (DOT) or attributes (HASH), this is a class selector.
			if (selector.indexOf(DOT) == -1 && selector.indexOf(HASH) == -1) {
				String[] strings = selector.split(" ");
				for (int i = 0; i < strings.length; i++) {
					selectors.add(strings[i].trim());
				}
				selector = selector.substring(selector.length());
			}
			// Check to see whether there are any classes (DOT) or attributes (HASH), and which appears first.
			if (selector.indexOf(DOT) > selector.indexOf(HASH)) {
				int index = -1;
				// Both class and attribute are present, but the attribute precedes the class.
				if (selector.indexOf(HASH) != -1) {
					index = selector.indexOf(HASH);
					// Add the parsed selector to the ArrayList, then reset the "selector" string to exclude the added selector.
					selectors.add(selector.substring(index, selector.indexOf(DOT)).trim());
					selector = selector.substring(selector.indexOf(DOT));
				}
				// Only a class is present
				else {
					index = selector.indexOf(DOT);
					// Add the parsed selector to the ArrayList, then reset the "selector" string to exclude the added selector.
					selectors.add(selector.substring(index).trim());
					selector = selector.substring(selector.length());
				}
			}
			// Check to see whether there are any classes (DOT) or attributes (HASH), and which appears first.
			if (selector.indexOf(HASH) > selector.indexOf(DOT)) {
				int index = -1;
				// Both attribute and class are present, but the class precedes the attribute.
				if (selector.indexOf(DOT) != -1) {
					index = selector.indexOf(DOT);
					// Add the parsed selector to the ArrayList, then reset the "selector" string to exclude the added selector.
					selectors.add(selector.substring(index, selector.indexOf(HASH)).trim());
					selector = selector.substring(selector.indexOf(HASH));
				}
				// Only an attribute is present
				else {
					index = selector.indexOf(HASH);
					// Add the parsed selector to the ArrayList, then reset the "selector" string to exclude the added selector.
					selectors.add(selector.substring(index).trim());
					selector = selector.substring(selector.length());
				}
			}
		}
		
		return selectors;
	}
}
