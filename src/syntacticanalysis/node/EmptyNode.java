package syntacticanalysis.node;

public class EmptyNode extends TypedNode {
	public EmptyNode(int numLine) {
		super(numLine, "empty");
	}

	public String toString() {return "";}
}
