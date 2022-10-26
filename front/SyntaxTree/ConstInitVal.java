package front.SyntaxTree;

import java.util.ArrayList;

public class ConstInitVal implements TreeNode{
    public enum Type{
        ConstExp, mulConstInitVal
    }
    private Type type;
    private ArrayList<TreeNode> childNode;
    public ConstInitVal(Type type, ArrayList<TreeNode> childNode){
        this.type = type;
        this.childNode = childNode;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
