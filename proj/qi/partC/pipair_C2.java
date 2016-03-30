
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pipair_java{
	
	/**
	 * key = SET(A), SET(A,B),....
	 * value = SET(SCOPE1,SCOPE2),....
	 */
	public static Map<Set<String>,Set<String>> location= new HashMap<Set<String>,Set<String>>();
	public static Set<String> nearByCallees= new HashSet<String>();
	//partc
	public static Map<String,Set<String>> callerCallee= new HashMap<String,Set<String>>();
	
	public static int expandLevel=1;
	
    public static void main(String[] args) throws Exception{  
    	
    	int support=3;
    	double confidence=0.65;
    	//String[] cmds = {"/usr/local/bin/opt","-print-callgraph",""};
    	int argsLength = args.length;
    	if(argsLength>=1){
			if(argsLength>=2){
				support = Integer.parseInt(args[1]);
				if(argsLength>=3){
				    confidence = Double.parseDouble(args[2]);
				    confidence = confidence/100;
				    if(argsLength>=4){
				    	expandLevel = Integer.parseInt(args[3]);
					}
				}
			}
		}

    	Scanner scanner=new Scanner(System.in);
		Pattern caller_p = Pattern.compile("Call.*'(.*)'.*"); 
		Pattern callee_p = Pattern.compile(".*'(.*)'.*");
		Matcher caller_m = null;
		Matcher callee_m = null;
		String line =null;
		while(scanner.hasNext()){
			line = scanner.nextLine();
			caller_m = caller_p.matcher(line);
			if(caller_m.matches() /*&& !"main".equals(caller_m.group(1))*/) {
				nearByCallees.clear();
				Set<String> callees = new HashSet<String>();
				while(scanner.hasNext()){
					line = scanner.nextLine();
					if(line.length()==0){
						break;
					}
					callee_m = callee_p.matcher(line);
					if(callee_m.matches()){
						// solution1:
						addValueToMap(callee_m.group(1),caller_m.group(1));
						nearByCallees.add(callee_m.group(1));
						callees.add(callee_m.group(1));
					}
				}
				// partc
				if(!callees.isEmpty()){
					callerCallee.put(caller_m.group(1), callees);
				}
			}
		}
		scanner.close();
		//partc:solution2:
		printBug(support,confidence);
    }
    
    
    
    public static void addValueToMap(String addValue,String addLocation){
    	
    	//----------------------location-------------------------
    	// check same addvalue
    	if(nearByCallees.contains(addValue)){
    		return;
    	}
    	
    	Set<String> locationMapKeySet = null;
    	Set<String> locationMapValueSet = null;
    	for(String key:nearByCallees){
    		// add otherpair
    		locationMapKeySet = new HashSet<String>();
    		locationMapKeySet.add(addValue);
    		locationMapKeySet.add(key);
    		if(!location.containsKey(locationMapKeySet)){
    			locationMapValueSet = new HashSet<String>();
    		}else{
    			locationMapValueSet = location.get(locationMapKeySet);
    		}
    		locationMapValueSet.add(addLocation);
    		location.put(locationMapKeySet, locationMapValueSet);
    		
    	}
    	
    	// add selfpair
		locationMapKeySet = new HashSet<String>();
		locationMapKeySet.add(addValue);
		if(!location.containsKey(locationMapKeySet)){
			locationMapValueSet = new HashSet<String>();
		}else{
			locationMapValueSet = location.get(locationMapKeySet);
		}
		locationMapValueSet.add(addLocation);
		location.put(locationMapKeySet, locationMapValueSet);
		
    	return;
    }
    
    
    
    
    
    public static void printBug(int support,double confidence){
    	
    	Integer supportKey = 0;
		Integer supportPair = 0;
		double confidencePair = 0;
		String singleKeyStr =null;
		String pairStr1 =null;
		String pairStr2 = null;
		
    	for(Set<String> singleKey:location.keySet()){
    		if(singleKey.size()==1){
    			supportKey = location.get(singleKey).size();
    			// bugsingle
    			// bugsingle
				singleKeyStr = singleKey.toArray()[0].toString();
				
    			for(Set<String> pipairKey:location.keySet()){
        			if(pipairKey.size()>1 && pipairKey.contains(singleKeyStr)){
        				supportPair = location.get(pipairKey).size();
        				confidencePair = (double)supportPair/(double)supportKey;
        				//System.out.println(singleKey+","+pipairKey+","+supportKey+","+supportPair+","+confidencePair);
        				
        				if(supportKey>=support && supportPair>=support && confidencePair<1 && confidencePair>=confidence){
        					
        					// bugpair
            				pairStr1 = pipairKey.toArray()[0].toString();
            				pairStr2 = pipairKey.toArray()[1].toString();
            				if(pairStr1.compareTo(pairStr2)<0){
            					pairStr1 = "("+pairStr1+", "+pairStr2+")";
            				}else{
            					pairStr1 = "("+pairStr2+", "+pairStr1+")";
            				}
            				            				
            				// buglocation
            				Set<String> locationTemp = new HashSet<String>();
            				String locationTempStr =null;
            				locationTemp.addAll(location.get(singleKey));
            				locationTemp.removeAll(location.get(pipairKey));
            				for(String locationTempElem:locationTemp){
            					locationTempStr = locationTempElem;
            					
            					// partc:solution2:
            					boolean flag = reachReducedBug(singleKey,pipairKey,callerCallee.get(locationTempStr));
            					if(flag==false){
            						// printbug
            						System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr1+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
            					}
            				}
            			}
        			}
        		}
    		}
    	}
    	
    }
    
    
    // partc:solution2
    public static boolean reachReducedBug(Set<String> singleKey,Set<String> pipairKey, Set<String> nearBy){
    	Set<String> pipair = new HashSet<String>();
    	pipair.addAll(pipairKey);
    	pipair.removeAll(singleKey);
    	String findPair = pipair.toArray()[0].toString();
    	for(String nearElem:nearBy){
    		if(singleKey.contains(nearElem)){
    			continue;
    		}
    		Stack<String> stack = new Stack<String>();
        	Set<String> visit = new HashSet<String>();
        	Map<String,Integer> level= new HashMap<String,Integer>();
        	String pop =null;
        	Set<String> popSet =null;
        	visit.add(nearElem);
        	stack.push(nearElem);
        	level.put(nearElem, 0);
        	while(!stack.isEmpty()){
        		pop = stack.pop();
        		popSet = callerCallee.get(pop);
        		if(popSet==null){
        			continue;
        		}
        		for(String popElem: popSet){
        			level.put(popElem, level.get(pop)+1);
        			// expandLevel means expand level, default =1
        			if(!visit.contains(popElem) && level.get(popElem)<=expandLevel){
        				if(popElem.equals(findPair)){
        					return true;
        				}
        		    	visit.add(popElem);
        		    	stack.push(popElem);
        			}
        		}
        	}
    	}
    	return false;
    }
    
    
}

