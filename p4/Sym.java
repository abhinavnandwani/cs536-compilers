
import java.util.List;


public class Sym {
	private String type;
	
	public Sym(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		return type;
	}

}



class FunctionSym extends Sym {
    private String returnType;
    private List<String> paramTypes;

    public FunctionSym(String returnType, List<String> paramTypes) {
        super("function");
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paramTypes.size(); i++) {
            sb.append(paramTypes.get(i));
            if (i != paramTypes.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("->");
        sb.append(returnType);
        return sb.toString();
    }
}


class StructDefSym extends Sym {
    private SymTab fields;

    public StructDefSym(SymTab fields) {
        super("struct");
        this.fields = fields;
    }

    public SymTab getFields() {
        return fields;
    }
}

class StructVarSym extends Sym {
    private StructDefSym defSym;

    public StructVarSym(String structTypeName, StructDefSym defSym) {
        super(structTypeName);  // the struct type name, e.g., "Point"
        this.defSym = defSym;
    }

    public StructDefSym getStructDef() {
        return defSym;
    }
}

