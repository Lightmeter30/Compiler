package front.SyntaxTree;


import java.util.ArrayList;

public class LAndExp implements TreeNode{
    private ArrayList<EqExp> eqExps;
    private final ArrayList<TreeNode> childNodes = new ArrayList<>();

    public LAndExp(ArrayList<EqExp> eqExps) {
        this.eqExps = eqExps;
        childNodes.addAll(eqExps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNodes;
    }
}
