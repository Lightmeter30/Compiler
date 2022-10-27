package front.SyntaxTree;

import java.util.ArrayList;

public class FuncDef implements TreeNode{
    private FuncType funcType;
    private Ident ident;
    private FuncFParams funcFParams;
    private Block block;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public FuncDef(FuncType funcType, Ident ident, FuncFParams funcFParams, Block block, TreeNode blockEnd) {
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
        childNode.add(funcType);
        childNode.add(ident);
        childNode.add(funcFParams);
        childNode.add(block);
        childNode.add(blockEnd); // ?
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
    public Ident getIdent(){
        return this.ident;
    }
    public Integer getArgc(){
        return this.funcFParams.getArgc();
    }
    public FuncType getFuncType(){
        return this.funcType;
    }
}
