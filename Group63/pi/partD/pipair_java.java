
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
	 * key is the list of ordered pipair callee and single callee, 	example: [A],[A,A],[A,B],[B,A]....
	 * value is the set of callers for the callee, 					example: SET(SCOPE1,SCOPE2),....
	 */
	public static Map<List<String>,Set<String>> location= new HashMap<List<String>,Set<String>>();
	/**
	 * the set to help initialize HashMap 'location'
	 */
	public static List<String> nearByCallees= new ArrayList<String>();
	
    public static void main(String[] args) throws Exception{  
    	
		// accept argument to set threshold support and confidence
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
		// Traverse HashMap ��location�� and print bug
		printBug(support,confidence);
    }
    
    
     /**
	 * the function to help initialize HashMap 'location'
	 */
    public static void addValueToMap(String addValue,String addLocation){
    	
    	//----------------------location-------------------------
    	List<String> locationMapKeyList = null;
    	Set<String> locationMapValueSet = null;
    	for(String key:nearByCallees){
    		// update pair function and its callers
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
    	
    	// update single function and its callers
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
    

     /**
	 * Traverse HashMap ��location�� to calculate support and confidence for each pipair
	 * and determine if a callee function is a bug in a caller function.
	 */
    public static void printBug(int support,double confidence){
    	
    	Integer supportKey = 0;
		Integer supportPair = 0;
		Integer supportPair2 = 0;
		double confidencePair = 0;
		double confidencePair2 = 0;
		String singleKeyStr =null;
		String pairStr =null;
		
    	for(List<String> key:location.keySet()){
    		if(key.size()==1){
    			List<String> singleKey = key;
				// support of single function
    			supportKey = location.get(singleKey).size();
    			// singleKeyStr is for bug line printout 
				singleKeyStr = singleKey.get(0);
				
    			for(List<String> pipairKey:location.keySet()){
    				// findbugpair single should be pipair
        			if(pipairKey.size()>1 && pipairKey.contains(singleKeyStr)){
						// support of pipair function
        				supportPair = location.get(pipairKey).size();
						// support of pipair function
        				confidencePair = (double)supportPair/(double)supportKey;
        				// check pipair to meet threshold support and confidence
        				if(supportKey>=support && supportPair>=support && confidencePair<1 && confidencePair>=confidence){
        					// pairStr1 is for bug line printout
            				pairStr = "("+pipairKey.get(0)+", "+pipairKey.get(1)+")";
            				// location for pipair should take place of single function 
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
    		
    	}
    	
    }
    
}
