package SymbolTable;

public class Func implements SymbolItem{
    public enum Type{
        voidFunc, intFunc
    }
    private String name;
    private int argcNum;
    private Type type;
    private int addr;
    public String loc;
    public Func(String name, Type type, int argcNum, String loc){
        this.name = name;
        this.type = type;
        this.argcNum = argcNum;
        this.loc = loc;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUniqueName(){
        return this.name;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public int setAddr(int addr) {
        this.addr = addr;
        return this.addr;
    }

    @Override
    public int getAddr() {
        return addr;
    }

    public boolean checkArgcNum(int Number){
        return argcNum == Number;
    }

    public Type getType(){
        return type;
    }

    @Override
    public String getLoc(){
        return this.loc;
    }
}
