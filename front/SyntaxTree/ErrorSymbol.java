package front.SyntaxTree;

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

    public ConstInfo getToken() {
        return token;
    }
}
