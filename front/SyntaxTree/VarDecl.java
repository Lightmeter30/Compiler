package front.SyntaxTree;

import Mid.MidCodeList;

import java.util.ArrayList;

public class VarDecl implements TreeNode{
    private BType bType;
    private ArrayList<VarDef> varDefs;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public VarDecl(BType bType, ArrayList<VarDef> varDefs){
        this.bType = bType;
        this.varDefs = varDefs;
        this.childNode.addAll(varDefs);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        for(VarDef varDef: varDefs) {
            varDef.createMidCode(midCodeList);
        }
        return "";
    }
}
