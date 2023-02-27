
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

public class PatternStatistics {

	public List<List<Object>> patternReprsentation;
	public List<Integer> patternIndices;
	public int size;
	
	public PatternStatistics(List<List<Object>> abstrcationInstances, List<Integer> patternIndices, int size) {
		super();
		this.patternReprsentation = abstrcationInstances;
		this.patternIndices = patternIndices;
		this.size = size;
	}


	public static void write(List<PatternStatistics> output_patterns_input_data)
	{
		
		 List<List<Object>> printingPatterns = new ArrayList<List<Object>>();
		 List l = new ArrayList<>();
		 l.add("Patterns");
		 l.add("Indices");
		 printingPatterns.add(l);
		 for(int i = 0; i<output_patterns_input_data.size(); i++)
		 {
			 List<Object> l_Obj = new ArrayList<Object>();
			 l_Obj.add(Csv_Writer.getStringRepresentation(output_patterns_input_data.get(i).patternReprsentation));
			 Collections.sort(output_patterns_input_data.get(i).patternIndices);
			 l_Obj.add((output_patterns_input_data.get(i).patternIndices.toString().replaceAll("[\\[ \\]\\s]", ""))); 
			 printingPatterns.add(l_Obj);
		 }
		 
		 PatternStatistics.writeCSV(printingPatterns);
	}
	
	public static void writeCSV (List<List<Object>> printingPatterns)
	{
		String write_PLI_output_results = "C:/Users/M.Hameed/Desktop/IncrementalRowPatterns.csv";
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
	
	@Override
	public String toString() {
		return "PatternStatistics [patternReprsentation=" + patternReprsentation + ", patternIndices=" + patternIndices
				+ ", size=" + size + "]";
	}
}
