import lexicalanalysis.Token;
import lexicalanalysis.Tokenizer;
import semanticanalysis.Analyser;
import semanticanalysis.SymbolTable;
import syntacticanalysis.node.Node;
import syntacticanalysis.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public static void main(String[] args) {


	String source_filename = args[0];
	StringBuilder source_file_content = new StringBuilder();
	try {
		File source_file = new File(source_filename);
		Scanner scanner = new Scanner(source_file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			source_file_content.append(line).append('\n');
		}
		scanner.close();
	} catch (FileNotFoundException e) {
		throw new RuntimeException(e);
	}

	//===========ANALYSE LEXICAL================
	Tokenizer tokenizer = new Tokenizer();
	System.out.println("----------lexicalAnalysis.Tokenizer----------");
	long start = System.currentTimeMillis();
	List<Token> tokens = tokenizer.tokenize(source_file_content.toString());
	long time = System.currentTimeMillis() - start;
	System.out.println(tokens.size() + " tokens en [" + time + "ms]");
//		for(lexicalAnalysis.Token token : tokens){
//			System.out.println(token);
//		}

	//================ANALYSE SYNTAXIQUE================
	System.out.println("----------syntacticAnalysis.Parser----------");
	Parser parser = new Parser();
	start = System.currentTimeMillis();
	List<Node> nodes = parser.parse(tokens);
	time = System.currentTimeMillis() - start;
	System.out.println("Parser en [" + time + "ms]");
//	for(Node node : nodes){
//		System.out.println(node);
//	}

	//================ANALYSE SEMANTIQUE================
	System.out.println("----------semanticalAnalysis.Analyser----------");
	Analyser analyser = new Analyser();
	start = System.currentTimeMillis();
	SymbolTable symbolTable = analyser.analyse(nodes);
	time = System.currentTimeMillis() - start;
	System.out.println("Analyser en [" + time + "ms]");
//	for(Map<String, Symbol> x : symbolTable.symbols){
//		for(Symbol s : x.values()){
//			System.out.println(s.variable + " " + s.type);
//		}
//		System.out.println();
//	}

}