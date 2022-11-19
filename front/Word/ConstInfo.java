package front.Word;

public class ConstInfo extends WordInfo{
    public ConstInfo(String word,String symbol,int lineCounter){
        super(word,symbol,lineCounter);
        this.setLineCounter(lineCounter);
    }
}
