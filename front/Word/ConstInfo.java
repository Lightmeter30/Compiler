package front.Word;

public class ConstInfo extends WordInfo{
    private int lineCounter;
    public ConstInfo(String word,String symbol,int lineCounter){
        super(word,symbol);
        this.setLineCounter(lineCounter);
    }

    public int getLineCounter() {
        return lineCounter;
    }

    public void setLineCounter(int lineCounter) {
        this.lineCounter = lineCounter;
    }
}
