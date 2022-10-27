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

    public Integer getValue() {
        int value = unaryExps.get(0).getValue();
        for(int index = 1; index < unaryExps.size(); index++){
            if(Ops.get(index - 1).getWord().equals("*"))
                value = value * unaryExps.get(index).getValue();
            else if(Ops.get(index - 1).getWord().equals("/"))
                value = value / unaryExps.get(index).getValue();
            else
                value = value % unaryExps.get(index).getValue();
        }
        return value;
    }

    public boolean isFuncCall() {
        return this.unaryExps.size() == 1 && this.unaryExps.get(0).isFuncCall();
    }
}
