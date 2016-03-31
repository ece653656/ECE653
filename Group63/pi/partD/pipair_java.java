
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
		//System.out.println(location);
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
		Integer supportPair2 = 0;
		double confidencePair = 0;
		double confidencePair2 = 0;
		String singleKeyStr =null;
		String pairStr =null;
		
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
        					// ignore order bug
        					List<String> pipairKey2 = new ArrayList<String>();
        					Set<String>locationTemp2 = new HashSet<String>();
        					pipairKey2.add(pipairKey.get(1));
        					pipairKey2.add(pipairKey.get(0));
        					locationTemp2 = location.get(pipairKey2);
        					
        					// bugpair
            				pairStr = "("+pipairKey.get(0)+", "+pipairKey.get(1)+")";
            				// buglocation
            				Set<String> locationTemp = new HashSet<String>();
            				String locationTempStr =null;
            				locationTemp.addAll(location.get(singleKey));
            				locationTemp.removeAll(location.get(pipairKey));
            				for(String locationTempElem:locationTemp){
            					locationTempStr = locationTempElem;
//            					// show appear more than once bug only
//        						if(pipairKey2.get(0).equals(pipairKey2.get(1))){
//        							System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
//        						}            					
            					// show wrong ordered bug only
//            					if(locationTemp2!=null && locationTemp2.contains(locationTempStr)){
//            						if(!pipairKey2.get(0).equals(pipairKey2.get(1))){
//                						//System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
//            						}
//            					}            					
            					//show new ordered bug only
//        						if(locationTemp2!=null && !locationTemp2.isEmpty() && !pipairKey2.get(0).equals(pipairKey2.get(1))){
//        							supportPair2 = location.get(pipairKey2).size();
//                    				confidencePair2 = (double)supportPair2/(double)supportKey;
//                    				if(supportKey>=support && supportPair2>=support && confidencePair2<1 && confidencePair2>=confidence){
//                    					Set<String> locationTempComp = new HashSet<String>();
//    	                				locationTempComp.addAll(location.get(singleKey));
//    	                				locationTempComp.removeAll(location.get(pipairKey2));
//    	                				if(locationTempComp.contains(locationTempStr)){
//    	                					System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
//    	                				}
//                    				}
//        						}
                				System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
            					
            				}
            			}
        			}     				
        		}
    		}
    		
    	}
    	
    }
    
}

