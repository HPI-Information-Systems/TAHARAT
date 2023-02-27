import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.sql.PooledConnection;

import java.util.Set;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class GroundTruthComparison {
	
	public void parseGroundTruth (String groundTruth, List<Integer> wellFormed, List<Integer> wanted, List<Integer> unwanted)
	{
		 Collections.sort(wellFormed);
		 Collections.sort(wanted);
		 Collections.sort(unwanted);
		 
//	     System.out.println("-------------------------");
//		 System.out.println(wellFormed);
//		 System.out.println("-------------------------");
//		 System.out.println(wanted);
//		 System.out.println("-------------------------");
//		 System.out.println(unwanted);
		 
		   CsvParserSettings annotation_settings = new CsvParserSettings();
		   annotation_settings.detectFormatAutomatically();
		   
		   annotation_settings.setIgnoreLeadingWhitespaces(false);
		   annotation_settings.setIgnoreTrailingWhitespaces(false);
		   annotation_settings.setKeepQuotes(false);
		   annotation_settings.setQuoteDetectionEnabled(true);
		   annotation_settings.setSkipEmptyLines(false);
		   annotation_settings.setCommentProcessingEnabled(false);
		   annotation_settings.setMaxCharsPerColumn(1000000);
		   annotation_settings.setDelimiterDetectionEnabled(true, ',');  
		   
		   CsvParser parser = new CsvParser(annotation_settings);
		   
		  
		   List<Record> allRecords = parser.parseAllRecords(new File(groundTruth));
		   
		   List<Integer> illFormed_annoatated_data = new ArrayList<Integer>();
		   List<Integer> wellFormed_annoatated_data = new ArrayList<Integer>();
		   
		   for(Record record : allRecords){
			 
			   String[] values = record.getValues();
			   if(!(values.toString().isEmpty()) && values[0].toString().matches("ill formed rows indices"))
				{
				   String[] arr = values[1].split(",");
				   for(String in : arr)
				   {
					   illFormed_annoatated_data.add(Integer.parseInt(in)); 
				   }
				   
				}
			   else if(!(values.toString().isEmpty()) && values[0].toString().matches("well formed rows indices"))
				{
				   String[] arr = values[1].split(",");
				   for(String in : arr)
				   {
					   wellFormed_annoatated_data.add(Integer.parseInt(in)); 
				   }
				  
				}
			  
		   }
		   
	
		//System.out.println("Ill_Formed_GT  "+illFormed_annoatated_data);
		//System.out.println("Well_Formed_GT  "+wellFormed_annoatated_data);
		// end ground truth data reading ..........................
		
	}
	
	public List<List<Object>> confusionMatrix(List<Integer> possible_Outlier_Rows_indicies_LIST, List<Integer> parsed_rows_indicies_LIST, List<Integer> illFormed_annoatated_data,  List<Integer> wellFormed_annoatated_data)
	{
		       
				float true_Positive = 0;
				float false_Negative = 0;
				float false_Positive = 0;
				float true_Negative = 0;
				List<Integer> false_Negative_LIST = new ArrayList<Integer>();
				List<Integer> false_Positive_LIST = new ArrayList<Integer>();
				List<List<Object>> combined_Parsed_and_Outlier_Incdicies_LIST = new ArrayList<List<Object>>();
				
				for(Integer in: illFormed_annoatated_data)
				{
					if(possible_Outlier_Rows_indicies_LIST.contains(in))
					{
						true_Positive++;
					}
					else if(parsed_rows_indicies_LIST.contains(in))
					{
						false_Negative++;
						false_Negative_LIST.add(in);
					}
				}
				
				for(Integer in: wellFormed_annoatated_data)
				{
					if(possible_Outlier_Rows_indicies_LIST.contains(in))
					{
						false_Positive++;
						false_Positive_LIST.add(in);
					}
					else if(parsed_rows_indicies_LIST.contains(in))
					{
						true_Negative++;
					}
				}
				
				float precision = 0;
				float recall = 0;
				float fMeasure = 0;
				DecimalFormat df = new DecimalFormat("#.#####");
				
				precision = true_Positive/ (true_Positive + false_Positive);
				recall = true_Positive / (true_Positive+ false_Negative);
				fMeasure = 2*precision*recall/(precision+recall);
				
				if(Float.isNaN(precision))
					precision=0;
				if(Float.isNaN(recall))
					recall=0;
				if(Float.isNaN(fMeasure))
					fMeasure=0;
				
				precision = Float.valueOf(df.format(precision));
				recall = Float.valueOf(df.format(recall));
				fMeasure = Float.valueOf(df.format(fMeasure));
				
				
				System.out.println("Precision     " + precision);
				System.out.println("Recall     "+ recall);
				System.out.println("F-1     "+ fMeasure);
				
				// write parsed and possible outlier rows combined for analysis
				List parsed_rows_SUBLIST = new ArrayList<>();
				parsed_rows_SUBLIST.add("Parsed Rows");
				parsed_rows_SUBLIST.add(parsed_rows_indicies_LIST);
				parsed_rows_SUBLIST.add(parsed_rows_indicies_LIST.size());
				combined_Parsed_and_Outlier_Incdicies_LIST.add(parsed_rows_SUBLIST);
				
				
				List outlier_rows_SUBLIST = new ArrayList<>();
				outlier_rows_SUBLIST.add("Possible Outliers");
				outlier_rows_SUBLIST.add(possible_Outlier_Rows_indicies_LIST);
				outlier_rows_SUBLIST.add(possible_Outlier_Rows_indicies_LIST.size());
				combined_Parsed_and_Outlier_Incdicies_LIST.add(outlier_rows_SUBLIST);
				
				List false_negative_SUBLIST = new ArrayList<>();
				false_negative_SUBLIST.add("False Negatives");
				false_negative_SUBLIST.add(false_Negative_LIST);
				false_negative_SUBLIST.add(false_Negative_LIST.size());
				combined_Parsed_and_Outlier_Incdicies_LIST.add(false_negative_SUBLIST);
				
				
				List false_positive_SUBLIST = new ArrayList<>();
				false_positive_SUBLIST.add("False Positives");
				false_positive_SUBLIST.add(false_Positive_LIST);
				false_positive_SUBLIST.add(false_Positive_LIST.size());
				combined_Parsed_and_Outlier_Incdicies_LIST.add(false_positive_SUBLIST);
				
				
				List confiusion_matrix_result = new ArrayList<>();
				confiusion_matrix_result.add("Precision   "+precision);
				confiusion_matrix_result.add("Recall   "+recall);
				confiusion_matrix_result.add("F Measure   "+fMeasure);
				combined_Parsed_and_Outlier_Incdicies_LIST.add(confiusion_matrix_result);
				
				return combined_Parsed_and_Outlier_Incdicies_LIST;
	}
}
