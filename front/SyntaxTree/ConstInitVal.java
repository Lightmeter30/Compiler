package front.SyntaxTree;

import java.util.ArrayList;

public class ConstInitVal implements TreeNode{
    public enum Type{
        ConstExp, mulConstInitVal
    }
    private Type type;
    private ArrayList<TreeNode> childNode;
    public ConstInitVal(Type type, ArrayList<TreeNode> childNode){
        this.type = type;
        this.childNode = childNode;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    public void getInitValue(ArrayList<String> initValues){
        if(this.type.equals(Type.ConstExp)) {
            initValues.add(Integer.toString(((ConstExp) childNode.get(0)).getValue()));
        }else {
            for(TreeNode node: this.childNode){
                ((ConstInitVal) node).getInitValue(initValues);
            }
        }
    }
}
