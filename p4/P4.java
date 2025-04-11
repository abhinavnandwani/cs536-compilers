import java.io.*;
import java_cup.runtime.*;

/****
 * Main program to test the bach parser and name analyzer.
 *
 * There should be 2 command-line arguments:
 * 1. the file to be parsed
 * 2. the output file into which the AST (with types) should be unparsed
 ****/

public class P4 {
    public static void main(String[] args) throws IOException {
        // check command-line arguments
        if (args.length != 2) {
            System.err.println("Please supply input and output file names");
            System.exit(-1);
        }

        // open input file
        FileReader inFile = null;
        try {
            inFile = new FileReader(args[0]);
        } catch (FileNotFoundException ex) {
            System.err.println("File " + args[0] + " not found");
            System.exit(-1);
        }

        // open output file
        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(args[1]);
        } catch (FileNotFoundException ex) {
            System.err.println("File " + args[1] + " could not be opened for writing");
            System.exit(-1);
        }

        // create parser
        parser P = new parser(new Yylex(inFile));
        Symbol root = null;

        // parse
        try {
            root = P.parse();
        } catch (Exception ex) {
            System.err.println("Exception occurred during parse: " + ex);
            System.exit(-1);
        }

        // if there were scanning or parsing errors, don't continue
        if (ErrMsg.anyErrors()) {
            System.exit(-1);
        }

        // do name analysis
        ProgramNode astRoot = (ProgramNode) root.value;
        SymTab globalSymTab = new SymTab();
        astRoot.nameAnalysis(globalSymTab);

        // if name analysis had errors, don't unparse
        if (!ErrMsg.anyErrors()) {
            // unparse to output file
            astRoot.unparse(outFile, 0);
        }

        outFile.close();
        return;
    }
}
