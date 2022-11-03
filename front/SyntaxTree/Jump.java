package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;

import java.util.ArrayList;

public class Jump implements TreeNode{
    public String label;

    public Jump(String label){
        this.label = label;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return null;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        return midCodeList.add(MidCode.Op.JUMP,"#NULL", "#NULL", label);
    }
}
