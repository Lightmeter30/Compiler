package front;

public class Error extends Exception{
    /*************************************************************************************************
     * @param errorCode
     * @return
     *[错误类别码]   [对应的ascii码(errorCode)]          [解释]
     *      a               97                  <FormatString>非法符号(报错行号在<FormatString>所在行) √
     *      b               98                  同一作用域下名字重定义(<Ident>所在行) √
     *      c               99                  未定义的名字(<Ident>所在行) √
     *      d               100                 函数参数个数不匹配(函数名调用语句的函数名所在的行) √
     *      e               101                 函数参数类型不匹配(函数名调用语句的函数名所在的行) √
     *      f               102                 无返回值的函数存在不匹配的return语句(return所在行) √
     *      g               103                 有返回值的函数缺少return语句(函数结尾'}'所在行) √
     *      h               104                 不能改变常量的值(<LVal>所在行)
     *      i               105                 缺少分号(缺少分号的前一个非终结符所在行)
     *      j               106                 缺少右小括号')'(缺少右小括号前一个非终结符所在行)
     *      k               107                 缺少右中括号’]’(缺少右中括号前一个非终结符所在行)
     *      l               108                 printf中'%d'数量宇表达式个数不匹配(printf所在行)
     *      m               109                 在非循环模块使用break | continue(break | continue所在行) √
     *      n               110                 其他错误
     ****************************************************************************************************/
    public char errorType;
    public int lineCount;

    public Error(char errorType, int lineCount) {
        this.errorType = errorType;
        this.lineCount = lineCount;
    }

    @Override
    public String toString(){
        return lineCount + " " + errorType;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Error)) return false;
        Error error = (Error) obj;
        return error == this || error.toString().equals(this.toString());
    }
}
