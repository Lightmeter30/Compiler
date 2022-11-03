package SymbolTable;

public interface SymbolItem {
    String getName();
    boolean isConst();
    int getSize();
    int setAddr(int addr);
    int getAddr();

    String getLoc();
//    private String name;    //名字
//    private short type;       //符号类型
//    private int address;    //地址
//    private int lineDeclare;    //声明行号
//    private int lineUse;        //引用行号
//    ...
//    public Symbol(String name, short type, int address, int lineDeclare,int lineUse){
//        this.name = name;
//        this.type = type;
//        this.address = address;
//        this.lineDeclare = lineDeclare;
//        this.lineUse = lineUse;
//    }
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getAddress() {
//        return address;
//    }
//
//    public void setAddress(int address) {
//        this.address = address;
//    }
//
//    public int getLineDeclare() {
//        return lineDeclare;
//    }
//
//    public void setLineDeclare(int lineDeclare) {
//        this.lineDeclare = lineDeclare;
//    }
//
//    public int getLineUse() {
//        return lineUse;
//    }
//
//    public void setLineUse(int lineUse) {
//        this.lineUse = lineUse;
//    }
//
//    public short getType() {
//        return type;
//    }
//
//    public void setType(short type) {
//        this.type = type;
//    }
}
