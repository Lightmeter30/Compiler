package front.SyntaxTree;

import java.util.ArrayList;

public class VarDef implements TreeNode{
    public Ident ident;
    private ArrayList<ConstExp> constExps;
    private InitVal initVal;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public VarDef(Ident ident, ArrayList<ConstExp> constExps){
        this.ident = ident;
        this.constExps = constExps;
        this.initVal = null;
        this.childNode.add(ident);
        this.childNode.addAll(constExps);
    }
    public VarDef(Ident ident, ArrayList<ConstExp> constExps, InitVal initVal){
        this.ident = ident;
        this.constExps = constExps;
        this.initVal = initVal;
        this.childNode.add(ident);
        this.childNode.addAll(constExps);
        this.childNode.add(initVal);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
