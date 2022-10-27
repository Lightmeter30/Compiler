package SymbolTable;

import front.SyntaxTree.*;

import java.util.ArrayList;

public class SymLink {
    private final TreeNode root;
    private ArrayList<SymbolTable> symbolTables = new ArrayList<>(); //存储着所有的符号表
    private int currentDepth;
    private final int[] depths = new int[100];
    private SymbolTable rootTable;
    private SymbolTable currentTable;
    private ArrayList<SymbolTable> funcTable = new ArrayList<>();
    public SymLink(TreeNode root){
        this.root = root;
        this.currentDepth = 0;
        for(int i = 0;i < 100; i++){
            depths[i] = 0;
        }
    }
    public void buildSymbolTable(){
        rootTable = new SymbolTable(new int[]{0,0},null);
        currentTable = rootTable;
        symbolTables.add(currentTable);
        travel(root,null);
    }
    private void travel(TreeNode node, TreeNode funcFormalArgs) throws Error{
        if( node == null ){
            return;
        }else if(node instanceof Block){
            currentDepth++;
            currentTable = new SymbolTable(new int[]{currentDepth, depths[currentDepth]},currentTable);
            depths[currentDepth]++;
            if(funcFormalArgs != null ){
                addFuncFormalArgs(funcFormalArgs);
            }
        }else if(node instanceof VarDef){
            VarDef varDef = (VarDef) node;
            String name = varDef.ident.getName();
            checkTable(name, varDef.ident,"Var");
            for(TreeNode childNode: varDef.getChild()){
                travel(childNode,null);
            } // may change
            SymbolItem item = new Var(name,false,varDef.getDimension(),varDef.getInitVal(),varDef.getShape());
            currentTable.symbolList.add(item);
        }else if(node instanceof ConstDef){
            ConstDef constDef = (ConstDef) node;
            String name = constDef.getIdent().getName();
            checkTable(name,constDef.getIdent(),"Var");
            for(TreeNode childNode: constDef.getChild()){
                travel(childNode,null);
            } // may change
            SymbolItem item = new Var(name, true, constDef.getDimension(),constDef.getConstInitVal(),constDef.getShape());
            currentTable.symbolList.add(item);
        }else if(node instanceof FuncDef){
            FuncDef funcDef = (FuncDef) node;
            String name = funcDef.getIdent().getName();
            checkTable(name,funcDef.getIdent(),"Func");
            Integer argc = funcDef.getArgc();
            Func.Type type = funcDef.getFuncType().getType().equals(FuncType.Type.Int) ? Func.Type.intFunc : Func.Type.voidFunc;
            SymbolItem item = new Func(name,type,argc);
            currentTable.symbolList.add(item);
            // currentFucName = name;
            travel_to_link_value();
        }
        for(TreeNode childNode: node.getChild()){
            // System.out.println();
            travel(childNode,null);
        }
    }

    /*
    * 对在建立符号表阶段可以初始化的常量进行初始化;
    * */
    private void travel_to_link_value(TreeNode node) {
        if(node == null) {
            return;
        }else if(node instanceof PrimaryExp && ((PrimaryExp) node).lVal != null){
            LVal lVal = ((PrimaryExp) node).lVal;
            SymbolItem item = findInSymbolTable(lVal.ident.getName(),lVal.ident,"Var",false,currentTable);
            if(item == null ) return; //may error
            if( item.isConst() && item instanceof Var ){
                // is Const int and const array
                Var var = (Var) item;
                ArrayList<String> initValues = new ArrayList<>();
                var.constInitVal.getInitValue(initValues);
                if(lVal.exps.size() == ((Var) item).getShape().size()) {
                    // the dimension is ok
                    String value = "";
                    if(lVal.exps.size() == 0) {
                        // the dimension is 0
                        value = initValues.get(0);
                    }else if(lVal.exps.size() == 1){
                        // the dimension is 1
                        if(lVal.exps.get(0).getValue() != null){ //constExp
                            value = initValues.get(lVal.exps.get(0).getValue());
                        }
                    }else if(lVal.exps.size() == 2){
                        // the dimension is 2
                        if(lVal.exps.get(0).getValue() != null || lVal.exps.get(1).getValue() != null){
                            value = initValues.get(var.getShape().get(1) * lVal.exps.get(0).getValue() + lVal.exps.get(1).getValue());
                        }
                    }
                    if(!value.equals("")) {
                        ((PrimaryExp) node).value = value;
                    }
                }
            }
        }
        for(TreeNode item: node.getChild()){
            travel_to_link_value(item);
        }
    }

    private SymbolItem findInSymbolTable(String name, Ident ident, String type, boolean checkError, SymbolTable tableStack) {
        for(SymbolItem item: tableStack.symbolList){
            if(item.getName().equals(name)){
                if(item instanceof Func && type.equals("Func"))
                    return item;
                else if((item instanceof Var || item instanceof FuncFormVar ) && type.equals("Var"))
                    return item;
            }
        }
        if(tableStack.fatherTable != null ){
            return findInSymbolTable(name, ident, type, checkError, tableStack.fatherTable);
        }
        if(checkError)
            System.out.println("checkError!!!");//may error
        return null;
    }

    private void addFuncFormalArgs(TreeNode Args) throws Error {
        FuncFParams params = (FuncFParams) Args;
        for(FuncFParam param: params.getFuncFParams()){
            String name = param.getName();
            checkTable(name, param.ident, "Var");
            int dimension = param.getDimension();
            FuncFormVar funcFormVar = new FuncFormVar(name,dimension,param.getShape());
            currentTable.symbolList.add(funcFormVar);
        }
    }
    private void checkTable(String name, Ident ident, String Type){
        for(SymbolItem item: currentTable.symbolList){
            if(item.getName().equals(name) && (( item instanceof Func && Type.equals("Func") ) || ( item instanceof Var && Type.equals("Var") ) || ( item instanceof FuncFormVar && Type.equals("FuncFormVar")))){
                System.out.println("redefine error!");
            }
        }
    }
}
