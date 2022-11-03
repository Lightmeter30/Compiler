package front.SyntaxTree;

import Mid.MidCodeList;

import java.util.ArrayList;

public class BType implements TreeNode{
    private ArrayList<TreeNode> childNodes = new ArrayList<>();
    public enum Type{
        Int
    }
    private Type type;
    public BType(Type type){
        this.type = type;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNodes;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        return null;
    }
}
