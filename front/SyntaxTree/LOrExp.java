package front.SyntaxTree;

import java.util.ArrayList;

public class LOrExp implements TreeNode{
    public ArrayList<LAndExp> lAndExps;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public LOrExp(ArrayList<LAndExp> lAndExps){
        this.lAndExps = lAndExps;
        childNode.addAll(lAndExps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
