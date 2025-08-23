import java.util.ArrayList;
import java.util.List;

public class LexicalAnalysis {

	private final String source_file_content;
	private final String regex_python_separators = "\n";

	public LexicalAnalysis(String source_file_content){
		this.source_file_content = source_file_content;
	}

	public String compile(){

		//===========TOKENIZED================
		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokens = tokenizer.tokenize(source_file_content, regex_python_separators);
		for(Token token : tokens){
			System.out.println(token);
		}

		//================PARSED================
//		Parser parser = new Parser();
//		List<Node> nodes = parser.parse(tokens);
//		for(Node node : nodes){
//			System.out.println(node);
//		}

		return "";
	}
}
