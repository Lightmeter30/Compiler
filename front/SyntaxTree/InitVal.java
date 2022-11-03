package front.SyntaxTree;

import front.Error;
import Mid.MidCodeList;

import java.util.ArrayList;

public class InitVal implements TreeNode{
    public enum Type{
        Exp, mulInitVal
    }
    private Type type;
    private ArrayList<TreeNode> childNode;
    public InitVal(Type type, ArrayList<TreeNode> childNode){
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
        if( this.type.equals(Type.Exp) ) {
            value = Integer.toString(((Exp) childNode.get(0)).getValue());
            if ( value != null ) // const initVal
                return value;
            value = (childNode.get(0)).createMidCode(midCodeList);
        } else
            return "#ARRAY";
        return value;
    }

    public void getInitValue(ArrayList<String> initValues, MidCodeList midCodeList) {
        if (this.type.equals(Type.Exp))
            initValues.add((childNode.get(0)).createMidCode(midCodeList));
        else {
            for(TreeNode node: childNode) {
                ((InitVal) node).getInitValue(initValues, midCodeList);
            }
        }
    }
}
