import java.util.List;

public abstract class Node {
	protected String toJava() {
		return "JAVA";
	}
}

//==========================EXPRESSION=================================

class AssignmentNode extends Node{
	private final String identifier;
	private final Node value;
	public AssignmentNode(String identifier, Node value){
		this.identifier = identifier;
		this.value = value;
	}
	public String toString(){return identifier + ANSI.PURPLE + " = " + ANSI.RESET + value;}
	@Override
	public String toJava(){return "type " + identifier + " = " + value.toJava() + " ;";}
}

class OperatorNode extends Node{
	private final Node left;
	private final String operator;
	private final Node right;
	public OperatorNode(Node left, String operaor, Node right){
		this.left = left;
		this.operator = operaor;
		this.right = right;
	}
	public String toString(){return "(" + left + ANSI.PURPLE + " " + operator + " " + ANSI.RESET + right + ")";}
	@Override
	public String toJava(){return "(" + left.toJava() + " " + operator + " " + right.toJava() + ")";}
}

class ArgumentNode extends Node{
	private final Node left;
	private final Node right;
	ArgumentNode(Node left, Node right){
		this.left = left;
		this.right = right;
	}
	public String toString(){return left + ANSI.PURPLE + ", " + ANSI.RESET + right;}
	@Override
	public String toJava(){return left.toJava() + ", " + right.toJava();}
}

class NotNode extends Node{
	private final Node expression;
	public NotNode(Node expression){
		this.expression = expression;
	}
	public String toString(){return ANSI.PURPLE + "not " + ANSI.RESET + expression;}
	@Override
	public String toJava(){return "!"+expression.toJava();}
}

class IfNode extends Node{
	private final Node boolExpr;
	private final Node block;
	private final List<IfNode> orElse;
	IfNode(Node boolExpr, Node block, List<IfNode> orElse){
		this.boolExpr = boolExpr;
		this.block = block;
		this.orElse = orElse;
	}
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append(ANSI.PURPLE + "if " + ANSI.RESET + boolExpr + " :\n    " + block+(orElse.isEmpty()?"":"\n"));
		for (int i = 0; i < orElse.size(); i++) {
			if(i==orElse.size()-1 && orElse.get(i).boolExpr==null){
				str.append(ANSI.PURPLE + "else" + ANSI.RESET+" :\n    "+orElse.get(i).block);
			}else{
				str.append(ANSI.PURPLE + "elif " + ANSI.RESET + orElse.get(i).boolExpr +" :\n    "+orElse.get(i).block+"\n");
			}
		}
		return str.toString();
	}
	@Override
	public String toJava(){
		StringBuilder str = new StringBuilder();
		str.append("if (" + boolExpr.toJava() + ") {\n" + block.toJava()+"\n}");
		for (int i = 0; i < orElse.size(); i++) {
			if(i==orElse.size()-1 && orElse.get(i).boolExpr==null){
				str.append("else"+" {\n    "+orElse.get(i).block.toJava()+"\n}");
			}else{
				str.append("else if (" + orElse.get(i).boolExpr.toJava() +") {\n    "+orElse.get(i).block.toJava()+"\n}");
			}
		}
		return str.toString();
	}
}

class WhileNode extends Node{
	private final Node bool_expr;
	private final Node block;
	WhileNode(Node bool_expr, Node block){
		this.bool_expr = bool_expr;
		this.block = block;
	}
	public String toString(){return ANSI.PURPLE + "while " + ANSI.RESET + bool_expr + " :\n    " + block;}
	@Override
	public String toJava(){return "while (" + bool_expr.toJava() + ") {\n" + block.toJava() + "\n}";}
}

class DefNode extends Node{
	private final String identifier;
	private final Node args;
	private final Node block;
	public DefNode(String identifier, Node args, Node block){
		this.identifier = identifier;
		this.args = args;
		this.block = block;
	}
	public String toString(){
		return ANSI.PURPLE + "def " + ANSI.RESET + identifier + "("+args+") :\n    " + block;
	}
	@Override
	public String toJava(){
		return "public void " + identifier + "("+args.toJava()+") {\n" + block.toJava() + "\n}";
	}
}

class ReturnNode extends Node{
	private final Node expression;
	public ReturnNode(Node expression){
		this.expression = expression;
	}
	public String toString(){return ANSI.PURPLE + "return " + ANSI.RESET + expression;}
	@Override
	public String toJava(){return "return " + expression.toJava() + " ;";	}
}

//=======================================TYPE====================================
//noeud terminaux

class LiteralNode extends Node {
	String value;
	LiteralNode(String value) {
		this.value = value;
	}
	public String toString() { return ANSI.CYAN + value + ANSI.RESET; }
	@Override
	public String toJava() { return value; }
}

class IdentifierNode extends Node {
	String identifier;
	IdentifierNode(String name) {
		this.identifier = name;
	}
	public String toString() { return ANSI.CYAN + identifier + ANSI.RESET; }
	@Override
	public String toJava(){ return identifier;}
}

class FonctionCallNode extends Node {
	String identifier;
	Node args;
	FonctionCallNode(String identifier, Node args) {this.identifier = identifier;this.args = args;}
	public String toString() {return identifier+"("+args+")";}
	@Override
	public String toJava(){return identifier+"("+args.toJava()+") ;";}
}

class BlockNode extends Node{
	List<Node> statements;
	BlockNode(List<Node> statements){
		this.statements = statements;
	}
	public String toString(){
		StringBuilder str = new StringBuilder();
		for(Node statement : statements){
			str.append(statement).append("\n    ");
		}
		return str.toString().substring(0, str.length()-5);
	}
	@Override
	public String toJava(){
		StringBuilder str = new StringBuilder();
		for(Node statement : statements){
			str.append(statement.toJava()).append("\n    ");
		}
		return str.toString().substring(0, str.length()-5);
	}
}