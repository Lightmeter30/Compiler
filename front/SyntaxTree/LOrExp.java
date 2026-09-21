package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;

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

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        String trueLabel = "";
        String boolVar = "";
        if ( lAndExps.size() == 1 ) {
            return lAndExps.get(0).createMidCode(midCodeList);
        }
        boolVar = midCodeList.add(MidCode.Op.ASSIGN, "#TEMP", "1", "#NULL"); // ?
        for( LAndExp lAndExp: lAndExps ) {
            String ans = lAndExp.createMidCode(midCodeList);
            if ( trueLabel.equals("") ){
                trueLabel = midCodeList.add(MidCode.Op.JUMP_IF, ans + " " + "0", "!=","#AUTO_LABEL");
            } else {
                midCodeList.add(MidCode.Op.JUMP_IF, ans + " " + "0", "!=",trueLabel);
            }
        }
        midCodeList.add(MidCode.Op.ASSIGN, boolVar, "0", "#NULL");
        midCodeList.add(MidCode.Op.LABEL, "#NULL", "#NULL",trueLabel);
        assert !boolVar.equals("");
        return boolVar;
    }
}
