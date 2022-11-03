package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;
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

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        AddExp left = addExps.get(0);
        String trueLabel = "";
        String boolVar = "";
        String leftAns = left.createMidCode(midCodeList);
        if ( addExps.size() == 1 )
            return leftAns;
        for(int i = 1;i < addExps.size();i++) {
            boolVar = midCodeList.add(MidCode.Op.ASSIGN, "#TEMP","1", "#NULL");
            String op = Ops.get(i - 1).getWord();
            String rightAns = addExps.get(i).createMidCode(midCodeList);
            trueLabel = midCodeList.add(MidCode.Op.JUMP_IF, leftAns + " " + rightAns, op, "#AUTO_LABEL");
            midCodeList.add(MidCode.Op.ASSIGN, boolVar, "0", "#NULL");
            midCodeList.add(MidCode.Op.LABEL, "#NULL", "#NULL", trueLabel);
            leftAns = boolVar;
        }
        assert !boolVar.equals("");
        return boolVar;
    }
}
