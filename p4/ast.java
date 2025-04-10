import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a bach program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and identifiers contain line and character 
// number information; for string literals and identifiers, they also 
// contain a string; for integer literals, they also contain an integer 
// value.
//
// Here are all the different kinds of AST nodes and what kinds of 
// children they have.  All of these kinds of AST nodes are subclasses
// of "ASTnode".  Indentation indicates further subclassing:
//
//     Subclass              Children
//     --------              --------
//     ProgramNode           DeclListNode
//     DeclListNode          linked list of DeclNode
//     DeclNode:
//       VarDeclNode         TypeNode, IdNode, int
//       FuncDeclNode        TypeNode, IdNode, FormalsListNode, FuncBodyNode
//       FormalDeclNode      TypeNode, IdNode
//       StructDeclNode      IdNode, DeclListNode
//
//     StmtListNode          linked list of StmtNode
//     ExpListNode           linked list of ExpNode
//     FormalsListNode       linked list of FormalDeclNode
//     FuncBodyNode          DeclListNode, StmtListNode
//
//     TypeNode:
//       BooleanNode         --- none ---
//       IntegerNode         --- none ---
//       VoidNode            --- none ---
//       StructNode          IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignExpNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       TrueNode            --- none ---
//       FalseNode           --- none ---
//       IdNode              --- none ---
//       IntLitNode          --- none ---
//       StrLitNode          --- none ---
//       StructAccessNode    ExpNode, IdNode
//       AssignExpNode       ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         EqualsNode
//         NotEqNode
//         LessNode
//         LessEqNode
//         GreaterNode
//         GreaterEqNode
//         AndNode
//         OrNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of children, 
// or internal nodes with a fixed number of children:
//
// (1) Leaf nodes:
//        BooleanNode,  IntegerNode,  VoidNode,    IdNode,  
//        TrueNode,     FalseNode,    IntLitNode,  StrLitNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, StmtListNode, ExpListNode, FormalsListNode
//
// (3) Internal nodes with fixed numbers of children:
//        ProgramNode,     VarDeclNode,      FuncDeclNode,  FormalDeclNode,
//        StructDeclNode,  FuncBodyNode,     StructNode,    AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode,  IfStmtNode,    IfElseStmtNode,
//        WhileStmtNode,   ReadStmtNode,     WriteStmtNode, CallStmtNode,
//        ReturnStmtNode,  StructAccessNode, AssignExpNode, CallExpNode,
//        UnaryExpNode,    UnaryMinusNode,   NotNode,       BinaryExpNode,   
//        PlusNode,        MinusNode,        TimesNode,     DivideNode,
//        EqualsNode,      NotEqNode,        LessNode,      LessEqNode,
//        GreaterNode,     GreaterEqNode,    AndNode,       OrNode
//
// **********************************************************************

// **********************************************************************
//   ASTnode class (base class for all other kinds of nodes)
// **********************************************************************F

abstract class ASTnode { 
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void doIndent(PrintWriter p, int indent) {
        for (int k=0; k<indent; k++) p.print(" ");
    }
}

// **********************************************************************
//   ProgramNode, DeclListNode, StmtListNode, ExpListNode, 
//   FormalsListNode, FuncBodyNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    // 1 child
    private DeclListNode myDeclList;

    public boolean nameAnalysis(SymTab symTab) {
        return myDeclList.nameAnalysis(symTab);
    }
    
    
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    // list of children (DeclNodes)
    private List<DeclNode> myDecls;

    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
    
        for (DeclNode decl : myDecls) {
            if (!decl.nameAnalysis(symTab)) {
                allOk = false;
            }
        }
    
        return allOk;
    }
    


    
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        } 
    }

    // list of children (StmtNodes)
    private List<StmtNode> myStmts;


    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
    
        for (StmtNode stmt : myStmts) {
            if (!stmt.nameAnalysis(symTab)) {
                allOk = false;
            }
        }
    
        return allOk;
    }
    
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) {         // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of children (ExpNodes)
    private List<ExpNode> myExps;

    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
    
        for (ExpNode exp : myExps) {
            if (!exp.nameAnalysis(symTab)) {
                allOk = false;
            }
        }
    
        return allOk;
    }
    
}
class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    // list of children (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;


    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
        for (FormalDeclNode formal : myFormals) {
            if (!formal.nameAnalysis(symTab)) {
                allOk = false;
            }
        }
        return allOk;
    }
    
    public List<String> getParamTypes() {
        List<String> types = new ArrayList<>();
        for (FormalDeclNode formal : myFormals) {
            types.add(formal.getType());  // Assumes you’ve implemented getType() in FormalDeclNode
        }
        return types;
    }
    
}

class FuncBodyNode extends ASTnode {
    public FuncBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    // 2 children
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
    
        if (!myDeclList.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        if (!myStmtList.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        return allOk;
    }
    
}


// **********************************************************************
// *****  DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    abstract public boolean nameAnalysis(SymTab symTab);
}

// declaration of a variable
class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(".");
    }

    // 3 children
    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NON_STRUCT if this is not a struct type

    public static int NON_STRUCT = -1;

    public boolean nameAnalysis(SymTab symTab) {
        boolean error = false;
        Sym sym = null;
    
        if (myType instanceof VoidNode) {
            // Variables cannot be declared void
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Non-function declared void");
            error = true;
        }
        else if (myType instanceof StructNode) {
            // struct Point p.
            StructNode structNode = (StructNode) myType;
            IdNode structId = structNode.getID();  // assumes StructNode has getID()
    
            Sym structSym = null;
            try {
                structSym = symTab.lookupGlobal(structId.name());
            } catch (SymTabEmptyException e) {
                System.err.println("Unexpected SymTabEmptyException during struct lookup");
                System.exit(-1);
            }
    
            if (structSym == null || !(structSym instanceof StructDefSym)) {
                ErrMsg.fatal(structId.lineNum(), structId.charNum(), "Name of struct type invalid");
                error = true;
            } else {
                // Valid struct type, create a struct-var symbol
                sym = new StructVarSym(structId.name(), (StructDefSym) structSym);
            }
        }
        else {
            // Basic types like integer or boolean
            String typeName = myType.toString();  
            sym = new Sym(typeName);              
        }
    
        // If no type errors, try adding the declaration to the current scope
        if (!error) {
            try {
                symTab.addDecl(myId.name(), sym);
                // Do NOT link the declared IdNode — declarations don't get links
            } catch (SymDuplicateException e) {
                ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Identifier multiply-declared");
            } catch (SymTabEmptyException e) {
                System.err.println("Unexpected SymTabEmptyException in VarDeclNode.nameAnalysis");
                System.exit(-1);
            }
        }
    
        return !error;
    }
    
    

}

class FuncDeclNode extends DeclNode {
    public FuncDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FuncBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("[");
        myFormalsList.unparse(p, 0);
        p.println("] [");
        myBody.unparse(p, indent+4);
        p.println("]\n");
    }

    // 4 children
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FuncBodyNode myBody;

    public boolean nameAnalysis(SymTab symTab) {
        boolean hasError = false;
    
        // Step 1: Check for multiple declarations of this function name
        try {
            if (symTab.lookupLocal(myId.name()) != null) {
                ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Identifier multiply-declared");
                hasError = true;
            }
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in FuncDeclNode.nameAnalysis");
            System.exit(-1);
        }
    
        // Step 2: Get parameter types from the formals list
        List<String> paramTypes = myFormalsList.getParamTypes(); // e.g., ["int", "boolean"]
    
        // Step 3: Create a FunctionSym with return type and parameters
        String returnType = myType.toString(); // or use myType.getType() if defined
        FunctionSym funcSym = new FunctionSym(returnType, paramTypes);
    
        // Step 4: Add function to symbol table if not already declared
        if (!hasError) {
            try {
                symTab.addDecl(myId.name(), funcSym);
            } catch (SymDuplicateException e) {
                ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Identifier multiply-declared");
                hasError = true;
            } catch (SymTabEmptyException e) {
                System.err.println("Unexpected SymTabEmptyException in FuncDeclNode.nameAnalysis");
                System.exit(-1);
            }
        }
        
        // Step 6: Push new scope for the function body
        symTab.addScope();
    
        // Step 7: Add formal parameters to the new scope
        if (!myFormalsList.nameAnalysis(symTab)) {
            hasError = true;
        }
    
        // Step 8: Analyze body (includes local vars and statements)
        if (!myBody.nameAnalysis(symTab)) {
            hasError = true;
        }
    
        // Step 9: Pop function scope
        try {
            symTab.removeScope();
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in FuncDeclNode.nameAnalysis (removing scope)");
            System.exit(-1);
        }
    
        return !hasError;
    }
    

}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    public String getType() {
        return myType.toString();
    }
    

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }

    // 2 children
    private TypeNode myType;
    private IdNode myId;

    public boolean nameAnalysis(SymTab symTab) {
        boolean hasError = false;
    
        if (myType instanceof VoidNode) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Non-function declared void");
            hasError = true;
        }
    
        Sym sym = new Sym(myType.toString());  // Or VarSym, if defined
    
        try {
            symTab.addDecl(myId.name(), sym);
        } catch (SymDuplicateException e) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Identifier multiply-declared");
            hasError = true;
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in FormalDeclNode.nameAnalysis");
            System.exit(-1);
        }
    
        return !hasError;
    }
    



}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct ");
        myId.unparse(p, 0);
        p.println(" [");
        myDeclList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("]\n");
    }

    // 2 children
    private IdNode myId;
    private DeclListNode myDeclList;

    public boolean nameAnalysis(SymTab symTab) {
        boolean hasError = false;
    
        // Step 1: Check if the struct name is already declared
        try {
            if (symTab.lookupLocal(myId.name()) != null) {
                ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Identifier multiply-declared");
                return false; // Per spec, don't process struct fields in this case
            }
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in StructDeclNode.nameAnalysis");
            System.exit(-1);
        }
    
        // Step 2: Create a new symbol table for struct fields
        SymTab structFieldTable = new SymTab();
    
        // Step 3: Analyze the field declarations in the struct using the new SymTab
        if (!myDeclList.nameAnalysis(structFieldTable)) {
            hasError = true;
        }
    
        // Step 4: Create a StructDefSym to hold the field table
        StructDefSym structSym = new StructDefSym(structFieldTable);
    
        // Step 5: Add the struct name to the outer (global) symbol table
        try {
            symTab.addDecl(myId.name(), structSym);
           
        } catch (SymDuplicateException e) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Identifier multiply-declared");
            hasError = true;
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in StructDeclNode.nameAnalysis");
            System.exit(-1);
        }
    
        return !hasError;
    }
    


}

// **********************************************************************
// ****  TypeNode and its subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
}

class BooleanNode extends TypeNode {
    public BooleanNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("boolean");
    }
    @Override
    public String toString() {
        return "boolean";
    }
}

class IntegerNode extends TypeNode {
    public IntegerNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("integer");
    }
    @Override
    public String toString() {
        return "integer";
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }

    @Override
    public String toString() {
        return "void";
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        myId.unparse(p, 0);
    }

    // Accessor for the IdNode representing the struct type name
    public IdNode getID() {
        return myId;
    }

    @Override
    public String toString() {
        return myId.name();  // Returns just the name of the struct
    }
	
	// 1 child
    private IdNode myId;
}

// **********************************************************************
// ****  StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
    abstract public boolean nameAnalysis(SymTab symTab);
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignExpNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(".");
    }

    // 1 child
    private AssignExpNode myAssign;

    public boolean nameAnalysis(SymTab symTab) {
        return myAssign.nameAnalysis(symTab);
    }
    
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("++.");
    }

    // 1 child
    private ExpNode myExp;

    public boolean nameAnalysis(SymTab symTab) {
        return myExp.nameAnalysis(symTab);
    }
    
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("--.");
    }

    // 1 child
    private ExpNode myExp;

    public boolean nameAnalysis(SymTab symTab) {
        return myExp.nameAnalysis(symTab);
    }
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");  
    }

    // 3 children
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
    
        // Step 1: Analyze the condition expression
        if (!myExp.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        // Step 2: Enter new scope for the if block
        symTab.addScope();
    
        // Step 3: Analyze declarations and statements
        if (!myDeclList.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        if (!myStmtList.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        // Step 4: Exit the if block scope
        try {
            symTab.removeScope();
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in IfStmtNode.nameAnalysis");
            System.exit(-1);
        }
    
        return allOk;
    }
    
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
        doIndent(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}"); 
    }

    // 5 children
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;

    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
    
        // Step 1: Analyze condition
        if (!myExp.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        // Step 2: Analyze then-block in new scope
        symTab.addScope();
        if (!myThenDeclList.nameAnalysis(symTab)) {
            allOk = false;
        }
        if (!myThenStmtList.nameAnalysis(symTab)) {
            allOk = false;
        }
        try {
            symTab.removeScope();
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in IfElseStmtNode (then)");
            System.exit(-1);
        }
    
        // Step 3: Analyze else-block in new scope
        symTab.addScope();
        if (!myElseDeclList.nameAnalysis(symTab)) {
            allOk = false;
        }
        if (!myElseStmtList.nameAnalysis(symTab)) {
            allOk = false;
        }
        try {
            symTab.removeScope();
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in IfElseStmtNode (else)");
            System.exit(-1);
        }
    
        return allOk;
    }
    
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }

    // 3 children
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
    
        // Analyze condition expression
        if (!myExp.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        // Add new scope for loop body
        symTab.addScope();
    
        if (!myDeclList.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        if (!myStmtList.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        try {
            symTab.removeScope();
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in WhileStmtNode.nameAnalysis");
            System.exit(-1);
        }
    
        return allOk;
    }
    
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("input -> ");
        myExp.unparse(p, 0);
        p.println(".");
    }

    // 1 child (actually can only be an IdNode or a StructAccessNode)
    private ExpNode myExp;

    public boolean nameAnalysis(SymTab symTab) {
        return myExp.nameAnalysis(symTab);
    }
    
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("disp <- (");
        myExp.unparse(p, 0);
        p.println(").");
    }

    // 1 child
    private ExpNode myExp;

    public boolean nameAnalysis(SymTab symTab) {
        return myExp.nameAnalysis(symTab);
    }
    
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myCall.unparse(p, indent);
        p.println(".");
    }

    // 1 child
    private CallExpNode myCall;

    public boolean nameAnalysis(SymTab symTab) {
        return myCall.nameAnalysis(symTab);
    }
    
    
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(".");
    }

    // 1 child
    private ExpNode myExp; // possibly null


    public boolean nameAnalysis(SymTab symTab) {
        if (myExp == null) return true;
        return myExp.nameAnalysis(symTab);
    }
    
}

// **********************************************************************
// ****  ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {

    abstract public boolean nameAnalysis(SymTab symTab);
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("TRUE");
    }

    private int myLineNum;
    private int myCharNum;

    public boolean nameAnalysis(SymTab symTab) {
        return true;
    }
    
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("FALSE");
    }

    private int myLineNum;
    private int myCharNum;

    public boolean nameAnalysis(SymTab symTab) {
        return true;
    }
    
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
        
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
        if (mySym != null) {
            p.print("{" + mySym.toString() + "}");
        }
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private Sym mySym;

    public void setSym (Sym s){
        mySym = s;
    }

    public Sym getSym () {
        return mySym;
    }

    public int lineNum() {
        return myLineNum;
    }
    
    public int charNum() {
        return myCharNum;
    }
    
    public String name() {
        return myStrVal;
    }
    

    public boolean nameAnalysis(SymTab symTab) {
        Sym sym = null;
        try {
            sym = symTab.lookupGlobal(myStrVal);
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in IdNode.nameAnalysis");
            System.exit(-1);
        }
    
        if (sym == null) {
            ErrMsg.fatal(myLineNum, myCharNum, "Identifier undeclared");
            return false;
        } else {
            this.mySym = sym;
            return true;
        }
    }
    
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;

    public boolean nameAnalysis(SymTab symTab) {
        return true;
    }
    
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;

    public boolean nameAnalysis(SymTab symTab) {
        return true;
    }
    
}

class StructAccessExpNode extends ExpNode {
    public StructAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;	
        myId = id;
    }

    // **** unparse ****
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myLoc.unparse(p, 0);
        p.print("):");
        myId.unparse(p, 0);
    }

    // 2 children
    private ExpNode myLoc;	
    private IdNode myId;

    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
    
        if (!myLoc.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        Sym lhsSym = null;
        int errLine = 0;
        int errChar = 0;
    
        if (myLoc instanceof IdNode) {
            IdNode locId = (IdNode) myLoc;
            lhsSym = locId.getSym();
            errLine = locId.lineNum();
            errChar = locId.charNum();
        } else if (myLoc instanceof StructAccessExpNode) {
            lhsSym = ((StructAccessExpNode) myLoc).myId.getSym();
            errLine = ((StructAccessExpNode) myLoc).myId.lineNum();
            errChar = ((StructAccessExpNode) myLoc).myId.charNum();
        } else {
            // fallback error if the LHS is of unexpected type
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Colon-access of non-struct type");
            return false;
        }
    
        if (lhsSym == null || !(lhsSym instanceof StructVarSym)) {
            ErrMsg.fatal(errLine, errChar, "Colon-access of non-struct type");
            return false;
        }
    
        StructDefSym structDef = ((StructVarSym) lhsSym).getStructDef();
        Sym fieldSym = null;
    
        try {
            fieldSym = structDef.getFields().lookupLocal(myId.name());
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in StructAccessExpNode.nameAnalysis");
            System.exit(-1);
        }
    
        if (fieldSym == null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Name of struct field invalid");
            allOk = false;
        } else {
            myId.setSym(fieldSym);
        }
    
        return allOk;
    }
    
    
}

class AssignExpNode extends ExpNode {
    public AssignExpNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)  p.print(")");   
    }

    // 2 children
    private ExpNode myLhs;
    private ExpNode myExp;

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myLhs.nameAnalysis(symTab);
        boolean rightOk = myExp.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");   
    }

    // 2 children
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null

    public boolean nameAnalysis(SymTab symTab) {
        boolean allOk = true;
    
        // Step 1: Lookup the function name in the symbol table
        Sym funcSym = null;
        try {
            funcSym = symTab.lookupGlobal(myId.name());
        } catch (SymTabEmptyException e) {
            System.err.println("Unexpected SymTabEmptyException in CallExpNode.nameAnalysis");
            System.exit(-1);
        }
    
        if (funcSym == null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Identifier undeclared");
            allOk = false;
        } else {
            myId.setSym(funcSym);  // Link the function name to its Sym
    
            if (!(funcSym instanceof FunctionSym)) {
                ErrMsg.fatal(myId.lineNum(), myId.charNum(), "Attempt to call a non-function");
                allOk = false;
            }
        }
    
        // Step 2: Analyze the arguments (if any)
        if (myExpList != null && !myExpList.nameAnalysis(symTab)) {
            allOk = false;
        }
    
        return allOk;
    }
    
    
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

    // 1 child
    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    // 2 children
    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// ****  Subclasses of UnaryExpNode
// **********************************************************************

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(^");
        myExp.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        return myExp.nameAnalysis(symTab);
    }
    
}

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        return myExp.nameAnalysis(symTab);
    }
    
}

// **********************************************************************
// ****  Subclasses of BinaryExpNode
// **********************************************************************

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" & ");
        myExp2.unparse(p, 0);
        p.print(")");
    }


    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" | ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    


}

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class NotEqNode extends BinaryExpNode {
    public NotEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" ^= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public boolean nameAnalysis(SymTab symTab) {
        boolean leftOk = myExp1.nameAnalysis(symTab);
        boolean rightOk = myExp2.nameAnalysis(symTab);
        return leftOk && rightOk;
    }
    
}
