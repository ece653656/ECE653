
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pipair_C1{
	
	/**
	 * key = SET(A), SET(A,B),....
	 * value = SET(SCOPE1,SCOPE2),....
	 */
	public static Map<Set<String>,Set<String>> location= new HashMap<Set<String>,Set<String>>();
	public static Set<String> nearByCallees= new HashSet<String>();
	//partc
	public static Map<String,Set<String>> callerCallee= new HashMap<String,Set<String>>();
	//partc
	public static Map<String,Set<String>> callerCalleeExtend= new HashMap<String,Set<String>>();
	
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
		//partc:solution1:
		initInterLocation();
//		System.out.println("callerCallee");
//		System.out.println(callerCallee);
//		System.out.println("callerCalleeExtend");
//		System.out.println(callerCalleeExtend);
//		System.out.println("location");
//		System.out.println(location);
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
		List<String> keyList =null;
		
    	for(Set<String> singleKey:location.keySet()){
    		if(singleKey.size()==1){
    			supportKey = location.get(singleKey).size();
    			// bugsingle
    			keyList = new ArrayList<String>();
				for(String elem:singleKey){
					keyList.add(elem);
				}
				singleKeyStr = keyList.get(0);
				
    			for(Set<String> pipairKey:location.keySet()){
        			if(pipairKey.size()>1 && pipairKey.contains(singleKeyStr)){
        				supportPair = location.get(pipairKey).size();
        				confidencePair = (double)supportPair/(double)supportKey;
        				//System.out.println(singleKey+","+pipairKey+","+supportKey+","+supportPair+","+confidencePair);
        				
        				if(supportKey>=support && supportPair>=support && confidencePair<1 && confidencePair>=confidence){
        					
        					// bugpair
            				keyList = new ArrayList<String>();
            				for(String elem:pipairKey){
            					keyList.add(elem);
            				}
            				pairStr1 = keyList.get(0);
            				pairStr2 = keyList.get(1);
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
            					// printbug
            					System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr1+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
            				}
            			}
        			}
        		}
    		}
    	}
    	
    }
    
    // partc:solution1
    public static void initInterLocation(){
    	// init callerCalleeExtend
        for(String caller:callerCallee.keySet()){
        	Set<String> extendCallees = new HashSet<String>();
        	extendCallees = getCalleeExtend(extendCallees,caller,0);
        	callerCalleeExtend.put(caller, extendCallees);
        }
        
    	// init initInterLocation
        int i,j;
        for(String caller:callerCalleeExtend.keySet()){
        	Set<String> extendCallees = callerCalleeExtend.get(caller);
        	List<String> extendCalleesList = new ArrayList<String>();
        	Object[] extendCalleesArray = extendCallees.toArray();
        	// find combination
        	for(i=0;i<extendCalleesArray.length;i++){
        		for(j=0;j<extendCalleesArray.length;j++){
        			// update locations
        			Set<String> tempKey = new HashSet<String>();
        			Set<String> tempValue = null;
        			tempKey.add(extendCalleesArray[i].toString());
        			tempKey.add(extendCalleesArray[j].toString());
        			if(!location.containsKey(tempKey)){
        				tempValue = new HashSet<String>();
        			}else{
        				tempValue = location.get(tempKey);
        			}
        			tempValue.add(caller);
        			location.put(tempKey, tempValue);
        			
        		}
        	}
        }
    	
    }
    
    // partc:solution1
    public static Set<String> getCalleeExtend(Set<String> result,String caller,Integer levelCount){
    	//System.out.println("levelCount:"+levelCount);
		//System.out.println("result:"+result);
    	// 1:means expand level =1
    	if(levelCount>1){
    		return result;
    	}else{
    		Set<String> callees = callerCallee.get(caller);
    		if(callees!=null && !callees.isEmpty()){
    			result.addAll(callees);
        		for(String callee:callees){
            		result = getCalleeExtend(result,callee,levelCount+1);
        		}
    		}
        	return result;
    	}
    }
    
    
}

