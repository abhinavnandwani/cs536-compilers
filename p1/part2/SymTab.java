import java.util.*;

/**
 * SymTab
 *
 * This class implements a symbol table that manages variable and function names
 * across nested scopes. Each scope is represented as a HashMap<String, Sym>,
 * and the scopes are stored in an ArrayList<HashMap<String, Sym>>.
 * 
 */

public class SymTab {

    // List of hash maps, where each HashMap represents a scope
    private List<HashMap<String, Sym>> SymTabList;

    /**
     * Constructor initializes the symbol table.
     * The symbol table starts with a single global scope.
     */
    public SymTab() {
        // Initialize with an ArrayList for efficient access
        SymTabList = new ArrayList<>();
        SymTabList.add(new HashMap<>()); // Add global scope
    }

    /**
     * Adds a new symbol to the current (innermost) scope.
     * 
     * @param name The name of the symbol.
     * @param sym  The symbol object containing type and other information.
     * @throws SymDuplicateException If the symbol already exists in the current scope.
     * @throws SymTabEmptyException  If the symbol table list is empty.
     */
    public void addDecl(String name, Sym sym) throws SymDuplicateException, SymTabEmptyException {
        // Validate input parameters
        if (name == null || sym == null) {
            throw new IllegalArgumentException("Name or symbol cannot be null.");
        }

        // Ensure the symbol table has at least one scope
        if (SymTabList.isEmpty()) {
            throw new SymTabEmptyException();
        }

        // Retrieve the current (innermost) scope
        Map<String, Sym> currentScope = SymTabList.get(SymTabList.size() - 1);

        // Ensure the symbol is not already declared in the current scope
        if (currentScope.containsKey(name)) {
            throw new SymDuplicateException();
        }

        // Add the new symbol to the current scope
        currentScope.put(name, sym);
    }

    /**
     * Adds a new scope (block) to the symbol table.
     * This represents entering a new block in the code.
     */
    public void addScope() {
        SymTabList.add(new HashMap<>()); // Create and add a new scope
    }

    /**
     * Looks up a symbol in the current (innermost) scope only.
     * 
     * @param name The name of the symbol.
     * @return The symbol if found, otherwise null.
     * @throws SymTabEmptyException If the symbol table is empty.
     */
    public Sym lookupLocal(String name) throws SymTabEmptyException {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        // Ensure the symbol table has at least one scope
        if (SymTabList.isEmpty()) {
            throw new SymTabEmptyException();
        }

        // Get the innermost scope and check for the symbol
        Map<String, Sym> currentScope = SymTabList.get(SymTabList.size() - 1);
        return currentScope.getOrDefault(name, null);
    }

    /**
     * Looks up a symbol across all scopes (global and local).
     * 
     * @param name The name of the symbol.
     * @return The symbol if found, otherwise null.
     * @throws SymTabEmptyException If the symbol table is empty.
     */
    public Sym lookupGlobal(String name) throws SymTabEmptyException {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        if (SymTabList.isEmpty()) {
            throw new SymTabEmptyException();
        }

        // Traverse all scopes from innermost to outermost
        for (int i = SymTabList.size() - 1; i >= 0; i--) {
            if (SymTabList.get(i).containsKey(name)) {
                return SymTabList.get(i).get(name);
            }
        }

        return null; // Symbol not found
    }

    /**
     * Removes the current (innermost) scope from the symbol table.
     * 
     * @throws SymTabEmptyException If there are no scopes left to remove.
     */
    public void removeScope() throws SymTabEmptyException {
        if (SymTabList.isEmpty()) {
            throw new SymTabEmptyException();
        }

        // Remove the last added scope (innermost scope)
        SymTabList.remove(SymTabList.size() - 1);
    }

    /**
     * Prints the contents of the symbol table for debugging.
     */
    public void print() {
        System.out.print("\n*** SymTab ***\n");
        for (int i = SymTabList.size() - 1; i >= 0; i--) {
            System.out.println(SymTabList.get(i).toString());
        }
        System.out.print("\n*** DONE ***\n");
    }
}
