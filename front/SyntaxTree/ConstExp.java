package front.SyntaxTree;

import front.Error;
import Mid.MidCodeList;

import java.util.ArrayList;

public class ConstExp implements TreeNode{
    private AddExp addExp;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public ConstExp(AddExp addExp){
        this.addExp = addExp;
        this.childNode.add(addExp);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        try {
            return Integer.toString(this.addExp.getValue());
        } catch (Error ignored){
        }
        return this.addExp.createMidCode(midCodeList);
    }

    public Integer getValue() throws Error {
        return this.addExp.getValue();
    }
}
