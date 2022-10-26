package front.SyntaxTree;

import java.util.ArrayList;

public class ConstDecl implements TreeNode{
    private BType bType;
    private ArrayList<ConstDef> constDefs;
    private ArrayList<TreeNode> childNodes = new ArrayList<>();
    public ConstDecl(BType bType, ArrayList<ConstDef> constDefs){
        this.bType = bType;
        this.constDefs = constDefs;
        this.childNodes.add(bType);
        this.childNodes.addAll(constDefs);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNodes;
    }
}
