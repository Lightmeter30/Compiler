public class Tools {
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
}
