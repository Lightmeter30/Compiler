package Mid;

import SymbolTable.SymbolItem;
import SymbolTable.SymbolTable;
import SymbolTable.FuncFormVar;
import front.SyntaxTree.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class MidCodeList {
    public ArrayList<MidCode> midCodes = new ArrayList<>();
    public HashMap<TreeNode, SymbolItem> nodeTableItem;
    public int blockDepth;
    public ArrayList<String> formatString = new ArrayList<>();
    public final int[] blockNum = new int[100];
    public HashMap<String, SymbolTable> currentFuncTable;
    public final Stack<String> beginTables = new Stack<>();
    public final Stack<String> endTables = new Stack<>();
    private int tmpIndex;
    private int labelCount;

    public MidCodeList( HashMap<TreeNode, SymbolItem> nodeTableItem, HashMap<String, SymbolTable> currentFuncTable ){
        this.currentFuncTable = currentFuncTable;
        this.nodeTableItem = nodeTableItem;
        this.labelCount = 0;
        this.tmpIndex = 0;
        this.blockDepth = 0;
        for(int i = 0; i < 100; i++){
            blockNum[i] = 0;
        }
    }
    public String add(MidCode.Op operation, String operand1, String operand2, String result){
        if ( operation.equals(MidCode.Op.WHILE_BIND) ) {
            // while循环的开始和结束标志
            beginTables.push(operand1);
            endTables.push(operand2);
            return null;
        } else if ( operation.equals(MidCode.Op.PUSH_PARA) && midCodes.get(midCodes.size() - 1).operation.equals(MidCode.Op.SIGNAL_ARR_ADDR)) {
            midCodes.remove(midCodes.size() - 1);
            operation = MidCode.Op.PUSH_PARA_ARR;
        } else if ( !operation.equals(MidCode.Op.PRINT) ) {
            // not print
            if(operand2.contains("[") && !operation.equals(MidCode.Op.ARR_LOAD) ) {
                // arrayVar must be load into temple var
                operand2 = this.add(MidCode.Op.ARR_LOAD, "#TEMP", operand2, "#NULL");
            }
            if( operand1.contains("[") && !operation.equals(MidCode.Op.ARR_SAVE) ){
                //oprand1 is Array
                if( operation.equals(MidCode.Op.JUMP_IF) || operation.equals(MidCode.Op.SET) ) {
                    String op1 = operand1.split(" ")[0];
                    String op2 = operand1.split(" ")[1];
                    if( op1.contains("[") )
                        op1 = this.add(MidCode.Op.ARR_LOAD, "#TEMP", op1, "#NULL");
                    if( op2.contains("[") )
                        op2 = this.add(MidCode.Op.ARR_LOAD, "#TEMP", op2, "#NULL");
                    operand1 = op1 + " " + op2;
                } else if( operation.equals(MidCode.Op.ASSIGN) || operation.equals(MidCode.Op.GETINT) ) {
                    if ( operation.equals(MidCode.Op.ASSIGN) )
                        operand2 = this.add(MidCode.Op.ASSIGN, "#TEMP", operand1, "#NULL");
                    else
                        operand2 = this.add(MidCode.Op.GETINT, "#TEMP", "#NULL", "#NULL");
                    operation = MidCode.Op.ARR_SAVE;
                } else {
                    operand1 = this.add(MidCode.Op.ARR_LOAD, "#TEMP", operand1, "#NULL");
                }
            }
        } else {
            if ( operand1.contains("[") && !operand2.equals("#STRCONS") ) {
                // array var
                operand1 = this.add(MidCode.Op.ARR_LOAD, "#TEMP", operand1, "#NULL");
            }
        }
        if( operation.equals(MidCode.Op.NEW_BLOCK) ) {
            blockDepth += 1;
            operand1 = blockDepth + "_" + blockNum[blockDepth];
            blockNum[blockDepth] += 1; // may change
        } else if( operation.equals(MidCode.Op.EXIT_BLOCK) ) {
            int temp = blockNum[blockDepth] - 1;
            operand1 = blockDepth + "_" + temp;
            blockDepth -= 1;
        }
        String end = result;
        if ( end.equals("#TEMP") ) {
            end = "#T" + tmpIndex;
            tmpIndex += 1;
        }
        if ( end.equals("#AUTO_LABEL") ) {
            end = "label" + labelCount;
            labelCount += 1;
        }
        if ( operand1.equals("#TEMP") ) {
            operand1 = "#T" + tmpIndex;
            if (operation.equals(MidCode.Op.ASSIGN) || operation.equals(MidCode.Op.ARR_LOAD) || operation.equals(MidCode.Op.GETINT)) // may change
                end = operand1;
            tmpIndex += 1;
        }
        if ( operand2.equals("#STRCONS") ) {
            formatString.add(operand1);
            operand2 = "#NULL";
            operand1 = "#str" + Integer.toString(formatString.size() - 1);
        }
        midCodes.add(new MidCode(operation, operand1, operand2, end));
        if ( operation.equals(MidCode.Op.FUNC) && !operand2.equals("main") ) {
            for (SymbolItem item: currentFuncTable.get(operand2).symbolList) {
                if ( item instanceof FuncFormVar && ((FuncFormVar) item).getDimension() == 0 ) // may change
                    this.add(MidCode.Op.FUNC_FORM_VAR_DEF, item.getUniqueName(), "#NULL", "#NULL");
            }
        }
        return end;
    }

    public String allocLabel() {
        String result = "label_" + labelCount;
        labelCount++;
        return result;
    }
}
