public class WordInfo {
    private String word;
    private int num; //对于数字则存储该值,对于FormatString则是存%d的个数
    private String symbol;

    public WordInfo(String word,String symbol){
        this.setWord(word);
        this.setSymbol(symbol);
    }
    public WordInfo(String word,String symbol,int num){
        this.setWord(word);
        this.setSymbol(symbol);
        this.setNum(num);
    }
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
