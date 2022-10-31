package front.SyntaxTree;

import java.util.ArrayList;

public class Stmt implements TreeNode{
    public enum Type{
        Assign, Exp, IfBranch, WhileBranch, BreakStmt, ContinueStmt,
        ReturnStmt, Input, Output, None, Block
    }
    private Type type;
    private ArrayList<TreeNode> childNode;
    public Stmt(Type type, ArrayList<TreeNode> childNode){
        this.type = type;
        this.childNode = childNode;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }

    public Type getType() {
        return type;
    }
}
