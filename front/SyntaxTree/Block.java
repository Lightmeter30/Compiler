package front.SyntaxTree;

import java.util.ArrayList;

public class Block implements TreeNode{
    private ArrayList<BlockItem> blockItems;

    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public Block(ArrayList<BlockItem> blockItems){
        this.blockItems = blockItems;
        this.childNode.addAll(blockItems);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
}
