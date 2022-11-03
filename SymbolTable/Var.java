package SymbolTable;

import front.SyntaxTree.ConstInitVal;
import front.SyntaxTree.InitVal;

import java.util.ArrayList;


public class Var implements SymbolItem{
    private String name;
    private boolean isConst;
    private InitVal initVal;
    public ConstInitVal constInitVal;
    public int addr;
    private int dimension;
    private ArrayList<Integer> shape;
    public String loc;
    public Var(String name, boolean isConst, int dimension, InitVal initVal, ArrayList<Integer> shape, String loc){
        this.name = name;
        this.isConst = isConst;
        this.initVal = initVal;
        this.dimension = dimension;
        this.shape = shape;
        this.loc = loc;
    }
    public Var(String name, boolean isConst, int dimension, ConstInitVal constInitVal, ArrayList<Integer> shape, String loc){
        this.name = name;
        this.isConst = isConst;
        this.constInitVal = constInitVal;
        this.dimension = dimension;
        this.shape = shape;
        this.loc = loc;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUniqueName(){
        return this.name + "@" + this.loc;
    }

    @Override
    public boolean isConst() {
        return isConst;
    }

    @Override
    public int getSize() {
        int size = 4;
        for(Integer dimensionSize: shape)
            size *= dimensionSize;
        return size;
    }

    @Override
    public int setAddr(int addr) {
        int size = 4;
        for(Integer dimensionSize: shape){
            size *= dimensionSize;
        }
        this.addr = addr;
        return this.addr + size;
    }

    @Override
    public int getAddr() {
        return addr;
    }

    public int getDimension(){
        return this.dimension;
    }

    public ArrayList<Integer> getShape(){
        return this.shape;
    }

    @Override
    public String getLoc(){
        return this.loc;
    }
}
