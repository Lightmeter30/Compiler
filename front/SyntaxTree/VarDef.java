package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;
import front.Error;

import java.util.ArrayList;

public class VarDef implements TreeNode{
    public Ident ident;
    private ArrayList<ConstExp> constExps;
    private InitVal initVal;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    private int dimension;
    private ArrayList<Integer> shape = new ArrayList<>();
    public VarDef(Ident ident, int dimension, ArrayList<ConstExp> constExps){
        this.ident = ident;
        this.dimension = dimension;
        this.constExps = constExps;
        this.initVal = null;
        this.childNode.add(ident);
        this.childNode.addAll(constExps);
    }
    public VarDef(Ident ident, int dimension, ArrayList<ConstExp> constExps, InitVal initVal){
        this.ident = ident;
        this.dimension = dimension;
        this.constExps = constExps;
        this.initVal = initVal;
        this.childNode.add(ident);
        this.childNode.addAll(constExps);
        this.childNode.add(initVal);
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
        if ( constExps.size() == 0 ) {
            // not array
            if( initVal != null ) {
                String value = initVal.createMidCode(midCodeList);
                midCodeList.add(MidCode.Op.VAR_DEF, name, value, "#NULL");
            } else {
                midCodeList.add(MidCode.Op.VAR_DEF, name, "#NULL", "#NULL");
            }
        } else {
            if ( initVal != null ) {
                String value = initVal.createMidCode(midCodeList);
                assert value.equals("#ARRAY");
                ArrayList<String> initValues = new ArrayList<>();
                initVal.getInitValue(initValues,midCodeList);
                int index = 0;
                for (String res : initValues) {
                    midCodeList.add(MidCode.Op.ARR_SAVE, name + "[" + index + "]", res, "#NULL");
                    index++;
                }
            } else {
                midCodeList.add(MidCode.Op.VAR_DEF, name, "#NULL", "#NULL");
            }
        }
        return "";
    }

    public InitVal getInitVal() {
        return this.initVal;
    }

    public int getDimension() {
        return this.dimension;
    }
    private void setShape() throws Error{
        for(ConstExp exp: constExps)
            shape.add(exp.getValue());
    }
    public ArrayList<Integer> getShape() throws Error{
        return this.shape;
    }
}
