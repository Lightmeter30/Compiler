package front.SyntaxTree;

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

    public Integer getValue() throws Error {
        return this.addExp.getValue();
    }
}
