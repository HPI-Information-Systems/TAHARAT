import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import abstractions.Gap_Class;
import javafx.util.Pair;


public class PatternTransformation {

	private static final Gap_Class GAP_CLASS = new Gap_Class();
    
	public void patternReciever(String cleanedFile, List<PatternAlignmentResult> patterns_to_Transform, Map<Integer, String[]> indicesForTransformation, List<Integer> wanted, List<Integer> wellFormed) 
	{
//		System.out.println("------------------------------------- Transformation Ready --------------------------------------");
		
		List<TransformationInputBlocks> patterns_For_Transformation = new ArrayList<TransformationInputBlocks>();
		for(PatternAlignmentResult pr : patterns_to_Transform)
		{
			List< Pair<String, Pair<List<Object>, List<Object>>> > blocks = new ArrayList<Pair<String,Pair<List<Object>,List<Object>>>>();
			List<String> keyList = new ArrayList<String>(pr.transformationBlocks.keySet());
	
			for(int i = 0; i < keyList.size(); i++) 
			{
			   if(i+1 < keyList.size())
			   {
				    if(keyList.get(i).toString().contains("Match") && !(keyList.get(i+1).toString().contains("Horizontal"))) 
				    {
				    	blocks.add(new Pair<String, Pair<List<Object>, List<Object>>>("Match", pr.pairs_of_patterns.get(i)));
				    }
				    else if(keyList.get(i).toString().contains("Mismatch") && !(keyList.get(i+1).toString().contains("Horizontal")))
				    {
				    	blocks.add(new Pair<String, Pair<List<Object>, List<Object>>>("Mismatch", pr.pairs_of_patterns.get(i)));
				    }
				    else if( (keyList.get(i).toString().contains("Mismatch") && keyList.get(i+1).toString().contains("Horizontal") )
				    		|| keyList.get(i).toString().contains("Horizontal"))
				    {
				    	blocks.add(new Pair<String, Pair<List<Object>, List<Object>>>("Horizontal", pr.pairs_of_patterns.get(i)));
				    }
				    else if( (keyList.get(i).toString().contains("Match") && keyList.get(i+1).toString().contains("Horizontal"))
				    		|| keyList.get(i).toString().contains("Horizontal"))
				    {
				    	blocks.add(new Pair<String, Pair<List<Object>, List<Object>>>("Horizontal", pr.pairs_of_patterns.get(i)));
				    }
				    else if(keyList.get(i).toString().contains("Vertical"))
				    {
				    	blocks.add(new Pair<String, Pair<List<Object>, List<Object>>>("Vertical", pr.pairs_of_patterns.get(i)));
				    } 
			    }
			   // if there is a need to include the line breaks, then uncomment the code below 
//			   else if(i+1 == keyList.size())
//			   {
//				   blocks.add(new Pair<String, Pair<List<Object>, List<Object>>>("Match", pr.pairs_of_patterns.get(i)));  // condition for the line break -- last index
//			   }
			    
		    }
			
			// combine  horizontal and vertical blocks together with position indices before sending them for transformations
	        
			List<Pair<Integer, List<Integer>>> position = new ArrayList<Pair<Integer,List<Integer>>>();
			List< Pair<String, Pair<List<Object>, List<Object>>> > new_blocks = new ArrayList<Pair<String,Pair<List<Object>,List<Object>>>>();
			int start = 0;
			
			for(int i = 0; i< blocks.size(); i++)
			{
				if(blocks.get(i).getKey().toString().matches("Match") || blocks.get(i).getKey().toString().matches("Mismatch"))
				{
					position.add(new Pair(start,i));
					new_blocks.add(new Pair(blocks.get(i).getKey(), new Pair (new ArrayList<>(blocks.get(i).getValue().getKey()), new ArrayList<>(blocks.get(i).getValue().getValue()))));
//					new_blocks.add(blocks.get(i));
					start++;
					
				}
				else if(blocks.get(i).getKey().toString().matches("Horizontal"))
				{
					List<Object> mergeList_Keys = new ArrayList<Object>();
					List<Object> mergeList_Values = new ArrayList<Object>();
					int blockSize = 0;
					mergeList_Keys.addAll(blocks.get(i).getValue().getKey());		
					while(i< blocks.size() && blocks.get(i).getKey().toString().matches("Horizontal"))
					{
						mergeList_Values.add(blocks.get(i).getValue().getValue());
						i++;
						blockSize++;
					}
					i--;
					new_blocks.add(new Pair("Horizontal", new Pair (new ArrayList<>(mergeList_Keys), new ArrayList<>(mergeList_Values))));
					
					// shift value block size // to understand from which part to which part shift had happened
					// new Pair(start, l) --> "start" is the position for correct row pattern while "l" is the list of positions of shifted values in ill-formed row
					List l = new LinkedList<>();
					for(int m = 0; m<blockSize; m++)
					{
						l.add(i-m);
					}
					Collections.reverse(l);
					position.add(new Pair(start, l));
					
					start++;
				}
				
				else if(blocks.get(i).getKey().toString().matches("Vertical"))
				{
					position.add(new Pair(start, i));
					new_blocks.add(new Pair(blocks.get(i).getKey(), new Pair (new ArrayList<>(blocks.get(i).getValue().getKey()), new ArrayList<>(blocks.get(i).getValue().getValue()))));
//					new_blocks.add(blocks.get(i));
					start++;
				}
				
			}
			
			// creating instance of ready for transformation blocks 
			patterns_For_Transformation.add(new TransformationInputBlocks(new_blocks, position, pr.row_indices));
    	 }  
	     
		 // clean records table
		 Map<Integer, String[]> cleanRecords = new LinkedHashMap<Integer, String[]>();
		 cleanRecords = PatternTransformation.extractRecords(indicesForTransformation, wellFormed);  // add well formed (wanted) records to clean records list
		
//		 for(Entry<Integer, String[]> mapValues: cleanRecords.entrySet())
//		 {
//			 for(String s: mapValues.getValue())
//				 System.out.println("new values  "+s);
//		 }
		 
		
		 for(int i = 0; i<patterns_For_Transformation.size(); i++)
		 {
			 
    		 PatternTransformationOperators patternTransformationOperator_Object = new PatternTransformationOperators();
//			 System.out.println(patterns_For_Transformation.get(i));
			 
			 
			 Map<Integer, String[]> valueMap = new LinkedHashMap<Integer, String[]>();
	
			 valueMap = PatternTransformation.extractRecords(indicesForTransformation, patterns_For_Transformation.get(i).row_indices); // extracting rows corresponding to patterns
			 
			 cleanRecords.putAll( patternTransformationOperator_Object.cleanErrors( valueMap , patterns_For_Transformation.get(i) )); // cleaning and adding transformed records to clean records table
		 }
	     
//		 System.out.println(cleanRecords.size());
//		 
//		 for (Entry<Integer, String[]> entry: cleanRecords.entrySet()) 
//		 {
//			 System.out.println(entry.getKey() +"    "+ entry.getValue());
//			 String[] testing = entry.getValue(); 	
//			 for(int j = 0; j <testing.length;j++)
//			 {
//				 System.out.println(testing[j]);
//			 }
//		 }
		 
		 // printing final output 
		 LinkedHashMap<Integer, String[]> sortedMap = new LinkedHashMap<>();
		 cleanRecords.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		 
		 
		 Csv_Writer.cleanCSV(sortedMap, cleanedFile);
	 }
	
	public static Map<Integer, String[]> extractRecords(Map<Integer, String[]> recordWithIndicesList, List<Integer> indices)
	{
		Map<Integer, String[]> brokenRecords = new LinkedHashMap<Integer, String[]>();
		
		for(int i= 0; i<indices.size(); i++)
		{
			brokenRecords.put(indices.get(i), recordWithIndicesList.get(indices.get(i)));
		}
		
		return brokenRecords;
	}
	
	
	
	
	//**********************************************************************************************************************************************************
	// ------------------code to select shortest path using edit distance metric (different cost model than the sequence alignment)----------------------------
	//**********************************************************************************************************************************************************
	
//	public void patternReciever(List<ComparePatternCombinations> cpcList, List<PatternStatistics> psList)
//	{
//		// cpcList contains valid patterns
//		// psList contains patterns that need to be further classified or transformed
//		
//		List<List<DynamicMatrixStatistics>> editDistanceMatrices= new ArrayList<List<DynamicMatrixStatistics>>();
//		List<DynamicMatrixStatistics> editDistanceMatrices_subList = new ArrayList<DynamicMatrixStatistics>();
//        
//		for(int i = 0; i<cpcList.size(); i++)
//		{ 
//			
//			editDistanceMatrices_subList.clear();
//			
////			String alpha = Csv_Writer.getStringRepresentation(cpcList.get(i).abstrcationInstances);
//			for(int j = 0; j<psList.size(); j++)
//			{
////				String beta = Csv_Writer.getStringRepresentation(psList.get(j).patternReprsentation);
////				System.out.println(alpha+"    "+ beta);
//				
//				int editDistanceMatrix[][]= PatternEditDistance.editDP(psList.get(j).patternReprsentation, cpcList.get(i).abstrcationInstances); // try to see how far the potential dominant pattern is from the dominant pattern
//				editDistanceMatrices_subList.add(new DynamicMatrixStatistics(cpcList.get(i).abstrcationInstances, psList.get(j).patternReprsentation, psList.get(j).patternIndices, editDistanceMatrix, editDistanceMatrix[editDistanceMatrix.length-1][editDistanceMatrix[editDistanceMatrix.length-1].length-1]));
//				
////				System.out.println("Patterns distance is "+ dynamicMatrix[cpcList.get(i).abstrcationInstances.size()][psList.get(j).patternReprsentation.size()]);   // printing the cost 
////		        System.out.println(dynamicMatrix[dynamicMatrix.length-1][dynamicMatrix[dynamicMatrix.length-1].length-1]);   // printing the cost 
//				
//				}	
//			
//			editDistanceMatrices.add(new ArrayList<DynamicMatrixStatistics>(editDistanceMatrices_subList));
//		}
//        
//		PatternTransformation.patDistance(editDistanceMatrices);
//	}
	
	// pattern edit distance manipulation ---------------------------- pattern edit distance manipulation -----------pattern edit distance manipulation 

//	public static void patDistance(List<List<DynamicMatrixStatistics>> editDistanceMatrices)
//	{
//			List<List<PatternEditDistanceResult>> output_PEDR = new LinkedList<List<PatternEditDistanceResult>>();
//			
//			if(editDistanceMatrices.size() > 1)
//			{
//				List<DynamicMatrixStatistics> updated_clusters = new ArrayList<DynamicMatrixStatistics>(); 
//				int count = editDistanceMatrices.size();
//				List<DynamicMatrixStatistics> subLists = new ArrayList<DynamicMatrixStatistics>();
//				for(int l = 0; l < editDistanceMatrices.get(0).size(); l++)
//				{
//				   int counter = 0;
//				   subLists.clear();
//				   while(count > counter)
//				   {
//					  subLists.add(editDistanceMatrices.get(counter).get(l));
//					  counter++;
//				   }
//				   updated_clusters.addAll(new ArrayList<DynamicMatrixStatistics>(getMinimumCostMatrix(subLists)));
//				}
//			
//				for(int k = 0; k<updated_clusters.size(); k++)
//				{
//					System.out.println(Csv_Writer.getStringRepresentation(updated_clusters.get(k).dominantPattern) +"       "+ Csv_Writer.getStringRepresentation(updated_clusters.get(k).potentialDominantPattern));
//					System.out.println("Pattern distance is "+ updated_clusters.get(k).cost);   // print normalized cost
////					for (int[] x : updated_clusters.get(k).dynamicMatrix)
////			        {
////			           for (int y : x)
////			           {
////			                System.out.print(y + " ");
////			           }
////			           System.out.println();
////			        }
//					output_PEDR.add(PatternEditDistance.shortestPathGraphCreation(updated_clusters.get(k).dynamicMatrix, updated_clusters.get(k).potentialDominantPattern, updated_clusters.get(k).dominantPattern, updated_clusters.get(k).crossponding_Rows));
//				}
//				
//			}
//			else
//			{
//				for(List<DynamicMatrixStatistics> list : editDistanceMatrices)
//				{
//					System.out.println("------------------------------------- pattern edit distance--------------------------------------");
//					for(int k = 0; k<list.size(); k++)
//					{
//						System.out.println(Csv_Writer.getStringRepresentation(list.get(k).dominantPattern) +"       "+ Csv_Writer.getStringRepresentation(list.get(k).potentialDominantPattern));
//						System.out.println("Pattern distance is "+ list.get(k).cost);   // print normalized cost
////						for (int[] x : list.get(k).dynamicMatrix)
////				        {
////				           for (int y : x)
////				           {
////				                System.out.print(y + " ");
////				           }
////				           System.out.println();
////				        }
//						
//						output_PEDR.add(PatternEditDistance.shortestPathGraphCreation(list.get(k).dynamicMatrix, list.get(k).potentialDominantPattern, list.get(k).dominantPattern, list.get(k).crossponding_Rows));
//					}
//				}
//			}
//			
//			String write_DM_EditDis_output_results = "C:/Users/M.Hameed/Desktop/EditDistancePaths.csv";
//			try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File(write_DM_EditDis_output_results)),"UTF-8"))
//			{
//			    CsvWriterSettings settings = new CsvWriterSettings();
//			    settings.setSkipEmptyLines(false);
//				
//				CsvWriter writer = new CsvWriter(outputWriter, settings);
//			    int counter = 1;
//				for(List<PatternEditDistanceResult> pedr_list : output_PEDR)	
//				{
//					writer.writeRow();
//					writer.writeRow(" ::::::::::::::::::::::::::::::::::::::::::::::: " +"    Record :: " + counter+"    :::::::::::::::::::::::::::::::::::::::::::::::: ");
//					writer.writeRow();
//					for(PatternEditDistanceResult pedr: pedr_list)
//					{
//						writer.writeRow(pedr.pairs_of_patterns + " :: " + pedr.pairs_of_indices+ " :: " + pedr.row_indices+ " :: " +pedr.pairs_score);
//					}
//				    counter++;
//				}
//				writer.close();
//		     }
//		     catch (IOException e) {
//		        // handle exception
//		     }	
//			
//	}
		

//	public static List<DynamicMatrixStatistics> getMinimumCostMatrix(List<DynamicMatrixStatistics> subLists) 
//	{
//	    int min = subLists.get(0).cost;    // Initialize 
//		List<DynamicMatrixStatistics> dmcList= new ArrayList<DynamicMatrixStatistics>();
//		dmcList.add(subLists.get(0));   // Initialize 
//			
//		for(DynamicMatrixStatistics dmc: subLists)
//		{
//			if(dmc.cost < min)
//			{
//				min = dmc.cost;
//				dmcList.set(0, dmc);
//			}
//		}
//		return dmcList;
//	}
	
}

