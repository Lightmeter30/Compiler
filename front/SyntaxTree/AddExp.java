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

    public Integer getValue() {
        int value = mulExps.get(0).getValue();
        for(int index = 1;index < mulExps.size(); index++){
            if(Ops.get(index - 1).getWord().equals("+"))
                value = value + mulExps.get(index).getValue();
            else
                value = value - mulExps.get(index).getValue();
        }
        return value;
    }

    public boolean isFuncCall() {
        return this.mulExps.size() == 1 && mulExps.get(0).isFuncCall();
    }
}
