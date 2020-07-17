package com.mapleworld.game.tapgame.main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class decrypt {
    public static Key i = null;
    public static void initKey(){

            String b = 171 + "3" + "5.27" + false;
            String var10000 = "KoY";
            KeyGenerator var3 = null;
            try {
                var3 = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            var3.init(128, new SecureRandom(b.getBytes()));
            byte[] var1 = var3.generateKey().getEncoded();
            i = new SecretKeySpec(var1, "AES");

    }
    public static void main() throws FileNotFoundException {
        initKey();
        String path = "D:\\BMS\\BMS171\\脚本\\道具";		//要遍历的路径
        File file = new File(path);		//获取其file对象
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        for(File f:fs){					//遍历File[]数组
            if(!f.isDirectory()){
                String str = "";
                try {
                    BufferedReader in = new BufferedReader(new FileReader(f));
                    String oneLine = "";
                    while ((oneLine = in.readLine()) != null) {
                        str += oneLine;
                    }
                    in.close();
                    //System.out.println(str);
                } catch (IOException e) {
                }
                //FileInputStream fileStream = new FileInputStream(f);


                String result = null;
                result = TestDecryptBMSScript.tryDecrypt(i, str);
                if(result != null){
                    try {
                        OutputStream os = new FileOutputStream(f);
                        os.write(result.getBytes());
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
            }

    }

}


class TestDecryptBMSScript {

    public static String tryDecrypt(Key key, String file) {
        //String s = FileUtil.readFileAsString("D:\\TencentFiles\\598943251\\FileRecv\\Boss_噩梦时间塔_路西德_困难.jse");
        //String s = "1ZWEA8DkOFgdjUgu1zCqHhuIRELpEvb7Fhj5ZPVhcl68+HglIcIu/g/De1EenKBgPaK6irUB43s84tpTqYFYIzULA6ituG+1Qhj9CWOb0oRS2saiZ/o+YLC2H3qorc2uAw22YGOxkoGwR2XYhLFO/qrDnAMrlK213jeJIG7lAdEOPYWoBnK7508mp8Fye4fncIC5fxWEq/kB/LQh5IGhnUTQHlIQW+bz6foSHY6yeZJj/LQKf+9Zyb2DkdABM7agReMI8Nf9XdK5umiiqhQktSfnQQbWfscvdI9DKRa+csYzobS7HhAcf3AMxPFIDcDby1yl9QVnX62evXyvcWHmaImRQ/DghBaJ8w5H6jxff2jwg0HuPZtv2a+msvC2TcVj48khVpo7ZkAorGh8eTbWxpOu0XLKPQrmDGPQUixfbrXmCQl8EOsPCslZZxF4RWRwF5MnytGqFEHNRKPzaohvb/URozaZsMQRNHW9brxgDnxJfKZlhTv+/2QdHNmp0FXEpS2x8+6+RjUR8u91ehF3WWilXALE492KuXy7MnRUUm0=";
        //System.out.println(file);
        return ALLATORIxDEMO(key, file);
    }

    private static String ALLATORIxDEMO(Key key, String var0) {
        try {
            byte[] var1;
            try{
                var1 = Base64.getDecoder().decode(var0);
            } catch (IllegalArgumentException e){
                System.out.println(e.getMessage());
                return null;
            }

            Cipher var2 = Cipher.getInstance("AES");
            var2.init(2, key);
            byte[] var4 = var2.doFinal(var1);
            return new String(var4, StandardCharsets.UTF_8);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }
}
