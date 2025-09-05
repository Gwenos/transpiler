package syntacticanalysis.node;

public class FieldDeclaration extends Node {
	private final Node modifiers;
	public final Node type;
	public final String fieldName;
	public final Node expression;
	public FieldDeclaration (int numLine, Node modifiers, Node type, String fieldName, Node expression){
		super(numLine);
		this.modifiers = modifiers;
		this.type = type;
		this.fieldName = fieldName;
		this.expression = expression;
	}
	public String toString (){
		return modifiers + " " + type + " " + fieldName + ((expression.toString().isEmpty()) ? "" : " = " + expression) + ";";
	}

}
