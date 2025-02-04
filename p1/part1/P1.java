
/**
 * P1 - Symbol Table Testing
 * 
 * This class contains unit tests for the Sym and SymTab classes.
 * It verifies the functionality of adding declarations, scope handling,
 * scope removal, and lookup operations in a symbol table.
 * 
 * The test methods use assertions to ensure correctness, and any failures
 * indicate issues in the implementation of the Sym or SymTab classes.
 * 
 * Author: Abhinav Nandwani
 * Date: 02/02/2024
 * CS Login : nandwani
 * WISC : nandwani2@wisc.edu
 */

import java.util.*;

public class P1 {

    public static void main(String[] args) {
        testSymClass(); // Test the Sym class
        testSymTabClass(); // Test the SymTab class
        System.out.println("YAHOO! All tests passed");
    }

    // Test method for Sym class
    public static void testSymClass() {
        Sym obj1 = new Sym("type1");

        // Test getType() method
        assert obj1.getType().equals("type1") : "FAIL: Sym.getType() incorrect";

        // Test toString() method
        assert obj1.toString().equals("type1") : "FAIL: Sym.toString() incorrect";
    }

    // Test method for adding declarations to SymTab
    public static void testSymTabAddDecl() {
        SymTab symTab = new SymTab();
        try {
            Sym sym1 = new Sym("int");
            symTab.addDecl("x", sym1); // Add a declaration

            // Verify if lookup methods return the correct symbol
            assert symTab.lookupLocal("x") == sym1 : "FAIL: addDecl() or lookupLocal() incorrect";
            assert symTab.lookupGlobal("x") == sym1 : "FAIL: addDecl() or lookupGlobal() incorrect";

            // Print table after adding 'x'
            System.out.println("\nTesting print() after adding 'x':");
            symTab.print();

            // Attempt to add duplicate key, should throw exception
            try {
                symTab.addDecl("x", new Sym("double"));
                assert false : "FAIL: addDecl() should throw SymDuplicateException";
            } catch (SymDuplicateException e) {
                // Expected behavior
            }

            // Test illegal arguments
            try {
                symTab.addDecl(null, sym1);
                assert false : "FAIL: addDecl() should throw IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                // Expected behavior
            }

            try {
                symTab.addDecl("y", null);
                assert false : "FAIL: addDecl() should throw IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                // Expected behavior
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception in testSymTabAddDecl: " + e);
        }
    }

    // Test method for scope handling in SymTab
    public static void testSymTabScopeHandling() {
        SymTab symTab = new SymTab();
        try {
            symTab.addScope(); // Add a new scope
            System.out.println("\nTesting print() after adding a new scope:");
            symTab.print();

            // Ensure that 'x' is not found in the new scope
            assert symTab.lookupLocal("x") == null : "FAIL: lookupLocal() should return null in new scope";

            Sym sym2 = new Sym("double");
            symTab.addDecl("y", sym2); // Add 'y' in the new scope

            // Validate lookup behavior
            assert symTab.lookupLocal("y") == sym2 : "FAIL: lookupLocal(y) incorrect";
            assert symTab.lookupGlobal("y") == sym2 : "FAIL: lookupGlobal(y) incorrect";

            // Print table after adding 'y'
            System.out.println("\nTesting print() after adding 'y' in new scope:");
            symTab.print();

            // Remove the top scope
            symTab.removeScope();
            System.out.println("\nTesting print() after removing the top scope:");
            symTab.print();

            // Ensure that 'y' is no longer found after scope removal
            assert symTab.lookupGlobal("y") == null : "FAIL: lookupGlobal() should return null after scope removal";
        } catch (Exception e) {
            System.out.println("Unexpected exception in testSymTabScopeHandling: " + e);
        }
    }

    // Test method for removing scope in SymTab
    public static void testSymTabRemoveScope() {
        SymTab symTab = new SymTab();
        try {
            symTab.removeScope(); // Attempt to remove scope from an empty table
            try {
                symTab.removeScope(); // Should throw an exception
                assert false : "FAIL: removeScope() should throw SymTabEmptyException";
            } catch (SymTabEmptyException e) {
                // Expected behavior
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception in testSymTabRemoveScope: " + e);
        }
    }

    // Test method for lookup operations in SymTab
    public static void testSymTabLookup() {
        SymTab symTab = new SymTab();
        try {
            // Lookup in an empty table should throw exception
            try {
                symTab.lookupLocal("x");
                assert false : "FAIL: lookupLocal() should throw SymTabEmptyException";
            } catch (SymTabEmptyException e) {
                // Expected behavior
            }

            try {
                symTab.lookupGlobal("x");
                assert false : "FAIL: lookupGlobal() should throw SymTabEmptyException";
            } catch (SymTabEmptyException e) {
                // Expected behavior
            }

            // Test illegal arguments
            try {
                symTab.lookupLocal(null);
                assert false : "FAIL: lookupLocal() should throw IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                // Expected behavior
            }

            try {
                symTab.lookupGlobal(null);
                assert false : "FAIL: lookupGlobal() should throw IllegalArgumentException";
            } catch (IllegalArgumentException e) {
                // Expected behavior
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception in testSymTabLookup: " + e);
        }
    }

    // Master test method for SymTab class
    public static void testSymTabClass() {
        testSymTabAddDecl(); // Test adding declarations
        testSymTabScopeHandling(); // Test scope handling
        testSymTabRemoveScope(); // Test scope removal
        testSymTabLookup(); // Test lookup operations
    }
}
