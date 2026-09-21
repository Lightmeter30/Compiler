package front.Word;

public abstract class WordInfo {
    private String word;
    private String symbol;
    private int lineCounter;

    public WordInfo(String word,String symbol,int lineCounter){
        this.setWord(word);
        this.setSymbol(symbol);
        this.setLineCounter(lineCounter);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getLineCounter() {
        return lineCounter;
    }

    public void setLineCounter(int lineCounter) {
        this.lineCounter = lineCounter;
    }
}
