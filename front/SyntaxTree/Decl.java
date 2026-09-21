package front.SyntaxTree;

import Mid.MidCodeList;

import java.util.ArrayList;

public class Decl implements TreeNode{
    private ConstDecl constDecl;
    private VarDecl varDecl;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public Decl(ConstDecl constDecl){
        this.constDecl = constDecl;
        this.varDecl = null;
        this.childNode.add(constDecl);
    }
    public Decl(VarDecl varDecl){
        this.varDecl = varDecl;
        this.constDecl = null;
        this.childNode.add(varDecl);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }

    @Override
    public String createMidCode(MidCodeList midCodeList) {
        if( constDecl != null )
            this.constDecl.createMidCode(midCodeList);
        else
            this.varDecl.createMidCode(midCodeList);
        return "";
    }
}
