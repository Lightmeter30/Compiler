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
    public String getName(){
        return ident.getName();
    }
    public int getDimension(){
        return this.dimension;
    }
    public ArrayList<Integer> getShape() throws Error{
        ArrayList<Integer> shape = new ArrayList<>();
        if(isArray) {
            shape.add(-1);
        }
        for(ConstExp exp: constExps){
            shape.add(exp.getValue());
        }
        return shape;
    }
}
