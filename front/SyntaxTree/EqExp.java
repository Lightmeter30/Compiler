package front.SyntaxTree;

import front.Word.ConstInfo;

import java.util.ArrayList;

public class EqExp implements TreeNode{
    public ArrayList<ConstInfo> Ops;
    public ArrayList<RelExp> relExps;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public EqExp(ArrayList<ConstInfo> Ops, ArrayList<RelExp> relExps) {
        this.Ops = Ops;
        this.relExps = relExps;
        childNode.addAll(relExps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
