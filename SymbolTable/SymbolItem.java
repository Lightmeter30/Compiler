package SymbolTable;

public interface SymbolItem {
    String getName();
    String getUniqueName();
    boolean isConst();
    int getSize();
    int setAddr(int addr);
    Integer getAddr();

    String getLoc();

}
