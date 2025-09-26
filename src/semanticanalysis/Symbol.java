package semanticanalysis;

import java.util.List;

public class Symbol {
	public String variable;
	public String type;
	public List<String> parametersTypes;
	public Symbol(String variable, String type, List<String> parametersTypes) {
		this.variable = variable;
		this.type = type;
		this.parametersTypes = parametersTypes;
	}
}
