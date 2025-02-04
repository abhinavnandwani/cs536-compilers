/*
 * Author : Abhinav Nandwani
 * Project : p1
 */

/*
 * The Sym class stores information about any generic symbol.
 */
public class Sym {

    private String type; // Stores the type of the symbol as a string

    /*
     * Constructor for the Sym class.
     * Initializes the type of the symbol.
     * 
     * @param type The type of the symbol.
     */
    public Sym(String type) {
        this.type = type;
    }

    /*
     * Returns the type of the symbol.
     * 
     * @return The type as a string.
     */
    public String getType() {
        return this.type;
    }

    /*
     * Returns a string representation of the symbol.
     * 
     * @return The type of the symbol as a string.
     */
    public String toString() {
        return this.type;
    }
}
