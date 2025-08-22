import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		String source_filename = args[0];

		String source_file_content = "";

		try {
			File source_file = new File(source_filename);
			Scanner scanner = new Scanner(source_file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				source_file_content += line + '\n';
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		LexicalAnalysis lexical_analysis = new LexicalAnalysis(source_file_content);
		String xxx = lexical_analysis.compile();

//		SyntacticAnalysis syntactic_analysis = new SyntacticAnalysis();


	}
}