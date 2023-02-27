import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import abstractions.Candidate_Delimiter_Class;
import javafx.util.Pair;

public class PatternClassification {
	
	private static final Candidate_Delimiter_Class CANDIDATE_DELIMITER_CLASS = new Candidate_Delimiter_Class();

	public List<PatternAlignmentResult> patternReciever(List<ComparePatternCombinations> cpcList, List<PatternStatistics> psList)
	{
		// cpcList contains valid patterns
		// psList contains patterns that need to be further classified or transformed
		
		List<List<DynamicMatrixStatistics>> editDistanceMatrices= new ArrayList<List<DynamicMatrixStatistics>>();
		List<DynamicMatrixStatistics> editDistanceMatrices_subList = new ArrayList<DynamicMatrixStatistics>();
		
		List<List<DynamicMatrixStatistics>> globalAlignment= new ArrayList<List<DynamicMatrixStatistics>>();
		List<DynamicMatrixStatistics> globalAlignment_subList = new ArrayList<DynamicMatrixStatistics>();
        
		for(int i = 0; i<cpcList.size(); i++)
		{ 
			
			editDistanceMatrices_subList.clear();
			globalAlignment_subList.clear();
//			String alpha = Csv_Writer.getStringRepresentation(cpcList.get(i).abstrcationInstances);
			for(int j = 0; j<psList.size(); j++)
			{
//				String beta = Csv_Writer.getStringRepresentation(psList.get(j).patternReprsentation);
//				System.out.println(alpha+"    "+ beta);
				
//				int editDistanceMatrix[][]= PatternEditDistance.editDP(psList.get(j).patternReprsentation, cpcList.get(i).abstrcationInstances); // try to see how far the potential dominant pattern is from the dominant pattern
//				editDistanceMatrices_subList.add(new DynamicMatrixStatistics(cpcList.get(i).abstrcationInstances, psList.get(j).patternReprsentation, psList.get(j).patternIndices, editDistanceMatrix, editDistanceMatrix[editDistanceMatrix.length-1][editDistanceMatrix[editDistanceMatrix.length-1].length-1]));
				
//				System.out.println("Patterns distance is "+ dynamicMatrix[cpcList.get(i).abstrcationInstances.size()][psList.get(j).patternReprsentation.size()]);   // printing the cost 
//		        System.out.println(dynamicMatrix[dynamicMatrix.length-1][dynamicMatrix[dynamicMatrix.length-1].length-1]);   // printing the cost 
				
				float seqAlignmentMatrix[][] = PatternAlignment.sequenceAlignment(psList.get(j).patternReprsentation, cpcList.get(i).abstrcationInstances);
				globalAlignment_subList.add(new DynamicMatrixStatistics(cpcList.get(i).abstrcationInstances, psList.get(j).patternReprsentation, psList.get(j).patternIndices, seqAlignmentMatrix, seqAlignmentMatrix[seqAlignmentMatrix.length-1][seqAlignmentMatrix[seqAlignmentMatrix.length-1].length-1]));
				
//				System.out.println("Patterns distance is "+ seqAlignmentMatrix[cpcList.get(i).abstrcationInstances.size()][psList.get(j).patternReprsentation.size()]);   // printing the cost 
//		        System.out.println(seqAlignmentMatrix[seqAlignmentMatrix.length-1][seqAlignmentMatrix[seqAlignmentMatrix.length-1].length-1]);   // printing the cost 
			}	
			
//			editDistanceMatrices.add(new ArrayList<DynamicMatrixStatistics>(editDistanceMatrices_subList));
			globalAlignment.add(new ArrayList<DynamicMatrixStatistics>(globalAlignment_subList));
			
		}
		
		return PatternClassification.patAlignment(globalAlignment);  // returning wanted and unwanted indices to Main_Class
		
	}
	
	// pattern alignment -------------------------------pattern alignment ----------------------------pattern alignment 
	
	public static List<PatternAlignmentResult> patAlignment(List<List<DynamicMatrixStatistics>> globalAlignment)
	{
		List<List<PatternAlignmentResult>> out_PAR = new LinkedList<List<PatternAlignmentResult>>();
		int dominanPatternSegmentSize = 0;
		if(globalAlignment.size() >1)
		{
			List<DynamicMatrixStatistics> updated_clusters = new ArrayList<DynamicMatrixStatistics>(); 
			int count = globalAlignment.size();
			List<DynamicMatrixStatistics> subLists = new ArrayList<DynamicMatrixStatistics>();
			for(int l = 0; l < globalAlignment.get(0).size(); l++)
			{
			   int counter = 0;
			   subLists.clear();
			   while(count > counter)
			   {
				  subLists.add(globalAlignment.get(counter).get(l));
				  counter++;
			   }
			   updated_clusters.addAll(new ArrayList<DynamicMatrixStatistics>(getMaximumAlignmentMatrix(subLists)));
			}
		
			for(int k = 0; k<updated_clusters.size(); k++)
			{
				//System.out.println(Csv_Writer.getStringRepresentation(updated_clusters.get(k).dominantPattern) +"       "+ Csv_Writer.getStringRepresentation(updated_clusters.get(k).potentialDominantPattern));
				//System.out.println("Pattern alignment score is "+ updated_clusters.get(k).cost_f);
//				for (float[] x : updated_clusters.get(k).dynamicMatrix_f)
//		        {
//		           for(float y : x)
//		           {
//		                System.out.print(y + " ");
//		           }
//		           System.out.println();
//		        }
				
				out_PAR.add(PatternAlignment.shortestPathGraphCreation(updated_clusters.get(k).dynamicMatrix_f, updated_clusters.get(k).potentialDominantPattern, updated_clusters.get(k).dominantPattern, updated_clusters.get(k).crossponding_Rows));
				
				if(updated_clusters.get(k).dominantPattern.size() > dominanPatternSegmentSize)
				 dominanPatternSegmentSize = updated_clusters.get(k).dominantPattern.size();
			}
		}
		else
		{
			for(List<DynamicMatrixStatistics> list : globalAlignment)
			{
				//System.out.println("------------------------------------- pattern alignments--------------------------------------");
				for(int k = 0; k<list.size(); k++)
				{
//					System.out.println(Csv_Writer.getStringRepresentation(list.get(k).dominantPattern) +"       "+ Csv_Writer.getStringRepresentation(list.get(k).potentialDominantPattern));
//					System.out.println("Pattern alignment score is "+ list.get(k).cost_f);
//					for (float[] x : list.get(k).dynamicMatrix_f)
//			        {
//			           for (float y : x)
//			           {
//			                System.out.print(y + " ");
//			           }
//			           System.out.println();
//			        }
					
					out_PAR.add(PatternAlignment.shortestPathGraphCreation(list.get(k).dynamicMatrix_f, list.get(k).potentialDominantPattern, list.get(k).dominantPattern, list.get(k).crossponding_Rows));
					
					dominanPatternSegmentSize = list.get(k).dominantPattern.size();
		       	}
			}
		}
		
		String write_DM_output_results_aligments = "C:/Users/M.Hameed/Desktop/AlignmentTraces.csv";
		try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File(write_DM_output_results_aligments)),"UTF-8"))
		{
			 
		    CsvWriterSettings settings = new CsvWriterSettings();
		    settings.setSkipEmptyLines(false);
			
			CsvWriter writer = new CsvWriter(outputWriter, settings);
			   
			int counter = 1;  // for writing on disk 
			for(List<PatternAlignmentResult> par_list : out_PAR)	
			{
				writer.writeRow();
				writer.writeRow(" ::::::::::::::::::::::::::::::::::::::::::::::: " +"    Record :: " + counter+"    :::::::::::::::::::::::::::::::::::::::::::::::: ");
				writer.writeRow();
				for(PatternAlignmentResult par: par_list)
				{
					writer.writeRow(par.pairs_of_patterns + " :: " + par.pairs_of_indices+ " :: " + par.row_indices+ " ::" +par.pairs_score);
				}
			    counter++;
			}
			writer.close();
	     }
	     catch (IOException e) {
	        // handle exception
	     }	
		
		return PatternClassification.patternAlignmentPrePro(out_PAR, dominanPatternSegmentSize); // initiate pre-processing of potential dominant patterns
	}
    
	// pattern alignment preprocessor -------------------------------pattern alignment preprocessor----------------------------pattern alignment preprocessor
	
	public static List<PatternAlignmentResult> patternAlignmentPrePro(List<List<PatternAlignmentResult>> out_PAR, int dominanPatternSegmentSize)
	{
		List<PatternAlignmentResult> patterns_for_classification = new ArrayList<PatternAlignmentResult>();
		 
		for(List<PatternAlignmentResult> par_list : out_PAR)	
		{
			for(PatternAlignmentResult par: par_list)
			{
				if(par.pairs_of_patterns.size() > dominanPatternSegmentSize)
				{
					// add pre-processed patterns to the list of ready for classification patterns
					patterns_for_classification.add(PatternAlignmentPreprocessor.repairPatterns(par)); 
				}
				else
				{
					//START----------------- Calculating distance after removing delimiters -----------------  added on 11 November 2022
//					List<Pair<List<Object>, List<Object>>> pairs_Pat = new ArrayList<Pair<List<Object>,List<Object>>>();
//					for(Pair<List<Object>, List<Object>> p: par.pairs_of_patterns )
//					{
//						if( !(p.getKey().toString().contains(CANDIDATE_DELIMITER_CLASS.toString()) && p.getValue().toString().contains(CANDIDATE_DELIMITER_CLASS.toString())) )
//							pairs_Pat.add(p);
//					}
//					par.pairs_score = PatternAlignmentScore.parseAlignmentSegments(pairs_Pat);
					//END----------------- Calculating distance after removing delimiters -----------------  added on 11 November 2022
					
					patterns_for_classification.add(par);
				}
			}
		}
		
        // the difference between here and "AlignmentTraces.csv" printing is List<List<PatternAlignmentResult>> and List<PatternAlignmentResult>
		// in the former one we need to access parts of the list one by one that is why we used List<List>>, however we can change "AlignmentTraces.csv" also to  List<PatternAlignmentResult>
		String write_DM_output_results_aligments = "C:/Users/M.Hameed/Desktop/AlignmentTracesPreProcessed.csv";
		try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File(write_DM_output_results_aligments)),"UTF-8"))
		{
			 
		    CsvWriterSettings settings = new CsvWriterSettings();
		    settings.setSkipEmptyLines(false);
			
			CsvWriter writer = new CsvWriter(outputWriter, settings);
			   
			int counter = 1;  // for writing on disk 
			for(PatternAlignmentResult par_list : patterns_for_classification)	
			{
				if(par_list.pairs_score <= 0.30)
				{
					writer.writeRow();
					writer.writeRow(" ::::::::::::::::::::::::::::::::::::::::::::::: " +"    Record :: " + counter+"    :::::::::::::::::::::::::::::::::::::::::::::::: ");
					writer.writeRow();
					
					writer.writeRow(par_list.pairs_of_patterns + " :: " + par_list.pairs_of_indices+ " :: " + par_list.row_indices+ " ::" +par_list.pairs_score);
				}
			    
				
			    counter++;
			}
			writer.close();
	     }
	     catch (IOException e) {
	        // handle exception
	     }	
		
		
//		System.out.println("------------------------------------- pre-processed--------------------------------------");
		
//		for(PatternAlignmentResult result: patterns_for_classification)
//		{
//			if(result.pairs_score <= 0.30)
//			 System.out.println(result.pairs_of_patterns+"     "+result.pairs_of_indices+"    "+"    "+result.row_indices + "     "+result.pairs_score);
//		}
		
		return patterns_for_classification;
	}
	
	public static List<DynamicMatrixStatistics> getMaximumAlignmentMatrix(List<DynamicMatrixStatistics> subLists) 
	{
		float min = subLists.get(0).cost_f;    // Initialize 
		List<DynamicMatrixStatistics> dmcList= new ArrayList<DynamicMatrixStatistics>();
		dmcList.add(subLists.get(0));   // Initialize 
		for(DynamicMatrixStatistics dmc: subLists)
		{
			if(dmc.cost_f < min)
			{
				min = dmc.cost_f;
				dmcList.set(0, dmc);
			}
		}
		return dmcList;
	}
	
}
