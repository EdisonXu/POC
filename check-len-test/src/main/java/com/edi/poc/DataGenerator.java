package com.edi.poc;

import java.util.Random;

public class DataGenerator {

    private Random lenRan = new Random();
    private static final int MAX_STRING_LEN = 255;
    
    private String [] alpha = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w",
            "x","w","z", "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","W","Z",",",".","和","我","哈"};
    
    public String genString(int num) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<num;i++){
            String c = alpha[r.nextInt(alpha.length)];
            sb.append(c);
        }
        return sb.toString();
    }
    
    public String genRandomString(){
        return genString(lenRan.nextInt(MAX_STRING_LEN));
    }

    public static void main(String[] args) {
        /*DataGenerator st = new DataGenerator();
        for(int i=0;i<5;i++)
        {
            String t = st.genRandomString();
            System.out.println(t.length());
            System.out.println(t);   
        }*/
        Random r = new Random(1200);
        for(int i=0;i<100;i++){
            System.out.println(r.nextInt(255));
        }
    }
}
