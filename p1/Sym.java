/*
 * Author : Abhinav Nandwani
 * Project : p1 
 */

 /*
  * The Sym class stores information about any generic symbol. 
  */
public class Sym {

    private String type; 

    // this is the constructor; it initializes the Sym class.
    public Sym (String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public String toString(){
        return this.type;
    }
}
