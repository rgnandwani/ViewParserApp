import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * The "View" parser class, for parsing the external JSON file.
 */
public class ViewsParser {
	// Interested view attribute names, as String constants.
	private final String CLASS = "class";
	private final String CLASSNAMES = "classNames";
	private final String CONTROL = "control";
	private final String CONTENTVIEW = "contentView";
	private final String IDENTIFIER = "identifier";
	private final String SUBVIEWS = "subviews";
	
	// The FileReader.
	private Reader reader = null;
	// The top-level (root) View will be stored here.
	private ViewNode rootNode = null;
	
	public ViewsParser(Reader reader) {
		this.reader = reader;
	}
	
	// The "parseViews" method will return the fully parsed JSON document (after calling the recursive "parseView" method on the top-level View).
	public ViewNode parseViews() {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(this.reader);
			// The top-level "View" doesn't have a parent ViewNode, hence a null is passed in as the 2nd argument.
			parseView(jsonObject, null);
		}
		catch (ParseException pe) {
			pe.printStackTrace();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return rootNode;
	}
	
	private void parseView(JSONObject jsonObject, ViewNode parentNode) {
		if (jsonObject != null) {
			// Each time the "parseView" method is called, we're dealing with a new "View" so a new "ViewNode" object is needed.
			ViewNode node = new ViewNode();
			node.setView(jsonObject.toString());
			
			// Set the parent and children node attributes for the parent "ViewNode" object (i.e. children nodes do not yet exist for the current "ViewNode" object).
			if (parentNode != null) {
				node.setParentNode(parentNode);
				parentNode.addChildNode(node);
			}
			
			// Iterate through the JSON object and set the current ViewNode's object values accordingly.
			for(Iterator<?> iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    if (key.matches(CLASS)) {
			    	node.setClazz(jsonObject.get(key).toString());
			    }
			    else if (key.matches(IDENTIFIER)) {
			    	node.setIdentifier(jsonObject.get(key).toString());
			    }
			    else if (key.matches(CLASSNAMES)) {
			        JSONArray items = (JSONArray) jsonObject.get(key);
			        for(Object item: items) {
			        	if (item instanceof String) {
			        		node.addClassName(item.toString());
			            }
			        }
			    }
			    else if (key.matches(CONTROL)) {
			    	JSONObject control = (JSONObject) jsonObject.get(key);
			    	for(Iterator<?> itr = control.keySet().iterator(); itr.hasNext();) {
					    String subKey = (String) itr.next();
					    if (subKey.matches(CLASS)) {
					    	node.setControlClazz(control.get(subKey).toString());
					    }
					    else if (subKey.matches(IDENTIFIER)) {
					    	node.setControlIdentifier(control.get(subKey).toString());
					    }
			    	}
			    }
			    else if (key.matches(CONTENTVIEW)) {
			    	JSONArray views = (JSONArray) ((JSONObject)jsonObject.get(CONTENTVIEW)).get(SUBVIEWS);
			        for(Object item: views) {
			            if (item instanceof JSONObject) {
			            	parseView((JSONObject)item, node);
			            }
			        }
			    }
			    else if (key.matches(SUBVIEWS)) {
			    	// Object's subview attribute is set by storing full subview JSON string.
			    	node.setSubview(((JSONArray) jsonObject.get(key)).toJSONString());
			        JSONArray views = (JSONArray) jsonObject.get(key);
			        for(Object item: views) {
			            if (item instanceof JSONObject) {
			            	// Get each sub-view from the "subviews" array and parse them individually.
			            	parseView((JSONObject)item, node);
			            }
			        }
			    }
			}
			
			// Set the object's rootNode variable only when we're all done parsing everything.
			if (parentNode == null) {
				this.rootNode = node;
			}
		}
	}
}
