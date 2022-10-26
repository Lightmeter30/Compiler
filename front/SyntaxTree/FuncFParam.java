package front.SyntaxTree;

import java.util.ArrayList;

public class FuncFParam implements TreeNode{
    private BType bType;
    public Ident ident;
    private ArrayList<ConstExp> constExps;
    private boolean isArray;

    private ArrayList<TreeNode> childNode = new ArrayList<>();
    private int dimension;

    public FuncFParam(BType bType, Ident ident, ArrayList<ConstExp> constExps, int dimension){
        this.bType = bType;
        this.ident = ident;
        this.constExps = constExps;
        this.dimension = dimension;
        childNode.add(bType);
        childNode.add(ident);
        childNode.addAll(constExps);
        this.isArray = dimension > 0;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
