package front.SyntaxTree;

import java.util.ArrayList;

public class ConstDef implements TreeNode{
    private Ident ident;
    private ArrayList<ConstExp> constExps;
    private ConstInitVal constInitVal;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public ConstDef(Ident ident, ArrayList<ConstExp> constExps, ConstInitVal constInitVal){
        this.ident = ident;
        this.constExps = constExps;
        this.constInitVal = constInitVal;
        this.childNode.add(ident);
        this.childNode.addAll(constExps);
        this.childNode.add(constInitVal);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
    public Ident getIdent(){
        return ident;
    }
}
