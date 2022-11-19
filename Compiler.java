import Mid.MidCode;
import Mid.MidCodeList;
import SymbolTable.SymLink;
import SymbolTable.SymbolTable;
import SymbolTable.SymbolItem;
import back.Mips;
import front.ErrorList;
import front.Lexer;
import front.SyntacticParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Compiler {
    public static void main(String[] args) throws IOException {

        Lexer.LexerIt(); //词法解析器
        SyntacticParser.SyntacticParse(); //语法分析器
        SymLink symLink = new SymLink(SyntacticParser.TreeRoot);

        //link Tree and SymbolTable
        symLink.buildSymbolTable();
        System.out.println("link Tree and SymbolTable may OK");

        if(!ErrorList.isNoError()) {
            ErrorList.outPutError("error.txt");
            System.out.println("the source codes have error!");
            return;
        }

        HashMap<String, SymbolTable> funcTables = symLink.getFuncTables();
        MidCodeList midCodeList = new MidCodeList(symLink.nodeTableItem, funcTables);
        SyntacticParser.TreeRoot.createMidCode(midCodeList);
        System.out.println("中间代码完成!");
        outPutMidCode(midCodeList, "PreMidCode.txt");

        HashMap<String, ArrayList<SymbolItem>> funcTable = symLink.getFuncTable();
        Mips mips = new Mips(midCodeList.midCodes, midCodeList.formatString, funcTable,symLink.rootTable);
        mips.createMipsCode();
        mips.PrintMipsCode("mips.txt");
        System.out.println("Mips Ok!");
    }

    private static void outPutMidCode(MidCodeList midCodeList, String name) throws IOException {
        String pathName = name;
        File file = new File(pathName);
        if(!file.exists()) file.createNewFile();
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for(MidCode midCode: midCodeList.midCodes){
            bw.write(midCode.toString() + "\n");
        }
        bw.close();
        System.out.println("outPut MidCode finish!");
    }
}
