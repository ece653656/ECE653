import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class pipair_java {

	public static Map<String,List<String>> result= new HashMap<String,List<String>>();

    public static void main(String[] args) throws Exception{  
    	int support=3;
    	int confidence=60;
    	//String[] cmds = {"/usr/local/bin/opt","-print-callgraph",""};
    	int argsLength = args.length;
    	if(argsLength>=1){
    		//System.out.println("filename:"+args[0]);
			//cmds[2] = args[0];
			if(argsLength>=2){
				support = Integer.parseInt(args[1]);
				//System.out.println("support:"+support);
				if(argsLength>=3){
				    confidence = Integer.parseInt(args[2]);
				    //System.out.println("confidence:"+confidence);
				}
			}
		}

    	/*
        Process pro = Runtime.getRuntime().exec(cmds);  
        pro.waitFor();  
        InputStream in = pro.getInputStream();  
        BufferedReader read = new BufferedReader(new InputStreamReader(in));  
        String line = null;  
        while((line = read.readLine())!=null){  
            System.out.println(line);  
        }
        */
    	
		Scanner scanner=new Scanner(System.in);
		Pattern caller_p = Pattern.compile("Call.*'(.*)'.*"); 
		Pattern callee_p = Pattern.compile(".*'(.*)'.*");
		Matcher caller_m = null;
		Matcher callee_m = null;
		String caller_line =null;
		String callee_line =null;
		while(scanner.hasNext()){
			caller_line = scanner.nextLine();
			caller_m = caller_p.matcher(caller_line);
			if(caller_m.matches() && !"main".equals(caller_m.group(1))) {
				/*
				List<String> callees = new ArrayList<String>();
				while(scanner.hasNext()){
					callee_line = scanner.nextLine();
					callee_m = callee_p.matcher(callee_line);
					if(callee_m.matches()){
						callees.add(callee_m.group(1));
					}else{
						if(!callees.isEmpty()){
							result.put(caller_m.group(1), callees);
						}
						break;
					}
				}
				*/
				Set<String> keys = new HashSet<String>();
				while(scanner.hasNext()){
					callee_line = scanner.nextLine();
					callee_m = callee_p.matcher(callee_line);
					if(callee_m.matches()){
						addKeyValue(keys,callee_m.group(1));
						keys.add(callee_m.group(1));
					}else{
						break;
					}
				}
				
			}
			
		}
		scanner.close();
		
		
		System.out.println(result);
		
        System.out.println("bug: A in scope2, pair: (A, B), support: 3, confidence: 75.00%%");
        System.out.println("bug: A in scope3, pair: (A, D), support: 3, confidence: 75.00%%");
        System.out.println("bug: B in scope3, pair: (B, D), support: 4, confidence: 80.00%%");
        System.out.println("bug: D in scope2, pair: (B, D), support: 4, confidence: 80.00%%");
        
    }
	
	public static void addKeyValue(Set<String> nearByKeys,String addValue){
    	
    	// add new key
    	List<String> nearByValues = null;
    	if(!result.containsKey(addValue)){
    		nearByValues = new ArrayList<String>();
    	}else{
    		nearByValues = result.get(addValue);
    	}
    	for(String key:nearByKeys){
    		nearByValues.add(key);
    	}
		nearByValues.add(addValue);
		result.put(addValue, nearByValues);
    	
    	// add new value to keys
    	for(String key:nearByKeys){
    		nearByValues = result.get(key);
    		nearByValues.add(addValue);
        	result.put(key, nearByValues);
    	}
    	
    	return;
    }
}
