import java.util.*;

/*
 * SymTab class
 */
public class SymTab {
	private List<HashMap<String, Sym>> list;
	
	public SymTab() {
		list = new LinkedList<HashMap<String, Sym>>();
		list.add(new HashMap<String, Sym>());
	}

	// Adds a new declaration to the current (top) scope only.
	public void addDecl(String name, Sym sym) 
	throws SymDuplicateException, SymTabEmptyException {
		if (name == null || sym == null)
			throw new IllegalArgumentException();
		
		if (list.isEmpty())
			throw new SymTabEmptyException();
		
		HashMap<String, Sym> symTab = list.get(0);
		if (symTab.containsKey(name))
			throw new SymDuplicateException();
		
		symTab.put(name, sym);
	}

	// Pushes a new (empty) scope onto the front of the list.
	public void addScope() {
		list.add(0, new HashMap<String, Sym>());
	}

	// Checks the current scope only (i.e., the top HashMap).
	public Sym lookupLocal(String name) 
	throws SymTabEmptyException {
		if (list.isEmpty())
			throw new SymTabEmptyException();
		
		HashMap<String, Sym> symTab = list.get(0); 
		return symTab.get(name);
	}

	// Searches all scopes from innermost to outermost.
	public Sym lookupGlobal(String name) 
	throws SymTabEmptyException {
		if (list.isEmpty())
			throw new SymTabEmptyException();
		
		for (HashMap<String, Sym> symTab : list) {
			Sym sym = symTab.get(name);
			if (sym != null)
				return sym;
		}
		return null;
	}

	// Pops the top scope off the stack.
	public void removeScope() throws SymTabEmptyException {
		if (list.isEmpty())
			throw new SymTabEmptyException();
		list.remove(0);
	}
	
	public void print() {
		System.out.print("\n*** SymTab ***\n");
		for (HashMap<String, Sym> symTab : list) {
			System.out.println(symTab.toString());
		}
		System.out.print("\n*** DONE ***\n");
	}
}
