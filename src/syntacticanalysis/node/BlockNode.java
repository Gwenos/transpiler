package syntacticanalysis.node;

import java.util.List;

public class BlockNode extends Node {
	public List<Node> statements;
	public BlockNode(int numLine, List<Node> statements){
		super(numLine);
		this.statements = statements;
	}
	public String toString(){
		StringBuilder str = new StringBuilder("BlockNode[\n");
		for(Node statement : statements){
			str.append(statement).append("\n");
		}
		str.append("]");
		return str.toString();
	}
}
