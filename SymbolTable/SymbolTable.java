package SymbolTable;


import java.util.ArrayList;

public class SymbolTable {
    public SymbolTable fatherTable;
    public final ArrayList<SymbolItem> symbolList = new ArrayList<>();
    public final ArrayList<SymbolTable> sonTable = new ArrayList<>();
    public String name;
    public Integer[] intName;
    public SymbolTable(int[] intName,SymbolTable fatherTable){
        this.name = "<" + intName[0] + "_" + intName[1] + ">";
        this.intName = new Integer[]{intName[0],intName[1]};
        this.fatherTable = fatherTable;
    }
}
