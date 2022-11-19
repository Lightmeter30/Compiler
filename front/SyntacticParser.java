package front;

import front.SyntaxTree.Number;
import front.Word.ConstInfo;
import front.Word.ParseInfo;
import front.Word.VarInfo;
import front.Word.WordInfo;
import front.SyntaxTree.*;

import java.util.ArrayList;

public class SyntacticParser{
    public static int WordlistNum = Lexer.Wordlist.size();
    public static int WordlistIndex = -1; //WordlistIndex表示当前读到的单词的下标
    public static int BlockType = -1; //当前Block块的属性
    public static CompUnit TreeRoot;
    public static boolean branch_opt = false;
    //不需要输出<BlockItem>, <Decl>, <BType>
    public static void SyntacticParse(){
        try{
        SyntacticParser.ReadOneWord();
        TreeRoot = SyntacticParser.CompUnit();
//        Lexer.OutputWordList();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static boolean ReadOneWord(){
        if( WordlistIndex < WordlistNum ){
            WordlistIndex++;
            return true;
        }
        System.out.println("There is no word!");
        return false;
    }
    public static void BackWord(int num){
        WordlistIndex -= num;
    }
    /*
    * 函数名: CompUnit 编译单元
    * 文法: CompUnit → {Decl} {FuncDef} MainFuncDef
    * */
    public static CompUnit CompUnit(){
        //
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        MainFuncDef mainFuncDef = null;
        while(Tools.isConstDecl(WordlistIndex) || Tools.isVarDecl(WordlistIndex)){
            decls.add(SyntacticParser.Decl()); // ';'
            SyntacticParser.ReadOneWord(); // ConstDecl  or VarDecl or FuncDef
        }
        while(Tools.isFuncDef(WordlistIndex)){
            funcDefs.add(SyntacticParser.FuncDef()); // '}'
            SyntacticParser.ReadOneWord(); // FuncDef or MainFunc
        }
        if(Tools.isMainFuncDef(WordlistIndex)){
            mainFuncDef = SyntacticParser.MainFuncDef();
        }
        else
            System.out.println("error");//没有main函数的错误
        SyntacticParser.NewParseInfo("<CompUnit>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<CompUnit>");
        return new CompUnit(decls,funcDefs,mainFuncDef);
    }
    /*
     * 函数名: Decl 声明
     * 文法: Decl → ConstDecl | VarDecl
     * */
    public static Decl Decl(){ //不需要输出
        if(Tools.isConstDecl(WordlistIndex)) {
            ConstDecl temp;
            temp =  SyntacticParser.ConstDecl();
            SyntacticParser.NewParseInfo("<Decl>","NoOutput");
            SyntacticParser.ReadOneWord();
//            System.out.println("<Decl>");
            return new Decl(temp);
        } else if(Tools.isVarDecl(WordlistIndex)) {
            VarDecl temp;
            temp = SyntacticParser.VarDecl();
            SyntacticParser.NewParseInfo("<Decl>","NoOutput");
            SyntacticParser.ReadOneWord();
//            System.out.println("<Decl>");
            return new Decl(temp);
        } else
            System.out.println("error");//不是Decl
        return null;
    }
    /*
     * 函数名: ConstDecl 常量声明
     * 文法: ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
     * */
    public static ConstDecl ConstDecl(){
        BType bType;
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        //目前是单词const,接下来继续读一个单词,应该是int
        SyntacticParser.ReadOneWord();
        bType = SyntacticParser.BType();
        SyntacticParser.ReadOneWord();
        constDefs.add(SyntacticParser.ConstDef());
        //此单词有两种合法情况:,或;
        SyntacticParser.ReadOneWord();
        while(Tools.isCOMMA(WordlistIndex)){//当其为逗号时循环
            SyntacticParser.ReadOneWord();//读入ConstDef中的第一个单词
            constDefs.add(SyntacticParser.ConstDef());
            SyntacticParser.ReadOneWord();//读入ConstDef后面的一个单词
        }
        //此时的单词应该是;
        SyntacticParser.NewParseInfo("<ConstDecl>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<ConstDecl>");
        return new ConstDecl(bType,constDefs);
    }
    /*
     * 函数名: ConstDef 常数定义
     * 文法: ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
     * */
    public static ConstDef ConstDef(){
        //此时单词为Ident
        Ident ident = new Ident((ConstInfo) Lexer.Wordlist.get(WordlistIndex));
        ArrayList<ConstExp> constExps = new ArrayList<>();
        ConstInitVal constInitVal;
        int arrayDimension = 0;
        SyntacticParser.ReadOneWord();//此时为[或=
        while(Tools.isLbrack(WordlistIndex)){
            SyntacticParser.ReadOneWord();//读入ConstExp的第一个单词
            constExps.add(SyntacticParser.ConstExp());
            SyntacticParser.ReadOneWord();//此时应该为]
            SyntacticParser.ReadOneWord();//此时为[或=
            arrayDimension++;
        }
        if(arrayDimension > 2) System.out.println("error"); //最多为二维数组
        SyntacticParser.ReadOneWord();//此时应该为ConstInitial的首个单词
        constInitVal =  SyntacticParser.ConstInitVal();
        SyntacticParser.NewParseInfo("<ConstDef>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<ConstDef>");
        return new ConstDef(ident,arrayDimension,constExps,constInitVal);
    }
    /*
     * 函数名: ConstInitial 常数初值
     * 文法: ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
     * */
    public static ConstInitVal ConstInitVal(){
        ConstInitVal.Type type = ConstInitVal.Type.mulConstInitVal;
        ArrayList<TreeNode> childNode = new ArrayList<>();
        if(Tools.isLbrace(WordlistIndex)){
            SyntacticParser.ReadOneWord();//可能是ConstInitial的首单词或者}
            if(!Tools.isRbrace(WordlistIndex)){//此时是可能ConstInitial的首单词
                childNode.add(SyntacticParser.ConstInitVal());
                SyntacticParser.ReadOneWord();//此时为,或者}
                while(Tools.isCOMMA(WordlistIndex)){//此时为,
                    SyntacticParser.ReadOneWord();
                    childNode.add(SyntacticParser.ConstInitVal());
                    SyntacticParser.ReadOneWord();
                }
            }
            //此时为}
        }else{
            type = ConstInitVal.Type.ConstExp;
            childNode.add(SyntacticParser.ConstExp());
        }
        SyntacticParser.NewParseInfo("<ConstInitVal>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<ConstInitVal>");
        return new ConstInitVal(type,childNode);
    }
    /*
     * 函数名: VarDecl 变量声明
     * 文法: VarDecl → BType VarDef { ',' VarDef } ';'
     * */
    public static VarDecl VarDecl(){
        BType bType;
        ArrayList<VarDef> varDefs = new ArrayList<>();
        //当前单词为BType
        bType =  SyntacticParser.BType();
        SyntacticParser.ReadOneWord();
        varDefs.add(SyntacticParser.VarDef());
        SyntacticParser.ReadOneWord();
        while(Tools.isCOMMA(WordlistIndex)){
            SyntacticParser.ReadOneWord();
            varDefs.add(SyntacticParser.VarDef());
            SyntacticParser.ReadOneWord();
        }
        //此时单词为;
        SyntacticParser.NewParseInfo("<VarDecl>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<VarDecl>");
        return new VarDecl(bType,varDefs);
    }
    /*
     * 函数名: VarDef 变量定义
     * 文法: VarDef → Ident { '[' ConstExp ']' } [ '=' InitVal ]
     * 解释: 包含普通变量、一维数组、二维数组定义
     * */
    public static VarDef VarDef(){
        Ident ident = new Ident((ConstInfo) Lexer.Wordlist.get(WordlistIndex));
        ArrayList<ConstExp> constExps = new ArrayList<>();
        InitVal initVal;
        int arrayDimension = 0;
        SyntacticParser.ReadOneWord();
        while(Tools.isLbrack(WordlistIndex)){//'['左括号,最多是二维数组
            SyntacticParser.ReadOneWord();
            constExps.add(SyntacticParser.ConstExp());
            SyntacticParser.ReadOneWord();
            SyntacticParser.ReadOneWord();
            arrayDimension++;
        }
        if(arrayDimension > 2) System.out.println("error");
        if(Tools.isEqu(WordlistIndex)){
            SyntacticParser.ReadOneWord();
            initVal =  SyntacticParser.InitVal();
            SyntacticParser.NewParseInfo("<VarDef>","");
            SyntacticParser.ReadOneWord();
//            System.out.println("<VarDef>");
            return new VarDef(ident, arrayDimension,constExps,initVal);
        }
        //不是'='
        SyntacticParser.BackWord(1);//多读了一个
        SyntacticParser.NewParseInfo("<VarDef>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<VarDef>");
        return new VarDef(ident, arrayDimension, constExps);
    }
    /*
     * 函数名: Initial 变量初值
     * 文法: InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
     * 解释: 表达式初值,一维数组、二维数组初值
     * */
    public static InitVal InitVal(){
        InitVal.Type type = InitVal.Type.mulInitVal;
        ArrayList<TreeNode> childNode = new ArrayList<>();
        if(Tools.isLbrace(WordlistIndex)){
            SyntacticParser.ReadOneWord();
            if(!Tools.isRbrace(WordlistIndex)){//此时可能是Initial的首单词
                childNode.add(SyntacticParser.InitVal());
                SyntacticParser.ReadOneWord();
                while(Tools.isCOMMA(WordlistIndex)){
                    SyntacticParser.ReadOneWord();
                    childNode.add(SyntacticParser.InitVal());
                    SyntacticParser.ReadOneWord();
                }
            }
            //此时为}
        }else{
            type = InitVal.Type.Exp;
            childNode.add(SyntacticParser.Exp());
        }
        SyntacticParser.NewParseInfo("<InitVal>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<InitVal>");
        return new InitVal(type,childNode);
    }
    /*
     * 函数名: FuncType 函数类型
     * 文法: FuncType → 'void' | 'int'
     * */
    public static FuncType FuncType(){
        FuncType funcType;
        if(Lexer.Wordlist.get(WordlistIndex).getSymbol().equals("VOIDTK"))
            funcType = new FuncType(FuncType.Type.Void);
        else
            funcType = new FuncType(FuncType.Type.Int);
        SyntacticParser.NewParseInfo("<FuncType>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<FuncType>");
        return funcType;
    }
    /*
     * 函数名: FuncFParams 函数形参表
     * 文法: FuncFParams → FuncFParam { ',' FuncFParam }
     * */
    public static FuncFParams FuncFParams(){
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        funcFParams.add(SyntacticParser.FuncFParam());
        SyntacticParser.ReadOneWord();
        while(Tools.isCOMMA(WordlistIndex)){
            SyntacticParser.ReadOneWord();
            funcFParams.add(SyntacticParser.FuncFParam());
            SyntacticParser.ReadOneWord();
        }
        //此时读入的单词应该是)
        SyntacticParser.BackWord(1);
        SyntacticParser.NewParseInfo("<FuncFParams>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<FuncFParams>");
        return new FuncFParams(funcFParams);
    }
    /*
     * 函数名: FuncFParam 函数形参
     * 文法: FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
     * */
    public static FuncFParam FuncFParam(){
        int dimension = 0;
        BType bType;
        ArrayList<ConstExp> constExps = new ArrayList<>();
        bType = SyntacticParser.BType();
        SyntacticParser.ReadOneWord();//Ident
        Ident ident = new Ident((ConstInfo) Lexer.Wordlist.get(WordlistIndex));
        SyntacticParser.ReadOneWord();
        if(Tools.isLbrack(WordlistIndex)){//'['
            dimension++;
            SyntacticParser.ReadOneWord();//应该是']'
            SyntacticParser.ReadOneWord();//可能是'['
            while(Tools.isLbrack(WordlistIndex)){
                dimension++;
                SyntacticParser.ReadOneWord();
                constExps.add(SyntacticParser.ConstExp());
                SyntacticParser.ReadOneWord();//应该是']'
                SyntacticParser.ReadOneWord();//可能是'['
            }
        }
        SyntacticParser.BackWord(1);
        SyntacticParser.NewParseInfo("<FuncFParam>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<FuncFParam>");
        return new FuncFParam(bType,ident,constExps,dimension);
    }
    /*
     * 函数名: Block 语句块
     * 文法: Block → '{' { BlockItem } '}'
     * */
    public static Block Block(){
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        SyntacticParser.ReadOneWord();
        if(!Tools.isRbrace(WordlistIndex)){ //不是'}'
            while(!Tools.isRbrace(WordlistIndex)){
                blockItems.add(SyntacticParser.BlockItem());
                SyntacticParser.ReadOneWord();//可能是'}'
            }
        }
        SyntacticParser.NewParseInfo("<Block>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<Block>");
        return new Block(blockItems);
    }
    /*
     * 函数名: BlockItem 语句块项
     * 文法: BlockItem → Decl | Stmt
     * */
    public static BlockItem BlockItem(){ //不需要输出
        if(Tools.isConstDecl(WordlistIndex) || Tools.isVarDecl(WordlistIndex)){
            Decl decl;
            decl = SyntacticParser.Decl();
            SyntacticParser.NewParseInfo("<BlockItem>","NoOutput");
            SyntacticParser.ReadOneWord();
            return new BlockItem(decl);
        } else{
            Stmt stmt;
            stmt = SyntacticParser.Stmt();
            SyntacticParser.NewParseInfo("<BlockItem>","NoOutput");
            SyntacticParser.ReadOneWord();
            return new BlockItem(stmt);
        }
//        System.out.println("<BlockItem>");
    }
    /*
     * 函数名: Stmt 语句
     * 文法: Stmt → LVal '=' Exp ';'                         Ident √
     *      | LVal '=' 'getint''('')'';'                    Ident √
     *      | [Exp] ';'                                     Ident| √
     *      | Block                                         {   √
     *      | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]        'if'    √
     *      | 'while' '(' Cond ')' Stmt                     'while' √
     *      | 'break' ';' | 'continue' ';'                  'break' | 'continue' √
     *      | 'return' [Exp] ';'                            'return'√
     *      | 'printf' '(' FormatString {','Exp} ')' ';'         'printf'   √
     * 解析:LVal Exp都以可能以Ident开头
     * */
    public static Stmt Stmt(){
        ArrayList<TreeNode> childNode = new ArrayList<>();
        Stmt.Type type = Stmt.Type.None;
        if(Tools.isIf(WordlistIndex)){
            type = Stmt.Type.IfBranch;
            SyntacticParser.ReadOneWord();//'('
            SyntacticParser.ReadOneWord();//Cond
            childNode.add(SyntacticParser.Cond());
            SyntacticParser.ReadOneWord();//')'
            SyntacticParser.ReadOneWord();//stmt
            childNode.add(SyntacticParser.Stmt());
            SyntacticParser.ReadOneWord();//可能是'else'
            if(Tools.isElse(WordlistIndex)){
                SyntacticParser.ReadOneWord();//stmt
                childNode.add(SyntacticParser.Stmt());
            }else
                SyntacticParser.BackWord(1);
        }else if(Tools.isWhile(WordlistIndex)){
            type = Stmt.Type.WhileBranch;
            SyntacticParser.ReadOneWord();//'('
            SyntacticParser.ReadOneWord();//Cond
            childNode.add(SyntacticParser.Cond());
            SyntacticParser.ReadOneWord();//')'
            SyntacticParser.ReadOneWord();//stmt
            childNode.add(SyntacticParser.Stmt());
        }else if(Tools.isBreak(WordlistIndex)){
            type = Stmt.Type.BreakStmt;
            childNode.add(new ErrorSymbol((ConstInfo) Lexer.Wordlist.get(WordlistIndex)));
            SyntacticParser.ReadOneWord();//';'
        }else if(Tools.isContinue(WordlistIndex)){
            type = Stmt.Type.ContinueStmt;
            childNode.add(new ErrorSymbol((ConstInfo) Lexer.Wordlist.get(WordlistIndex)));
            SyntacticParser.ReadOneWord();//';'
        }else if(Tools.isReturn(WordlistIndex)){
            type = Stmt.Type.ReturnStmt;
            childNode.add(new ErrorSymbol((ConstInfo) Lexer.Wordlist.get(WordlistIndex)));
            SyntacticParser.ReadOneWord();//';' Exp
            if(!Tools.isSEMI(WordlistIndex)){
                childNode.add(SyntacticParser.Exp());
                SyntacticParser.ReadOneWord();
            }
        }else if(Tools.isPrintf(WordlistIndex)){
            type = Stmt.Type.Output;
            childNode.add(new ErrorSymbol((ConstInfo) Lexer.Wordlist.get(WordlistIndex)));
            SyntacticParser.ReadOneWord();// '('
            SyntacticParser.ReadOneWord(); // FormatString
            childNode.add(new FormatString((VarInfo) Lexer.Wordlist.get(WordlistIndex)));
            SyntacticParser.ReadOneWord(); // ','  ')'
            while(Tools.isCOMMA(WordlistIndex)){
                SyntacticParser.ReadOneWord(); // Exp
                childNode.add(SyntacticParser.Exp());
                SyntacticParser.ReadOneWord();// ','  ')'
            }
            SyntacticParser.ReadOneWord();// ';'
        }else if(Tools.isLbrace(WordlistIndex)){
            type = Stmt.Type.Block;
            childNode.add(SyntacticParser.Block());
        }else if(Tools.isLVal(WordlistIndex)){
            childNode.add(SyntacticParser.LVal());
            SyntacticParser.ReadOneWord(); // '='
            SyntacticParser.ReadOneWord(); // 'getint' Exp
            if(Tools.isGetint(WordlistIndex)){
                type = Stmt.Type.Input;
                SyntacticParser.ReadOneWord(); // '('
                SyntacticParser.ReadOneWord(); // ')'
            }else{
                type = Stmt.Type.Assign;
                childNode.add(SyntacticParser.Exp());
            }
            SyntacticParser.ReadOneWord(); // ';'
        }else{
            if(!Tools.isSEMI(WordlistIndex)){
                type = Stmt.Type.Exp;
                childNode.add(SyntacticParser.Exp());
                SyntacticParser.ReadOneWord(); // ';'
            }
        }
        SyntacticParser.NewParseInfo("<Stmt>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<Stmt>");
        return new Stmt(type,childNode);
    }
    /*
     * 函数名: Exp 表达式
     * 文法: Exp → AddExp
     * */
    public static Exp Exp(){
        AddExp addExp = SyntacticParser.AddExp();
        SyntacticParser.NewParseInfo("<Exp>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<Exp>");
        return new Exp(addExp);
    }
    /*
     * 函数名: Cond 条件表达式
     * 文法: Cond → LOrExp
     * */
    public static Cond Cond(){
        LOrExp lOrExp = SyntacticParser.LOrExp();
        SyntacticParser.NewParseInfo("<Cond>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<Cond>");
        return new Cond(lOrExp);
    }
    /*
     * 函数名: LVal 左值表达式
     * 文法: LVal → Ident {'[' Exp ']'}
     * */
    public static LVal LVal(){
        Ident ident = new Ident((ConstInfo) Lexer.Wordlist.get(WordlistIndex));
        ArrayList<Exp> exps = new ArrayList<>();
        SyntacticParser.ReadOneWord(); // '[' other
        while(Tools.isLbrack(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // Exp
            exps.add(SyntacticParser.Exp());
            SyntacticParser.ReadOneWord(); // ']'
            SyntacticParser.ReadOneWord(); // '[' other
        }
        SyntacticParser.BackWord(1);
        SyntacticParser.NewParseInfo("<LVal>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<LVal>");
        return new LVal(ident,exps);
    }
    /*
     * 函数名: PrimaryExp 基本表达式
     * 文法: PrimaryExp → '(' Exp ')' | LVal | Number
     * */
    public static PrimaryExp PrimaryExp(){
        if(Tools.isLpar(WordlistIndex)){
            Exp exp;
            SyntacticParser.ReadOneWord(); // Exp
            exp = SyntacticParser.Exp();
            SyntacticParser.ReadOneWord(); // ')'
            SyntacticParser.NewParseInfo("<PrimaryExp>","");
            SyntacticParser.ReadOneWord();
            return new PrimaryExp(exp);
        }else if(Tools.isConstInt(WordlistIndex)){
            Number number = SyntacticParser.Number();
            SyntacticParser.NewParseInfo("<PrimaryExp>","");
            SyntacticParser.ReadOneWord();
            return new PrimaryExp(number);
        }else if(Tools.isIdent(WordlistIndex)){
            LVal lval = SyntacticParser.LVal();
            SyntacticParser.NewParseInfo("<PrimaryExp>","");
            SyntacticParser.ReadOneWord();
            return new PrimaryExp(lval);
        }else
            System.out.println("error");
//        System.out.println("<PrimaryExp>");
        return null; // may change
    }
    /*
     * 函数名: Number 数值
     * 文法: Number → IntConst
     * */
    public static Number Number(){
        //IntConst
        IntConst intConst = new IntConst((VarInfo) Lexer.Wordlist.get(WordlistIndex));
        SyntacticParser.NewParseInfo("<Number>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<Number>");
        return new Number(intConst);
    }
    /*
     * 函数名: UnaryExp 一元表达式
     * 文法: UnaryExp → PrimaryExp
     *                 | Ident '(' [FuncRParams] ')' 3种情况均需覆盖,函数调用也需要覆盖FuncRParams的不同情况
     *                 | UnaryOp UnaryExp
     * */
    public static UnaryExp UnaryExp(){
        UnaryExp.Type type = UnaryExp.Type.PrimaryExp; //may change
        ArrayList<TreeNode> childNode = new ArrayList<>();
        if(Tools.isUnaryOp(WordlistIndex)){
            type = UnaryExp.Type.UnaryExp;
            childNode.add(SyntacticParser.UnaryOp());
            SyntacticParser.ReadOneWord();// UnaryExp
            childNode.add(SyntacticParser.UnaryExp());
        }else if(Tools.isIdent(WordlistIndex)){ //函数调用或者PrimaryExp中存在LVal
            // Ident
            SyntacticParser.ReadOneWord(); // '(' or other
            if(Tools.isLpar(WordlistIndex)){ // '('
                type = UnaryExp.Type.FuncCall;
                childNode.add(new Ident((ConstInfo) Lexer.Wordlist.get(WordlistIndex - 1)));
                SyntacticParser.ReadOneWord();// ')' or FuncRParams
                if(!Tools.isRpar(WordlistIndex)){// Not ')'
                    childNode.add(SyntacticParser.FuncRParams());
                    SyntacticParser.ReadOneWord(); // ')'
                }
            }else{ // It's other,so we need to back one word
                type = UnaryExp.Type.PrimaryExp;
                SyntacticParser.BackWord(1);
                childNode.add(SyntacticParser.PrimaryExp());
            }
        }else if(Tools.isLpar(WordlistIndex) || Tools.isConstInt(WordlistIndex)){
            type = UnaryExp.Type.PrimaryExp;
            childNode.add(SyntacticParser.PrimaryExp());
        }else
            System.out.println("error");
        SyntacticParser.NewParseInfo("<UnaryExp>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<UnaryExp>");
        return new UnaryExp(type,childNode);
    }
    /*
     * 函数名: UnaryOp 单目运算符
     * 文法: UnaryOp → '+' | '−' | '!'
     * */
    public static UnaryOp UnaryOp(){
        //front.SyntacticParser.ReadOneWord(); // '+' '-' '!'
        SyntacticParser.NewParseInfo("<UnaryOp>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<UnaryOp>");
        return new UnaryOp((ConstInfo) Lexer.Wordlist.get(WordlistIndex - 1));
    }
    /*
     * 函数名: FuncRParams 函数实参表
     * 文法: FuncRParams → Exp { ',' Exp }
     * */
    public static FuncRParams FuncRParams(){
        ArrayList<Exp> exps = new ArrayList<>();
        exps.add(SyntacticParser.Exp());
        SyntacticParser.ReadOneWord();// ',' or other
        while(Tools.isCOMMA(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // Exp
            exps.add(SyntacticParser.Exp());
            SyntacticParser.ReadOneWord();// ',' or other
        }
        SyntacticParser.BackWord(1);
        SyntacticParser.NewParseInfo("<FuncRParams>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<FuncRParams>");
        return new FuncRParams(exps);
    }
    /*
     * 函数名: MulExp 乘除模表达式
     * 文法: MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
     * 改写后的文法: MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
     * */
    public static MulExp MulExp(){
        ArrayList<ConstInfo> Ops = new ArrayList<>();
        ArrayList<UnaryExp> unaryExps = new ArrayList<>();
        unaryExps.add(SyntacticParser.UnaryExp());
        SyntacticParser.NewParseInfo("<MulExp>","");
        SyntacticParser.ReadOneWord(); // <MulExp>
        SyntacticParser.ReadOneWord(); // '*' | '/' | '%' | other
        while(Tools.isMulOp(WordlistIndex)){
            Ops.add((ConstInfo) Lexer.Wordlist.get(WordlistIndex));
            SyntacticParser.ReadOneWord(); // UnaryExp
            unaryExps.add(SyntacticParser.UnaryExp());
            SyntacticParser.NewParseInfo("<MulExp>","");
            SyntacticParser.ReadOneWord(); // <MulExp>
            SyntacticParser.ReadOneWord(); // MulOp or other
        }
        SyntacticParser.BackWord(1);
//        System.out.println("<MulExp>");
        return new MulExp(Ops,unaryExps);
    }
    /*
     * 函数名: AddExp 加减表达式
     * 文法: AddExp → MulExp | AddExp ('+' | '−') MulExp
     * 改写后的文法: AddExp → MulExp { ('+' | '−') MulExp }
     * */
    public static AddExp AddExp(){
        ArrayList<ConstInfo> Ops = new ArrayList<>();
        ArrayList<MulExp> mulExps = new ArrayList<>();
        mulExps.add(SyntacticParser.MulExp());
        SyntacticParser.NewParseInfo("<AddExp>","");
        SyntacticParser.ReadOneWord(); // <AddExp>
        SyntacticParser.ReadOneWord(); // '+' | '−' or other
        while(Tools.isAddOp(WordlistIndex)){
            Ops.add((ConstInfo) Lexer.Wordlist.get(WordlistIndex));
            SyntacticParser.ReadOneWord(); // MulExp
            mulExps.add(SyntacticParser.MulExp());
            SyntacticParser.NewParseInfo("<AddExp>","");
            SyntacticParser.ReadOneWord(); // <AddExp>
            SyntacticParser.ReadOneWord(); // AddOp or other
        }
        SyntacticParser.BackWord(1);
//        System.out.println("<AddExp>");
        return new AddExp(Ops, mulExps);
    }
    /*
     * 函数名: RelExp 关系表达式
     * 文法: RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
     * 改写后的文法: RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
     * */
    public static RelExp RelExp(){
        ArrayList<ConstInfo> Ops = new ArrayList<>();
        ArrayList<AddExp> addExps = new ArrayList<>();
        addExps.add(SyntacticParser.AddExp());
        SyntacticParser.NewParseInfo("<RelExp>","");
        SyntacticParser.ReadOneWord(); // <RelExp>
        SyntacticParser.ReadOneWord(); // RelOp or other
        while(Tools.isRelOp(WordlistIndex)){
            Ops.add((ConstInfo) Lexer.Wordlist.get(WordlistIndex));
            SyntacticParser.ReadOneWord(); // AddExp
            addExps.add(SyntacticParser.AddExp());
            SyntacticParser.NewParseInfo("<RelExp>","");
            SyntacticParser.ReadOneWord(); // <RelExp>
            SyntacticParser.ReadOneWord(); // RelOp or other
        }
        SyntacticParser.BackWord(1);
//        System.out.println("<RelExp>");
        return new RelExp(Ops, addExps);
    }
    /*
     * 函数名: EqExp 相等性表达式
     * 文法: EqExp → RelExp | EqExp ('==' | '!=') RelExp
     * 改写后的文法: EqExp → RelExp { ('==' | '!=') RelExp }
     * */
    public static EqExp EqExp(){
        ArrayList<ConstInfo> Ops = new ArrayList<>();
        ArrayList<RelExp> relExps = new ArrayList<>();
        relExps.add(SyntacticParser.RelExp());
        SyntacticParser.NewParseInfo("<EqExp>","");
        SyntacticParser.ReadOneWord(); // <EqExp>
        SyntacticParser.ReadOneWord(); // EqOp or other
        while(Tools.isEqOp(WordlistIndex)){
            Ops.add((ConstInfo) Lexer.Wordlist.get(WordlistIndex));
            SyntacticParser.ReadOneWord(); // RelExp
            relExps.add(SyntacticParser.RelExp());
            SyntacticParser.NewParseInfo("<EqExp>","");
            SyntacticParser.ReadOneWord(); // <EqExp>
            SyntacticParser.ReadOneWord(); // EqOp or other
        }
        SyntacticParser.BackWord(1);
//        System.out.println("<EqExp>");
        return new EqExp(Ops, relExps);
    }
    /*
     * 函数名: LAndExp 逻辑与表达式
     * 文法: LAndExp → EqExp | LAndExp '&&' EqExp
     * 改写后的文法: LAndExp → EqExp { '&&' EqExp }
     * */
    public static LAndExp LAndExp(){
        ArrayList<EqExp> eqExps = new ArrayList<>();
        eqExps.add(SyntacticParser.EqExp());
        SyntacticParser.NewParseInfo("<LAndExp>","");
        SyntacticParser.ReadOneWord(); // <LAndExp>
        SyntacticParser.ReadOneWord(); // '&&' or other
        while(Tools.isAnd(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // EqExp
            eqExps.add(SyntacticParser.EqExp());
            SyntacticParser.NewParseInfo("<LAndExp>","");
            SyntacticParser.ReadOneWord(); // <LAndExp>
            SyntacticParser.ReadOneWord(); // '&&' or other
        }
        SyntacticParser.BackWord(1);
//        System.out.println("<LAndExp>");
        return new LAndExp(eqExps);
    }
    /*
     * 函数名: LOrExp 逻辑或表达式
     * 文法: LOrExp → LAndExp | LOrExp '||' LAndExp
     * 改写后的文法: LOrExp → LAndExp { '||' LAndExp }
     * */
    public static LOrExp LOrExp(){
        ArrayList<LAndExp> lAndExps = new ArrayList<>();
        lAndExps.add(SyntacticParser.LAndExp());
        SyntacticParser.NewParseInfo("<LOrExp>","");
        SyntacticParser.ReadOneWord(); // <LOrExp>
        SyntacticParser.ReadOneWord(); // '||' or other
        while(Tools.isOr(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // LAndExp
            lAndExps.add(SyntacticParser.LAndExp());
            SyntacticParser.NewParseInfo("<LOrExp>","");
            SyntacticParser.ReadOneWord(); // <LOrExp>
            SyntacticParser.ReadOneWord(); // '||' or other
        }
        SyntacticParser.BackWord(1);
//        System.out.println("<LOrExp>");
        return new LOrExp(lAndExps);
    }
    /*
     * 函数名: ConstExp 常量表达式
     * 文法: ConstExp → AddExp
     * */
    public static ConstExp ConstExp(){
        AddExp addExp = SyntacticParser.AddExp();
        SyntacticParser.NewParseInfo("<ConstExp>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<ConstExp>");
        return new ConstExp(addExp);
    }
    /*
     * 函数名:
     * 文法:
     * */
    public static BType BType(){ //不需要输出
        BType.Type type = BType.Type.Int;
        SyntacticParser.NewParseInfo("<BType>","NoOutput");
        SyntacticParser.ReadOneWord();
//        System.out.println("<BType>");
        return new BType(type);
    }
    /*
     * 函数名: FuncDef 函数定义
     * 文法: FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
     * */
    public static FuncDef FuncDef(){
        Block block;
        FuncFParams funcFParams = new FuncFParams(new ArrayList<>());
        FuncType funcType = SyntacticParser.FuncType();
        SyntacticParser.ReadOneWord();
        //此时单词为Ident
        Ident ident = new Ident((ConstInfo) Lexer.Wordlist.get(WordlistIndex));
        SyntacticParser.ReadOneWord(); // '('
        SyntacticParser.ReadOneWord(); // 可能')'
        if(!Tools.isRpar(WordlistIndex)){//可能为FuncParams
            funcFParams = SyntacticParser.FuncFParams();
            SyntacticParser.ReadOneWord();
        }
        // ')'
        SyntacticParser.ReadOneWord();
//        应该是'{'
        block = SyntacticParser.Block();
        ErrorSymbol blockEnd = new ErrorSymbol((ConstInfo) Lexer.Wordlist.get(WordlistIndex - 1));
        SyntacticParser.NewParseInfo("<FuncDef>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<FuncDef>");
        return new FuncDef(funcType,ident,funcFParams,block,blockEnd);
    }
    /*
     * 函数名: MainFuncDef 主函数定义
     * 文法: MainFuncDef → 'int' 'main' '(' ')' Block
     * */
    public static MainFuncDef MainFuncDef(){
        Block block;
        SyntacticParser.ReadOneWord();//'main'
        SyntacticParser.ReadOneWord();//'('
        SyntacticParser.ReadOneWord();//')'
        SyntacticParser.ReadOneWord();//应该是'{'
        block = SyntacticParser.Block();
        ErrorSymbol blockEnd = new ErrorSymbol((ConstInfo) Lexer.Wordlist.get(WordlistIndex - 1));
        SyntacticParser.NewParseInfo("<MainFuncDef>","");
        SyntacticParser.ReadOneWord();
//        System.out.println("<MainFuncDef>");
        return new MainFuncDef(block,blockEnd);
    }
    /*
     * 函数名: NewParseInfo
     * 功能: 新建一个语法分析结果类,并且将其插入到Wordlist合适的位置中
     * */
    public static void NewParseInfo(String word,String symbol){
        WordInfo temp = new ParseInfo(word,symbol);
        Lexer.Wordlist.add(WordlistIndex+1,temp);
        WordlistNum++;
    }
}
