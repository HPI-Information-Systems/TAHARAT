import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PatternRegeneration {

	public static List<ComparePatternCombinations> patternRegeneration (Map<Integer, List<String>> recordWithIndicesList, List<Integer> outlierRows)
	{
		// re-run SURAGH pattern generation on extracted ill-formed records to get ill-formed patterns 
					PatternGeneration patGen_Object = new PatternGeneration();
					PruningPatterns pruPat_Object = new PruningPatterns();
					PatternSchema patSchema_Object = new PatternSchema();
					
					
					List<String> valueList = new ArrayList<String>();
					
					for(Entry<Integer,List<String>> mapValues : PatternRegeneration.extractRecords(recordWithIndicesList, outlierRows).entrySet())
					{
						valueList.addAll(mapValues.getValue());
					}
					
					int getMaxListSize = getMaxSizeList(PatternRegeneration.extractRecords(recordWithIndicesList, outlierRows));
					Map<Integer,List<List<Object>>> map_regeneratedPatterns = patGen_Object.patternComputation(valueList, getMaxListSize, Main_Class.univocityDetetced_delimiter); // if half of the file has comma as delimiter and half as semicolon, "Main_Class.univocityDetetced_delimiter" is not the right way to deal with it. We then, have to call again univocity parser to get new value using new delimiter for ill-formed records. Keep in mind we have to fix this in the end during transformation as both patterns will have <DEL> as abstraction for both comma and semicolon.  
					List<List<Object>> optimalObjectList = pruPat_Object.patternWeights_patternPruning(map_regeneratedPatterns, 1, 1, outlierRows.size()); // trying 1% for both threshold "Main_Class.col_T" and  "Main_Class.row_T", changed this code on 10 June 2022
					List<ComparePatternCombinations> outputPatterns= patSchema_Object.schemaGeneration(Main_Class.row_T, outlierRows.size(), optimalObjectList);
					
					if(patSchema_Object.schemaGeneration(Main_Class.row_T, outlierRows.size(), optimalObjectList) == null)  // the method returns null if any column pattern coverage is less than the given threshold so basically the column information is missing
						return null;
					
						
					return outputPatterns;
	}

	
	public static Map<Integer, List<String>> extractRecords(Map<Integer, List<String>> recordWithIndicesList, List<Integer> possible_Outlier_Rows_indicies_LIST)
	{
		Map<Integer, List<String>> brokenRecords = new LinkedHashMap<Integer, List<String>>();
		
		for(int i= 0; i<possible_Outlier_Rows_indicies_LIST.size(); i++)
		{
			brokenRecords.put(possible_Outlier_Rows_indicies_LIST.get(i), recordWithIndicesList.get(possible_Outlier_Rows_indicies_LIST.get(i)));
		}
		
		return brokenRecords;
	}
	
	public static int getMaxSizeList (Map<Integer, List<String>> map)
	{
		int listSize = 0;
		
		for(Entry<Integer, List<String>> var: map.entrySet())
		{
			if(var.getValue().size()>listSize)
			{
				listSize = var.getValue().size();
			}
		}
		
		return listSize ;
	}
}
