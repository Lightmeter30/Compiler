package front.SyntaxTree;

import Mid.MidCodeList;
import front.Word.ConstInfo;

import java.util.ArrayList;

public class ErrorSymbol implements TreeNode{
    private ConstInfo token;

    public ErrorSymbol(ConstInfo token){
        this.token = token;
    }

    @Override
    public ArrayList<TreeNode> getChild() {
        return new ArrayList<>();
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        return null;
    }

    public ConstInfo getToken() {
        return token;
    }
}
