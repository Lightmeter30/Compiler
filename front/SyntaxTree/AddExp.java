package front.SyntaxTree;

import front.Error;
import Mid.MidCode;
import Mid.MidCodeList;
import front.Word.ConstInfo;

import java.util.ArrayList;

public class AddExp implements TreeNode{
    public ArrayList<ConstInfo> Ops;
    public ArrayList<MulExp> mulExps;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public AddExp(ArrayList<ConstInfo> Ops, ArrayList<MulExp> mulExps){
        this.Ops = Ops;
        this.mulExps = mulExps;
        this.childNode.addAll(mulExps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        try{
            return Integer.toString(this.getValue());
        } catch (Error ignored){
        }
        String op1 = mulExps.get(0).createMidCode(midCodeList);
        String result = op1;
        for(int i = 0;i < Ops.size();i++){
            String op2 = mulExps.get(i + 1).createMidCode(midCodeList);
            MidCode.Op op = Ops.get(i).getWord().equals("+") ? MidCode.Op.ADD : MidCode.Op.SUB;
            result = midCodeList.add(op,op1,op2,"#TEMP");
            op1 = result;
        }
        return result;
    }

    public Integer getValue() throws Error{
        int value = mulExps.get(0).getValue();
        for(int index = 1;index < mulExps.size(); index++){
            if(Ops.get(index - 1).getWord().equals("+"))
                value = value + mulExps.get(index).getValue();
            else
                value = value - mulExps.get(index).getValue();
        }
        return value;
    }

    public boolean isFuncCall() {
        return this.mulExps.size() == 1 && mulExps.get(0).isFuncCall();
    }

    public String getName(){
        if(!Ops.isEmpty()) {
            return null;
        }
        return this.mulExps.get(0).getName();
    }

    public int getDimension() {
        if( !Ops.isEmpty() )
            return 0;
        return this.mulExps.get(0).getDimension();
    }
}
