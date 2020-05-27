package com.loocc.util;

import java.util.Random;

public class StringUtil {

    public static boolean isEmpty(String str){
        if(str == null || "".equals(str.trim())){
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str){
        if(str != null &&! "".equals(str.trim())){
            return true;
        }
        return false;
    }
    /**
     * 生成六位随机数
     */
    public static String getSixVCode(){
        Random random = new Random();
        StringBuffer VCode = new StringBuffer();
        for(int i = 0;i < 6;i++){
            VCode.append(random.nextInt(10));
        }
        return VCode.toString();
    }
}
