package front.SyntaxTree;

import Mid.MidCodeList;

import java.util.ArrayList;

public interface TreeNode {
    ArrayList<TreeNode> getChild();

    String createMidCode(MidCodeList midCodeList);
}
