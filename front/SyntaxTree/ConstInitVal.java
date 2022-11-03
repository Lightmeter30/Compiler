package front.SyntaxTree;

import front.Error;
import Mid.MidCodeList;

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

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        String value = "";
        if(this.type.equals(Type.ConstExp)){
            try{
                value = Integer.toString(((ConstExp) childNode.get(0)).getValue());
            }catch (Error ignored){
                value = childNode.get(0).createMidCode(midCodeList);
            }
        } else {
            //it's array
            value = "#ARRAY";
        }
        return value;
    }

    public void getInitValue(ArrayList<String> initValues){
        if(this.type.equals(Type.ConstExp)) {
            try{
                initValues.add(Integer.toString(((ConstExp) childNode.get(0)).getValue()));
            } catch (Error ignored){
            }
        }else {
            for(TreeNode node: this.childNode){
                ((ConstInitVal) node).getInitValue(initValues);
            }
        }
    }
}
