package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;
import SymbolTable.FuncFormVar;
import SymbolTable.SymbolItem;
import SymbolTable.Var;

import java.util.ArrayList;

public class LVal implements TreeNode{
    public Ident ident;
    public ArrayList<Exp> exps;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public LVal(Ident ident,ArrayList<Exp> exps){
        this.ident = ident;
        this.exps = exps;
        this.childNode.add(ident);
        this.childNode.addAll(exps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }

    // may change
    @Override
    public String createMidCode(MidCodeList midCodeList) {
        SymbolItem item = midCodeList.nodeTableItem.get(ident);
        String name = ident.getName() + "@" + item.getLoc() ;
        ArrayList<Integer> shape;
        if(item instanceof Var)
            shape = ((Var) item).getShape();
        else
            shape = ((FuncFormVar) item).getShape();
        if( !exps.isEmpty() ) {
            //24岁,是数组
            if(shape.size() > 1 && shape.size() == exps.size()) {
                //二维数组; shape == exps == 2
                String x = exps.get(0).createMidCode(midCodeList);
                String y = exps.get(1).createMidCode(midCodeList);
                String base;
                try {
                    base = String.valueOf(Integer.parseInt(x) * Integer.parseInt(shape.get(1).toString()));
                } catch (Exception ignore){
                    base = midCodeList.add(MidCode.Op.MUL,x,shape.get(1).toString(),"#TEMP");
                }
                try {
                    name += "[" + (Integer.parseInt(y) + Integer.parseInt(base)) + "]";
                } catch (Exception ignore) {
                    name += "[" + midCodeList.add(MidCode.Op.ADD,y,base, "#TEMP") + "]";
                }
            } else {
                //24岁,是一维数组 exps == 1
                String index = exps.get(0).createMidCode(midCodeList);
                if ( index.contains("[") && shape.size() == 1 ) {
                    index = midCodeList.add(MidCode.Op.ARR_LOAD, "#TEMP", index, "#NULL");
                }
                if ( shape.size() == 2 ) {
                    // 2 1
                    midCodeList.add(MidCode.Op.SIGNAL_ARR_ADDR, "#NULL", "#NULL", "#NULL");
                }
                name += "[" + index + "]";
            }
        } else if( !shape.isEmpty() ) //(exp == 0,说明不能是array[]的形式,array是二维数组)
            midCodeList.add(MidCode.Op.SIGNAL_ARR_ADDR, "#NULL", "#NULL", "#NULL");
        return name;
    }

    public String getName() {
        return this.ident.getName();
    }

    public int getDimension() {
        return this.exps.size();
    }
}
