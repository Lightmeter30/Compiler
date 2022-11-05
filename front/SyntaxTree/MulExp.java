package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;
import front.Word.ConstInfo;
import front.Error;

import java.util.ArrayList;

public class MulExp implements TreeNode{
    public ArrayList<ConstInfo> Ops;
    public ArrayList<UnaryExp> unaryExps;
    private ArrayList<TreeNode> childNode = new ArrayList<>();

    public MulExp(ArrayList<ConstInfo> Ops, ArrayList<UnaryExp> unaryExps){
        this.Ops = Ops;
        this.unaryExps = unaryExps;
        this.childNode.addAll(unaryExps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        try {
            return Integer.toString(this.getValue());
        } catch (Error ignored){
        }
        String op1 = unaryExps.get(0).createMidCode(midCodeList);
        String result = op1;
        for(int i = 0;i < Ops.size(); i++){
            String op2 = unaryExps.get(i + 1).createMidCode(midCodeList);
            MidCode.Op op = Ops.get(i).getWord().equals("*") ? MidCode.Op.MUL :
                    Ops.get(i).getWord().equals("/") ? MidCode.Op.DIV : MidCode.Op.MOD;
            result = midCodeList.add(op,op1,op2,"#TEMP");
            op1 = result;
        }
        return result;
    }

    public Integer getValue() throws Error{
        int value = unaryExps.get(0).getValue();
        for(int index = 1; index < unaryExps.size(); index++){
            if(Ops.get(index - 1).getWord().equals("*"))
                value = value * unaryExps.get(index).getValue();
            else if(Ops.get(index - 1).getWord().equals("/"))
                value = value / unaryExps.get(index).getValue();
            else {
                System.out.println(unaryExps.get(index).toString());
                value = value % unaryExps.get(index).getValue();
            }

        }
        return value;
    }

    public boolean isFuncCall() {
        return this.unaryExps.size() == 1 && this.unaryExps.get(0).isFuncCall();
    }

    public String getName() {
        if( !Ops.isEmpty() ) return null;
        return this.unaryExps.get(0).getName();
    }

    public int getDimension() {
        if( !Ops.isEmpty() )
            return 0;
        return this.unaryExps.get(0).getDimension();
    }
}
