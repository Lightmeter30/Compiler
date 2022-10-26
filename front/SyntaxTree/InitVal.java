package front.SyntaxTree;

import java.util.ArrayList;

public class InitVal implements TreeNode{
    public enum Type{
        Exp, mulInitVal
    }
    private Type type;
    private ArrayList<TreeNode> childNode;
    public InitVal(Type type, ArrayList<TreeNode> childNode){
        this.type = type;
        this.childNode = childNode;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
