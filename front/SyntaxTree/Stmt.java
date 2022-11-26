package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;
import front.SyntacticParser;

import java.util.ArrayList;

public class Stmt implements TreeNode{
    public enum Type{
        Assign, Exp, IfBranch, WhileBranch, BreakStmt, ContinueStmt,
        ReturnStmt, Input, Output, None, Block
    }
    private Type type;
    private ArrayList<TreeNode> childNode;
    public Stmt(Type type, ArrayList<TreeNode> childNode){
        this.type = type;
        this.childNode = childNode;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        String result = "";
        switch (type){
            case Assign:
                String name = this.childNode.get(0).createMidCode(midCodeList); // Cond的结果:0 or 1
                if(name.contains("[")) {
                    String tmp = midCodeList.add(MidCode.Op.ASSIGN, "#TEMP",
                            this.childNode.get(1).createMidCode(midCodeList), "#NULL");
                    midCodeList.add(MidCode.Op.ARR_SAVE, name, tmp, "#NULL");
                } else {
                    result = midCodeList.add(MidCode.Op.ASSIGN,
                            this.childNode.get(0).createMidCode(midCodeList),
                            this.childNode.get(1).createMidCode(midCodeList), "#NULL");
                }
                break;
            case Exp:
            case Block:
                result = this.childNode.get(0).createMidCode(midCodeList);
                break;
            case IfBranch:
                if( !SyntacticParser.branch_opt ){
                    String boolValue = childNode.get(0).createMidCode(midCodeList); //Cond
                    if ( childNode.size() == 2 ) {
                        //no else
                        String endIf = midCodeList.add(MidCode.Op.JUMP_IF, boolValue + " " + 0, "==", "#AUTO_LABEL");
                        childNode.get(1).createMidCode(midCodeList); // stmt
                        midCodeList.add(MidCode.Op.LABEL, "#NULL", "#NULL", endIf);
                    } else {
                        String elseLabel = midCodeList.add(MidCode.Op.JUMP_IF,boolValue + " " + 0, "==", "#AUTO_LABEL");
                        childNode.get(1).createMidCode(midCodeList); // if stmt
                        String endIf = midCodeList.add(MidCode.Op.JUMP, "#NULL", "#NULL", "#AUTO_LABEL");
                        midCodeList.add(MidCode.Op.LABEL,"#NULL", "#NULL", elseLabel);
                        childNode.get(2).createMidCode(midCodeList);//else stmt
                        midCodeList.add(MidCode.Op.LABEL,"#NULL", "#NULL",endIf);
                    }
                } else {
                    //branch_opt
                    return "";
                }
                break;
            case WhileBranch:
                if( !SyntacticParser.branch_opt ) {
                    String judgeValue = midCodeList.add(MidCode.Op.LABEL, "#NULL", "#NULL","#AUTO_LABEL");
                    String condValue = childNode.get(0).createMidCode(midCodeList); //cond: 0 | 1
                    String endLoop = midCodeList.add(MidCode.Op.JUMP_IF,condValue + " " + 0, "==" ,"#AUTO_LABEL");
                    String beginLoop = midCodeList.allocLabel();
                    midCodeList.add(MidCode.Op.WHILE_BIND, judgeValue, endLoop, "#NULL");
                    midCodeList.add(MidCode.Op.LABEL, "#NULL", "#NULL", beginLoop);
                    childNode.get(1).createMidCode(midCodeList);
                    midCodeList.beginTables.pop();
                    midCodeList.endTables.pop();
                    String reCondValue = childNode.get(0).createMidCode(midCodeList);
                    midCodeList.add(MidCode.Op.JUMP_IF,reCondValue + " " + 0,"!=", beginLoop);
                    midCodeList.add(MidCode.Op.LABEL,"#NULL", "#NULL", endLoop);

                }else {
                    return "";
                }
                break;
            case BreakStmt:
                String end = midCodeList.endTables.peek();
                assert end != null;
                midCodeList.add(MidCode.Op.JUMP, "#NULL", "#NULL", end);
                break;
            case ContinueStmt:
                String start = midCodeList.beginTables.peek();
                assert start != null;
                midCodeList.add(MidCode.Op.JUMP, "#NULL", "#NULL", start);
                break;
            case ReturnStmt:
                result = midCodeList.add(MidCode.Op.RETURN,
                        (childNode.size() == 2) ? childNode.get(1).createMidCode(midCodeList) : "#NULL",
                        "#NULL", "#NULL");
                break;
            case Input:
                result = midCodeList.add(MidCode.Op.GETINT,
                        this.childNode.get(0).createMidCode(midCodeList),
                        "#NULL", "#NULL");
                break;
            case Output:
                String[] cutFormatString = ((FormatString) childNode.get(1)).getString().split("%d");
                int i = 0;
                for(TreeNode node: childNode.subList(2,childNode.size())) {
                    if ( i < cutFormatString.length && !cutFormatString[i].equals("") ) {
                        midCodeList.add(MidCode.Op.PRINT, cutFormatString[i], "#STRCONS", "#NULL");
                    }
                    // number
                    midCodeList.add(MidCode.Op.PRINT, node.createMidCode(midCodeList), "#NULL", "#NULL");
                    i++;
                }
                if ( i < cutFormatString.length && !cutFormatString[i].equals("") ){
                    midCodeList.add(MidCode.Op.PRINT, cutFormatString[i], "#STRCONS", "#NULL");
                }
                break;
            case None:
            default:
                break;
        }
        return result;
    }

    public Type getType() {
        return type;
    }
}
