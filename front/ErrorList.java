package front;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ErrorList {
    private static ArrayList<Error> errorList = new ArrayList<>();

    public static ArrayList<Error> getErrorList() {
        return errorList;
    }
    public static void addError(Error e) {
        if(e.errorType != 'n') errorList.add(e);
    }
    public static void outPutError(String name) throws IOException {
        File file = new File(name);
        if(!file.exists()) file.createNewFile();
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for(Error error : errorList){
            bw.write(error.toString() + "\n");
        }
        bw.close();
        System.out.println("output Error finish!");
    }
    public static boolean isNoError(){
        return errorList.isEmpty();
    }
}
