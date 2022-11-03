package back;

import Mid.MidCode;
import SymbolTable.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Mips {
    public boolean optimizeMul = false;
    public boolean optimizeAssignRegister = true;
    public boolean divOptHard = true;

    public int paraNum = 0;
    public static final int LocalAddrInit = 104;
    public static final int StackTBegin = 60;
    public static final int StackSBegin = 24;
    public static final String StackRA = "0($sp)";
    public static final int _dataStart = 0x10010000;
    public int funcCallSpOffset = 0;
    public String currentFuncName = "";
    public final Stack<Integer> prepareCnt = new Stack<>();

    public int index;

    public HashMap<String, Integer> globalArrayAddr = new HashMap<>();
    public int globalSize = 0;
    public int tmpLabelIndex = 1;
    public ArrayList<MidCode> midCodes;
    public ArrayList<String> mipsCodesList = new ArrayList<>(); // the end
    public ArrayList<String> formatString; //存放printf输出的字符串
    public HashMap<String, SymbolTable> allFuncTable;
    public SymbolTable globalTable;
    public final HashMap<String, Integer> funcStackSize = new HashMap<>();

    // mips寄存器初始化

    public ArrayList<String> registerS = new ArrayList<>();
    public ArrayList<String> registerT = new ArrayList<>();

    public final String[] reg = new String[]{
            "$zero", "$at", "$v0", "$v1",
            "$a0", "$a1", "$a2", "$a3",
            "$t0", "$t1", "$t2", "$t3",
            "$t4", "$t5", "$t6", "$t7",
            "$s0", "$s1", "$s2", "$s3",
            "$s4", "$s5", "$s6", "$s7",
            "$t8", "$t9", "$k0", "$k1",
            "$gp", "$sp", "$fp", "$ra"
    };

    //mips 运算符号 - 指令转化的hash表
    public HashMap<MidCode.Op, String> mipsAOp = new HashMap<>(); //算数运算符
    public HashMap<String, String> mipsBOp = new HashMap<>(); //判断跳转
    public HashMap<String, String> mipsSOp = new HashMap<>(); // set判断指令

    public Mips(ArrayList<MidCode> midCodes, ArrayList<String> formatString, HashMap<String, SymbolTable> allFuncTable, SymbolTable globalTable) {
        this.midCodes = midCodes;
        this.formatString = formatString;
        this.allFuncTable = allFuncTable;
        this.globalTable = globalTable;
        Init();
        for(Map.Entry<String, SymbolTable> item : allFuncTable.entrySet() ) {
            String name = item.getKey();
            if( item.getValue().symbolList.isEmpty() ) {
                funcStackSize.put(name, 0);
            }
            SymbolItem lastItem = item.getValue().symbolList.get(item.getValue().symbolList.size() - 1);
            funcStackSize.put(name, lastItem.getAddr() + lastItem.getSize());
        }
        for(SymbolItem item : globalTable.symbolList )
            globalSize = item.setAddr(globalSize);
    }

    public void addOneMipsCode(String code) {
        mipsCodesList.add(code);
    }

    public void addOneMipsCode(String operation, String operand1) {
        mipsCodesList.add(operation + " " + operand1);
    }

    public void addOneMipsCode(String operation, String operand1, String operand2) {
        mipsCodesList.add(operation + " " + operand1 + ", " + operand2);
        release(operand2);
        if( operation.equals("sw") || operation.equals("bltz") || operation.equals("bgez") || operation.equals("bgtz") ) {
            release(operand1);
        }
    }

    public void addOneMipsCode(String operation, String num1, String num2, String num3) {
        addOneMipsCode(operation, num1, num2, num3, true);
    }

    private boolean isConst(String operand) {
        return true; // may change
    }

    private boolean isConJump(String operation) {
        return this.mipsBOp.containsValue(operation);
    }

    public void addOneMipsCode(String operation, String num1, String num2, String num3, boolean isRelease) {
        if ( operation.equals("mul") && isConst(num3) && optimizeMul ) { // may change
            System.out.println("mul optimize!");
        }
        addOneMipsCode(operation + " " + num1 + ", " + num2 + ", " + num3);
        if( !isRelease ) return;
        if( operation.equals("addu") || operation.equals("subu") || operation.equals("mul") || operation.equals("div") || operation.equals("sll") || operation.equals("sra") ) {
            // addu, subu, mul, div, sll, sra
            if( !num1.equals(num2) )
                release(num2);
            if( !num1.equals(num3) )
                release(num3);
        } else if(isConJump(operation)) {
            release(num1);
            release(num2);
        }
    }

    private void release(String addr) {
        if (addr.charAt(0) == '$' && addr.charAt(1) == 't' ) {
            // $t寄存器需要释放喵
            addOneMipsCode("# RELEASE" + addr + " bind var " + registerT.get(addr.charAt(2) - '0'));
            registerT.set(addr.charAt(2) - '0', "#NULL");
        }
    }

    public void createMipsCode() {
        addOneMipsCode(".data");
        int addr = 0;
        // 遍历全局符号表,把数组填入内存
        for( SymbolItem item : globalTable.symbolList ) {
            if( item instanceof Var && !((Var) item).getShape().isEmpty() ) {
                // array
                addOneMipsCode("array_" + item.getName() + "_: .space " + item.getSize());
                globalArrayAddr.put(item.getUniqueName(), addr);
                addr += item.getSize();
            }
        }
        //把待输出字符串保存到内存
        for(int i = 0;i < formatString.size();i++)
            addOneMipsCode("str" + i + ": .asciiz" + " \"" + formatString.get(i) + "\"");
        addOneMipsCode(".text");
        boolean init = true;
        addOneMipsCode("addi $gp, $gp, " + this.globalSize);
        this.index = 0;

    }

    /**
     * 对类Mips相关变量进行初始化
     **/
    private void Init(){
        //  $s0 ~ $s7 初始化
        for(int i = 0;i < 8;i++)
            this.registerS.add("#NULL");
        //  $t0 ~$t9 初始化
        for(int i = 0;i < 10;i++)
            this.registerT.add("#NULL");
        //  算数运算符初始化
        mipsAOp.put(MidCode.Op.ADD, "addu");
        mipsAOp.put(MidCode.Op.SUB, "sub");
        mipsAOp.put(MidCode.Op.MUL, "mul");
        mipsAOp.put(MidCode.Op.DIV, "div");
        mipsAOp.put(MidCode.Op.MOD, "mod");
        //  判断跳转初始化
        mipsBOp.put("!=", "bne");
        mipsBOp.put("==", "beq");
        mipsBOp.put(">=", "bge");
        mipsBOp.put("<=", "ble");
        mipsBOp.put(">", "bgt");
        mipsBOp.put("<", "blt");
        // set判断指令 op a b c ---- b op c ? a = 1 : a = 0
        mipsSOp.put("!=", "sne");
        mipsSOp.put("==", "seq");
        mipsSOp.put(">=", "sge");
        mipsSOp.put("<=", "sle");
        mipsSOp.put(">", "sgt");
        mipsSOp.put("<", "slt");
    }
}
