package syntacticanalysis.node;

public class NotNode extends TypedNode {
	public final TypedNode expression;
	public NotNode(int numLine, TypedNode expression){
		super(numLine, "notNode");
		this.expression = expression;
		this.type = expression.type;
	}
	public String toString(){return "!" + expression;}
}
