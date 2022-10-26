package front.SyntaxTree;

import java.util.ArrayList;

public class Cond implements TreeNode{
    private LOrExp lOrExp;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public Cond(LOrExp lOrExp){
        this.lOrExp = lOrExp;
        this.childNode.add(lOrExp);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }
}
