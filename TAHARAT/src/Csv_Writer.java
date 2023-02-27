import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import abstractions.Candidate_Delimiter_Class;
import abstractions.Line_Break_Class;
import abstractions.Padded_Class;

public class Csv_Writer {

	
	public static void write(List<ComparePatternCombinations> output_patterns_input_data)
	{
		
		 List<List<Object>> printingPatterns = new ArrayList<List<Object>>();
		 List l = new ArrayList<>();
		 l.add("Patterns");
		 l.add("Indices");
		 printingPatterns.add(l);
		 for(int i = 0; i<output_patterns_input_data.size(); i++)
		 {
			 List<Object> l_Obj = new ArrayList<Object>();
			 l_Obj.add(Csv_Writer.getStringRepresentation(output_patterns_input_data.get(i).abstrcationInstances));
			 Collections.sort(output_patterns_input_data.get(i).intersection);
			 l_Obj.add(output_patterns_input_data.get(i).intersection.toString().replaceAll("[\\[ \\]\\s]", "")); 
			 printingPatterns.add(l_Obj);
		 }
		 
		 Csv_Writer.writeCSV(printingPatterns);
	}
	
	public static String getStringRepresentation(List<List<Object>> abstrcationInstances) 
	 {
	 		StringBuilder str_builder_getStringRepresentation = new StringBuilder();
	 		for(List<Object> list_obj: abstrcationInstances)
	 			{
		 			for(Object obj : list_obj )
		 			{
		 				str_builder_getStringRepresentation.append(obj.toString());
		 			}
	 			}
	 			
	 		return str_builder_getStringRepresentation.toString();
	 	
	 }
	 
	 
	public static void writeCSV (List<List<Object>> printingPatterns)
	{
		String write_PLI_output_results = "C:/Users/M.Hameed/Desktop/RowPatterns.csv";
		try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File(write_PLI_output_results)),"UTF-8"))
		{
			 
		    CsvWriterSettings settings = new CsvWriterSettings();
		    settings.setSkipEmptyLines(false);
			
			CsvWriter writer = new CsvWriter(outputWriter, settings);
			   
			for (int i = 0; i < printingPatterns.size(); i++) 
			{
			    writer.writeRow(printingPatterns.get(i));
			}
			writer.close();
	     }
	     catch (IOException e) {
	        // handle exception
	     }	
	 }
	
	public static void writeOutliers (List<Integer> outliers)
	{
		 List<List<Object>> printingOutliers = new ArrayList<List<Object>>();
		 List l = new ArrayList<>();
		 l.add("Outliers");
		 printingOutliers.add(l);
		 List<Object> l_Obj = new ArrayList<Object>();
		 Collections.sort(outliers);
		 l_Obj.add(outliers.toString().replaceAll("[\\[ \\]\\s]", ""));
		 printingOutliers.add(l_Obj);
		 String write_PLI_output_results = "C:/Users/M.Hameed/Desktop/Outliers.csv";
		 try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File(write_PLI_output_results)),"UTF-8"))
		 {
			 
		    CsvWriterSettings settings = new CsvWriterSettings();
		    settings.setSkipEmptyLines(false);
			
		    CsvWriter writer = new CsvWriter(outputWriter, settings);
			  
		    for (int i = 0; i < printingOutliers.size(); i++) 
			{
			    writer.writeRow(printingOutliers.get(i));
			}
			
			writer.close();
	      }
	      catch (IOException e) {
	        // handle exception
	     }	
	 }
	
	
	public static void cleanCSV (Map<Integer, String[]> printingPatterns)
	{
		String _results = "C:/Users/M.Hameed/Desktop/Cleaned_Test.csv";
		
		try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File(_results)),"UTF-8"))
		{
			for (Entry<Integer, String[]> entry : printingPatterns.entrySet()) 
			{
				String[] testing = entry.getValue();
				List<Object> list = new ArrayList<Object>();
				for (int j = 0; j < testing.length; j++) 
				{
					if (testing[j] == null) // for null values
					{
						list.add("");
					}
					else if (!(testing[j].toString().equals(Main_Class.univocityDetetced_Line)) &&!(testing[j].toString().equals(Main_Class.univocityDetetced_delimiter))
							&& !(testing[j].toString().equals(new Padded_Class().toString()))) {
						list.add(testing[j]);
					}
				}
				
				for(int i = 0; i<list.size(); i++)
				{
					outputWriter.write(list.get(i)+"");
					if(i !=list.size()-1)
					  outputWriter.write(Main_Class.univocityDetetced_delimiter);
					if(i ==list.size()-1)
					  outputWriter.write(Main_Class.univocityDetetced_Line);
				}
			}
			outputWriter.flush();
			outputWriter.close();
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// ------------------------------ univocity writer with more features but dependent --------------------------------
		
//		try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File(_results)),"UTF-8"))
//		{
//			 
//		    CsvWriterSettings settings = new CsvWriterSettings();
//	
//		    //settings.getFormat().setQuote('\0');
//		    //settings.setQuoteEscapingEnabled(false);
//		 
//		   
//			CsvWriter writer = new CsvWriter(outputWriter, settings);
//					
//			for (Entry<Integer, String[]> entry: printingPatterns.entrySet()) 
//			{
//				List<Object> list = new ArrayList<Object>();
//				String[] testing = entry.getValue();
//
//				for (int j = 0; j < testing.length; j++) 
//				{
//					if(testing[j] == null) // for null values
//					{
//						list.add(testing[j]);
//					}
//					// only printing column values, as parser will add the delimiter and line breaks itself
//					else if (!(testing[j].toString().equals(Main_Class.univocityDetetced_Line)) && !(testing[j].toString().equals(Main_Class.univocityDetetced_delimiter))
//							&& !(testing[j].toString().equals(new Padded_Class().toString())) )  
//					{
//						list.add(testing[j]);
//					}
//				}
//				
//				writer.writeRow(list);
//			}
//			writer.close();
//	     }
//	     catch (IOException e) {
//	        // handle exception
//	     }	
	 }
	
	
}
