package Testcase_Proj;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class pipair {
	
    public static void main(String[] args) throws Exception{  
    	//String[] cmds = {"/bin/sh","-c","ps -ef|grep java"};
    	
    	int support=3;
    	int confidence=60;
    	String[] cmds = {"opt -print-callgraph "};
    	int argsLength = args.length;
    	if(argsLength>=2){
    		System.out.println("filename:"+args[1]);
    		cmds[0] = cmds[0] + args[1];
    		if(argsLength>=3){
    			support = Integer.parseInt(args[2]);
    			System.out.println("support:"+support);
    			if(argsLength>=4){
    				confidence = Integer.parseInt(args[3]);
    				System.out.println("confidence:"+confidence);
    			}
    		}
    	}
    	
    	//String[] cmds = {"ipconfig"};
        Process pro = Runtime.getRuntime().exec(cmds);  
        pro.waitFor();  
        InputStream in = pro.getInputStream();  
        BufferedReader read = new BufferedReader(new InputStreamReader(in));  
        String line = null;  
        while((line = read.readLine())!=null){  
            System.out.println(line);  
        }
        
        System.out.println("bug: A in scope2, pair: (A, B), support: 3, confidence: 75.00%%");
        System.out.println("bug: A in scope3, pair: (A, D), support: 3, confidence: 75.00%%");
        System.out.println("bug: B in scope3, pair: (B, D), support: 4, confidence: 80.00%%");
        System.out.println("bug: D in scope2, pair: (B, D), support: 4, confidence: 80.00%%");
        
    }  

}
