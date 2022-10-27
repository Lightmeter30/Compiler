package front.SyntaxTree;

import java.util.ArrayList;

public class UnaryExp implements TreeNode{
    public enum Type {
        PrimaryExp, FuncCall, UnaryExp
    }
    public Type type;
    private ArrayList<TreeNode> childNode;
    public UnaryExp(Type type, ArrayList<TreeNode> childNode){
        this.type = type;
        this.childNode = childNode;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        //need change?
        return childNode;
    }
    public Integer getValue() {
        if(type == Type.PrimaryExp){
            return ((PrimaryExp) this.childNode.get(0)).getValue();
        }else if(type == Type.UnaryExp){
            if(childNode.get(0).toString().equals("+")){
                return ((UnaryExp) this.childNode.get(1)).getValue();
            }else if(childNode.get(0).toString().equals("-")){
                return -((UnaryExp) this.childNode.get(1)).getValue();
            }else if(childNode.get(0).toString().equals("!")){
                if( ((UnaryExp) this.childNode.get(1)).getValue() == 0 )
                    return 1;
                else
                    return 0;
            }
        }
        return null;// may change
    }
    public boolean isFuncCall() {
        return type == Type.FuncCall || this.type == Type.PrimaryExp && ((PrimaryExp) this.childNode.get(0)).isFuncCall();
    }
}
