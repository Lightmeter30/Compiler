package front.SyntaxTree;

import java.util.ArrayList;

public class MainFuncDef implements TreeNode{
    private Block block;

    private ArrayList<TreeNode> childNode = new ArrayList<>();

    public MainFuncDef(Block block, TreeNode blockEnd){
        this.block = block;
        this.childNode.add(block);
        this.childNode.add(blockEnd);
    }
    public MainFuncDef(Block block){
        this.block = block;
        this.childNode.add(block);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
