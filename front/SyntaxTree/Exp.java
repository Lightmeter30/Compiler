package front.SyntaxTree;

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
}
