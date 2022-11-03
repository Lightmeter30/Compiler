package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;
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

    // may change
    @Override
    public String createMidCode(MidCodeList midCodeList) {
        if( relExps.size() == 1 )
            return relExps.get(0).createMidCode(midCodeList);
        RelExp left = relExps.get(0);
        String trueLabel = "";
        String boolVar = "";
        String leftAns = left.createMidCode(midCodeList);
        for (int i = 1;i < relExps.size();i++){
            boolVar = midCodeList.add(MidCode.Op.ASSIGN,"#TEMP","1","#NULL"); // 把boolVar赋值为1
            String op = Ops.get(i - 1).getWord();
            RelExp right = relExps.get(i);
            String rightAns = right.createMidCode(midCodeList);
            trueLabel = midCodeList.add(MidCode.Op.JUMP_IF, leftAns + " " + rightAns, op, "#AUTO_LABEL");
            midCodeList.add(MidCode.Op.ASSIGN, boolVar, "0", "#NULL"); //为 真 则跳过该赋值语句(把boolVar赋值为0)
            midCodeList.add(MidCode.Op.LABEL,"#NULL","NULL",trueLabel);
            leftAns = boolVar;
        }
        assert !boolVar.equals("");
        return boolVar;
    }
}
