package front.SyntaxTree;

import java.util.ArrayList;

public class VarDef implements TreeNode{
    public Ident ident;
    private ArrayList<ConstExp> constExps;
    private InitVal initVal;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    private int dimension;
    private ArrayList<Integer> shape = new ArrayList<>();
    public VarDef(Ident ident, int dimension, ArrayList<ConstExp> constExps){
        this.ident = ident;
        this.dimension = dimension;
        this.constExps = constExps;
        this.initVal = null;
        this.childNode.add(ident);
        this.childNode.addAll(constExps);
    }
    public VarDef(Ident ident, int dimension, ArrayList<ConstExp> constExps, InitVal initVal){
        this.ident = ident;
        this.dimension = dimension;
        this.constExps = constExps;
        this.initVal = initVal;
        this.childNode.add(ident);
        this.childNode.addAll(constExps);
        this.childNode.add(initVal);
        this.setShape();
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    public InitVal getInitVal() {
        return this.initVal;
    }

    public int getDimension() {
        return this.dimension;
    }
    private void setShape(){
        for(ConstExp exp: constExps)
            shape.add(exp.getValue());
    }
    public ArrayList<Integer> getShape() throws Error{
        return this.shape;
    }
}
