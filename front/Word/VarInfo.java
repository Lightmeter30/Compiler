package front.Word;

public class VarInfo extends WordInfo {
    private int num; //对于数字则存储该值,对于FormatString则是存%d的个数
    private int lineCounter;

    public VarInfo(String word, String symbol, int num,int lineCounter){
        super(word,symbol);
        this.setNum(num);
        this.setLineCounter(lineCounter);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getLineCounter() {
        return lineCounter;
    }

    public void setLineCounter(int lineCounter) {
        this.lineCounter = lineCounter;
    }

    public String getString(){
        return super.getWord().replaceAll("\"","");
    }
}
