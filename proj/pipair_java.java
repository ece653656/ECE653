
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
	 * key = A,B,C,...
	 * value = [A,B,C,A,C,D...]
	 */
	public static Map<String,List<String>> map= new HashMap<String,List<String>>();
	/**
	 * key = SET(A), SET(A,B),....
	 * value = SET(SCOPE1,SCOPE2),....
	 */
	public static Map<Set<String>,Set<String>> location= new HashMap<Set<String>,Set<String>>();
	public static Set<String> nearByCallees= new HashSet<String>();
	
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
						// solution1:
						addValueToMap(callee_m.group(1),caller_m.group(1));
						nearByCallees.add(callee_m.group(1));
					}
				}
				
			}
		}
		scanner.close();
		//System.out.println(map);
		//System.out.println(location);
		printBug(support,confidence);
    }
    
    
    
    public static void addValueToMap(String addValue,String addLocation){
    	
    	//--------------------map-------------------------
    	// check same addvalue
    	if(nearByCallees.contains(addValue)){
    		return;
    	}
    	
    	// add new key
    	List<String> nearByValues = null;
    	if(!map.containsKey(addValue)){
    		nearByValues = new ArrayList<String>();
    	}else{
    		nearByValues = map.get(addValue);
    	}
    	for(String key:nearByCallees){
    		nearByValues.add(key);
    	}
		nearByValues.add(addValue);
		map.put(addValue, nearByValues);
    	
    	// add new value to keys
    	for(String key:nearByCallees){
    		nearByValues = map.get(key);
    		nearByValues.add(addValue);
    		map.put(key, nearByValues);
    	}
    	
    	//----------------------location-------------------------
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
    	
    	Map<Set<String>,Integer> countMap = null;
    	Set<String> countMapKeySet = null;
    	for(String key:map.keySet()){
    		countMap = new HashMap<Set<String>,Integer>();
    		//get value
    		List<String> valueList = map.get(key);
    		//System.out.println("valueList: "+valueList);
    		
    		//initCountMap
    		for(String elem: valueList){
    			countMapKeySet = new HashSet<String>();
    			countMapKeySet.add(key);
    			countMapKeySet.add(elem);
    			if(!countMap.containsKey(countMapKeySet)){
    				countMap.put(countMapKeySet, 1);
    			}else{
    				countMap.put(countMapKeySet, countMap.get(countMapKeySet)+1);
    			}
    		}
    		//System.out.println("countMap: "+countMap);

    		//support,confidence
			countMapKeySet = new HashSet<String>();
    		countMapKeySet.add(key);
    		Integer supportKey = countMap.get(countMapKeySet);
    		Integer supportPair = null;
    		double confidencePair = 0;
    		for(Set<String> countMapKey: countMap.keySet()){
    			if(!countMapKey.equals(countMapKeySet)){
    				supportPair = countMap.get(countMapKey);
        			confidencePair = (double)supportPair/(double)supportKey;

        			//System.out.println(supportKey+";"+supportPair+";"+confidencePair);

        			if(supportKey>=support && supportPair>=support && confidencePair<1 && confidencePair>=confidence){
        				// bugpair
        				List<String> countMapKeyList = new ArrayList<String>();
        				for(String countMapKeyElem:countMapKey){
        					countMapKeyList.add(countMapKeyElem);
        				}
        				String pairStr1 = countMapKeyList.get(0);
        				String pairStr2 = countMapKeyList.get(1);
        				if(pairStr1.compareTo(pairStr2)<0){
        					pairStr1 = "("+pairStr1+", "+pairStr2+")";
        				}else{
        					pairStr1 = "("+pairStr2+", "+pairStr1+")";
        				}
        				
        				
        				// buglocation
        				Set<String> locationTemp = new HashSet<String>();
        				String locationTempStr =null;
        				locationTemp.addAll(location.get(countMapKeySet));
        				locationTemp.removeAll(location.get(countMapKey));
        				for(String locationTempElem:locationTemp){
        					locationTempStr = locationTempElem;
        					// printbug
            				System.out.println("bug: "+key+" in "+locationTempStr+", pair: "+pairStr1+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
        				}
        			}
    			}
    		}
    		
    		
    	}
    	/*
        System.out.println("bug: A in scope3, pair: (A, D), support: 3, confidence: 75.00%");
        System.out.println("bug: B in scope3, pair: (B, D), support: 4, confidence: 80.00%");
        System.out.println("bug: D in scope2, pair: (B, D), support: 4, confidence: 80.00%");
        */
    }
    
}

