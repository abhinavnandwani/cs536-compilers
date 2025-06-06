import java.io.*;
import java_cup.runtime.*;

/****
 * Main program to test the bach parser.
 *
 * There should be 2 command-line arguments:
 * 1. the file to be parsed
 * 2. the output file into which the AST built by the parser should be unparsed
 ****/

public class P5 {
    public static void main(String[] args)
        throws IOException // may be thrown by the scanner
    {
        // check for command-line args
        if (args.length != 2) {
            System.err.println("please supply name of file to be parsed " +
			                   "and name of file for unparsed version");
            System.exit(-1);
        }

        // open input file
        FileReader inFile = null;
        try {
            inFile = new FileReader(args[0]);
        } catch (FileNotFoundException ex) {
            System.err.println("file " + args[0] + " not found");
            System.exit(-1);
        }

        // open output file
        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(args[1]);
        } catch (FileNotFoundException ex) {
            System.err.println("file " + args[1] +
                               " could not be opened for writing");
            System.exit(-1);
        }

        parser P = new parser(new Yylex(inFile));

        Symbol root = null; // parser returns a Symbol whose value field
                            // is the translation of the root nonterminal
                            // (i.e., of the nonterminal "program")

        try {
            root = P.parse(); // do the parse
            System.out.println ("program parsed correctly");
        } catch (Exception ex){
            System.err.println("exception occured during parse: " + ex);
            System.exit(-1);
        }
		
        ((ProgramNode)root.value).nameAnalysis();  // perform name analysis
        ((ProgramNode) root.value).typeCheck();    // perform type checking

        if (!ErrMsg.getErr()) {  // if no errors, unparse
            ((ASTnode)root.value).unparse(outFile, 0);
        }
        outFile.close();

        return;
    }
}
