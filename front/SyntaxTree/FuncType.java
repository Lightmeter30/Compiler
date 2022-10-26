package front.SyntaxTree;

import java.util.ArrayList;

public class FuncType implements TreeNode{
    public enum Type{
        Int, Void
    }
    private Type type;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public FuncType(Type type){
        this.type = type;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
