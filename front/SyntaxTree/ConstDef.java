package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;
import front.Error;

import java.util.ArrayList;

public class ConstDef implements TreeNode{
    private Ident ident;
    private ArrayList<ConstExp> constExps;
    private ConstInitVal constInitVal;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    private int dimension;
    private ArrayList<Integer> shape = new ArrayList<>();
    public ConstDef(Ident ident, int dimension, ArrayList<ConstExp> constExps, ConstInitVal constInitVal){
        this.ident = ident;
        this.dimension = dimension;
        this.constExps = constExps;
        this.constInitVal = constInitVal;
        this.childNode.add(ident);
        this.childNode.addAll(constExps);
        this.childNode.add(constInitVal);
        try {
            this.setShape();
        } catch (Error ignored) {
            ignored.printStackTrace();
        }
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        String name = ident.getName() + "@" + midCodeList.nodeTableItem.get(ident).getLoc();
        if( constExps.size() == 0 ){
            //not-Array
            String value = constInitVal.createMidCode(midCodeList);
            midCodeList.add(MidCode.Op.CONST_DEF, name, value, "#NULL");
        }else {
            String value = constInitVal.createMidCode(midCodeList); //ConstInitial
            assert value.equals("#ARRAY");
            ArrayList<String> initValues = new ArrayList<>();
            constInitVal.getInitValue(initValues);
            int index = 0;
            for(String res: initValues) {
                midCodeList.add(MidCode.Op.ARR_SAVE, name + "[" + index + "]", res, "#NULL");
                index++;
            }
        }
        return "";
    }

    public Ident getIdent(){
        return ident;
    }
    private void setShape() throws Error {
        for(ConstExp exp: constExps)
            shape.add(exp.getValue());
    }
    public ArrayList<Integer> getShape(){
        return this.shape;
    }
    public int getDimension(){
        return this.dimension;
    }
    public ConstInitVal getConstInitVal(){
        return this.constInitVal;
    }
}
