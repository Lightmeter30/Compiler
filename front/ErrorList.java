package front;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ErrorList {
    private static final ArrayList<Error> errorList = new ArrayList<>();

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
        List<Error> result = errorList;
        result.sort(Comparator.comparingInt(o -> o.lineCount));
        for(Error error : result){
            bw.write(error.toString() + "\n");
        }
        bw.close();
        System.out.println("output Error finish!");
    }
    public static boolean isNoError(){
        return errorList.isEmpty();
    }
}
