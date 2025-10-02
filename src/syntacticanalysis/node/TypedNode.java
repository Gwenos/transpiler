package syntacticanalysis.node;

public abstract class TypedNode extends Node {
	public String type;

	public TypedNode(int numLine, String type) {
		super(numLine);
		this.type = type;
	}
}
