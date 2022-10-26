package front.SyntaxTree;

import java.util.ArrayList;

public class BlockItem implements TreeNode{
    private Decl decl;
    private Stmt stmt;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();
    public BlockItem(Decl decl){
        this.decl = decl;
        this.stmt = null;
        this.childNode.add(decl);
    }
    public BlockItem(Stmt stmt){
        this.decl = null;
        this.stmt = stmt;
        this.childNode.add(stmt);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }
}
