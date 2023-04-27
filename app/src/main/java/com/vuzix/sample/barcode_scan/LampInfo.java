package com.vuzix.sample.barcode_scan;

public class LampInfo {
    String hueAppKey=null, bridgeAddress=null, rid=null, rtype=null;
    boolean isComplete = false; // true only when all other fields are not null

    LampInfo(String input){
        // temporary information for now. Later convert from JSON to java object
        hueAppKey = "L1JTeMlPoTLIg3VkdAL55GeqX6LeuSNansWBtWB7";
        bridgeAddress = "192.168.1.183";
        rid = "67b10258-3668-4c0e-a6dd-b5493603d0a1";
        rtype = "light";

        isComplete = checkAllInfoPresent();
    }

    private boolean checkAllInfoPresent(){
        return
            hueAppKey!=null &&
            bridgeAddress!=null &&
            rid!=null &&
            rtype!=null;
    }
}
