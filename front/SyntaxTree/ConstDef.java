package front.SyntaxTree;

import java.util.ArrayList;

public class ConstDef implements TreeNode{
    private Ident ident;
    private ArrayList<ConstExp> constExps;
    private ConstInitVal constInitVal;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    private int dimension;
    private ArrayList<Integer> shape = new ArrayList<>();
    public ConstDef(Ident ident, int dimension, ArrayList<ConstExp> constExps, ConstInitVal constInitVal){
        this.ident = ident;
        this.dimension = dimension;
        this.constExps = constExps;
        this.constInitVal = constInitVal;
        this.childNode.add(ident);
        this.childNode.addAll(constExps);
        this.childNode.add(constInitVal);
        this.setShape();
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
    public Ident getIdent(){
        return ident;
    }
    private void setShape(){
        for(ConstExp exp: constExps)
            shape.add(exp.getValue());
    }
    public ArrayList<Integer> getShape() throws Error{
        return this.shape;
    }
    public int getDimension(){
        return this.dimension;
    }
    public ConstInitVal getConstInitVal(){
        return this.constInitVal;
    }
}
