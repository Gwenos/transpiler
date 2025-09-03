import java.util.ArrayList;
import java.util.List;

public class LexicalAnalysis {

	private final String source_file_content;

	public LexicalAnalysis(String source_file_content){
		this.source_file_content = source_file_content;
	}

	public void compile(){

		String regex_python_separators = "\n";

		//===========TOKENIZED================
		//analyse lexical
		Tokenizer tokenizer = new Tokenizer();
		System.out.println("----------Tokenizer----------");
		long start = System.currentTimeMillis();
		List<Token> tokens = tokenizer.tokenize(source_file_content, regex_python_separators);
		long time = System.currentTimeMillis() - start;
		System.out.println(tokens.size() + " tokens en [" + time + "ms]");
//		for(Token token : tokens){
//			System.out.println(token);
//		}

		//================PARSED================
		//analyse syntaxique
		System.out.println("----------Parser----------");
		Parser parser = new Parser();
		start = System.currentTimeMillis();
		List<Node> nodes = parser.parse(tokens);
		time = System.currentTimeMillis() - start;
		System.out.println(nodes.size() + " nodes en [" + time + "ms]");
		for(Node node : nodes){
			System.out.println(node);
		}
//		for(Node node : nodes){
//			System.out.println(node.toJava());
//		}

		//================TYPE CHECK================
//		TypeChecker typeChecker = new TypeChecker();
//		typeChecker.check();

	}
}