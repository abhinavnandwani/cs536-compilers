import java.util.*;

/**
 * SymTab
 *
 * This class implements a symbol table that manages variable and function names
 * across nested scopes. Each scope is represented as a HashMap<String, Sym>,
 * and
 * the scopes are stored in a LinkedList<HashMap<String, Sym>>.
 * 
 * A LinkedList is used instead of an ArrayList because pushing and popping
 * scopes
 * are frequent operations and can be done in O(1) time.
 */

public class SymTab {

    // A linked list of hash maps, where each HashMap represents a scope
    private List<HashMap<String, Sym>> SymTabList;

    // Constructor to initialize the class
    public SymTab() {

        // Initialize the list with one empty HashMap representing the global scope
        SymTabList = new LinkedList<>();
        SymTabList.addFirst(new HashMap<>()); // Add global scope
    }

    /**
     * Adds a new symbol to the current (innermost) scope.
     * 
     * @param name The name of the symbol.
     * @param sym  The symbol object containing type and other information.
     * @throws SymDuplicateException If the symbol already exists in the current
     *                               scope.
     * @throws SymTabEmptyException  If the symbol table list is empty.
     */
    public void addDecl(String name, Sym sym) throws SymDuplicateException, SymTabEmptyException {

        // Check for invalid arguments
        if (name == null || sym == null) {
            throw new IllegalArgumentException();
        }

        // Ensure the symbol table has at least one scope
        if (SymTabList.isEmpty()) {
            throw new SymTabEmptyException();
        }

        // Retrieve the current (innermost) scope
        Map<String, Sym> currentScope = SymTabList.getFirst();

        // Ensure the symbol is not already declared in the current scope
        if (currentScope.containsKey(name)) {
            throw new SymDuplicateException();
        }

        // Add the new symbol to the current scope
        currentScope.put(name, sym);
    }

    public void addScope() {
        SymTabList.addFirst(new HashMap<>());
    }

    public Sym lookupLocal(String name) throws SymTabEmptyException {

        if (name == null) {
            throw new IllegalArgumentException();
        }

        // Ensure the symbol table has at least one scope
        if (SymTabList.isEmpty()) {
            throw new SymTabEmptyException();
        }

        if (SymTabList.getFirst().containsKey(name)) {
            return SymTabList.getFirst().get(name);
        }

        return null;
    }

    public Sym lookupGlobal(String name) throws SymTabEmptyException {

        if (name == null) {
            throw new IllegalArgumentException();
        }

        if (SymTabList.isEmpty()) {
            throw new SymTabEmptyException();
        }

        for (HashMap<String, Sym> map : SymTabList) {
            if (map.containsKey(name)) {
                return map.get(name);
            }
        }

        return null;

    }

    public void removeScope() throws SymTabEmptyException {

        if (SymTabList.isEmpty()) {
            throw new SymTabEmptyException();
        }

        SymTabList.removeFirst();
    }

    public void print() {
        System.out.print("\n*** SymTab ***\n");
        for (HashMap<String, Sym> map : SymTabList) {
            System.out.println(map.toString());
        }
        System.out.print("\n*** DONE ***\n");
    }

}
