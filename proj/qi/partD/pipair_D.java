
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pipair_java{
	
	/**
	 * key = SET(A), SET(A,B),....
	 * value = SET(SCOPE1,SCOPE2),....
	 */
	public static Map<List<String>,Set<String>> location= new HashMap<List<String>,Set<String>>();
	public static List<String> nearByCallees= new ArrayList<String>();
	
    public static void main(String[] args) throws Exception{  
    	
    	int support=3;
    	double confidence=0.65;
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
				while(scanner.hasNext()){
					line = scanner.nextLine();
					if(line.length()==0){
						break;
					}
					callee_m = callee_p.matcher(line);
					if(callee_m.matches()){
						addValueToMap(callee_m.group(1),caller_m.group(1));
						nearByCallees.add(callee_m.group(1));
					}
				}
				
			}
		}
		scanner.close();
		System.out.println(location);
		printBug(support,confidence);
    }
    
    
    
    public static void addValueToMap(String addValue,String addLocation){
    	
    	//----------------------location-------------------------
    	List<String> locationMapKeyList = null;
    	Set<String> locationMapValueSet = null;
    	for(String key:nearByCallees){
    		// add otherpair positive order
    		locationMapKeyList = new ArrayList<String>();
    		locationMapKeyList.add(key);
    		locationMapKeyList.add(addValue);
    		if(!location.containsKey(locationMapKeyList)){
    			locationMapValueSet = new HashSet<String>();
    		}else{
    			locationMapValueSet = location.get(locationMapKeyList);
    		}
    		locationMapValueSet.add(addLocation);
    		location.put(locationMapKeyList, locationMapValueSet);
    		
    	}
    	
    	// add selfpair
		locationMapKeyList = new ArrayList<String>();
		locationMapKeyList.add(addValue);
		if(!location.containsKey(locationMapKeyList)){
			locationMapValueSet = new HashSet<String>();
		}else{
			locationMapValueSet = location.get(locationMapKeyList);
		}
		locationMapValueSet.add(addLocation);
		location.put(locationMapKeyList, locationMapValueSet);
		
    	return;
    }
    
    
    public static void printBug(int support,double confidence){
    	
    	Integer supportKey = 0;
		Integer supportPair = 0;
		Integer supportPairAll = 0;
		double confidencePair = 0;
		double confidencePairAll = 0;
		String singleKeyStr =null;
		String pairStr =null;
		String pairStr2 =null;
		
    	for(List<String> key:location.keySet()){
    		if(key.size()==1){
    			List<String> singleKey = key;
    			supportKey = location.get(singleKey).size();
    			// bugsingle
				singleKeyStr = singleKey.get(0);
				
    			for(List<String> pipairKey:location.keySet()){
    				// findbugpair single should be pipair
        			if(pipairKey.size()>1 && pipairKey.contains(singleKeyStr)){
        				supportPair = location.get(pipairKey).size();
        				confidencePair = (double)supportPair/(double)supportKey;
        				//System.out.println(singleKey+","+pipairKey+","+supportKey+","+supportPair+","+confidencePair);
        				if(supportKey>=support && supportPair>=support && confidencePair<1 && confidencePair>=confidence){
        					
        					// bugpair
            				pairStr = "("+pipairKey.get(0)+", "+pipairKey.get(1)+")";
            				// buglocation
            				Set<String> locationTemp = new HashSet<String>();
            				String locationTempStr =null;
            				locationTemp.addAll(location.get(singleKey));
            				locationTemp.removeAll(location.get(pipairKey));
            				for(String locationTempElem:locationTemp){
            					locationTempStr = locationTempElem;
            					// printbug
                				System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
            				}
            			}
        			}     				
        		}
    		}
    		
    		// findbugpair pipair should be another pipair
			if(key.size()>1){
				List<String> pipairKey = key;
				List<String> pipairKey2 = new ArrayList<String>();
				Set<String> pipairValue = new HashSet<String>();
				Set<String> pipairValue2 = new HashSet<String>();
				pipairKey2.add(pipairKey.get(1));
				pipairKey2.add(pipairKey.get(0));
				pipairValue = location.get(pipairKey);
				pipairValue2 = location.get(pipairKey2);
				if(pipairValue2!=null && !pipairValue2.isEmpty()){
					Set<String> pipairlocation = new HashSet<String>();
					pipairlocation.addAll(pipairValue);
					pipairlocation.addAll(pipairValue2);
					supportPair = pipairValue.size();
					supportPairAll = pipairlocation.size();
					confidencePairAll = (double)supportPair/(double)supportPairAll;
					//System.out.println(pipairKey+","+pipairKey2+","+supportPair+","+supportPairAll+","+confidencePairAll);
					if(supportPair>=support && supportPairAll>=support && confidencePairAll<1 && confidencePairAll>=confidence){
						// bugpair
        				pairStr = "("+pipairKey.get(0)+", "+pipairKey.get(1)+")";
        				pairStr2 = "("+pipairKey2.get(0)+", "+pipairKey2.get(1)+")";
        				// buglocation
        				Set<String> locationTemp = new HashSet<String>();
        				String locationTempStr =null;
        				locationTemp.addAll(pipairValue);
        				for(String locationTempElem:locationTemp){
        					locationTempStr = locationTempElem;
        					// printbug
            				System.out.println("bug: "+pairStr+" in "+locationTempStr+", pair: "+pairStr2+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePairAll	*100)+"%");
        				}
    				}
				}
			}

    	}
    	
    }
    
}

