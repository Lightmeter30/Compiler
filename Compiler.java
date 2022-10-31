import SymbolTable.SymLink;
import front.Lexer;
import front.SyntacticParser;

public class Compiler {
    public static void main(String[] args){
        Lexer.LexerIt(); //词法解析器
        SyntacticParser.SyntacticParse(); //语法分析器
        SymLink symLink = new SymLink(SyntacticParser.TreeRoot);
        symLink.buildSymbolTable();
    }
}
