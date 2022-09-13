import java.io.*;

public class Lexer {
    public static int lineNum = 1;
    public static int ch; //现在读取的字符
    public static boolean isRetract = false;
    public static Reader reader; //读取字符
    public static void Lexer(){
        try{
            String token; //token:存放单词的字符串
            String symbol; //symbol:存放当前所识别单词的类型
            String pathName = "114514"; //文档相对路径
            File file = new File(pathName);
            reader = new InputStreamReader(new FileInputStream(file));
            while(getChar() != -1){
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if(((char) ch) != '\r' && ((char) ch) != ' '){//对于space和\r不做处理直接跳过

                }
            }
            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void SwitchRetract(){
        isRetract = !isRetract;
    }
    public static int getChar() throws IOException {
        if(isRetract) {
            Lexer.SwitchRetract();//消除回退状态;
        }else{
            ch = reader.read();
        }
        return ch;//返回当前读到的字符
    }
}
