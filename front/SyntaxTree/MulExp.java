package front.SyntaxTree;

import front.Word.ConstInfo;

import java.util.ArrayList;

public class MulExp implements TreeNode{
    public ArrayList<ConstInfo> Ops;
    public ArrayList<UnaryExp> unaryExps;
    private ArrayList<TreeNode> childNode = new ArrayList<>();

    public MulExp(ArrayList<ConstInfo> Ops, ArrayList<UnaryExp> unaryExps){
        this.Ops = Ops;
        this.unaryExps = unaryExps;
        this.childNode.addAll(unaryExps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
