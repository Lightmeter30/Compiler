package back;

import Mid.MidCode;
import SymbolTable.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Mips {
    public boolean optimizeMul = false;
    public boolean optimizeSetRegister = true;
    public boolean divOptHard = true;

    public int paraNum = 0;
    public String paraAddr;
    public static final int LocalAddrInit = 104;    // 函数调用必备栈运行空间偏移量(寄存器S,T等)
    public static final int StackTBegin = 60;       // stack中$t0相对于$sp的偏移
    public static final int StackSBegin = 24;       // stack中$s0相对于$sp的偏移
    public static final String StackRA = "0($sp)";  //stack中$ra相对于$sp的偏移
    public static final int _dataStart = 0x10010000;    // .data段开始地址
    public int funcCallSpOffset = 0;                    // 函数调用前$sp指针的偏移量
    public String currentFuncName = "";                 // 当前函数的名字
    public final Stack<Integer> prepareCnt = new Stack<>(); //栈顶为函数调用时参数的个数,进行函数调用后需要pop掉;暂时没啥用
    public final ArrayList<Integer> spSize = new ArrayList<Integer>() {{add(0);}}; // 相当于一个栈, 函数调用完成后弹出相关信息, 其和为当前偏移量;

    public int index;

    public HashMap<String, Integer> globalArrayAddr = new HashMap<>();
    public int globalSize = 0;
    public int tmpLabelIndex = 1;
    public ArrayList<MidCode> midCodes;
    public ArrayList<String> mipsCodesList = new ArrayList<>(); // the end
    public ArrayList<String> formatString; //存放printf输出的字符串
    public HashMap<String, ArrayList<SymbolItem>> allFuncTable;
    public SymbolTable globalTable;
    public final HashMap<String, Integer> funcStackSize = new HashMap<>(); //函数占用的栈的内存大小

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

    public Mips(ArrayList<MidCode> midCodes, ArrayList<String> formatString, HashMap<String, ArrayList<SymbolItem>> allFuncTable, SymbolTable globalTable) {
        this.midCodes = midCodes;
        this.formatString = formatString;
        this.allFuncTable = allFuncTable;
        this.globalTable = globalTable;
        Init();
        for(Map.Entry<String, ArrayList<SymbolItem>> item : allFuncTable.entrySet() ) { // may change
            String name = item.getKey();
            if( item.getValue().isEmpty() ) {
                funcStackSize.put(name, 0);
                continue;
            }
            SymbolItem lastItem = item.getValue().get(item.getValue().size() - 1);
            funcStackSize.put(name, lastItem.getAddr() + lastItem.getSize());
        }
        for( SymbolItem item : globalTable.symbolList )
            globalSize = item.setAddr(globalSize);
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
        for (index = 0; index < midCodes.size(); index++) {
            MidCode midCode = midCodes.get(index);
            mipsCodesList.add("# ====" + midCode + "====");
            MidCode.Op operation = midCode.operation;
            String operand1 = midCode.operand1;
            String operand2 = midCode.operand2;
            String result = midCode.result;
            switch (operation){
                case FUNC:
                    // new Function
                    if(init) {
                        addOneMipsCode("addi $sp, $sp -" + (funcStackSize.get("main") + LocalAddrInit)); // 将main函数压入栈，改变栈指针$sp的值
                        addOneMipsCode("j main");
                        init = false;
                    }
                    currentFuncName = operand2;
                    addOneMipsCode(operand2 + ":");
                    funcCallSpOffset = 0;
                    for (int i = 0;i < registerS.size(); i++)
                        registerS.set(i, "#NULL");
                    for(SymbolItem item : allFuncTable.get(currentFuncName)) {
                        if( item instanceof FuncFormVar ) {
                            String registerS = setRegisterS(item.getUniqueName());
                            if( registerS.equals("#FULL") ){
                                break;
                            } else {
                                addOneMipsCode("lw", registerS, (item.getAddr() + funcCallSpOffset + "($sp)"));
                            }
                        }
                    }
                    break;
                case RETURN:
                    if( !operand1.equals("#NULL") ) {
                        loadValue(operand1, "$v0");
                    }
                    if(currentFuncName.equals("main")) {
                        //It's the end of mips code
                        addOneMipsCode("addi $gp, $gp, -" + this.globalSize);
                        addOneMipsCode("addi $sp, $sp, " + (funcStackSize.get("main") + LocalAddrInit));
                        addOneMipsCode("li $v0, 10");
                        addOneMipsCode("syscall");
                    } else {
                        addOneMipsCode("jr $ra");
                    }
                    break;
                case PREPARE_CALL:
                    prepareCnt.push(0); // ???
                    spSize.add(LocalAddrInit + funcStackSize.get(operand1)); // 调用函数的$sp地址
                    funcCallSpOffset = sum(spSize);
                    addOneMipsCode("addi $sp, $sp -" + spSize.get(spSize.size() - 1));
                    break;
                case PUSH_PARA:
                    paraNum = prepareCnt.peek();
                    paraAddr = allFuncTable.get(operand2).get(paraNum).getAddr().toString() + "($sp)";
                    prepareCnt.set(prepareCnt.size() - 1, paraNum + 1 );
                    String reg = "$a1";
                    boolean bInRegister = isInRegister(operand1) || setRegister(operand1, true);
                    String b = operandToAddr(operand1);
                    if(bInRegister)
                        addOneMipsCode("sw", b, paraAddr);
                    else if(isConst(operand1)) {
                        addOneMipsCode("li", reg, b);
                        addOneMipsCode("sw", reg, paraAddr);
                    } else {
                        addOneMipsCode("lw", reg, b);
                        addOneMipsCode("sw", reg, paraAddr);
                    }
                    break;
                case PUSH_PARA_ARR:
                    paraNum = prepareCnt.peek();
                    paraAddr = allFuncTable.get(operand2).get(paraNum).getAddr().toString() + "($sp)";
                    prepareCnt.set(prepareCnt.size() - 1,paraNum + 1);
                    String arrayRank = "0";
                    if( operand1.split("\\[").length > 1 ) {
                        arrayRank = operand1.split("\\[")[1].substring(0, operand1.split("\\[")[1].length() - 1);
                        operand1 = operand1.split("\\[")[0];
                    }
                    String pushRegister = operandToAddrArray(operand1, arrayRank);
                    addOneMipsCode("sw",pushRegister, paraAddr);
                    break;
                case CALL:
                    prepareCnt.pop();
                    ArrayList<Integer> savedS = new ArrayList<>(), savedT = new ArrayList<>();
                    ArrayList<String> oldT = new ArrayList<>(); // (ArrayList<String>) registerT.clone()
                    oldT.addAll(registerT);
                    for(int i = 0;i < registerT.size();i++) {
                        if( !registerT.get(i).equals("#NULL") ) {
                            savedT.add(i);
                            addOneMipsCode("sw", "$t" + i, (StackTBegin + 4 * i + funcStackSize.get(operand1)) + "($sp)");
                            registerT.set(i, "#NULL");
                        }
                    }
                    ArrayList<String> oldS = new ArrayList<>();
                    oldS.addAll(registerS);
                    for(int i = 0;i < registerS.size();i++) {
                        if( !registerS.get(i).equals("#NULL") ) {
                            savedS.add(i);
                            addOneMipsCode("sw", ((i == registerS.size() - 1) ? "$fp" : ("$s" + i)), (StackSBegin + 4 * i + funcStackSize.get(operand1)) + "($sp)");
                            registerS.set(i, "#NULL");
                        }
                    }
                    if( !currentFuncName.equals("main") ) {
                        addOneMipsCode("sw", "$ra", StackRA);
                    }
                    addOneMipsCode("jal", operand1);
                    if( !currentFuncName.equals("main") ) {
                        addOneMipsCode("lw", "$ra", StackRA);
                    }
                    for(int i : savedS) {
                        addOneMipsCode("lw", ((i == registerS.size() - 1) ? "$fp" : ("$s" + i)), (StackSBegin + 4 * i + funcStackSize.get(operand1)) + "($sp)");
                    }
                    registerS = oldS;
                    for(int i : savedT) {
                        addOneMipsCode("lw", "$t" + i, (StackTBegin + 4 * i + funcStackSize.get(operand1)) + "($sp)");
                    }
                    registerT = oldT;
                    addOneMipsCode("addi $sp, $sp, " + spSize.get(spSize.size() - 1));
                    spSize.remove(spSize.size() - 1);
                    funcCallSpOffset = sum(spSize);
                    break;
                case PRINT:
                    if(operand1.charAt(0) == '#' && operand1.charAt(1) != 'T') {
                        addOneMipsCode("la $a0, " + operand1.substring(1));
                        addOneMipsCode("li $v0, 4"); //print string
                    } else {
                        loadValue(operand1, "$a0");
                        addOneMipsCode("li $v0, 1"); // print number
                    }
                    addOneMipsCode("syscall");
                    break;
                case GETINT:
                case EMPTY_INPUT:
                    addOneMipsCode("li $v0, 5"); //input
                    addOneMipsCode("syscall");
                    if(operation.equals(MidCode.Op.GETINT))
                        saveValue("$v0", operand1); // may change
                    break;
                case ASSIGN:
                case VAR_DEF:
                case CONST_DEF:
                    if( !operand2.equals("#NULL") )
                        GenAssign(operand1, operand2);
                    break;
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                case BITAND:
                    String mipsOp = mipsAOp.get(operation);
                    String Reg1 = "$a1";
                    String Reg2 = "$a2";
                    boolean a_in_reg = isInRegister(result) || setRegister(result, false);
                    boolean b_in_reg_or_const = isConst(operand1) || isInRegister(operand1) || setRegister(operand1, true);
                    boolean c_in_reg_or_const = isConst(operand2) || isInRegister(operand2) || setRegister(operand2, true);

                    String aa = operandToAddr(result);
                    String bb = operandToAddr(operand1);
                    String cc = operandToAddr(operand2);
                    boolean sra = false;

                    if(isConst(bb) && isConst(cc)) {
                        int ans = 0;
                        switch (mipsOp) {
                            case "addu":
                                ans = Integer.parseInt(bb) + Integer.parseInt(cc);
                                break;
                            case "sub":
                                ans = Integer.parseInt(bb) - Integer.parseInt(cc);
                                break;
                            case "mul":
                                ans = Integer.parseInt(bb) * Integer.parseInt(cc);
                                break;
                            case "div":
                                ans = Integer.parseInt(bb) / Integer.parseInt(cc);
                                break;
                            case "mod":
                                ans = Integer.parseInt(bb) % Integer.parseInt(cc);
                                break;
                        }
                        if(a_in_reg)
                            addOneMipsCode("li", aa, Integer.toString(ans));
                        else {
                            addOneMipsCode("li", Reg1, Integer.toString(ans));
                            addOneMipsCode("sw", Reg1, aa);
                        }
                        break;
                    }
                    if(a_in_reg) {
                        if(b_in_reg_or_const && c_in_reg_or_const) {
                            if(isConst(bb)) {
                                addOneMipsCode("li", Reg2, bb);
                                bb = Reg2;
                            }
                            GenArithmetic(mipsOp, aa, bb, cc);
                        } else if(b_in_reg_or_const) {
                            addOneMipsCode("lw", Reg1, cc);
                            if(isConst(bb)) {
                                addOneMipsCode("li", Reg2, bb);
                                bb = Reg2;
                            }
                            GenArithmetic(mipsOp, aa, bb, Reg1);
                        } else if(c_in_reg_or_const) {
                            addOneMipsCode("lw", Reg1, bb);
                            GenArithmetic(mipsOp, aa, Reg1, cc);
                        } else {
                            addOneMipsCode("lw", Reg1, bb);
                            addOneMipsCode("lw", Reg2, cc);
                            GenArithmetic(mipsOp, aa, Reg1, Reg2);
                        }
                    } else {
                        if(b_in_reg_or_const && c_in_reg_or_const) {
                            if(isConst(bb)) {
                                addOneMipsCode("li", Reg2, bb);
                                bb = Reg2;
                            }
                            GenArithmetic(mipsOp, Reg1, bb, cc);
                            addOneMipsCode("sw", Reg1, aa);
                        } else if(b_in_reg_or_const) {
                            addOneMipsCode("lw", Reg1, cc);
                            if(isConst(bb)) {
                                addOneMipsCode("li", Reg2, bb);
                                bb = Reg2;
                            }
                            GenArithmetic(mipsOp, Reg1, bb, Reg1);
                            addOneMipsCode("sw", Reg1, aa);
                        } else if(c_in_reg_or_const) {
                            addOneMipsCode("lw", Reg1, bb);
                            GenArithmetic(mipsOp, Reg1, Reg1, cc);
                            addOneMipsCode("sw", Reg1, aa);
                        } else {
                            addOneMipsCode("lw", Reg1, bb);
                            addOneMipsCode("lw", Reg2, cc);
                            GenArithmetic(mipsOp, Reg1, Reg1, Reg2);
                            addOneMipsCode("sw", Reg1, aa);
                        }
                    }
                    break;
                case ARR_LOAD:
                case ARR_SAVE:
                    //LOAD: a = b[c]
                    //save: a[b] = c
                    String arrayOp;
                    boolean aInRegister;
                    String a;
                    if(operation.equals(MidCode.Op.ARR_SAVE)) {
                        arrayOp = operand1;
                        aInRegister = isInRegister(operand2) || setRegister(result, true);
                        a = operandToAddr(operand2);
                    } else {
                        arrayOp = operand2;
                        aInRegister = isInRegister(operand1) || setRegister(result, false);
                        a = operandToAddr(operand1);
                    }
                    String itemAddr;
                    String register0 = "$a0";
                    String register1 = "$a1";
                    String register2 = "$a2";

                    // name[rank] name: array@<1_5>
                    String name = arrayOp.split("\\[")[0];
                    String rank = arrayOp.split("\\[")[1].substring(0, arrayOp.split("\\[")[1].length() - 1);
                    boolean isGlobal = isInGlobal(name);
                    boolean isPointer = isPointer(name);
                    if(isPointer) {
                        // Pointer on Stack
                        if(isInRegister(name)) {
                            register0 = operandToAddr(name);
                        } else {
                            addOneMipsCode("lw", register0, operandToAddr(name));
                        }
                        if( Tools.isBeginByNumber(rank)) {
                            int offset = Integer.parseInt(rank) * 4;
                            itemAddr = offset + "(" + register0 + ")";
                        } else {
                            boolean rankInRegister = isInRegister(rank);
                            rank = operandToAddr(rank);
                            if(rankInRegister) {
                                addOneMipsCode("sll", register1, rank, "2");
                            } else {
                                addOneMipsCode("lw", register1, rank);
                                addOneMipsCode("sll", register1, register1, "2");
                            }
                            GenArithmetic("addu", register1, register0, register1);
                            itemAddr = "0(" + register1 + ")";
                        }
                    } else {
                        // Not Pointer
                        if(Tools.isBeginByNumber(rank)) {
                            //  const rank
                            int offset = Integer.parseInt(rank) * 4;
                            if(isGlobal) {
                                itemAddr = "array_" + name.split("@")[0] + "_+" + offset + "($zero)";
                            } else {
                                // on stack
                                offset += Objects.requireNonNull(findItemInFuncTable(name)).getAddr() + funcCallSpOffset;
                                itemAddr = offset + "($sp)";
                            }
                        } else {
                            // rank is inmemory or register
                            boolean rankInRegister = isInRegister(rank);
                            rank = operandToAddr(rank);
                            if(rankInRegister) {
                                addOneMipsCode("sll", register1, rank, "2");
                            } else {
                                addOneMipsCode("lw", register1, rank);
                                addOneMipsCode("sll",register1,register1, "2");
                            }
                            if(isGlobal) {
                                itemAddr = "array_" + name.split("@")[0] + "_(" + register1 + ")";
                            } else {
                                addOneMipsCode("addu", register1, register1, String.valueOf((Objects.requireNonNull(findItemInFuncTable(name)).getAddr() + funcCallSpOffset)));
                                addOneMipsCode("addu", register1, register1, "$sp");
                                itemAddr = "0(" + register1 + ")";
                            }
                        }
                    }
                    if(operation.equals(MidCode.Op.ARR_LOAD)) {
                        if(aInRegister) {
                            addOneMipsCode("lw", a, itemAddr);
                        } else {
                            addOneMipsCode("lw", register2, itemAddr);
                            addOneMipsCode("sw", register2, a);
                        }
                    } else {
                        //ARR_SAVE
                        if(aInRegister) {
                            addOneMipsCode("sw", a, itemAddr);
                        } else if(isConst(operand2)) {
                            addOneMipsCode("li", register2, a);
                            addOneMipsCode("sw", register2, itemAddr);
                        } else {
                            addOneMipsCode("lw", register2, a);
                            addOneMipsCode("sw", register2, itemAddr);
                        }
                    }
                    break;
                case JUMP:
                    addOneMipsCode("j", result);
                    break;
                case JUMP_IF:
                    String left = operand1.split(" ")[0];
                    String right = operand1.split(" ")[1];
                    boolean leftInReg = isInRegister(left) || setRegister(left,true);
                    boolean rightInReg = isInRegister(right) || setRegister(right,true);
                    String leftReg = "$a1";
                    String rightReg = "$a2";
//                    String Left = operandToAddr(left);
//                    String Right = operandToAddr(right);

                    if(isConst(left)) {
                        addOneMipsCode("li", leftReg, operandToAddr(left));
                    } else if(leftInReg){
                        leftReg = operandToAddr(left);
                    } else {
                        addOneMipsCode("lw", leftReg, operandToAddr(left));
                    }

                    if(isConst(right)) {
                        addOneMipsCode("li", rightReg, operandToAddr(right));
                    } else if(rightInReg) {
                        rightReg = operandToAddr(right);
                    }else{
                        addOneMipsCode("lw", rightReg, operandToAddr(right));
                    }

                    addOneMipsCode(mipsBOp.get(operand2), leftReg, rightReg, result);
                    break;
                case LABEL:
                    addOneMipsCode(result + ":");
                    break;
                case SET:
                    String num1 = operand1.split(" ")[0];
                    String num2 = operand1.split(" ")[1];
                    boolean aInReg = isInRegister(result) || setRegister(result, false);
                    boolean bInRegOrConst = isConst(num1) || isInRegister(num1) ||setRegister(num1, true);
                    boolean cInReg = isInRegister(num2) || setRegister(num2, true);
                    String reg0 = "$a0";
                    String reg1 = "$a1";
                    String reg2 = "$a2";

                    String A = operandToAddr(result);
                    if(aInReg) reg0 = A;
                    String B = operandToAddr(num1);
                    String C = operandToAddr(num2);
                    if(bInRegOrConst) {
                        if(isConst(B))
                            addOneMipsCode("li", reg1, B);
                        else
                            reg1 = B;
                    } else {
                        addOneMipsCode("lw", reg1, B);
                    }
                    if(isConst(C)) {
                        if(operand2.equals("<")) {
                            addOneMipsCode("slti", reg0, reg1, C);
                        } else {
                            addOneMipsCode("li", reg2, C);
                            addOneMipsCode(mipsSOp.get(operand2), reg0, reg1, C);
                        }
                    } else {
                        if(cInReg)
                            reg2 = C;
                        else
                            addOneMipsCode("lw", reg2, C);
                        addOneMipsCode(mipsSOp.get(operand2), reg0, reg1, reg2);
                    }
                    if(!aInReg)
                        addOneMipsCode("sw", reg0, A);
                    break;
            }
        }
    }

    private void GenArithmetic(String operation, String num1, String num2, String num3) {
        String reg3 = "$a3";
        if (isConst(num2)) {
            if (operation.equals("addu")) {
                addOneMipsCode("addiu", num1, num3, num2);
            } else if (operation.equals("mul")) {
                addOneMipsCode(operation, num1, num3, num2);
            } else if (operation.equals("div") || operation.equals("subu") || operation.equals("mod")) {
                addOneMipsCode("li", reg3, num2);
                if (operation.equals("div") || operation.equals("mod")) {
                    addOneMipsCode("div", reg3, num3);
                    addOneMipsCode(operation.equals("mod") ? "mfhi" : "mflo", num1);
                    if (!num1.equals(num3)) {
                        release(num3);
                    }
                } else {
                    addOneMipsCode("li", reg3, num2);
                    addOneMipsCode(operation, num1, reg3, num3);
                }
            } else {
                addOneMipsCode(operation, num1, num2, num3);
            }
        } else if ((operation.equals("div") || operation.equals("mod")) && !isConst(num3)) {
            addOneMipsCode("div", num2, num3);
            addOneMipsCode(operation.equals("mod") ? "mfhi" : "mflo", num1);
            if (!num1.equals(num2)) {
                release(num2);
            }
            if (!num1.equals(num3)) {
                release(num3);
            }
        } else if (operation.equals("sra")) {
            String label = setLabel();
            String label2 = setLabel();
            addOneMipsCode(operation, num1, reg3, num3);
            addOneMipsCode("subu", num1, "$zero", num1);
            addOneMipsCode("j", label2);
            addOneMipsCode(label + ":");
            addOneMipsCode(operation, num1, num2, num3);
            addOneMipsCode(label2 + ":");
        } else if (operation.equals("addu") && isConst(num3)) {
            if (num3.equals("1073741824")) {
                addOneMipsCode("lui", reg3, "0x4000");
                addOneMipsCode("addu", num1, num2, reg3);
            } else {
                addOneMipsCode("addiu", num1, num2, num3);
            }
        } else if (operation.equals("subu") && isConst(num3)) {
            if (num3.equals("1073741824")) {
                addOneMipsCode("lui", reg3, "0xc000");
                addOneMipsCode("addu", num1, num2, reg3);
            } else {
                String neg = num3.charAt(0) == '+' ? '-' + num3.substring(1)
                        : num3.charAt(0) == '-' ? num3.substring(1) : '-' + num3;
                addOneMipsCode("addiu", num1, num2, neg);
            }
        } else {
            if (operation.equals("mod")) {
                addOneMipsCode("div", num1, num2, num3);
                addOneMipsCode("mfhi", num1);
            } else {
                addOneMipsCode(operation, num1, num2, num3);
            }
        }
    }

    private String setLabel() {
        String label = "tmp_label_" + tmpLabelIndex;
        tmpLabelIndex++;
        return label;
    }

    /*
    * 判断变量(数组名)是否为函数的参数
    * */
    private boolean isPointer(String name) {
        SymbolItem curItem = null;
        if (!currentFuncName.equals("")) {
            for (SymbolItem item : allFuncTable.get(currentFuncName)) {
                if (item.getUniqueName().equals(name)) {
                    curItem = item;
                    break;
                }
            }
        }
        if (curItem == null) {
            for (SymbolItem item : globalTable.symbolList) {
                if (item.getUniqueName().equals(name)) {
                    curItem = item;
                    break;
                }
            }
        }
        assert curItem != null;
        return curItem instanceof FuncFormVar;
    }

    /*
    * 赋值语句:把operand1 = operand2
    * */
    private void GenAssign(String operand1, String operand2) {
        boolean aInRegister = isInRegister(operand1) || setRegister(operand1, false);
        String a = operandToAddr(operand1);
        String reg = "$a1";
        if(aInRegister) {
            loadValue(operand2, a);
        } else {
            boolean bInRegister = isInRegister(operand2) || setRegister(operand2,false);
            String b = operandToAddr(operand2);
            if(bInRegister) {
                addOneMipsCode("sw", b, a);
            } else if(isConst(operand2)){
                addOneMipsCode("li", reg, b);
                addOneMipsCode("sw", reg, a);
            } else {
                addOneMipsCode("lw", reg, b);
                addOneMipsCode("sw", reg, a);
            }
        }
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

    public void addOneMipsCode(String operation, String num1, String num2, String num3, boolean isRelease) {
        if ( operation.equals("mul") && isConst(num3) && optimizeMul ) { // may change
            System.out.println("mul optimize!");
        }
        addOneMipsCode(operation + " " + num1 + ", " + num2 + ", " + num3);
        if( !isRelease ) return;
        if( operation.equals("addu") || operation.equals("subu") || operation.equals("mul") || operation.equals("div") || operation.equals("sll") || operation.equals("sra") || operation.equals("sub") || operation.equals("add")) {
            // addu, subu, mul, div, sll, sra, sub, add
            if( !num1.equals(num2) )
                release(num2);
            if( !num1.equals(num3) )
                release(num3);
        } else if(isConJump(operation)) {
            release(num1);
            release(num2);
        }
    }

    // 释放寄存器 addr
    private void release(String addr) {
        if (addr.charAt(0) == '$' && addr.charAt(1) == 't' ) {
            // $t寄存器需要释放喵
            addOneMipsCode("# RELEASE" + addr + " bind var " + registerT.get(addr.charAt(2) - '0'));
            registerT.set(addr.charAt(2) - '0', "#NULL");
        }
    }

    // operand是否为常数
    private boolean isConst(String operand) {
        return Tools.isBeginByNumber(operand) && !operand.equals("0") && !(operand).endsWith("($sp)") && !(operand).endsWith("($gp)");
    }

    // 变量(operantion)是否在mipsBOp里面
    private boolean isConJump(String operation) {
        return this.mipsBOp.containsValue(operation);
    }

    // 为变量(name)分配$s寄存器
    public String setRegisterS(String name) {
        for(int i = 0;i < registerS.size();i++){
            if( registerS.get(i).equals("#NULL")) {
                registerS.set(i,name);
                return ( i == registerS.size() - 1) ? "$fp" : ("$s" + i);
            }
        }
        AllocRegister.tryReleaseRegisterS(registerS, midCodes.get(this.index), mipsCodesList);
        for(int i = 0;i < registerS.size();i++){
            if( registerS.get(i).equals("#NULL")) {
                registerS.set(i,name);
                return ( i == registerS.size() - 1) ? "$fp" : ("$s" + i);
            }
        }
        return "#FULL";
    }

    // 为变量(name)分配$t寄存器
    public String setRegisterT(String name) {
        int min = -1;
        for(int i = 0;i < registerT.size();i++) {
            if( registerT.get(i).equals("#NULL") ) {
                registerT.set(i, name);
                return "$t" + i;
            } else {
                if( min == -1 || Integer.parseInt(registerT.get(min).substring(2)) > Integer.parseInt(registerT.get(i).substring(2)) ) {
                    min = i;
                }
            }
        }
        registerT.set(min, name);
        return "$t" + min;
//        return "#FULL";
    }
    /**
     * loadValue: 把operand的value读取到寄存器register中
     * @param operand maybe number, in Register, in memory
     * @param register the name of register
     */
    private void loadValue(String operand, String register) {
        boolean inReg = isInRegister(operand) || setRegister(operand, true);
        String addr = operandToAddr(operand);
        if( inReg )
            addOneMipsCode("move", register, addr);
        else if( isConst(operand) )
            addOneMipsCode("li", register, addr);
        else
            addOneMipsCode("lw", register, addr);
    }

    /**
     * saveValue: 把register中的值存储到operand所在的地址
     * @param operand maybe in register, in memory, not number
     * @param register have the value we need to store
     */
    private void saveValue(String register, String operand) {
        boolean inReg = isInRegister(operand) || setRegister(operand,false);
        String addr = operandToAddr(operand);
        if (inReg)
            addOneMipsCode("move", addr, register);
        else if( !isConst(operand) )
            addOneMipsCode("sw", register, addr);
        else
            assert false; // may change
    }

    /**
     * 根据变量operand的类型，返回其所对应的寄存器编号或者常数
     * @param operand 变量
     * @return 寄存器编号或者operand所在内存的地址: 4($gp) 100($sp)
     */
    private String operandToAddr(String operand) {
        if( Tools.isBeginByNumber(operand) ) {
            if( operand.equals("0") )
                return "$zero";
            return operand;
        }
        if( operand.equals("%RTX") )
            return "$v0";
        for(int i = 0;i < registerT.size();i++){
            if(registerT.get(i).equals(operand))
                return "$t" + i;
        }
        for(int i = 0;i< registerS.size(); i++){
            if(registerS.get(i).equals(operand))
                return (i == registerS.size() - 1) ? "$fp" : ("$s" + i);
        }
        // operand不在寄存器中,此时需要读取内存
        SymbolItem curItem = null;
        if( !currentFuncName.equals("") ) {
            for(SymbolItem item : allFuncTable.get(currentFuncName)) {
                if(item.getUniqueName().equals(operand)) {
                    curItem = item;
                    break;
                }
            }
        }
        if( curItem == null ) {
            for(SymbolItem item : globalTable.symbolList) {
                if(item.getUniqueName().equals(operand))
                    return (item.getAddr() - globalSize) + "($gp)";
            }
        }
        assert curItem != null;
        System.out.println((funcCallSpOffset + curItem.getAddr()) + "($sp)");
        return (funcCallSpOffset + curItem.getAddr()) + "($sp)"; // may change
    }

    /**
     * 根据变量operand,获取operand[arrayRank]对应的内存地址,并且将其保存到$a0;
     * @param operand
     * @param arrayRank
     * @return $a0
     */
    private String operandToAddrArray(String operand, String arrayRank) {
        SymbolItem curItem = null;
        boolean inGlobal = false;
        String register0 = "$a0";
        String register1 = "$a1";
        if( !currentFuncName.equals("")) {
            for(SymbolItem item : allFuncTable.get(currentFuncName)) {
                if(item.getUniqueName().equals(operand)) {
                    curItem = item;
                    break;
                }
            }
        }
        // 函数符号表中没找到，接下来在全局符号表查找
        if(curItem == null) {
            inGlobal = true;
            for(SymbolItem item : globalTable.symbolList) {
                if(item.getUniqueName().equals(operand)) {
                    curItem = item;
                    break;
                }
            }
        }
        assert curItem != null;

        if(curItem instanceof Var) {
            ArrayList<Integer> shape = ((Var) curItem).getShape();
            if(Character.isDigit(arrayRank.charAt(0))) {
                int arrayRankValue = Integer.parseInt(arrayRank);
                if(arrayRankValue != 0)
                    arrayRankValue = arrayRankValue * shape.get(1);
                int offset = curItem.getAddr();
                offset = offset + arrayRankValue * 4 + funcCallSpOffset;
                if(inGlobal) {
                    //全局变量
                    addOneMipsCode("li " + register0 + ", " + "0x" + Integer.toHexString(globalArrayAddr.get(curItem.getUniqueName()) + arrayRankValue * 4 + _dataStart));
                } else {
                    addOneMipsCode("move", register0, "$sp");
                    addOneMipsCode("addiu", register0, register0, Integer.toString(offset));
                }
            } else {
                //arrayRank为变量
                String offset = operandToAddr(arrayRank);
                if(isInRegister(arrayRank)) {
                    addOneMipsCode("sll",register1, offset, "2"); //sll rd, rt, sa; rd <- rt << sa (logic);作用是4对齐;
                } else {
                    addOneMipsCode("lw",register1, offset);
                    addOneMipsCode("sll",register1, register1, "2");
                }
                if(inGlobal) {
                    // operand为全局变量
                    if(shape.size() > 1) {
                        addOneMipsCode("mul", register1, register1, shape.get(1).toString());
                    }
                    addOneMipsCode("addiu", register0, register1, "0x" + Integer.toHexString(globalArrayAddr.get(curItem.getUniqueName()) + _dataStart));
                } else {
                    if(shape.size() > 1) {
                        addOneMipsCode("mul", register1, register1, shape.get(1).toString());
                    }
                    addOneMipsCode("addiu", register0, "$sp", Integer.toString(funcCallSpOffset + Objects.requireNonNull(findItemInFuncTable(operand)).getAddr()));
                    addOneMipsCode("add",register0, register0, register1);
                }
            }
        } else {
            // already in funcFormVar
            ArrayList<Integer> shape = ((FuncFormVar) curItem).getShape();
            if(Character.isDigit(arrayRank.charAt(0))) {
                int arrayRankValue = Integer.parseInt(arrayRank);
                if(arrayRankValue != 0)
                    arrayRankValue = arrayRankValue * shape.get(1);
                if(isInRegister(operand)) {
                    addOneMipsCode("addiu", register0, operandToAddr(operand), Integer.toString(arrayRankValue * 4));
                } else {
                    addOneMipsCode("lw",register0, operandToAddr(operand));
                    addOneMipsCode("addiu",register0, register0, Integer.toString(arrayRankValue * 4));
                }
            } else {
                String offset = operandToAddr(arrayRank);
                if(isInRegister(arrayRank)) {
                    addOneMipsCode("sll", register1, offset, "2");
                } else {
                    addOneMipsCode("lw",register1,offset);
                    addOneMipsCode("sll",register1,register1, "2");
                }
                if (shape.size() > 1)
                    addOneMipsCode("mul",register1, register1, shape.get(1).toString());
                if(isInRegister(operand)) {
                    addOneMipsCode("add", register0, operandToAddr(operand), register1);
                } else {
                  addOneMipsCode("lw", register0, operandToAddr(operand));
                  addOneMipsCode("add", register0, register0, register1);
                }
            }
        }
        return register0;
    }

    // 为变量(str)分配寄存器
    private boolean setRegister(String str, boolean onlyPara) {
        if( str.charAt(0) != '#' && !isInGlobal(str) && optimizeSetRegister && !isConst(str) ) {
            //对非临时变量、非全局变量、非常数的变量进行寄存器s的分配
            SymbolItem item = findItem(str);
            if(item instanceof Var && !onlyPara ) {
                String registerS = setRegisterS(str);
                return !registerS.equals("#FULL");
            }
        } else if( str.charAt(0) == '#' && !onlyPara && optimizeSetRegister) {
            //对临时变量且onlyPara为false的变量分配寄存器$t
            String registerT = setRegisterT(str);
            return !registerT.equals("#FULL");
        }
        return false;
    }

    // find item in funcTable, globalTable;
    private SymbolItem findItem(String operand) {
        SymbolItem curItem = null;
        if( !currentFuncName.equals("")) {
            for(SymbolItem item : allFuncTable.get(currentFuncName)) {
                if(item.getUniqueName().equals(operand)) {
                    curItem = item;
                    break;
                }
            }
        }
        if( curItem == null ) {
            for( SymbolItem item : globalTable.symbolList) {
                if(item.getUniqueName().equals(operand)) {
                    curItem = item;
                    break;
                }
            }
        }
        assert curItem != null;
        return curItem;
    }

    // find item in funcTable;
    private SymbolItem findItemInFuncTable(String name) {
        for(SymbolItem item : allFuncTable.get(currentFuncName))
            if(item.getUniqueName().equals(name)) return item;
        return null;
    }

    // 判断变量是否在全局符号表
    private boolean isInGlobal(String str) {
        for (SymbolItem item : globalTable.symbolList)
            if(item.getUniqueName().equals(str)) return true;
        return false;
    }

    // 指针$sp总的偏移量
    private int sum(ArrayList<Integer> spSize) {
        int sum = 0;
        for(Integer i : spSize)
            sum += i;
        return sum;
    }

    // operand是否在寄存器中
    private boolean isInRegister(String operand) {
        if( operand.equals("0") || operand.equals("%RTX") )
            return true;
        if( isConst(operand))
            return false;
        for(String t : registerT)
            if(t.equals(operand))
                return true;
        for(String s : registerS)
            if(s.equals(operand))
                return true;
        return false;
    }

    // 打印mips代码
    public void PrintMipsCode(String name) throws IOException {
        File file = new File(name);
        if(!file.exists()) file.createNewFile();
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for(String mipsCode: this.mipsCodesList) {
            bw.write(mipsCode + "\n");
        }
        bw.close();
        System.out.println("MipsCode Output Finish!");
    }

    /**
     * 对类Mips相关变量进行初始化
     **/
    private void Init(){
        //  $s0 ~ $s7 ,$fp(i == 8)初始化
        for(int i = 0;i < 9;i++)
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
        mipsAOp.put(MidCode.Op.BITAND, "and");
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
