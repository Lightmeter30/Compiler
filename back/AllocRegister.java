package back;

import Mid.MidCode;

import java.util.ArrayList;

public class AllocRegister {
    public static void tryReleaseRegisterS(ArrayList<String> registerS, MidCode midCode, ArrayList<String> mipsCodesList) {
        // 寄存器优化
        for (String regS : registerS)
            if(regS.equals("#NULL")) return;
    }
}
