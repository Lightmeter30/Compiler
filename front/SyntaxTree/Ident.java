package front.SyntaxTree;

import front.Word.ConstInfo;

import java.util.ArrayList;

public class Ident implements TreeNode{
    private ConstInfo token;
    private String name;
    private int lineNum;
    private ArrayList<TreeNode> childNode = new ArrayList<>();
    public Ident(ConstInfo token){
        this.name = token.getWord();
        this.lineNum = token.getLineCounter();
    }
    @Override
    public ArrayList<TreeNode> getChild() {
        return childNode;
    }
    public int getLineNum(){
        return this.lineNum;
    }
    public String getName(){
        return this.name;
    }
    public ConstInfo getToken(){
        return this.token;
    }
}
