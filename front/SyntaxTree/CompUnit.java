package front.SyntaxTree;

import java.util.ArrayList;

public class CompUnit implements TreeNode{
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncdef;
    private ArrayList<TreeNode> childNodes = new ArrayList<>(); //子节点
    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncdef) {
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncdef = mainFuncdef;
        this.childNodes.addAll(decls);
        this.childNodes.addAll(funcDefs);
        this.childNodes.add(mainFuncdef);
    }
    @Override
    public ArrayList<TreeNode> getChild(){
        return childNodes;
    }
}
