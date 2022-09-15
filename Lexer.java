import java.io.*;
import java.util.ArrayList;

public class Lexer implements Error{
    public static int lineNum = 1;
    public static int ch; //现在读取的字符
    public static boolean isRetract = false;
    public static Reader reader; //读取字符
    public static StringBuffer token = new StringBuffer(255); //token:存放单词的字符串
    public static ArrayList<WordInfo> Wordlist = new ArrayList<WordInfo>();
    public static void LexerIt(){
        try{
            String pathName = "testfile.txt"; //文档相对路径
            File file = new File(pathName);
            reader = new InputStreamReader(new FileInputStream(file));
            Error error = new Lexer();//错误
            while(getChar() != -1){
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if( !Tools.isNewline((char) ch) && !Tools.isSpace((char) ch) && !Tools.isTab((char) ch) ){//对于space和\r以及\t不做处理直接跳过

                    if(Tools.isLetter((char) ch) || Tools.isUline((char) ch)) { //各种除注释、printf中的单词，包括变量名，函数名，int，getint，printf等。
                        token.append((char) ch);
                        getChar();
                        while( Tools.isLetter((char) ch) || Tools.isUline((char) ch) || Tools.isDigit((char) ch)){
                            token.append((char) ch);
                            getChar();
                        }
                        String word = token.toString();
                        token.delete(0, token.length());
                        SwitchRetract();
                        NewIdentInfo(word);
                    }else if(Tools.isDigit((char) ch)){ //24岁，是数字
                        while(Tools.isDigit((char) ch)){
                            token.append((char) ch);
                            getChar();
                        }
                        NewNumberInfo(token.toString(), "INTCON",Integer.parseInt(token.toString()));
                        token.delete(0, token.length());
                        SwitchRetract();
                    }else if(Tools.isDquotes((char) ch)){ //printf里面的东西捏
                        token.append((char) ch);
                        getChar();
                        boolean isMod = false,is_n = false; //判断前一个符号是不是 %  \
                        int formatCharNum = 0;    //记录FormatChar的个数，即%d的个数。
                        while(!Tools.isDquotes((char) ch)){ // 没有读到"前不会结束,假如中间出现换行、非NormalChar的字符、%后面不是d、\后面不是n结束则报错
                            if(Tools.isNormalChar((char) ch) && !isMod ){
                                token.append((char) ch);
                                if( is_n && ch != 'n')
                                    error.SolveError(0);// \后面不是n的错误
                                else if(is_n)
                                    is_n = false;
                                if( ch == '\\' )
                                    is_n = true;
                            }else if( Tools.isMod((char) ch) && !isMod ){
                                token.append((char) ch);
                                isMod = true;
                            }else if( isMod && ch == 'd' ){
                                token.append((char) ch);
                                formatCharNum++;
                                isMod = false;
                            }else if(isMod){
                                error.SolveError(0);//不是%d的错误，暂定与出现非NormalChar字符错误一致
                            }else if( Tools.isEnter((char) ch)){
                                lineNum++;
                                error.SolveError(0);//出现换行
                            }else if( ch == -1 ){
                                error.SolveError(0); //已经结束力！
                            }
                            if( ch == -1 ) break;
                            getChar();
                        }
                        //此时 ch = “ 或 -1
                        if( ch == -1 ) break;
                        token.append((char) ch);
                        NewNumberInfo(token.toString(), "STRCON",formatCharNum);
                        token.delete(0, token.length());
                    }else if(Tools.isNot((char) ch)){//!(NOT)
                        getChar();
                        if(Tools.isEqu((char) ch)) {// !=
                            NewWordInfo("!=","NEQ");
                        }else{
                            NewWordInfo("!","NOT");
                            SwitchRetract();
                        }
                    }else if(Tools.isAnd((char) ch)){//&(AND)
                        getChar();
                        if( Tools.isAnd((char) ch)){
                            NewWordInfo("&&","AND");
                        }else{
                            error.SolveError(0);// &不能单独出现，错误码为;
                        }
                    }else if(Tools.isOr((char) ch)){// |(OR)
                        getChar();
                        if( Tools.isOr((char) ch)){
                            NewWordInfo("||","OR");
                        }else{
                            error.SolveError(0);// |不能单独出现，错误码为;
                        }
                    }else if(Tools.isPlus((char) ch)){// +
                        NewWordInfo("+","PLUS");
                    }else if(Tools.isMinus((char) ch)){// -
                        NewWordInfo("-","MINU");
                    }else if(Tools.isMult((char) ch)){// *
                        NewWordInfo("*","MULT");
                    }else if(Tools.isDivi((char) ch)){// /
                        getChar();
                        if(Tools.isDivi((char) ch)){ // //注释
                            while(!Tools.isEnter((char) ch) && ch != -1 )
                                getChar();
                            lineNum++;
                        }else if(Tools.isMult((char) ch)) { // /*...*/多行注释
                            while( getChar() != -1 ){
                                if(Tools.isMult((char) ch)){ // *
                                    getChar();
                                    if(Tools.isEnter((char) ch))// \n
                                        lineNum++;
                                    else if(Tools.isDivi((char) ch) || ch == -1 )// / 或 \0
                                        break;
                                }else if(Tools.isEnter((char) ch)){ // \n
                                    lineNum++;
                                }
                            }
                        }else{ // 其它,说明只是除号
                            NewWordInfo("/","DIV");
                            SwitchRetract();
                        }
                    }else if(Tools.isMod((char) ch)){// %
                        NewWordInfo("%","MOD");
                    }else if(Tools.isLss((char) ch)){// <
                        getChar();
                        if(Tools.isEqu((char) ch)){ // <=
                            NewWordInfo("<=","LEQ");
                        }else { // <
                            NewWordInfo("<","LSS");
                            SwitchRetract();
                        }
                    }else if(Tools.isGre((char) ch)){// >
                        getChar();
                        if(Tools.isEqu((char) ch)){ // >=
                            NewWordInfo(">=","GEQ");
                        }else { // >
                            NewWordInfo(">","GRE");
                            SwitchRetract();
                        }
                    }else if(Tools.isEqu((char) ch)){// =
                        getChar();
                        if(Tools.isEqu((char) ch)){ // ==
                            NewWordInfo("==","EQL");
                        }else{  // =
                            NewWordInfo("=","ASSIGN");
                            SwitchRetract();//多读了一个字符
                        }
                    }else if(Tools.isSemi((char) ch)){// ;
                        NewWordInfo(";","SEMICN");
                    }else if(Tools.isComma((char) ch)){// ,
                        NewWordInfo(",","COMMA");
                    }else if(Tools.isLpar((char) ch)){// (
                        NewWordInfo("(","LPARENT");
                    }else if(Tools.isRpar((char) ch)){// )
                        NewWordInfo(")","RPARENT");
                    }else if(Tools.isLbrack((char) ch)){// [
                        NewWordInfo("[","LBRACK");
                    }else if(Tools.isRbrack((char) ch)){// ]
                        NewWordInfo("]","RBRACK");
                    }else if(Tools.isLbrace((char) ch)){// {
                        NewWordInfo("{","LBRACE");
                    }else if(Tools.isRbrace((char) ch)){// }
                        NewWordInfo("}","RBRACE");
                    }else if(Tools.isEnter((char) ch)){
                        lineNum++;  //每读到一次\n就行数加1
                    }else{
                        error.SolveError(0);//出现无法识别的字符
                        reader.close();
                        return;
                    }
                }
                if( ch == -1 ) break;
            }
            reader.close();
            System.out.println("analyze finish!");
            OutputWordList();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /*
    * @author: takune
    * 切换回退状态；
    * */
    public static void SwitchRetract(){
        isRetract = !isRetract;
    }
    /*
    * @author: takune
    * 新建一个词法信息类并将其添加到Wordlist(非数字)
    * */
    public static void NewWordInfo(String word,String symbol){
        WordInfo tempInfo = new WordInfo(word,symbol);
        Wordlist.add(tempInfo);
    }
    /*
     * @author: takune
     * 新建一个词法信息类并将其添加到Wordlist(数字)
     * */
    public static void NewNumberInfo(String word,String symbol,int num){
        WordInfo tempInfo = new WordInfo(word,symbol,num);
        Wordlist.add(tempInfo);
    }
    public static int getChar() throws IOException {
        if(isRetract) {
            Lexer.SwitchRetract();//消除回退状态;
        }else{
            ch = reader.read();
        }
        return ch;//返回当前读到的字符
    }
    public static void OutputWordList() throws IOException {
        String pathName = "output.txt";
        File file = new File(pathName);
        if(!file.exists()) file.createNewFile();
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for(int i = 0; i < Wordlist.size(); i++){
            if( i == Wordlist.size() - 1 )
                bw.write(Wordlist.get(i).getSymbol() + " " + Wordlist.get(i).getWord() );
            else
                bw.write(Wordlist.get(i).getSymbol() + " " + Wordlist.get(i).getWord() + "\n");
        }
        bw.close();
        System.out.println("output finish!");
    }
    public static void NewIdentInfo(String word){
        switch (word) {
            case "main":
                NewWordInfo("main", "MAINTK");
                break;
            case "const":
                NewWordInfo("const", "CONSTTK");
                break;
            case "int":
                NewWordInfo("int", "INTTK");
                break;
            case "break":
                NewWordInfo("break", "BREAKTK");
                break;
            case "continue":
                NewWordInfo("continue", "CONTINUETK");
                break;
            case "if":
                NewWordInfo("if", "IFTK");
                break;
            case "else":
                NewWordInfo("else", "ELSETK");
                break;
            case "while":
                NewWordInfo("while", "WHILETK");
                break;
            case "getint":
                NewWordInfo("getint", "GETINTTK");
                break;
            case "printf":
                NewWordInfo("printf", "PRINTFTK");
                break;
            case "return":
                NewWordInfo("return", "RETURNTK");
                break;
            case "void":
                NewWordInfo("void", "VOIDTK");
                break;
            default:
                NewWordInfo(word, "IDENFR");
                break;
        }
    }
    /*
    * @author: takune
    * 处理词法错误，可以之后再完善
    * */
    public int SolveError(int errorCode){
        System.out.println("the errorcode is " + errorCode );
        return errorCode;
    }
}
