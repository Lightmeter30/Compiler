package front.SyntaxTree;

import Mid.MidCodeList;

import java.util.ArrayList;

public class Number implements TreeNode{
    private IntConst intConst;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public Number(IntConst intConst){
        this.intConst = intConst;
        this.childNode.add(intConst);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        return null;
    }

    public Integer getValue() {
        return intConst.getValue();
    }
}
