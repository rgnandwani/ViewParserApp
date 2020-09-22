import java.util.ArrayList;

/*
 * The "View" bean class.
 */
public class ViewNode {
	// Current View JSON string.
	String view = null;
	
	// Selector matching attributes.
	String clazz = null;
	ArrayList<String> classNames = null;
	String identifier = null;
	
	// Additional attributes tracked for identifying nested child views.
	Control control = null;
	String subview = null;
	
	// Parent and children View node objects (for maintaining the hierarchical relationships).
	ViewNode parentNode = null;
	ArrayList<ViewNode> childNodes = null;
	
	// Empty constructor.
	public ViewNode() {
	}
	
	// Mostly getter and setter methods, with occasional 'match' and 'add' convenience methods, where applicable.
	public String getView() {
		return this.view;
	}
	
	public void setView(String view) {
		this.view = view;
	}
	
	public String getClazz() {
		return this.clazz;
	}
	
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	public ArrayList<String> getClassNames() {
		return this.classNames;
	}
	
	public void setClassNames(ArrayList<String> classNames) {
		this.classNames = classNames;
	}
	
	public Control getControl() {
		return this.control;
	}
	
	public void setControl(Control control) {
		this.control = control;
	}
	
	public String getControlClazz() {
		if (this.control != null) {
			return this.control.getClazz();
		}
		return null;
	}
	
	public void setControlClazz(String controlclazz) {
		if (this.control == null) {
			this.control = new Control();
		}
		this.control.setClazz(controlclazz);
	}
	
	public String getControlIdentifier() {
		if (this.control != null) {
			return this.control.getIdentifier();
		}
		return null;
	}
	
	public void setControlIdentifier(String controlIdentifier) {
		if (this.control == null) {
			this.control = new Control();
		}
		this.control.setIdentifier(controlIdentifier);
	}
	
	public boolean isClassName(String className) {
		if (className != null && this.classNames != null) {
			for (int i = 0; i < this.classNames.size(); i++) {
				if (this.classNames.get(i).compareTo(className) == 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void addClassName(String className) {
		if (className != null) {
			if (this.classNames == null) {
				this.classNames = new ArrayList<String>();
			}
			this.classNames.add(className);
		}
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getSubview() {
		return subview;
	}
	
	public void setSubview(String subview) {
		this.subview = subview;
	}
	
	public ViewNode getParentNode() {
		return this.parentNode;
	}
	
	public void setParentNode(ViewNode parentNode) {
		this.parentNode = parentNode;
	}
	
	public ArrayList<ViewNode> getChildNodes() {
		return this.childNodes;
	}
	
	public void setChildNodes(ArrayList<ViewNode> childNodes) {
		this.childNodes = childNodes;
	}
	
	public void addChildNode(ViewNode childNode) {
		if (childNode != null) {
			if (this.childNodes == null) {
				this.childNodes = new ArrayList<ViewNode>();
			}
			this.childNodes.add(childNode);
		}
	}
	
	/*
	 * Control class for depicting the "control" attribute (Note: we're only interested in 2 of its members).
	 */
	class Control {
	    String clazz = null;
	    String identifier = null;
	    
	    String getClazz() {
			return this.clazz;
		}
		
		void setClazz(String clazz) {
			this.clazz = clazz;
		}
		
		String getIdentifier() {
			return this.identifier;
		}
		
		void setIdentifier(String identifier) {
			this.identifier = identifier;
		}
	}
}
