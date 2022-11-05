package front.SyntaxTree;

import front.Error;
import Mid.MidCode;
import Mid.MidCodeList;

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

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        try {
            return Integer.toString(this.getValue());
        } catch (Error ignored) {
        }
        if( this.type.equals(Type.UnaryExp) ) {
            String type = ((UnaryOp) childNode.get(0)).getType();
            MidCode.Op op = type.equals("PLUS") ? MidCode.Op.ADD :
                    type.equals("MINU") ? MidCode.Op.SUB : MidCode.Op.NOT;
            if(op.equals(MidCode.Op.NOT)) {
                return midCodeList.add(MidCode.Op.SET, this.childNode.get(1).createMidCode(midCodeList) + " 0", "==", "#TEMP");
            } else {
                return midCodeList.add(op, "0", this.childNode.get(1).createMidCode(midCodeList), "#TEMP");
            }
        } else if( this.type.equals(Type.FuncCall) ) {
            String funcName = ((Ident) this.childNode.get(0)).getName();
            midCodeList.add(MidCode.Op.PREPARE_CALL, funcName, "#NULL", "#NULL");
            if( childNode.size() > 1 ) {
                for(Exp exp: ((FuncRParams) this.childNode.get(1)).exps) {
                    String name = exp.createMidCode(midCodeList);
                    midCodeList.add(MidCode.Op.PUSH_PARA, name, funcName, "#NULL");
                }
            }
            midCodeList.add(MidCode.Op.CALL,funcName, "#NULL", "#NULL");
            // TODO return value pass by %RET or void
            return midCodeList.add(MidCode.Op.ADD, "%RTX", "0", "#TEMP");
        }
        return this.childNode.get(0).createMidCode(midCodeList);
    }

    public int getValue() throws Error {
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
        // FUNC_CALL
        throw new Error('n', -1);
    }
    public boolean isFuncCall() {
        return type == Type.FuncCall || this.type == Type.PrimaryExp && ((PrimaryExp) this.childNode.get(0)).isFuncCall();
    }
    public String getFuncCallName(){
        assert (type.equals(Type.FuncCall));
        return ((Ident) childNode.get(0)).getName();
    }

    public String getName() {
        if ( this.type == Type.PrimaryExp )
            return ((PrimaryExp) this.childNode.get(0)).getName();
        else if ( this.type == Type.FuncCall )
            return ((Ident) this.childNode.get(0)).getName();
        return null;
    }

    public int getDimension() {
        if ( this.type != Type.PrimaryExp )
            return 0;
        return ((PrimaryExp) this.childNode.get(0)).getDimension();
    }
}
