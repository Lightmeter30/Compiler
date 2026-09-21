package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;

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

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        midCodeList.add(MidCode.Op.NEW_BLOCK,"#NULL","#NULL","#NULL");
        for(BlockItem blockItem: blockItems) {
            blockItem.createMidCode(midCodeList);
        }
        midCodeList.add(MidCode.Op.EXIT_BLOCK,"#NULL","#NULL","#NULL");
        return "";
    }
}
