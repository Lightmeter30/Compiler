package front.SyntaxTree;

import front.Word.ConstInfo;

import java.util.ArrayList;

public class AddExp implements TreeNode{
    public ArrayList<ConstInfo> Ops;
    public ArrayList<MulExp> mulExps;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public AddExp(ArrayList<ConstInfo> Ops, ArrayList<MulExp> mulExps){
        this.Ops = Ops;
        this.mulExps = mulExps;
        this.childNode.addAll(mulExps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
