package front.SyntaxTree;

import java.util.ArrayList;

public class UnaryExp implements TreeNode{
    public enum Type {
        PrimaryExp, FuncCall, UnaryExp
    }
    public Type type;
    private ArrayList<TreeNode> childNode;
    public UnaryExp(Type type, ArrayList<TreeNode> childNode){
        this.type = type;
        this.childNode = childNode;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        //need change?
        return childNode;
    }
}
