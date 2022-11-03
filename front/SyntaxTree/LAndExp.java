package front.SyntaxTree;


import Mid.MidCode;
import Mid.MidCodeList;

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

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        String falseLabel = "";
        String boolVar = "";
        if(eqExps.size() == 1) {
            return eqExps.get(0).createMidCode(midCodeList);
        }
        boolVar = midCodeList.add(MidCode.Op.ASSIGN, "#TEMP", "0", "#NULL");
        for(EqExp eqExp: eqExps){
            String ans = eqExp.createMidCode(midCodeList);
            if( falseLabel.equals("") ) {
                falseLabel = midCodeList.add(MidCode.Op.JUMP_IF, ans + " " + "0", "==", "#AUTO_LABEL" );
            } else {
                midCodeList.add(MidCode.Op.JUMP_IF, ans + " " + "0", "==",falseLabel);
            }
        }
        midCodeList.add(MidCode.Op.ASSIGN, boolVar, "1", "#NULL");
        midCodeList.add(MidCode.Op.LABEL,"#NULL","#NULL",falseLabel);
        assert !boolVar.equals("");
        return boolVar;
    }
}
