package semanticAnalysis;

import util.Error;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

	private final Deque<Map<String, Symbol>> symbols = new ArrayDeque<>();

	public SymbolTable(){
		enterScope();
	}

	public void enterScope(){
		symbols.push(new HashMap<>());
	}

	public void exitScope(){
		symbols.pop();
	}

	public void declare(String variable, String type) {
		Map<String, Symbol> current = symbols.peek();
		assert current != null;
		if (current.containsKey(variable)) {
			Error.semanticError("Variable " + variable + " already declared");
		}
		current.put(variable, new Symbol(variable, type));
	}

	public Symbol lookup(String variable) {
		for(Map<String, Symbol> current : symbols) {
			if (current.containsKey(variable)) {
				return current.get(variable);
			}
		}
		Error.semanticError("Undeclared identifier: " + variable);
		return null;
	}
}
