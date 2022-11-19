package SymbolTable;

import front.ErrorList;
import front.SyntaxTree.*;
import front.Word.ConstInfo;
import front.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymLink {
    private final TreeNode root;
    private final HashMap<String, SymbolTable> symbolTables = new HashMap<>(); //存储着所有的符号表
    public final HashMap<TreeNode,SymbolItem> nodeTableItem = new HashMap<>();
    private final HashMap<String, SymbolTable> funcTables = new HashMap<>();
    private int currentDepth;
    private final int[] depths = new int[100];
    public SymbolTable rootTable;
    private SymbolTable currentTable;
    private boolean isAssign = false;
    private String currentFuncName;
    private int debug = 0;
    private int addr;
    private enum BlockType{
        whileBlock, voidFuncBlock, intFuncBlock
    }
    public SymLink(TreeNode root){
        this.root = root;
        this.currentDepth = 0;
        for(int i = 0;i < 100; i++){
            depths[i] = 0;
        }
    }

    public HashMap<String, ArrayList<SymbolItem>> getFuncTable(){
        HashMap<String, ArrayList<SymbolItem>> result = new HashMap<>();
        for(Map.Entry<String,SymbolTable> item : funcTables.entrySet()){
            String funcName = item.getKey();
            SymbolTable curTable = item.getValue();
            ArrayList<SymbolItem> table = new ArrayList<>();
            addr = 4;
            addAllTableItemInTable(table, curTable);
            result.put(funcName, table);
        }
        return result;
    }

    private void addAllTableItemInTable(ArrayList<SymbolItem> tableItemList, SymbolTable table){
        for(SymbolItem item : table.symbolList) {
            addr = item.setAddr(addr);
            tableItemList.add(item);
        }
        if( table.sonTable.size() != 0 ) {
            for(SymbolTable sonTable : table.sonTable)
                addAllTableItemInTable(tableItemList,sonTable);
        }
    }

    public void buildSymbolTable(){
        try {
            rootTable = new SymbolTable(new int[]{0,0},null);
            currentTable = rootTable;
            symbolTables.put("<0_0>",currentTable);
            travel(root,null);
            travelForCheck(root);
        } catch (Error ignored) {
            ignored.printStackTrace();
        }
    }
//    public HashMap<String,ArrayList<SymbolItem>> getFuncTable()

    private void travel(TreeNode node, TreeNode funcFormalArgs) throws Error{
        if( node == null ) {
            return;
        }else if( node instanceof MainFuncDef ) {
//            System.out.println("It's MainFuncDef!");
            //main是否加入其中 may change
            currentFuncName = "main";
        }else if(node instanceof Block) {
            currentDepth++;
            SymbolTable father = currentTable;
            currentTable = new SymbolTable(new int[]{currentDepth, depths[currentDepth]},father);
            father.sonTable.add(currentTable);
            symbolTables.put("<" + currentDepth + "_" + depths[currentDepth] + ">", currentTable);
            depths[currentDepth]++;
            if(funcFormalArgs != null ){
                funcTables.put(currentFuncName, currentTable);
                addFuncFormalArgs(funcFormalArgs);
            }
            if(currentFuncName.equals("main")) {
                funcTables.put(currentFuncName, currentTable);
                currentFuncName = "_main_block_";
            }
        }else if(node instanceof VarDef){
            VarDef varDef = (VarDef) node;
            String name = varDef.ident.getName();
            checkTable(name, varDef.ident,"Var");
            for(TreeNode childNode: varDef.getChild()){
                travel(childNode,null);
            } // may change
            SymbolItem item = new Var(name,false,varDef.getDimension(),varDef.getInitVal(),varDef.getShape(),getLoc());
            currentTable.symbolList.add(item);
            nodeTableItem.put(varDef.ident, item);
            return;
        }else if(node instanceof ConstDef) {
            ConstDef constDef = (ConstDef) node;
            String name = constDef.getIdent().getName();
            checkTable(name,constDef.getIdent(),"Var");
            for(TreeNode childNode: constDef.getChild()){
                travel(childNode,null);
            } // may change
            SymbolItem item = new Var(name, true, constDef.getDimension(),constDef.getConstInitVal(),constDef.getShape(),getLoc());
            currentTable.symbolList.add(item);
            nodeTableItem.put(constDef.getIdent(),item);
            return;
        }else if(node instanceof FuncDef){
            FuncDef funcDef = (FuncDef) node;
            String name = funcDef.getIdent().getName();
            checkTable(name,funcDef.getIdent(),"Func");
            Integer argc = funcDef.getArgc();
            Func.Type type = funcDef.getFuncType().getType().equals(FuncType.Type.Int) ? Func.Type.intFunc : Func.Type.voidFunc;
            SymbolItem item = new Func(name,type,argc,getLoc());
            currentTable.symbolList.add(item);
            nodeTableItem.put(funcDef.getIdent(),item);
            currentFuncName = name;
            travel_to_link_value(funcDef.getChild().get(2)); //link_value in FuncFParams
            travel(funcDef.getChild().get(3), funcDef.getChild().get(2)); // 将函数形参加入符号表
            return;
        }else if(node instanceof UnaryExp &&((UnaryExp) node).type.equals(UnaryExp.Type.FuncCall)) {
            // UnaryExp FuncCall
            UnaryExp unaryExp = (UnaryExp) node;
            Ident ident = (Ident) node.getChild().get(0);
            SymbolItem item = findInSymbolTable(unaryExp.getFuncCallName(), ident,"Func", true, currentTable);
            int paramNum = 0;
            if(unaryExp.getChild().size() > 1)
                paramNum = unaryExp.getChild().get(1).getChild().size();
            if(item != null && !((Func) item).checkArgcNum(paramNum)){
                ErrorList.addError(new Error('d', ident.getLineNum()));//System.out.println("mismatch of params!"); // may change
            }else{
                // the paramsNum is ok
                if( unaryExp.getChild().size() > 1 ) {
                    String funcName = unaryExp.getFuncCallName();
                    SymbolTable table = funcTables.get(funcName);
                    if( funcName == null ) return;
                    if( table != null ) {
                        int index = 0;
                        // ArrayList<Integer> realShape = null;
                        for( TreeNode nod: node.getChild().get(1).getChild() ){ // exp in FuncRParams
                            Exp exp = (Exp) nod;
                            int formDimension = 0;
                            String name = exp.getName();
                            if( name != null ) {
                                SymbolItem funcCall = findInSymbolTable(name, ident, "Func", false, currentTable);
                                if ( funcCall != null ) {
                                    if( ((Func) funcCall).getType().equals(Func.Type.voidFunc) && exp.isFuncCall() ) {
                                        ErrorList.addError(new Error('e', ident.getLineNum()));//System.out.println("Error! funcCall argType mismatch!");// may change
                                        index++;
                                        continue;
                                    } else if( ((Func) funcCall).getType().equals(Func.Type.intFunc) && exp.isFuncCall() ) {
                                        if( ((FuncFormVar) table.symbolList.get(index)).getDimension() != 0 )
                                            ErrorList.addError(new Error('e', ident.getLineNum()));//System.out.println("Error! funcCall argType mismatch!");// may change
                                        index++;
                                        continue;
                                    }
                                }
                                SymbolItem funcItem = findInSymbolTable(name, null, "Var", false, currentTable);
                                if( funcItem == null ) {
                                    break;
                                }
                                int varDimension;
                                if ( funcItem instanceof Var )
                                    varDimension = ((Var) funcItem).getDimension();
                                else
                                    varDimension = ((FuncFormVar) funcItem).getDimension();
                                formDimension = varDimension - exp.getDimension();
                            }
                            if ( ((FuncFormVar) table.symbolList.get(index)).getDimension() != formDimension )
                                ErrorList.addError(new Error('e', ident.getLineNum()));//System.out.println("Error! funcCall argType mismatch!"); // may change
                            index++;
                        }
                    }
                }
            }
            nodeTableItem.put(node,item);
        } else if( node instanceof Stmt && (((Stmt) node).getType().equals(Stmt.Type.Assign) ||
                ((Stmt) node).getType().equals(Stmt.Type.Input)) ) {
            //Stmt: Assign, Input
            this.isAssign = true;
            Stmt stmt = (Stmt) node;
            Ident ident = (Ident) stmt.getChild().get(0).getChild().get(0);
            SymbolItem item = findInSymbolTable(ident.getName(), ident, "Var", true, currentTable);
            if(item != null && item.isConst()){
                System.out.println("Error! can't change const"); // may change
            }
            nodeTableItem.put(ident,item);
        } else if( node instanceof Stmt && ((Stmt) node).getType().equals(Stmt.Type.Output) ){
            Stmt stmt = (Stmt) node;
            ConstInfo token = ((ErrorSymbol) node.getChild().get(0)).getToken();
            FormatString formatString = (FormatString) stmt.getChild().get(1);
            if( formatString.getFormatStringNum() != stmt.getChild().size() - 2 ){
                System.out.println("Error! the FormatString %d mismatch"); // may change
            }
        } else if( node instanceof PrimaryExp && ((PrimaryExp) node).lVal != null ) {
            PrimaryExp primaryExp = (PrimaryExp) node;
            Ident ident = primaryExp.lVal.ident;
            SymbolItem item = findInSymbolTable(ident.getName(), ident, "Var", true, currentTable);
            nodeTableItem.put(ident,item);
        }
        for(TreeNode childNode: node.getChild()){
            // System.out.println();
            travel(childNode,null);
        }

        // 此时node节点的子节点已经被遍历完力!
        if( node instanceof Block ) {
            currentDepth--;
            currentTable = currentTable.fatherTable;
        } else if( node instanceof Stmt && (((Stmt) node).getType().equals(Stmt.Type.Assign) ||
                ((Stmt) node).getType().equals(Stmt.Type.Input)) ) {
            this.isAssign = false;
        } else if( node instanceof PrimaryExp && ((PrimaryExp) node).lVal != null ) {
            PrimaryExp primaryExp = (PrimaryExp) node;
            LVal lVal = primaryExp.lVal;
            SymbolItem item = findInSymbolTable(lVal.ident.getName(),lVal.ident,"Var",false, currentTable);
            if( item == null )
                return;
            if( item.isConst() && item instanceof Var ) {
                setConstPrimaryExp((Var) item, node, lVal);
            }
        }
    }

    private final Stack<BlockType> blockTypeStack = new Stack<>();
    private final Stack<BlockType> funcBlockStack = new Stack<>();

    private void travelForCheck(TreeNode node) {
        if(node == null) return;
        if(node instanceof FuncDef && ((FuncDef) node).getFuncType().getType().equals(FuncType.Type.Void)) { //void func
            blockTypeStack.push(BlockType.voidFuncBlock);
            funcBlockStack.push(BlockType.voidFuncBlock);
        }else if(node instanceof FuncDef && ((FuncDef) node).getFuncType().getType().equals(FuncType.Type.Int) || node instanceof MainFuncDef) { //int func
            blockTypeStack.push(BlockType.intFuncBlock);
            funcBlockStack.push(BlockType.intFuncBlock);
            Block block = (Block) node.getChild().get(node.getChild().size() - 2);
            if(block.getChild().size() != 0){
                TreeNode stmt = block.getChild().get(block.getChild().size() - 1).getChild().get(0);
                if(!(stmt instanceof Stmt && ((Stmt) stmt).getType().equals(Stmt.Type.ReturnStmt))) {
                    ErrorList.addError(new Error('g', ((ErrorSymbol) node.getChild().get(node.getChild().size() - 1)).getToken().getLineCounter())); //有返回值的函数缺少return语句
                }
            } else {
                ErrorList.addError(new Error('g', ((ErrorSymbol) node.getChild().get(node.getChild().size() - 1)).getToken().getLineCounter())); //有返回值的函数缺少return语句
            }
        }else if(node instanceof Stmt) { //stmt
            Stmt.Type type = ((Stmt) node).getType();
            if(type.equals(Stmt.Type.ReturnStmt)){
                if(node.getChild().size() > 1 && funcBlockStack.peek().equals(BlockType.voidFuncBlock)) {
                    ErrorList.addError(new Error('f', ((ErrorSymbol) node.getChild().get(0)).getToken().getLineCounter()));
                }
//                else if(blockTypeStack.peek().equals(BlockType.intFuncBlock)){
//
//                }
            }else if(type.equals(Stmt.Type.WhileBranch)) {
                blockTypeStack.push(BlockType.whileBlock);
            }else if(type.equals(Stmt.Type.BreakStmt) || type.equals(Stmt.Type.ContinueStmt)){
                if(!blockTypeStack.peek().equals(BlockType.whileBlock)) {
                    ErrorList.addError(new Error('m', ((ErrorSymbol) node.getChild().get(0)).getToken().getLineCounter()));
                }
            }
        }
        for(TreeNode child : node.getChild()){
            travelForCheck(child);
        }
        if(node instanceof FuncDef || node instanceof MainFuncDef ) {
            blockTypeStack.pop();
            funcBlockStack.pop();
        } else if( (node instanceof Stmt && ((Stmt) node).getType().equals(Stmt.Type.WhileBranch)) ) {
            blockTypeStack.pop();
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
            if( item == null ) return; //may error
            if( item.isConst() && item instanceof Var ){
                setConstPrimaryExp((Var) item, node, lVal);
            }
        }
        for(TreeNode item: node.getChild()){
            travel_to_link_value(item);
        }
    }

    private SymbolItem findInSymbolTable(String name, Ident ident, String type, boolean checkError, SymbolTable currentTable) {
        for(SymbolItem item: currentTable.symbolList){
            if(item.getName().equals(name)){
                if(item instanceof Func && type.equals("Func"))
                    return item;
                else if((item instanceof Var || item instanceof FuncFormVar ) && type.equals("Var"))
                    return item;
            }
        }
        if(currentTable.fatherTable != null ){
            return findInSymbolTable(name, ident, type, checkError, currentTable.fatherTable);
        }
        if(checkError)
            ErrorList.addError(new Error('c', ident.getLineNum()));//System.out.println("checkError!!!");//may change
        return null; // may change
    }

    private void addFuncFormalArgs(TreeNode Args) throws Error {
        FuncFParams params = (FuncFParams) Args;
        for(FuncFParam param: params.getFuncFParams()){
            String name = param.getName();
            checkTable(name, param.ident, "Var");
            int dimension = param.getDimension();
            FuncFormVar funcFormVar = new FuncFormVar(name,dimension,param.getShape(),getLoc());
            currentTable.symbolList.add(funcFormVar);
        }
    }
    private void checkTable(String name, Ident ident, String Type){
        // may change
        for(SymbolItem item: currentTable.symbolList){
            if(item.getName().equals(name) && (( item instanceof Func && Type.equals("Func") ) || ( item instanceof Var && Type.equals("Var") ) || ( item instanceof FuncFormVar && Type.equals("FuncFormVar")))){
                ErrorList.addError(new Error('b', ident.getLineNum())); //System.out.println("redefine error!");
            }
        }
    }
    private void setConstPrimaryExp(Var var,TreeNode node, LVal lVal){
        ArrayList<String> initValues = new ArrayList<>();
        var.constInitVal.getInitValue(initValues);
        if( lVal.exps.size() == var.getShape().size() ) {
            String value = "";
            if( lVal.exps.size() == 0 ) {
                value = initValues.get(0);
            } else if( lVal.exps.size() == 1 ) {
                if( lVal.exps.get(0).getValue() != null ) {
                    //ConstExp
                    value = initValues.get(lVal.exps.get(0).getValue());
                }
            } else if( lVal.exps.size() == 2 ) {
                if( lVal.exps.get(0).getValue() != null && lVal.exps.get(1).getValue() != null ) {
                    value = initValues.get( var.getShape().get(1) * lVal.exps.get(0).getValue() + lVal.exps.get(1).getValue() );
                }
            }
            if( !value.equals("")) {
                ((PrimaryExp) node).value = value;
            }
        }
    }
    private String getLoc(){
        return "<" + currentDepth + "_" + (depths[currentDepth] > 0 ? depths[currentDepth] - 1 : 0) + ">";
    }

    public HashMap<String, SymbolTable> getFuncTables(){
        return this.funcTables;
    }

    public HashMap<String, SymbolTable> getSymbolTables() {
        return symbolTables;
    }
}
