package syntacticanalysis.node;

import java.util.List;

public class ClassDeclarationNode extends Node {
	private final Node modifiers;
	public final String className;
	private final Node extend;
	public final List<Node> classMembers;
	public ClassDeclarationNode (int numLine, Node modifiers, String className, Node extend, List<Node> classMembers){
		super(numLine);
		this.modifiers = modifiers;
		this.className = className;
		this.extend = extend;
		this.classMembers = classMembers;
	}
	public String toString (){
		StringBuilder sb = new StringBuilder();
		sb.append("ClassDeclarationNode ").append(className).append("[\n");
		for (Node member : classMembers){
			sb.append(member).append("\n");
		}
		sb.append("]");
		return sb.toString();
	}
}
