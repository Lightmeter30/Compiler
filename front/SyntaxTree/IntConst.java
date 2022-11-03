package front.SyntaxTree;

import Mid.MidCodeList;
import front.Word.VarInfo;

import java.util.ArrayList;

public class IntConst implements TreeNode{
    private VarInfo token;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public IntConst(VarInfo token){
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

    public Integer getValue(){
        return this.token.getNum();
    }
}
