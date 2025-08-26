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
		Tokenizer tokenizer = new Tokenizer();
		System.out.println("----------Tokenizer----------");
		List<Token> tokens = tokenizer.tokenize(source_file_content, regex_python_separators);

		//================PARSED================
		System.out.println("----------Parser----------");
		Parser parser = new Parser();
		List<Node> nodes = parser.parse(tokens);
		for(Node node : nodes){
			System.out.println(node);
		}
		for(Node node : nodes){
			System.out.println(node.toJava());
		}
	}
}