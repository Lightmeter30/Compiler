package front.SyntaxTree;

import front.Word.ConstInfo;

import java.util.ArrayList;

public class RelExp implements TreeNode{
    public ArrayList<ConstInfo> Ops;
    public ArrayList<AddExp> addExps;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public RelExp(ArrayList<ConstInfo> Ops, ArrayList<AddExp> addExps) {
        this.Ops = Ops;
        this.addExps = addExps;
        childNode.addAll(addExps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
