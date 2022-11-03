package front.SyntaxTree;

import Mid.MidCodeList;

import java.util.ArrayList;

public class FuncRParams implements TreeNode{
    public ArrayList<Exp> exps;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public FuncRParams(ArrayList<Exp> exps){
        this.exps = exps;
        this.childNode.addAll(exps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        return "";
    }
}
