import java.util.*;
import java.io.*;
import java_cup.runtime.*; // defines Symbol

/**
 * This program is to be used to test the bach scanner.
 * This version is set up to test all tokens, but more code is needed to test
 * other aspects of the scanner (e.g., input that causes errors, character
 * numbers, values associated with tokens)
 *
 * Author: Abhinav Nandwani
 * Date: 02/18/2024
 * CS Login : nandwani
 * WISC : nandwani2@wisc.edu
 */
public class P2 {
    public static void main(String[] args) throws IOException {
        // Run tests on multiple categories
        testAllTokens();
        testStringTokens();
        testIntegerTokens();
        testCharacterCount();
        testIllegalCharacter();
        testEOF();  // Final test for unterminated string literal (EOF)
    }

    /**
     * Runs a scanner test on a given input file and writes results to an output
     * file.
     */
    private static void runTest(String inputFileName, String outputFileName) throws IOException {
        CharNum.num = 1;

        FileReader inFile = new FileReader(inputFileName);
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFileName));

        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();

        while (token.sym != sym.EOF) {
            outFile.println(formatToken(token));
            token = scanner.next_token();
        }

        outFile.close();
    }

    /**
     * Formats a token into a string for output, including line and character
     * number.
     */
    private static String formatToken(Symbol token) {
        String tokenText = "";
        TokenVal tokenVal = (TokenVal) token.value;

        switch (token.sym) {
            case sym.BOOLEAN:
                tokenText = "boolean";
                break;
            case sym.INTEGER:
                tokenText = "integer";
                break;
            case sym.VOID:
                tokenText = "void";
                break;
            case sym.STRUCT:
                tokenText = "struct";
                break;
            case sym.IF:
                tokenText = "if";
                break;
            case sym.ELSE:
                tokenText = "else";
                break;
            case sym.WHILE:
                tokenText = "while";
                break;
            case sym.INPUT:
                tokenText = "input";
                break;
            case sym.DISPLAY:
                tokenText = "disp";
                break;
            case sym.RETURN:
                tokenText = "return";
                break;
            case sym.TRUE:
                tokenText = "TRUE";
                break;
            case sym.FALSE:
                tokenText = "FALSE";
                break;
            case sym.ID:
                tokenText = ((IdTokenVal) tokenVal).idVal;
                break;
            case sym.INTLIT:
                tokenText = String.valueOf(((IntLitTokenVal) tokenVal).intVal);
                break;
            case sym.STRINGLIT:
                tokenText = ((StrLitTokenVal) tokenVal).strVal;
                break;
            case sym.LCURLY:
                tokenText = "{";
                break;
            case sym.RCURLY:
                tokenText = "}";
                break;
            case sym.LPAREN:
                tokenText = "(";
                break;
            case sym.RPAREN:
                tokenText = ")";
                break;
            case sym.LSQUARE:
                tokenText = "[";
                break;
            case sym.RSQUARE:
                tokenText = "]";
                break;
            case sym.COLON:
                tokenText = ":";
                break;
            case sym.COMMA:
                tokenText = ",";
                break;
            case sym.DOT:
                tokenText = ".";
                break;
            case sym.READOP:
                tokenText = "->";
                break;
            case sym.WRITEOP:
                tokenText = "<-";
                break;
            case sym.PLUSPLUS:
                tokenText = "++";
                break;
            case sym.MINUSMINUS:
                tokenText = "--";
                break;
            case sym.PLUS:
                tokenText = "+";
                break;
            case sym.MINUS:
                tokenText = "-";
                break;
            case sym.TIMES:
                tokenText = "*";
                break;
            case sym.DIVIDE:
                tokenText = "/";
                break;
            case sym.NOT:
                tokenText = "^";
                break;
            case sym.AND:
                tokenText = "&";
                break;
            case sym.OR:
                tokenText = "|";
                break;
            case sym.EQUALS:
                tokenText = "==";
                break;
            case sym.NOTEQ:
                tokenText = "^=";
                break;
            case sym.LESS:
                tokenText = "<";
                break;
            case sym.GREATER:
                tokenText = ">";
                break;
            case sym.LESSEQ:
                tokenText = "<=";
                break;
            case sym.GREATEREQ:
                tokenText = ">=";
                break;
            case sym.ASSIGN:
                tokenText = "=";
                break;
            default:
                tokenText = "!!! UNKNOWN TOKEN !!!";
                break;
        }

        return String.format("%-10s (Line: %d, Char: %d)", tokenText, tokenVal.lineNum, tokenVal.charNum);
    }

    /**
     * testAllTokens
     *
     * Open and read from file allTokens.txt
     * For each token read, write the corresponding string to allTokens.out
     * If the input file contains all tokens, one per line, we can verify
     * correctness of the scanner by comparing the input and output files
     * (e.g., using a 'diff' command).
     */
    private static void testAllTokens() throws IOException {
        System.out.println("Running testAllTokens...");
        runTest("allTokens.in", "allTokens.out");
    }

    /**
     * testStringTokens
     *
     * Open and read from file stringTokens.in
     * For each string token read, write the corresponding string to stringTokens.out
     * This helps verify correct handling of valid and invalid string literals.
     */
    private static void testStringTokens() throws IOException {
        System.out.println("Running testStringTokens...");
        runTest("stringTokens.in", "stringTokens.out");
    }



    /**
     * testIntegerTokens
     *
     * Open and read from file integerTokens.in
     * For each integer token read, write the corresponding value to integerTokens.out
     * This helps verify correct handling of integer literals.
     */
    private static void testIntegerTokens() throws IOException {
        System.out.println("Running testIntegerTokens...");
        runTest("integerTokens.in", "integerTokens.out");
    }

    /**
     * testCharacterCount
     *
     * Open and read from file charCountTokens.in
     * For each token read, write the corresponding character count to charCountTokens.out
     * This helps verify correct handling of character positions in tokens.
     */
    private static void testCharacterCount() throws IOException {
        System.out.println("Running testCharacterCount...");
        runTest("charCountTokens.in", "charCountTokens.out");
    }

    /**
     * testIllegalCharacter
     *
     * Open and read from file illegalCharacter.in
     * For each token read, write the corresponding token to illegalCharacter.out
     * This helps verify correct handling of illegal characters.
     */
    private static void testIllegalCharacter() throws IOException {
        System.out.println("Running testIllegalCharacter...");
        runTest("illegalCharacter.in", "illegalCharacter.out");
    }


    /**
     * testEOF
     *
     * Open and read from file eof.txt, which contains an unterminated string literal.
     * This test ensures that the scanner correctly handles the case where EOF is reached
     * before the closing quote of a string literal.
     * 
     * On Linux, running "cat eof.txt" should show the command-line prompt immediately
     * at the end of the last line, confirming there is no final newline.
     */
    private static void testEOF() throws IOException {
        System.out.println("Running testEOF (unterminated string literal)...");
        runTest("eof.txt", "eof.out");
    }
}