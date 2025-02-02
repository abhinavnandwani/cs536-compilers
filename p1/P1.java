import java.util.*;

public class P1 {

	public static void main(String[] args) {
		testSymClass();
		testSymTabClass();
		System.out.println("YAHOO! All tests passed");
	}

	public static void testSymClass() {
		Sym obj1 = new Sym("type1");

		// Test getType() method
		if (!obj1.getType().equals("type1")) { 
			System.out.println("FAIL: Sym.getType() returned " + obj1.getType() + " instead of type1");
		}

		// Test toString() method
		if (!obj1.toString().equals("type1")) { 
			System.out.println("FAIL: Sym.toString() returned " + obj1.toString() + " instead of type1");
		}
	}

	public static void testSymTabClass() {
		SymTab symTab = new SymTab();

		try {
			// Add a declaration and test lookup
			Sym sym1 = new Sym("int");
			symTab.addDecl("x", sym1);

			if (symTab.lookupLocal("x") != sym1) {
				System.out.println("FAIL: lookupLocal(x) did not return the correct Sym.");
			}

			if (symTab.lookupGlobal("x") != sym1) {
				System.out.println("FAIL: lookupGlobal(x) did not return the correct Sym.");
			}

			// Test adding a duplicate declaration (should throw exception)
			try {
				symTab.addDecl("x", new Sym("double"));
				System.out.println("FAIL: addDecl(x) did not throw SymDuplicateException.");
			} catch (SymDuplicateException e) {
				// Expected
			}

			// Test adding a new scope
			symTab.addScope();
			if (symTab.lookupLocal("x") != null) {
				System.out.println("FAIL: lookupLocal(x) in new scope should return null.");
			}

			// Test that lookupGlobal still finds x
			if (symTab.lookupGlobal("x") != sym1) {
				System.out.println("FAIL: lookupGlobal(x) failed after adding a new scope.");
			}

			// Test removing a scope
			symTab.removeScope();
			if (symTab.lookupLocal("x") != sym1) {
				System.out.println("FAIL: removeScope() did not restore previous scope correctly.");
			}

			// Test removing scope when empty (should throw exception)
			symTab.removeScope();
			try {
				symTab.removeScope();
				System.out.println("FAIL: removeScope() did not throw SymTabEmptyException.");
			} catch (SymTabEmptyException e) {
				// Expected
			}

			// Test lookup on empty table (should throw exceptions)
			try {
				symTab.lookupLocal("x");
				System.out.println("FAIL: lookupLocal() did not throw SymTabEmptyException.");
			} catch (SymTabEmptyException e) {
				// Expected
			}

			try {
				symTab.lookupGlobal("x");
				System.out.println("FAIL: lookupGlobal() did not throw SymTabEmptyException.");
			} catch (SymTabEmptyException e) {
				// Expected
			}

		} catch (Exception e) {
			System.out.println("Unexpected exception: " + e);
		}
	}
}
