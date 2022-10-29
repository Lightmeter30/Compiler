package front.SyntaxTree;

import java.util.ArrayList;

public class LVal implements TreeNode{
    public Ident ident;
    public ArrayList<Exp> exps;
    private final ArrayList<TreeNode> childNode = new ArrayList<>();

    public LVal(Ident ident,ArrayList<Exp> exps){
        this.ident = ident;
        this.exps = exps;
        this.childNode.add(ident);
        this.childNode.addAll(exps);
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return this.childNode;
    }

    public String getName() {
        return this.ident.getName();
    }

    public int getDimension() {
        return this.exps.size();
    }
}
