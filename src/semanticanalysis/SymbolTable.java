package semanticanalysis;

import util.Error;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

	public final Deque<Map<String, Symbol>> symbols = new ArrayDeque<>();

	public SymbolTable(){
		enterScope();
	}

	public void enterScope(){
		symbols.push(new HashMap<>());
	}

	public void exitScope(){
		symbols.pop();
	}

	public void declare(int numLine, String variable, String type) {
		Map<String, Symbol> current = symbols.peek();
		if (current.containsKey(variable)) {
			Error.semanticError(numLine, "Variable " + variable + " already declared");
		}else{
			current.put(variable, new Symbol(variable, type));
		}
	}

	public Symbol lookup(int numLine, String variable) {
		for(Map<String, Symbol> current : symbols) {
			if (current.containsKey(variable)) {
				return current.get(variable);
			}
		}
		Error.semanticError(numLine, "Undeclared identifier: " + variable);
		return null;
	}

	public boolean exists(String variable) {
		for(Map<String, Symbol> current : symbols) {
			if (current.containsKey(variable)) {
				return true;
			}
		}
		return false;
	}
}
