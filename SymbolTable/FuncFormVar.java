package SymbolTable;

import java.util.ArrayList;

public class FuncFormVar implements SymbolItem{
    public String name;
    private int dimension;
    private ArrayList<Integer> shape;
    public int addr;

    public FuncFormVar(String name, int dimension, ArrayList<Integer> shape){
        this.name = name;
        this.dimension = dimension;
        this.shape = shape;
    }

    @Override
    public String getName() {
        return name;
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
        return this.addr + 4;
    }

    @Override
    public int getAddr() {
        return this.addr;
    }
    public int getDimension(){
        return this.dimension;
    }
    public ArrayList<Integer> getShape(){
        return shape;
    }
}
