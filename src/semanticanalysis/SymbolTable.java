package semanticanalysis;

import util.Error;

import java.util.*;

public class SymbolTable {

	private final Deque<Map<String, Symbol>> SYMBOLS = new ArrayDeque<>();

	public SymbolTable(){
		enterScope();
	}

	public void enterScope(){
		this.SYMBOLS.push(new HashMap<>());
	}

	public void exitScope(){
		this.SYMBOLS.pop();
	}

	public void declare(int numLine, String variable, String type, List<String> parametersType) {
		Map<String, Symbol> current = this.SYMBOLS.peek();
		if (current.containsKey(variable)) {
			Error.semanticError(numLine, "Variable " + variable + " already declared");
		}else{
			current.put(variable, new Symbol(variable, type, parametersType));
		}
	}

	public Symbol lookup(int numLine, String variable) {
		for(Map<String, Symbol> current : this.SYMBOLS) {
			if (current.containsKey(variable)) {
				return current.get(variable);
			}
		}
		Error.semanticError(numLine, "Undeclared identifier: " + variable);
		return null;
	}
}
