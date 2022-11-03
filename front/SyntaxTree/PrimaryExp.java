package front.SyntaxTree;

import Mid.MidCodeList;
import front.Error;

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

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        try {
            return Integer.toString(this.getValue());
        } catch (Error ignored) {
        }
        if ( this.exp != null )
            return exp.createMidCode(midCodeList);
        else if( this.lVal !=null )
            return lVal.createMidCode(midCodeList);
        return Integer.toString(this.number.getValue());
    }

    public boolean isFuncCall() {
        return this.exp != null && this.exp.isFuncCall();
    }

    public Integer getValue() throws Error {
        if(this.number != null){
            return number.getValue();
        }else if(this.exp != null){
            return this.exp.getValue();
        }
        throw new Error('n', -1);
        // return -114514;
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
