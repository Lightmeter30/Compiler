package front.SyntaxTree;

import front.Error;
import Mid.MidCodeList;

import java.util.ArrayList;

public class Exp implements TreeNode{
    private AddExp addExp;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public Exp(AddExp addExp){
        this.addExp = addExp;
        this.childNode.add(addExp);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        try {
            return Integer.toString(this.addExp.getValue());
        } catch (Error ignored) {
        }
        return this.addExp.createMidCode(midCodeList);
    }

    public boolean isFuncCall() {
        return this.addExp.isFuncCall();
    }

    public Integer getValue() {
        try{
            return addExp.getValue();
        } catch (Error ignored){
        }
        return null;
    }

    public String getName(){
        return this.addExp.getName();
    }

    public int getDimension() {
        return this.addExp.getDimension();
    }
}
