package Testcase_Proj;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class pipair {
	
    public static void main(String[] args) throws Exception{  
    	//String[] cmds = {"/bin/sh","-c","ps -ef|grep java"};
    	String[] cmds = {"ipconfig"};
        Process pro = Runtime.getRuntime().exec(cmds);  
        pro.waitFor();  
        InputStream in = pro.getInputStream();  
        BufferedReader read = new BufferedReader(new InputStreamReader(in));  
        String line = null;  
        while((line = read.readLine())!=null){  
            System.out.println(line);  
        }  
    }  

}
