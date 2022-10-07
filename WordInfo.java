public abstract class WordInfo {
    private String word;
    private String symbol;

    public WordInfo(String word,String symbol){
        this.setWord(word);
        this.setSymbol(symbol);
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
}
