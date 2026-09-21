package Mid;

import java.util.ArrayList;
import java.util.HashMap;


public class MidCode {
    public enum Op {
        ASSIGN, ADD, SUB, MUL, DIV, MOD, NOT, PRINT,
        GETINT, FUNC, END_FUNC, PREPARE_CALL, CALL,
        PUSH_PARA,RETURN, VAR_DEF, CONST_DEF,
        ARR_SAVE, ARR_LOAD, PUSH_PARA_ARR,
        JUMP_IF, JUMP, LABEL, SET,BITAND,
        //???
        SIGNAL_ARR_ADDR, NEW_BLOCK, EXIT_BLOCK,
        WHILE_BIND, EMPTY, EMPTY_INPUT, ENTER_WHILE,
        EXIT_WHILE, FUNC_FORM_VAR_DEF
    }
    public final HashMap<Op, String> OpToTring = new HashMap<>();
    public Op operation;
    public String operand1;
    public String operand2;
    public String result;

    public Integer dIndex = null;
    public String define;
    public ArrayList<String> use;

    public MidCode(Op operation, String operand1, String operand2, String result){
        OpToStringInit();
        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.result = result;
    }
    private void OpToStringInit(){
        OpToTring.put(Op.ASSIGN, "ASSIGN");
        OpToTring.put(Op.ADD, "ADD");
        OpToTring.put(Op.SUB, "SUB");
        OpToTring.put(Op.MUL, "MUL");
        OpToTring.put(Op.DIV, "DIV");
        OpToTring.put(Op.MOD, "MOD");
        OpToTring.put(Op.NOT, "NOT");
        OpToTring.put(Op.PRINT, "PRINT");
        OpToTring.put(Op.GETINT, "GETINT");
        OpToTring.put(Op.FUNC, "FUNC");
        OpToTring.put(Op.END_FUNC, "END_FUNC");
        OpToTring.put(Op.PREPARE_CALL, "PREPARE_CALL");
        OpToTring.put(Op.CALL, "CALL");
        OpToTring.put(Op.PUSH_PARA, "PUSH_PARA");
        OpToTring.put(Op.RETURN, "RETURN");
        OpToTring.put(Op.VAR_DEF, "VAR_DEF");
        OpToTring.put(Op.CONST_DEF, "CONST_DEF");
        OpToTring.put(Op.NEW_BLOCK, "NEW_BLOCK");
        OpToTring.put(Op.EXIT_BLOCK, "EXIT_BLOCK");
        OpToTring.put(Op.PUSH_PARA_ARR, "PUSH_PARA_ARR");
        OpToTring.put(Op.JUMP_IF, "JUMP_IF");
        OpToTring.put(Op.JUMP, "JUMP");
        OpToTring.put(Op.LABEL, "LABEL");
        OpToTring.put(Op.SET, "SET");
        OpToTring.put(Op.BITAND, "BITAND");
    }

    public static boolean isArithmetic(MidCode.Op op){
        return op == Op.ADD || op == Op.SUB || op == Op.MUL || op == Op.DIV || op == Op.MOD;
    }

/*    public String getDefine(){
        switch (this.operation) {
            case ASSIGN:
            case VAR_DEF:
            case GETINT:
            case ARR_LOAD:
            case FUNC_FORM_VAR_DEF:
                this.define = (!)
        }
    }*/

/*    public static boolean isVar(String operand) {
        if ( operand.split("@").length == 2 && operand.split("@")[1].equals("<0,0>"))
            return false;
        return
    }*/

    @Override
    public String toString(){
        switch (operation){
            case EMPTY:
                return "_E_M_P_T_Y_";
            case FUNC_FORM_VAR_DEF:
                return "FUNC_FORM_VAR_DEF " + this.operand1;
            case EMPTY_INPUT:
                return "EMPTY_INPUT";
            case LABEL:
                return result + ":";
            case JUMP:
                return "JUMP " + result;
            case JUMP_IF:
                return "JUMP_IF " + operand1.split(" ")[0] + " " + operand2 + " " +operand1.split(" ")[1] + " " + result;
            case SET:
                return "SET " + result + " := " + operand1.split(" ")[0] + " " + operand2 + " " + operand1.split(" ")[1];
            case GETINT:
                return operand1 + " = " + "input()";
            case VAR_DEF:
            case CONST_DEF:
                return "var " + operand1 + (operand2.equals("#NULL")? "" : "= " + operand2);
            case FUNC:
            case END_FUNC:
                return "########" + OpToTring.get(operation) + " " + operand1 + " " + operand2 + "########";
            case ASSIGN:
            case ARR_SAVE:
            case ARR_LOAD:
                return operand1 + " = " + operand2;
        }
        if( !result.equals("#NULL") )
            return result + " = " + operand1 + " " + OpToTring.get(operation) + " " + operand2;
        if( operand1.equals("#NULL") && operand2.equals("#NULL") )
            return OpToTring.get(operation);
        if( operand2.equals("#NULL") )
            return OpToTring.get(operation) + " " + operand1;
        return OpToTring.get(operation) + " " + operand1 + " " + operand2;
    }
}
