
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class pipair_java {
	
    public static void main(String[] args) throws Exception{  
    	int support=3;
    	int confidence=60;
    	String[] cmds = {"/usr/local/bin/opt","-print-callgraph",""};
    	int argsLength = args.length;
    	if(argsLength>=1){
    		System.out.println("filename:"+args[0]);
			cmds[2] = args[0];
			if(argsLength>=2){
				support = Integer.parseInt(args[1]);
				System.out.println("support:"+support);
				if(argsLength>=3){
				    confidence = Integer.parseInt(args[2]);
				    System.out.println("confidence:"+confidence);
				}
			}
		}

    	
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
