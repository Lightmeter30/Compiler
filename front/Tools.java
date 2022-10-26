package front;

public class Tools {
    /*词法分析工具函数*/
    public static boolean isSpace(char word){
        return word == ' ';
    }
    public static boolean isNewline(char word){
        return word == '\r';
    }
    public static boolean isTab(char word){
        return word == '\t';
    }
    public static boolean isLetter(char word){
        return (word >= 65 && word <= 90) || (word >= 97 && word <= 122);
    }
    public static boolean isDigit(char word){
        return word >= 48 && word <= 57;
    }
    public static boolean isUline(char word){
        return word == '_';
    }
    public static boolean isNormalChar(char word){
        return word == 32 || word == 33 || (word >= 40 && word <= 126);
    }
    public static boolean isComma(char word){
        return word == ',';
    }
    public static boolean isSemi(char word){
        return word == ';';
    }
    public static boolean isEqu(char word){
        return word == '=';
    }
    public static boolean isPlus(char word){
        return word == '+';
    }
    public static boolean isMinus(char word){
        return word == '-';
    }
    public static boolean isMult(char word){
        return word == '*';
    }
    public static boolean isDivi(char word){
        return word == '/';
    }
    public static boolean isMod(char word){
        return word == '%';
    }
    public static boolean isNot(char word){
        return word == '!';
    }
    public static boolean isAnd(char word){
        return word == '&';
    }
    public static boolean isOr(char word){
        return word == '|';
    }
    public static boolean isGre(char word){
        return word == '>';
    }
    public static boolean isLss(char word){
        return word == '<';
    }
    public static boolean isDquotes(char word){
        return word == '"';
    }
    public static boolean isLpar(char word){
        return word == '(';
    }
    public static boolean isRpar(char word){
        return word == ')';
    }
    public static boolean isLbrack(char word){
        return word == '[';
    }
    public static boolean isRbrack(char word){
        return word == ']';
    }
    public static boolean isLbrace(char word){
        return word == '{';
    }
    public static boolean isRbrace(char word){
        return word == '}';
    }
    public static boolean isEnter(char word){
        return word == '\n';
    }
    /*语法分析工具函数*/
    public static boolean isConstDecl(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("CONSTTK");
    }
    public static boolean isVarDecl(int index){
        if(Lexer.Wordlist.get(index).getSymbol().equals("INTTK")){
            index++;
            if(Lexer.Wordlist.get(index).getSymbol().equals("IDENFR")){
                index++;
                return Lexer.Wordlist.get(index).getSymbol().equals("COMMA") ||
                        Lexer.Wordlist.get(index).getSymbol().equals("SEMICN") ||
                        Lexer.Wordlist.get(index).getSymbol().equals("LBRACK") ||
                        Lexer.Wordlist.get(index).getSymbol().equals("ASSIGN");
            }
        }
        return false;
    }
    public static boolean isFuncDef(int index){
        if(Lexer.Wordlist.get(index).getSymbol().equals("INTTK")){
            index++;
            if(Lexer.Wordlist.get(index).getSymbol().equals("IDENFR")){
                index++;
                return Lexer.Wordlist.get(index).getSymbol().equals("LPARENT");
            }
        }
        return Lexer.Wordlist.get(index).getSymbol().equals("VOIDTK");
    }
    public static boolean isMainFuncDef(int index){
        if(Lexer.Wordlist.get(index).getSymbol().equals("INTTK")){
            index++;
            return Lexer.Wordlist.get(index).getSymbol().equals("MAINTK");
        }
        return false;
    }
    public static boolean isCOMMA(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("COMMA");
    }
    public static boolean isSEMI(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("SEMICN");
    }
    public static boolean isLbrack(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("LBRACK");
    }
    public static boolean isLbrace(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("LBRACE");
    }
    public static boolean isRbrace(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("RBRACE");
    }
    public static boolean isEqu(int index) {
        return Lexer.Wordlist.get(index).getSymbol().equals("ASSIGN");
    }
    public static boolean isLpar(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("LPARENT");
    }
    public static boolean isRpar(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("RPARENT");
    }
    public static boolean isIf(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("IFTK");
    }
    public static boolean isElse(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("ELSETK");
    }
    public static boolean isBreak(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("BREAKTK");
    }
    public static boolean isContinue(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("CONTINUETK");
    }
    public static boolean isNot(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("NOT");
    }
    public static boolean isAnd(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("AND");
    }
    public static boolean isOr(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("OR");
    }
    public static boolean isWhile(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("WHILETK");
    }
    public static boolean isGetint(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("GETINTTK");
    }
    public static boolean isPrintf(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("PRINTFTK");
    }
    public static boolean isReturn(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("RETURNTK");
    }
    public static boolean isPlus(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("PLUS");
    }
    public static boolean isMinu(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("MINU");
    }
    public static boolean isMult(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("MULT");
    }
    public static boolean isDiv(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("DIV");
    }
    public static boolean isMod(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("MOD");
    }
    public static boolean isLss(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("LSS");
    }
    public static boolean isLeq(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("LEQ");
    }
    public static boolean isGre(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("GRE");
    }
    public static boolean isGeq(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("GEQ");
    }
    public static boolean isEql(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("EQL");
    }
    public static boolean isNeq(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("NEQ");
    }
    public static boolean isIdent(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("IDENFR");
    }
    public static boolean isConstInt(int index){
        return Lexer.Wordlist.get(index).getSymbol().equals("INTCON");
    }
    public static boolean isLVal(int index){//只要在读到';'之前发现等号,就说明是LVal,同时一定要在同一行
        while(!Tools.isSEMI(index)){
            if(Tools.isEqu(index))
                return true;
            index++;
        }
        return false;
    }
    public static boolean isUnaryOp(int index){
        return Tools.isPlus(index) || Tools.isMinu(index) || Tools.isNot(index);
    }
    public static boolean isMulOp(int index){
        return Tools.isMult(index) || Tools.isDiv(index) || Tools.isMod(index);
    }
    public static boolean isAddOp(int index){
        return Tools.isPlus(index) || Tools.isMinu(index);
    }
    public static boolean isRelOp(int index){
        return Tools.isLss(index) || Tools.isLeq(index) || Tools.isGre(index) || Tools.isGeq(index);
    }
    public static boolean isEqOp(int index){
        return Tools.isEql(index) || Tools.isNeq(index);
    }
}


