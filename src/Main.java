import java.io.File;
import java.io.FileNotFoundException;
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

	LexicalAnalysis lexical_analysis = new LexicalAnalysis(source_file_content.toString());
	lexical_analysis.compile();
}