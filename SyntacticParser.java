public class SyntacticParser implements Error{
    public static int WordlistNum = Lexer.Wordlist.size();
    public static int WordlistIndex = -1; //WordlistIndex表示当前读到的单词的下标
    public static Error e = new SyntacticParser();
    public static int BlockType = -1; //当前Block块的属性
    //不需要输出<BlockItem>, <Decl>, <BType>
    public static void SyntacticParse(){
        try{
        SyntacticParser.ReadOneWord();
        SyntacticParser.CompUnit();
        Lexer.OutputWordList();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public int SolveError(int errorCode){
        System.out.println("the errorcode is " + errorCode );
        return errorCode;
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
    public static void CompUnit(){
        //
        while(Tools.isConstDecl(WordlistIndex) || Tools.isVarDecl(WordlistIndex)){
            SyntacticParser.Decl();// ';'
            SyntacticParser.ReadOneWord(); // ConstDecl  or VarDecl or FuncDef
        }
        while(Tools.isFuncDef(WordlistIndex)){
            SyntacticParser.FuncDef(); // '}'
            SyntacticParser.ReadOneWord(); // FuncDef or MainFunc
        }
        if(Tools.isMainFuncDef(WordlistIndex)){
            SyntacticParser.MainFuncDef();
        }
        else
            e.SolveError(1);//没有main函数的错误
        SyntacticParser.NewParseInfo("<CompUnit>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<CompUnit>");
    }
    /*
     * 函数名: Decl 声明
     * 文法: Decl → ConstDecl | VarDecl
     * */
    public static void Decl(){ //不需要输出
        if(Tools.isConstDecl(WordlistIndex))
            SyntacticParser.ConstDecl();
        else if(Tools.isVarDecl(WordlistIndex))
            SyntacticParser.VarDecl();
        else
            e.SolveError(1);//不是Decl
        SyntacticParser.NewParseInfo("<Decl>","NoOutput");
        SyntacticParser.ReadOneWord();
        System.out.println("<Decl>");
    }
    /*
     * 函数名: ConstDecl 常量声明
     * 文法: ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
     * */
    public static void ConstDecl(){
        //目前是单词const,接下来继续读一个单词,应该是int
        SyntacticParser.ReadOneWord();
        SyntacticParser.BType();
        SyntacticParser.ReadOneWord();
        SyntacticParser.ConstDef();
        //此单词有两种合法情况:,或;
        SyntacticParser.ReadOneWord();
        while(Tools.isCOMMA(WordlistIndex)){//当其为逗号时循环
            SyntacticParser.ReadOneWord();//读入ConstDef中的第一个单词
            SyntacticParser.ConstDef();
            SyntacticParser.ReadOneWord();//读入ConstDef后面的一个单词
        }
        //此时的单词应该是;
        SyntacticParser.NewParseInfo("<ConstDecl>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<ConstDecl>");
    }
    /*
     * 函数名: ConstDef 常数定义
     * 文法: ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
     * */
    public static void ConstDef(){
        //此时单词为Ident
        int arrayDimension = 0;
        SyntacticParser.ReadOneWord();//此时为[或=
        while(Tools.isLbrack(WordlistIndex)){
            SyntacticParser.ReadOneWord();//读入ConstExp的第一个单词
            SyntacticParser.ConstExp();
            SyntacticParser.ReadOneWord();//此时应该为]
            SyntacticParser.ReadOneWord();//此时为[或=
            arrayDimension++;
        }
        if(arrayDimension > 2) e.SolveError(1); //最多为二维数组
        SyntacticParser.ReadOneWord();//此时应该为ConstInitial的首个单词
        SyntacticParser.ConstInitVal();
        SyntacticParser.NewParseInfo("<ConstDef>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<ConstDef>");
    }
    /*
     * 函数名: ConstInitial 常数初值
     * 文法: ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
     * */
    public static void ConstInitVal(){
        if(Tools.isLbrace(WordlistIndex)){
            SyntacticParser.ReadOneWord();//可能是ConstInitial的首单词或者}
            if(!Tools.isRbrace(WordlistIndex)){//此时是可能ConstInitial的首单词
                SyntacticParser.ConstInitVal();
                SyntacticParser.ReadOneWord();//此时为,或者}
                while(Tools.isCOMMA(WordlistIndex)){//此时为,
                    SyntacticParser.ReadOneWord();
                    SyntacticParser.ConstInitVal();
                    SyntacticParser.ReadOneWord();
                }
            }
            //此时为}
        }else{
            SyntacticParser.ConstExp();
        }
        SyntacticParser.NewParseInfo("<ConstInitVal>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<ConstInitVal>");
    }
    /*
     * 函数名: VarDecl 变量声明
     * 文法: VarDecl → BType VarDef { ',' VarDef } ';'
     * */
    public static void VarDecl(){
        //当前单词为BType
        SyntacticParser.BType();
        SyntacticParser.ReadOneWord();
        SyntacticParser.VarDef();
        SyntacticParser.ReadOneWord();
        while(Tools.isCOMMA(WordlistIndex)){
            SyntacticParser.ReadOneWord();
            SyntacticParser.VarDef();
            SyntacticParser.ReadOneWord();
        }
        //此时单词为;
        SyntacticParser.NewParseInfo("<VarDecl>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<VarDecl>");
    }
    /*
     * 函数名: VarDef 变量定义
     * 文法: VarDef → Ident { '[' ConstExp ']' } [ '=' InitVal ]
     * 解释: 包含普通变量、一维数组、二维数组定义
     * */
    public static void VarDef(){
        int arrayDimension = 0;
        SyntacticParser.ReadOneWord();
        while(Tools.isLbrack(WordlistIndex)){//左括号,最多是二维数组
            SyntacticParser.ReadOneWord();
            SyntacticParser.ConstExp();
            SyntacticParser.ReadOneWord();
            SyntacticParser.ReadOneWord();
            arrayDimension++;
        }
        if(arrayDimension > 2) e.SolveError(1);
        if(Tools.isEqu(WordlistIndex)){
            SyntacticParser.ReadOneWord();
            SyntacticParser.InitVal();
        }else{
            SyntacticParser.BackWord(1);//多读了一个
        }
        SyntacticParser.NewParseInfo("<VarDef>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<VarDef>");
    }
    /*
     * 函数名: Initial 变量初值
     * 文法: InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
     * 解释: 表达式初值,一维数组、二维数组初值
     * */
    public static void InitVal(){
        if(Tools.isLbrace(WordlistIndex)){
            SyntacticParser.ReadOneWord();
            if(!Tools.isRbrace(WordlistIndex)){//此时可能是Initial的首单词
                SyntacticParser.InitVal();
                SyntacticParser.ReadOneWord();
                while(Tools.isCOMMA(WordlistIndex)){
                    SyntacticParser.ReadOneWord();
                    SyntacticParser.InitVal();
                    SyntacticParser.ReadOneWord();
                }
            }
            //此时为}
        }else{
            SyntacticParser.Exp();
        }
        SyntacticParser.NewParseInfo("<InitVal>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<InitVal>");
    }
    /*
     * 函数名: FuncType 函数类型
     * 文法: FuncType → 'void' | 'int'
     * */
    public static void FuncType(){
        SyntacticParser.NewParseInfo("<FuncType>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<FuncType>");
    }
    /*
     * 函数名: FuncFParams 函数形参表
     * 文法: FuncFParams → FuncFParam { ',' FuncFParam }
     * */
    public static void FuncFParams(){
        SyntacticParser.FuncFParam();
        SyntacticParser.ReadOneWord();
        while(Tools.isCOMMA(WordlistIndex)){
            SyntacticParser.ReadOneWord();
            SyntacticParser.FuncFParam();
            SyntacticParser.ReadOneWord();
        }
        //此时读入的单词应该是)
        SyntacticParser.BackWord(1);
        SyntacticParser.NewParseInfo("<FuncFParams>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<FuncFParams>");
    }
    /*
     * 函数名: FuncFParam 函数形参
     * 文法: FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
     * */
    public static void FuncFParam(){
        SyntacticParser.BType();
        SyntacticParser.ReadOneWord();//Ident
        SyntacticParser.ReadOneWord();
        if(Tools.isLbrack(WordlistIndex)){//'['
            SyntacticParser.ReadOneWord();//应该是']'
            SyntacticParser.ReadOneWord();//可能是[
            while(Tools.isLbrack(WordlistIndex)){
                SyntacticParser.ReadOneWord();
                SyntacticParser.ConstExp();
                SyntacticParser.ReadOneWord();//应该是']'
                SyntacticParser.ReadOneWord();//可能是'['
            }
        }
        SyntacticParser.BackWord(1);
        SyntacticParser.NewParseInfo("<FuncFParam>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<FuncFParam>");
    }
    /*
     * 函数名: Block 语句块
     * 文法: Block → '{' { BlockItem } '}'
     * */
    public static void Block(){
        SyntacticParser.ReadOneWord();
        if(!Tools.isRbrace(WordlistIndex)){ //不是'}'
            while(!Tools.isRbrace(WordlistIndex)){
                SyntacticParser.BlockItem();
                SyntacticParser.ReadOneWord();//可能是'}'
            }
        }
        SyntacticParser.NewParseInfo("<Block>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<Block>");
    }
    /*
     * 函数名: BlockItem 语句块项
     * 文法: BlockItem → Decl | Stmt
     * */
    public static void BlockItem(){ //不需要输出
        if(Tools.isConstDecl(WordlistIndex) || Tools.isVarDecl(WordlistIndex))
            SyntacticParser.Decl();
        else
            SyntacticParser.Stmt();
        SyntacticParser.NewParseInfo("<BlockItem>","NoOutput");
        SyntacticParser.ReadOneWord();
        System.out.println("<BlockItem>");
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
    public static void Stmt(){
        if(Tools.isIf(WordlistIndex)){
            SyntacticParser.ReadOneWord();//'('
            SyntacticParser.ReadOneWord();//Cond
            SyntacticParser.Cond();
            SyntacticParser.ReadOneWord();//')'
            SyntacticParser.ReadOneWord();//stmt
            SyntacticParser.Stmt();
            SyntacticParser.ReadOneWord();//可能是'else'
            if(Tools.isElse(WordlistIndex)){
                SyntacticParser.ReadOneWord();//stmt
                SyntacticParser.Stmt();
            }else
                SyntacticParser.BackWord(1);
        }else if(Tools.isWhile(WordlistIndex)){
            SyntacticParser.ReadOneWord();//'('
            SyntacticParser.ReadOneWord();//Cond
            SyntacticParser.Cond();
            SyntacticParser.ReadOneWord();//')'
            SyntacticParser.ReadOneWord();//stmt
            SyntacticParser.Stmt();
        }else if(Tools.isBreak(WordlistIndex) || Tools.isContinue(WordlistIndex)){
            SyntacticParser.ReadOneWord();//';'
        }else if(Tools.isReturn(WordlistIndex)){
            SyntacticParser.ReadOneWord();//';' Exp
            if(!Tools.isSEMI(WordlistIndex)){
                SyntacticParser.Exp();
                SyntacticParser.ReadOneWord();
            }
        }else if(Tools.isPrintf(WordlistIndex)){
            SyntacticParser.ReadOneWord();// '('
            SyntacticParser.ReadOneWord(); // FormatString
            SyntacticParser.ReadOneWord(); // ','  ')'
            while(Tools.isCOMMA(WordlistIndex)){
                SyntacticParser.ReadOneWord(); // Exp
                SyntacticParser.Exp();
                SyntacticParser.ReadOneWord();// ','  ')'
            }
            SyntacticParser.ReadOneWord();// ';'
        }else if(Tools.isLbrace(WordlistIndex)){
            SyntacticParser.Block();
        }else if(Tools.isLVal(WordlistIndex)){
            SyntacticParser.LVal();
            SyntacticParser.ReadOneWord(); // '='
            SyntacticParser.ReadOneWord(); // 'getint' Exp
            if(Tools.isGetint(WordlistIndex)){
                SyntacticParser.ReadOneWord(); // '('
                SyntacticParser.ReadOneWord(); // ')'
            }else{
                SyntacticParser.Exp();
            }
            SyntacticParser.ReadOneWord(); // ';'
        }else{
            if(!Tools.isSEMI(WordlistIndex)){
                SyntacticParser.Exp();
                SyntacticParser.ReadOneWord(); // ';'
            }
        }
        SyntacticParser.NewParseInfo("<Stmt>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<Stmt>");
    }
    /*
     * 函数名: Exp 表达式
     * 文法: Exp → AddExp
     * */
    public static void Exp(){
        SyntacticParser.AddExp();
        SyntacticParser.NewParseInfo("<Exp>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<Exp>");
    }
    /*
     * 函数名: Cond 条件表达式
     * 文法: Cond → LOrExp
     * */
    public static void Cond(){
        SyntacticParser.LOrExp();
        SyntacticParser.NewParseInfo("<Cond>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<Cond>");
    }
    /*
     * 函数名: LVal 左值表达式
     * 文法: LVal → Ident {'[' Exp ']'}
     * */
    public static void LVal(){
        SyntacticParser.ReadOneWord(); // '[' other
        while(Tools.isLbrack(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // Exp
            SyntacticParser.Exp();
            SyntacticParser.ReadOneWord(); // ']'
            SyntacticParser.ReadOneWord(); // '[' other
        }
        SyntacticParser.BackWord(1);
        SyntacticParser.NewParseInfo("<LVal>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<LVal>");
    }
    /*
     * 函数名: PrimaryExp 基本表达式
     * 文法: PrimaryExp → '(' Exp ')' | LVal | Number
     * */
    public static void PrimaryExp(){
        if(Tools.isLpar(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // Exp
            SyntacticParser.Exp();
            SyntacticParser.ReadOneWord(); // ')'
        }else if(Tools.isConstInt(WordlistIndex)){
            SyntacticParser.Number();
        }else if(Tools.isIdent(WordlistIndex)){
            SyntacticParser.LVal();
        }else
            e.SolveError(1);
        SyntacticParser.NewParseInfo("<PrimaryExp>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<PrimaryExp>");
    }
    /*
     * 函数名: Number 数值
     * 文法: Number → IntConst
     * */
    public static void Number(){
        //IntConst
        SyntacticParser.NewParseInfo("<Number>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<Number>");
    }
    /*
     * 函数名: UnaryExp 一元表达式
     * 文法: UnaryExp → PrimaryExp
     *                 | Ident '(' [FuncRParams] ')' 3种情况均需覆盖,函数调用也需要覆盖FuncRParams的不同情况
     *                 | UnaryOp UnaryExp
     * */
    public static void UnaryExp(){
        if(Tools.isUnaryOp(WordlistIndex)){
            SyntacticParser.UnaryOp();
            SyntacticParser.ReadOneWord();// UnaryExp
            SyntacticParser.UnaryExp();
        }else if(Tools.isIdent(WordlistIndex)){ //函数调用或者PrimaryExp中存在LVal
            SyntacticParser.ReadOneWord(); // '(' or other
            if(Tools.isLpar(WordlistIndex)){ // '('
                SyntacticParser.ReadOneWord();// ')' or FuncRParams
                if(!Tools.isRpar(WordlistIndex)){// Not ')'
                    SyntacticParser.FuncRParams();
                    SyntacticParser.ReadOneWord(); // ')'
                }
            }else{ // It's other,so we need to back one word
                SyntacticParser.BackWord(1);
                SyntacticParser.PrimaryExp();
            }
        }else if(Tools.isLpar(WordlistIndex) || Tools.isConstInt(WordlistIndex)){
            SyntacticParser.PrimaryExp();
        }else
            e.SolveError(1);
        SyntacticParser.NewParseInfo("<UnaryExp>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<UnaryExp>");
    }
    /*
     * 函数名: UnaryOp 单目运算符
     * 文法: UnaryOp → '+' | '−' | '!'
     * */
    public static void UnaryOp(){
        //SyntacticParser.ReadOneWord(); // '+' '-' '!'
        SyntacticParser.NewParseInfo("<UnaryOp>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<UnaryOp>");
    }
    /*
     * 函数名: FuncRParams 函数实参表
     * 文法: FuncRParams → Exp { ',' Exp }
     * */
    public static void FuncRParams(){
        SyntacticParser.Exp();
        SyntacticParser.ReadOneWord();// ',' or other
        while(Tools.isCOMMA(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // Exp
            SyntacticParser.Exp();
            SyntacticParser.ReadOneWord();// ',' or other
        }
        SyntacticParser.BackWord(1);
        SyntacticParser.NewParseInfo("<FuncRParams>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<FuncRParams>");
    }
    /*
     * 函数名: MulExp 乘除模表达式
     * 文法: MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
     * 改写后的文法: MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
     * */
    public static void MulExp(){
        SyntacticParser.UnaryExp();
        SyntacticParser.NewParseInfo("<MulExp>","");
        SyntacticParser.ReadOneWord(); // <MulExp>
        SyntacticParser.ReadOneWord(); // '*' | '/' | '%' | other
        while(Tools.isMulOp(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // UnaryExp
            SyntacticParser.UnaryExp();
            SyntacticParser.NewParseInfo("<MulExp>","");
            SyntacticParser.ReadOneWord(); // <MulExp>
            SyntacticParser.ReadOneWord(); // MulOp or other
        }
        SyntacticParser.BackWord(1);
        System.out.println("<MulExp>");
    }
    /*
     * 函数名: AddExp 加减表达式
     * 文法: AddExp → MulExp | AddExp ('+' | '−') MulExp
     * 改写后的文法: AddExp → MulExp { ('+' | '−') MulExp }
     * */
    public static void AddExp(){
        SyntacticParser.MulExp();
        SyntacticParser.NewParseInfo("<AddExp>","");
        SyntacticParser.ReadOneWord(); // <AddExp>
        SyntacticParser.ReadOneWord(); // '+' | '−' or other
        while(Tools.isAddOp(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // MulExp
            SyntacticParser.MulExp();
            SyntacticParser.NewParseInfo("<AddExp>","");
            SyntacticParser.ReadOneWord(); // <AddExp>
            SyntacticParser.ReadOneWord(); // AddOp or other
        }
        SyntacticParser.BackWord(1);
        System.out.println("<AddExp>");

    }
    /*
     * 函数名: RelExp 关系表达式
     * 文法: RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
     * 改写后的文法: RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
     * */
    public static void RelExp(){
        SyntacticParser.AddExp();
        SyntacticParser.NewParseInfo("<RelExp>","");
        SyntacticParser.ReadOneWord(); // <RelExp>
        SyntacticParser.ReadOneWord(); // RelOp or other
        while(Tools.isRelOp(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // AddExp
            SyntacticParser.AddExp();
            SyntacticParser.NewParseInfo("<RelExp>","");
            SyntacticParser.ReadOneWord(); // <RelExp>
            SyntacticParser.ReadOneWord(); // RelOp or other
        }
        SyntacticParser.BackWord(1);
        System.out.println("<RelExp>");
    }
    /*
     * 函数名: EqExp 相等性表达式
     * 文法: EqExp → RelExp | EqExp ('==' | '!=') RelExp
     * 改写后的文法: EqExp → RelExp { ('==' | '!=') RelExp }
     * */
    public static void EqExp(){
        SyntacticParser.RelExp();
        SyntacticParser.NewParseInfo("<EqExp>","");
        SyntacticParser.ReadOneWord(); // <EqExp>
        SyntacticParser.ReadOneWord(); // EqOp or other
        while(Tools.isEqOp(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // RelExp
            SyntacticParser.RelExp();
            SyntacticParser.NewParseInfo("<EqExp>","");
            SyntacticParser.ReadOneWord(); // <EqExp>
            SyntacticParser.ReadOneWord(); // EqOp or other
        }
        SyntacticParser.BackWord(1);
        System.out.println("<EqExp>");
    }
    /*
     * 函数名: LAndExp 逻辑与表达式
     * 文法: LAndExp → EqExp | LAndExp '&&' EqExp
     * 改写后的文法: LAndExp → EqExp { '&&' EqExp }
     * */
    public static void LAndExp(){
        SyntacticParser.EqExp();
        SyntacticParser.NewParseInfo("<LAndExp>","");
        SyntacticParser.ReadOneWord(); // <LAndExp>
        SyntacticParser.ReadOneWord(); // '&&' or other
        while(Tools.isAnd(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // EqExp
            SyntacticParser.EqExp();
            SyntacticParser.NewParseInfo("<LAndExp>","");
            SyntacticParser.ReadOneWord(); // <LAndExp>
            SyntacticParser.ReadOneWord(); // '&&' or other
        }
        SyntacticParser.BackWord(1);
        System.out.println("<LAndExp>");
    }
    /*
     * 函数名: LOrExp 逻辑或表达式
     * 文法: LOrExp → LAndExp | LOrExp '||' LAndExp
     * 改写后的文法: LOrExp → LAndExp { '||' LAndExp }
     * */
    public static void LOrExp(){
        SyntacticParser.LAndExp();
        SyntacticParser.NewParseInfo("<LOrExp>","");
        SyntacticParser.ReadOneWord(); // <LOrExp>
        SyntacticParser.ReadOneWord(); // '||' or other
        while(Tools.isOr(WordlistIndex)){
            SyntacticParser.ReadOneWord(); // LAndExp
            SyntacticParser.LAndExp();
            SyntacticParser.NewParseInfo("<LOrExp>","");
            SyntacticParser.ReadOneWord(); // <LOrExp>
            SyntacticParser.ReadOneWord(); // '||' or other
        }
        SyntacticParser.BackWord(1);
        System.out.println("<LOrExp>");
    }
    /*
     * 函数名: ConstExp 常量表达式
     * 文法: ConstExp → AddExp
     * */
    public static void ConstExp(){
        SyntacticParser.AddExp();
        SyntacticParser.NewParseInfo("<ConstExp>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<ConstExp>");
    }
    /*
     * 函数名:
     * 文法:
     * */
    public static void BType(){ //不需要输出
        SyntacticParser.NewParseInfo("<BType>","NoOutput");
        SyntacticParser.ReadOneWord();
        System.out.println("<BType>");
    }
    /*
     * 函数名: FuncDef 函数定义
     * 文法: FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
     * */
    public static void FuncDef(){
        SyntacticParser.FuncType();
        SyntacticParser.ReadOneWord();
        //此时单词为Ident
        SyntacticParser.ReadOneWord(); // '('
        SyntacticParser.ReadOneWord(); // 可能')'
        if(!Tools.isRpar(WordlistIndex)){//可能为FuncParams
            SyntacticParser.FuncFParams();
            SyntacticParser.ReadOneWord();
        }
        // ')'
        SyntacticParser.ReadOneWord();
//        应该是'{'
        SyntacticParser.Block();
        SyntacticParser.NewParseInfo("<FuncDef>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<FuncDef>");
    }
    /*
     * 函数名: MainFuncDef 主函数定义
     * 文法: MainFuncDef → 'int' 'main' '(' ')' Block
     * */
    public static void MainFuncDef(){
        SyntacticParser.ReadOneWord();//'main'
        SyntacticParser.ReadOneWord();//'('
        SyntacticParser.ReadOneWord();//')'
        SyntacticParser.ReadOneWord();//应该是'{'
        SyntacticParser.Block();
        SyntacticParser.NewParseInfo("<MainFuncDef>","");
        SyntacticParser.ReadOneWord();
        System.out.println("<MainFuncDef>");
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
