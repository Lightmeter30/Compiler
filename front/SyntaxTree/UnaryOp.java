package front.SyntaxTree;

import Mid.MidCodeList;
import front.Word.ConstInfo;

import java.util.ArrayList;

public class UnaryOp implements TreeNode{
    private ConstInfo token;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public UnaryOp(ConstInfo token) {
        this.token = token;
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        return null;
    }

    @Override
    public String toString(){
        return token.getWord();
    }
    public String getType(){
        return token.getSymbol();
    }
}
