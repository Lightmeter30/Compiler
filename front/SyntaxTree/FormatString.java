package front.SyntaxTree;

import Mid.MidCodeList;
import front.Word.VarInfo;

import java.util.ArrayList;

public class FormatString implements TreeNode{
    private VarInfo token;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public FormatString(VarInfo token){
        this.token = token;
    }

    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        return null;
    }

    public int getFormatStringNum(){
        return token.getNum();
    }

    /**
     * 获取带""的FormatString
     */
    public String toString(){
        return token.getWord();
    }

    /**
     * 获取不带""的FormatString
     */
    public String getString() {
        return token.getString();
    }
}
