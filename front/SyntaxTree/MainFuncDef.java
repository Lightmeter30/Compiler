package front.SyntaxTree;

import Mid.MidCode;
import Mid.MidCodeList;

import java.util.ArrayList;

public class MainFuncDef implements TreeNode{
    private Block block;

    private ArrayList<TreeNode> childNode = new ArrayList<>();

    public MainFuncDef(Block block, TreeNode blockEnd){
        this.block = block;
        this.childNode.add(block);
        this.childNode.add(blockEnd);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        midCodeList.add(MidCode.Op.FUNC, "int", "main", "#NULL");
        this.block.createMidCode(midCodeList);
        midCodeList.add(MidCode.Op.END_FUNC, "int", "main", "#NULL");
        return null;
    }

}
