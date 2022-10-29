package front.SyntaxTree;

import java.util.ArrayList;

public class PrimaryExp implements TreeNode{
    public Exp exp;
    public LVal lVal;
    public Number number;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();
    public String value;

    public PrimaryExp(Exp exp) {
        this.exp = exp;
        this.lVal = null;
        this.number = null;
        this.value = null;
        this.childNode.add(exp);
    }

    public PrimaryExp(LVal lVal) {
        this.lVal = lVal;
        this.exp = null;
        this.number = null;
        this.value = null;
        this.childNode.add(lVal);
    }

    public PrimaryExp(Number number) {
        this.number = number;
        this.exp = null;
        this.lVal = null;
        this.value = null;
        this.childNode.add(number);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }

    public boolean isFuncCall() {
        return this.exp != null && this.exp.isFuncCall();
    }

    public Integer getValue() {
        if(this.number != null){
            return number.getValue();
        }else if(this.exp != null){
            return this.exp.getValue();
        }
        return null;
    }

    public String getName() {
        if ( this.lVal != null )
            return this.lVal.getName();
        else if ( this.exp != null )
            return this.exp.getName();
        return null;
    }

    public int getDimension() {
        if ( this.lVal != null )
            return this.lVal.getDimension();
        else if ( this.exp != null )
            return this.exp.getDimension();
        return 0;
    }
}
