package front.SyntaxTree;

import Mid.MidCodeList;

import java.util.ArrayList;

public class FuncFParams implements TreeNode{
    private ArrayList<FuncFParam> funcFParams;
    private ArrayList<TreeNode> childNode = new ArrayList<>();

    public FuncFParams(ArrayList<FuncFParam> funcFParams){
        this.funcFParams = funcFParams;
        this.childNode.addAll(funcFParams);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        return null;
    }

    public ArrayList<FuncFParam> getFuncFParams(){
        return funcFParams;
    }
    public Integer getArgc(){
        return funcFParams.size();
    }
}
