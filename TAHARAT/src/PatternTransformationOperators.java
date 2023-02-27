import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.spec.PSource;
import javax.swing.RepaintManager;
import javax.swing.text.Position;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.util.concurrent.CycleDetectingLockFactory.PotentialDeadlockException;
import com.sun.javafx.scene.EnteredExitedHandler;

import abstractions.Arithmetic_Oprt_Class;
import abstractions.Brackets_Class;
import abstractions.Candidate_Delimiter_Class;
import abstractions.Digit_Class;
import abstractions.EmptyValues_Class;
import abstractions.Full_Text_Class;
import abstractions.Gap_Class;
import abstractions.Line_Break_Class;
import abstractions.Lower_Letter_Class;
import abstractions.MissingValues_Class;
import abstractions.Number_Class;
import abstractions.Padded_Class;
import abstractions.Quotation_Class;
import abstractions.Sequence_Digit_Class;
import abstractions.Sequence_LowerLetter_Class;
import abstractions.Sequence_UpperLetter_Class;
import abstractions.Space_Class;
import abstractions.Symbol_Class;
import abstractions.Text_Class;
import abstractions.Upper_Letter_Class;
import abstractions.WhiteSpace_Class;
import algebra.Drop;
import algebra.Merge;
import algebra.Extract;
import javafx.util.Pair;

public class PatternTransformationOperators {


	private static final Padded_Class PADDED_CLASS = new Padded_Class();
	private static final Gap_Class GAP_CLASS = new Gap_Class();
	private static final Candidate_Delimiter_Class CANDIDATE_DELIMITER_CLASS = new Candidate_Delimiter_Class();
	
	private static Map<Integer, String[]> transformedd = new LinkedHashMap<Integer, String[]>();
	
	public Map<Integer, String[]> cleanErrors(Map<Integer, String[]> valuesMap, TransformationInputBlocks patternBlocks) 
	{
		 
		 transformedd = valuesMap;
//		 for(Entry<Integer, String[]> mapValues: valuesMap.entrySet())
//		 {
//			 for(String s: mapValues.getValue())
//				 System.out.println("new values  "+s);
//		 }
		 
		 for(int i = 0; i < patternBlocks.blocks.size(); i++)
		 {
			 if(patternBlocks.blocks.get(i).getKey().toString().contains("Match"))
			 {
				 // skip blocks -- no transformation required  -- remove empty blocks -- may be in pre-processing steps (before sending patterns in this class)
			 }
			 else if(patternBlocks.blocks.get(i).getKey().toString().contains("Mismatch"))
			 {
				 PatternTransformationOperators.diagonal_Transform(transformedd, patternBlocks.blocks.get(i).getValue(), patternBlocks.positions.get(i) );
			 }
			 else if(patternBlocks.blocks.get(i).getKey().toString().contains("Horizontal"))
			 {
				PatternTransformationOperators.horizontal_Transform(transformedd, patternBlocks.blocks.get(i).getValue(), patternBlocks.positions.get(i) );
			 }
			 else if(patternBlocks.blocks.get(i).getKey().toString().contains("Vertical"))
			 { 
				 PatternTransformationOperators.vertical_Transform(transformedd, patternBlocks.blocks.get(i).getValue(), patternBlocks.positions.get(i) );
				
//				 for(Entry<Integer, String[]> mapValues: transformedd.entrySet())
//				 {
//					 String[] test = mapValues.getValue(); 	
//					 System.out.println("Transformed Row");
//					 for(int j = 0; j <test.length;j++)
//					 {
//						 System.out.print(test[j]);
//					 }
//				 }
			 }
		 }
		
		return transformedd;
	}

	public static Map<Integer, String[]> diagonal_Transform(Map<Integer, String[]> valuesMap, Pair<List<Object>, List<Object>> pair, Pair<Integer, List<Integer>> postion_pair)
	{
		// apply transformation on pattern and then to the corresponding lines
		
		// before sending pattern for transformation, save it for later use
		List<Object> readyto_Transform = new ArrayList<Object>(pair.getValue());
		
		Extract split_Object = new Extract();
		Map<Integer, Object> split_Output = split_Object.apply(pair.getKey(), pair.getValue());
		
//		if(split_Output == null || split_Output.isEmpty())
//		    System.out.println("No transformation");
//		else
//			System.out.println("Transformations"+ split_Output);
		
		for(Entry<Integer, String[]> mapValues: valuesMap.entrySet())
		{
			if(split_Output == null || split_Output.isEmpty())
			{
//				System.out.println("No transformation");
				transformedd.put(mapValues.getKey(), mapValues.getValue());
			}
			else
			{
//				System.out.println("Transformations "+ split_Output);
				String[] test = mapValues.getValue();
				List<String> reparied_new = new ArrayList<String>();
				for (int j = 0; j < test.length; j++)
				{
				  reparied_new.add(test[j]);
				}
				
				//================ applying transformation on real values START==================================
			    
			    String toBeFixed = reparied_new.get(postion_pair.getKey());
			    StringBuilder sb = new StringBuilder();
			    
			   // dropping appended metadata or incorrect data part from real values 
			   
			    // converting map of entries into list (these indices we drop from the real values)
			    List<Integer> deleteEntries = new ArrayList<Integer>();
			    for(Entry<Integer, Object> entryVal: split_Output.entrySet())
			    {
			    	deleteEntries.add(entryVal.getKey());
			    }
		        
			    // check if pattern only contains characters  (if pattern contains abstraction then we have to find the right place to drop)
			    boolean flag_for_character_check = true;
			    for(int i = 0; i <readyto_Transform.size(); i++)
			    {
			    	if(!(readyto_Transform.get(i) instanceof Character))
			    	{
			    		flag_for_character_check = false;
			    	}
			    }
//			    System.out.println(flag_for_character_check+ " check for characters  "+ readyto_Transform);
			    // if ready to transform only contains characters 
			    if(flag_for_character_check == true)
			    {
//			    	System.out.println("Only character class!!!!");
			    	for(int j = 0; j < toBeFixed.length(); j++)
				    {
				    	if(!(deleteEntries.contains(j)))
						    sb.append(toBeFixed.charAt(j));
				    }
			    }
			    else // if ready_to_transform contains abstraction(s) -- find the right place to drop 
			    {
//			    	System.out.println("with abstraction classes!!!!");
			    	
			    	List<Object> string_TO_object = new ArrayList<Object>(); 
			    	for(int k = 0; k<toBeFixed.length(); k++) 
			    	{
			    		string_TO_object.add(toBeFixed.charAt(k));   // convert string into list of objects to find character positions
			    	}
//			    	System.out.println("Original string   "+string_TO_object);
			    	
			    	// send list of objects to GetAbstractioIndex to receive drop indices 
			    	GetAbstractionIndex getIndex_Object = new GetAbstractionIndex();

			    	for(Entry<Integer,List<Integer>> entry : getIndex_Object.abstractionIndex(readyto_Transform, new ArrayList<Object>(string_TO_object)).entrySet())
			    	{
//			    		System.out.println(entry.getKey()+"  ------------   "+ entry.getValue());
			    		if(deleteEntries.contains(entry.getKey()))
			    		{
			    			for(int m = 0; m<entry.getValue().size();m++)
			    			{
			    				string_TO_object.set(entry.getValue().get(m), PADDED_CLASS);
			    			}
			    		}
			    	}
			    	
//			    	System.out.println("  String to Object Original  "+ string_TO_object);
			        
			    	//remove padded values
			    	string_TO_object.removeIf( obj -> obj.equals(PADDED_CLASS) );

//			    	System.out.println("  String to Object after removing padded values  "+ string_TO_object);

			    	//converting List<object> to string
			    	for(int i =0; i<string_TO_object.size(); i++)
			    	{
			    		sb.append(string_TO_object.get(i));
			    	}
			    }
			    
//			    System.out.println(" cleaned string  "+ sb);
			    
				//================== applying transformation on real values END==================================
				
		        //=======Inserting the split results into the original data row			    
		        reparied_new.set(postion_pair.getKey(), sb.toString());
		    
				String[] stringArr = new String[reparied_new.size()];
				stringArr = reparied_new.toArray(new String[0]);

				transformedd.put(mapValues.getKey(), stringArr);
			}
			
		}
		
		// when applying transformation on real values, even if split results have no transformation -- remove special characters from potential dominant that are not present in dominant pattern
		// also drop empty columns -- possible in match block -- Mendeley files
		
		//======================================================================= OLD CODE (DIAOGNAL) ================================================//
				//``````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````//
		
//		 for(Entry<Integer, String[]> mapValues: valuesMap.entrySet())
//		 {
//			 String[] test = mapValues.getValue(); 	
//			 List<String> reparied_new = new ArrayList<String>();
//				
//				for (int j = 0; j < test.length; j++)
//				{
//					reparied_new.add(test[j]);
//				}
//				
//				// extract original mismatch string and send it to the split method for transformation
//				String toBeFixed = reparied_new.get(postion_pair.getKey());
//				List<Object> list_for_toBeFixed_string = new ArrayList<Object>();
//				
//				if(toBeFixed != null)
//				{
//					for(int i = 0; i<toBeFixed.length(); i++)
//					{
//	 					list_for_toBeFixed_string.add(toBeFixed.charAt(i));
//					}
//				}
// 				
////				System.out.println("To be Fixed   "+toBeFixed);
////				System.out.println("To be Fixed   "+list_for_toBeFixed_string);
//				
//				
////				Split split_Object = new Split();
////			    List<Object> split_Output = split_Object.apply(pair.getKey(), list_for_toBeFixed_string); // Sending original value for transformation
////			    
////			    // convert List<Object> into string
////			    StringBuilder sb = new StringBuilder();
////			    
////			    System.out.println("To be Fixed   "+toBeFixed);
////			    System.out.println("split_Output   "+split_Output);
////			    
////			    for( int i = 0; i<split_Output.size(); i++)
////			    {
////			    	//sb.append(split_Output.get(i));  // !error! -- split_Output.get(i) extracts value form the potential object -- extract value form the original data line 
////			    	if(sb.length() != 0)
////			    	  sb.append(toBeFixed.charAt(i));       // fixed above error
////			    }
//			    
//                //------------------Inserting the split results into the original data row------------------	
//				StringBuilder sb = new StringBuilder();
//				Split split_Object = new Split();
//				List<Object> split_Output = split_Object.apply(pair.getKey(), list_for_toBeFixed_string);
////				System.out.println("split_Output   "+split_Output);
//				
//			    for (int i = 0; i < split_Output.size(); i++) 
//			    {
//				    sb.append(split_Output.get(i)); 
//			    }
//			 
//			    reparied_new.set(postion_pair.getKey(), sb.toString());
//				
//				String[] stringArr = new String[reparied_new.size()];
//				stringArr = reparied_new.toArray(new String[0]);
//
//				transformedd.put(mapValues.getKey(), stringArr);
//				
////				for(Entry<Integer, String[]> map: transformedd.entrySet())
////				 {
////					 String[] testing = map.getValue(); 	
////					 System.out.println("Transformed Row for Diagnol operations");
////					 for(int j = 0; j <testing.length;j++)
////					 {
////						 System.out.print(testing[j]);
////					 }
////				 }
//		 }
		
			return transformedd;
	}
	
	public static boolean noQuoteEnd(List<Object> input) // Check if ending part has quote , then probably no shift occurred
	{
		if(input.get(input.size()-1) == Main_Class.univocityDetetced_QUOTE || (input.get(input.size()-1) instanceof Character && (char)input.get(input.size()-1) == 34) )
			return true;
		else
		   return false;
	}

	public static Map<Integer, String[]> horizontal_Transform(Map<Integer, String[]> valuesMap, Pair<List<Object>, List<Object>> pair, Pair<Integer, List<Integer>> postion_pair)
	{
		
		// ==================== Use pair.getKey() for the right position to fix real values ==============
		
		//apply transformation on pattern and then the corresponding lines
		
		// ---------------data metadata case, when there is no quote characters involved ----------- 
		// when first entry does contain Quote character but no shifted values i.e., "alpha"Fiscal Year<DEL>Month<DEL>Total SSR<DEL>Internet SSR<DEL>Percentage

		if( !(pair.getValue().get(0).toString().contains(Main_Class.univocityDetetced_QUOTE)) || noQuoteEnd((List<Object>)pair.getValue().get(0)) )
		{
			
			List<Object> delete_metadata = new ArrayList<Object>(); // use this list to store indices for metadata parts
			
			int index = 0;
			
			for(int j = 0; j<pair.getValue().size(); j++)
			{
				if(pair.getValue().get(j).toString().contains(CANDIDATE_DELIMITER_CLASS.toString()))
				{
				    index = pair.getValue().indexOf(pair.getValue().get(j));
					break;
				}
			}
			
			//--------- drop operation(s) on pattern-level also apply on real data
			pair.getValue().subList(index, pair.getValue().size()).clear();  // drop metadata from start (index) to end (potential pattern size)
			List<Object> new_valueList =  new ArrayList<Object>((List<Object>) pair.getValue().get(0)) ; // extract list from <List<List<Object>>
			
			// before sending pattern for transformation, save it for later use // updated this code for the values with data/metadata on 2 January 2023
			 List<Object> readyto_Transform = new ArrayList<Object>((List<Object>) pair.getValue().get(0)) ;
			 
			//-------------drop metadata from the index it starts to the end
			for(int i = postion_pair.getKey()+1; i<postion_pair.getValue().get(postion_pair.getValue().size()-1)+1; i++)
			{
				delete_metadata.add(i);
			}
			
//			System.out.println("Drop After Metadata "+ delete_metadata);  // indices for actual strings while "split" return the indices inside the strings (objects)
			
			// ------------- after metadata values dropped, next, send remaining pattern to split operation ------------
			Extract split_Object = new Extract();
			Map<Integer, Object> split_Output = split_Object.apply(pair.getKey(), new_valueList); 
			
			// dropping metadata from real values and also apply split operation on real values ............
//			if(split_Output == null || split_Output.isEmpty())
//			    System.out.println("No transformation");
//			else
//				System.out.println("Transformations "+ split_Output);
			
			for(Entry<Integer, String[]> mapValues: valuesMap.entrySet())
			{
				if(split_Output == null || split_Output.isEmpty())
				{
//					System.out.println("No transformation");
					if(delete_metadata.isEmpty())
					{
						transformedd.put(mapValues.getKey(), mapValues.getValue());
					}
					else  // added this part on 3rd January 2023
					{
						String[] test = mapValues.getValue();
						List<String> reparied_new = new ArrayList<String>();
						for (int j = 0; j < test.length; j++)
						{
						  reparied_new.add(test[j]);
						}
						
					    // delete metadata part
						try {
							 reparied_new.subList((int)delete_metadata.get(0), (int)delete_metadata.get(delete_metadata.size()-1)+1).clear();  // +1 because sublist excludes the last item 
						}
					    catch(Exception ex)
					    {
					    	System.out.println("Exception  "+ ex);
					    }
					    					    
						String[] stringArr = new String[reparied_new.size()];
						stringArr = reparied_new.toArray(new String[0]);

						transformedd.put(mapValues.getKey(), stringArr);
					}
					
				}
				else
				{
//					System.out.println("Transformations "+ split_Output);
					String[] test = mapValues.getValue();
					List<String> reparied_new = new ArrayList<String>();
					for (int j = 0; j < test.length; j++)
					{
					  reparied_new.add(test[j]);
					}
					
					//================ applying transformation on real values START ====================
				    // first delete metadata part if any
				    reparied_new.subList((int)delete_metadata.get(0), (int)delete_metadata.get(delete_metadata.size()-1)+1).clear();  // +1 because sublist excludes the last item 
				    
				    String toBeFixed = reparied_new.get(postion_pair.getKey());
				    StringBuilder sb = new StringBuilder();
				    
				   // dropping appended metadata or incorrect data part from real values 
				   
				    // converting map of entries into list (these indices we drop from the real values)
				    List<Integer> deleteEntries = new ArrayList<Integer>();
				    for(Entry<Integer, Object> entryVal: split_Output.entrySet())
				    {
				    	deleteEntries.add(entryVal.getKey());
				    }
			        
//				    System.out.println("deleteEntries========================================deleteEntries "+deleteEntries);
				    
				    // check if pattern only contains characters  (if pattern contains abstraction then we have to find the right place to drop)
				    boolean flag_for_character_check = true;
				    for(int i = 0; i <readyto_Transform.size(); i++)
				    {
				    	if(!(readyto_Transform.get(i) instanceof Character))
				    	{
				    		flag_for_character_check = false;
				    	}
				    }
//				    System.out.println(flag_for_character_check+ "   "+ readyto_Transform);
				    // if ready to transform only contains characters 
				    if(flag_for_character_check == true)
				    {
//				    	System.out.println("Only character class!!!!");
				    	for(int j = 0; j < toBeFixed.length(); j++)
					    {
					    	if(!(deleteEntries.contains(j)))
							    sb.append(toBeFixed.charAt(j));
					    }
				    }
				    else // if ready_to_transform contains abstraction(s) -- find the right place to drop 
				    {
//				    	System.out.println("with abstraction classes!!!!");
				    	
				    	List<Object> string_TO_object = new ArrayList<Object>(); 
				    	for(int k = 0; k<toBeFixed.length(); k++) 
				    	{
				    		string_TO_object.add(toBeFixed.charAt(k));   // convert string into list of objects to find character positions
				    	}
//				    	System.out.println("Original string   "+string_TO_object);
				    	
				    	// send list of objects to GetAbstractioIndex to receive drop indices 
				    	GetAbstractionIndex getIndex_Object = new GetAbstractionIndex();
				    	
				    	for(Entry<Integer,List<Integer>> entry : getIndex_Object.abstractionIndex(new ArrayList<Object>(readyto_Transform), new ArrayList<Object>(string_TO_object)).entrySet())
				    	{
//				    		System.out.println(entry.getKey()+"  ------------   "+ entry.getValue());
				    		if(deleteEntries.contains(entry.getKey()))
				    		{
				    			for(int m = 0; m<entry.getValue().size();m++)
				    			{
				    				string_TO_object.set(entry.getValue().get(m), PADDED_CLASS);
				    			}
				    		}
				    	}
				    	
//				    	System.out.println("  String to Object Original  "+ string_TO_object);
				        
				    	//remove padded values
				    	string_TO_object.removeIf( obj -> obj.equals(PADDED_CLASS) );

//				    	System.out.println("  String to Object after removing padded values  "+ string_TO_object);

				    	//converting List<object> to string
				    	for(int i =0; i<string_TO_object.size(); i++)
				    	{
				    		sb.append(string_TO_object.get(i));
				    	}
				    }
				    
//				    System.out.println(" cleaned string  "+ sb);
					//================== applying transformation on real values END ========================
					
			        //=======Inserting the split results into the original data row			    
			        reparied_new.set(postion_pair.getKey(), sb.toString());
			    
					String[] stringArr = new String[reparied_new.size()];
					stringArr = reparied_new.toArray(new String[0]);

					transformedd.put(mapValues.getKey(), stringArr);
				}
				
			}
			
		}
		
		//===============================================================================================================//
		// ----------------shifted values case together with the incorrect line breaks --------------
		else
		{
			for(Entry<Integer, String[]> mapValues: valuesMap.entrySet())
			{
				String[] test = mapValues.getValue();
				List<String> reparied_new = new ArrayList<String>();
				for (int j = 0; j < test.length; j++)
				{
				  reparied_new.add(test[j]);
				}
				
				Merge merge_Object = new Merge();
			    StringBuilder toBeFixed = new StringBuilder();
			   
			    for(int t = postion_pair.getValue().get(0); t<=postion_pair.getValue().get(postion_pair.getValue().size()-1); t++)
			    {
			    	toBeFixed.append(reparied_new.get(t));
			    }
			   
//			    System.out.println("merge read objects       "+toBeFixed);
			    
			    // before inserting new results, delete the shifted values from the real data
			    // create new list to add results into before deleting from the main list to avoid boundException ((ADD PADEDD VALUES)) ""5 <DEL>773""
			    // Also, add padded values for entire block even if contains shifted values and next metadata columns as we have to remove these extra metadata columns
			    // for example --""5 <DEL>773""Fiscal Year<DEL>Month<DEL>Total SSR<DEL>Internet SSR<DEL>Percentage
			    for(int t = postion_pair.getValue().get(0); t<=postion_pair.getValue().get(postion_pair.getValue().size()-1); t++)
			    {	
			    	reparied_new.set(t, PADDED_CLASS.toString()); // remove these padded values while printing in "CSV Writer" Class
			    }
			    
//			    System.out.println("repaired with padded values   "+merge_Object.shiftFIX(toBeFixed.toString(), Main_Class.univocityDetetced_QUOTE, Main_Class.univocityDetetced_ESCAPE));
			    //Inserting the merge results into the original data row	
			     //===== for only shifted values i.e. ""5 <DEL>773"", we can directly add as given above, however for cases such as, 
		    	//""5 <DEL>773""Fiscal Year<DEL>Month<DEL>Total SSR<DEL>Internet SSR<DEL>Percentage // first try to extract or split information
			    reparied_new.set(postion_pair.getKey(), merge_Object.shiftFIX(toBeFixed.toString(), Main_Class.univocityDetetced_QUOTE, Main_Class.univocityDetetced_ESCAPE));  // sending shifted values for repairing
			    
				String[] stringArr = new String[reparied_new.size()];
				stringArr = reparied_new.toArray(new String[0]);

				transformedd.put(mapValues.getKey(), stringArr);
				
//				for(Entry<Integer, String[]> map: transformedd.entrySet())
//				 {
//					 String[] testing = map.getValue(); 	
//					 System.out.println("Transformed Row From Horizontal for Shifted values");
//					 for(int j = 0; j <testing.length;j++)
//					 {
//						 System.out.print(testing[j]);
//					 }
//				 }
			}	
		} 
		
		
		//======================================================================= OLD CODE (HORIZONTAL)================================================//
		//``````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````//
			
//		for(Entry<Integer, String[]> mapValues: valuesMap.entrySet())
//		 {
//			String[] test = mapValues.getValue();
//			List<String> reparied_new = new ArrayList<String>();
//			
//			for (int j = 0; j < test.length; j++)
//			{
//				reparied_new.add(test[j]);
//			}
//	
//			// ------------- 
//			
//			//apply transformation here on values ...... first transform patterns and then apply on actual values
//			
//			// ---------------data metadata case----------- 
//			if(!(pair.getValue().toString().contains(Main_Class.univocityDetetced_QUOTE)))
//			{
//				
//				int index = 0;
//				for(int j = 0; j<pair.getValue().size(); j++)
//				{
//					if(pair.getValue().get(j).toString().contains(CANDIDATE_DELIMITER_CLASS.toString()))
//					{
//					    index = pair.getValue().indexOf(pair.getValue().get(j));
//						break;
//					}
//				}
//				
//				// drop operation(s) on pattern-level also apply on real data
//				pair.getValue().subList(index, pair.getValue().size()).clear();  // drop metadata from start (index) to end (potential pattern size)
//				List<Object> new_valueList =  (List<Object>) pair.getValue().get(0); // extract list from <List<List<Object>
//				
//				reparied_new.subList(postion_pair.getKey()+1, postion_pair.getValue().get(postion_pair.getValue().size()-1)+1).clear();  // apply drop transformation on real values
//				
//				// split operation 
//				// after dropping metadata columns, get the score between the remaining potential and dominant pattern segment
//			     
////				System.out.println(pair.getKey()+  "   Potential value after dropping metadata  "+new_valueList);
//	
//				// add corner cases before sending lists to "split" (if one of the object list is empty!!) -----
//				Split split_Object = new Split();
//			    List<Object> split_Output = split_Object.apply(pair.getKey(), new_valueList);
//			    
//			    String toBeFixed = reparied_new.get(postion_pair.getKey());
//			    // convert List<Object> into string
//			    StringBuilder sb = new StringBuilder();
//			    for( int i = 0; i<split_Output.size(); i++)
//			    {
//			    	//sb.append(split_Output.get(i));  // !error! -- split_Output.get(i) extracts value form the potential object -- extracts value form the original data line 
//			    	sb.append(toBeFixed.charAt(i));       // fixed above error
//			    }
//			    
////			    Inserting the split results into the original data row			    
//			    reparied_new.set(postion_pair.getKey(), sb.toString());
//				
//				String[] stringArr = new String[reparied_new.size()];
//				stringArr = reparied_new.toArray(new String[0]);
//
//				
//				transformedd.put(mapValues.getKey(), stringArr);
//				
////				for(Entry<Integer, String[]> map: transformedd.entrySet())
////				 {
////					 String[] testing = map.getValue(); 	
////					 System.out.println("Transformed Row From Horizontal for Data-Metadata");
////					 for(int j = 0; j <testing.length;j++)
////					 {
////						 System.out.print(testing[j]);
////					 }
////				 }
//				
//			}
//			// ----------------shifted values case together with the incorrect line breaks --------------
//			else
//			{
//				Merge merge_Object = new Merge();
//			    StringBuilder toBeFixed = new StringBuilder();
//			   
//			    for(int t = postion_pair.getValue().get(0); t<=postion_pair.getValue().get(postion_pair.getValue().size()-1); t++)
//			    {
//			    	toBeFixed.append(reparied_new.get(t));
//			    }
//			   
//			    // before inserting new results, delete the shifted values from the real data
//			    // create new list to add results into before deleting from the main list to avoid boundException ((ADD PADEDD VALUES))
//			    for(int t = postion_pair.getValue().get(0); t<=postion_pair.getValue().get(postion_pair.getValue().size()-1); t++)
//			    {	
//			    	reparied_new.set(t, new Padded_Class().toString()); // remove these padded values while printing in "CSV Writer" Class
//			    }
//			    
////			    Inserting the merge results into the original data row			    
//			    reparied_new.set(postion_pair.getKey(), merge_Object.shiftFIX(toBeFixed.toString(), Main_Class.univocityDetetced_QUOTE, Main_Class.univocityDetetced_ESCAPE));  // sending shifted values for repairing
//			    
//				String[] stringArr = new String[reparied_new.size()];
//				stringArr = reparied_new.toArray(new String[0]);
//
//				transformedd.put(mapValues.getKey(), stringArr);
//				
////				for(Entry<Integer, String[]> map: transformedd.entrySet())
////				 {
////					 String[] testing = map.getValue(); 	
////					 System.out.println("Transformed Row From Horizontal for Shifted values");
////					 for(int j = 0; j <testing.length;j++)
////					 {
////						 System.out.print(testing[j]);
////					 }
////				 }
//			}
//	
//		 }
		
		return transformedd;
	}
	
	public static Map<Integer, String[]> vertical_Transform(Map<Integer, String[]> valuesMap, Pair<List<Object>, List<Object>> pair, Pair<Integer, List<Integer>> postion_pair)
	{
		 //apply transformation on pattern and then the corresponding lines
		
		// for padding null or delimiter cases
		
		
		if(pair.getKey().toString().contains(CANDIDATE_DELIMITER_CLASS.toString()) && pair.getValue().toString().contains(GAP_CLASS.toString()))
		{
			 for(Entry<Integer, String[]> mapValues: valuesMap.entrySet())
			 {
				String[] test = mapValues.getValue();
				List<String> reparied_new = new ArrayList<String>();
				
				for (int j = 0; j < test.length; j++)
				{
					reparied_new.add(test[j]);
				}
		
				reparied_new.add(postion_pair.getKey(), Main_Class.univocityDetetced_delimiter);   // padding extra delimiters
				
				String[] stringArr = new String[reparied_new.size()];
				stringArr = reparied_new.toArray(new String[0]);

				
				transformedd.put(mapValues.getKey(), stringArr);
			 }
		}
		else if(!(pair.getKey().toString().contains(CANDIDATE_DELIMITER_CLASS.toString())) && pair.getValue().toString().contains(GAP_CLASS.toString()))
		{
			for(Entry<Integer, String[]> mapValues: valuesMap.entrySet())
			 {
				String[] test = mapValues.getValue();
				List<String> reparied_new = new ArrayList<String>();
				
				for (int j = 0; j < test.length; j++)
				{
					reparied_new.add(test[j]);
				}
				
				reparied_new.add(postion_pair.getKey(), null);    // padding nulls as column values
				
				String[] stringArr = new String[reparied_new.size()];
				stringArr = reparied_new.toArray(new String[0]);

				
				transformedd.put(mapValues.getKey(), stringArr);
			 }
		}
		else
		{
			System.out.println("Vertical transformation error at! "+ pair);
		}
		 
		return transformedd;
	}
	
//	private static boolean no_Shift_problem(List<Object> list) // for the data/metadata cases where quote characters appear but without shift problem
//	{
//		List<Integer> quoteList = new ArrayList<>();
//		int seperator = 0;
//		// if index of delimiter is in between indices of quote characters , then
//		// everything is fine and there is no shift, return true
//		for (int i = 0; i < list.size(); i++) {
//			if (list.get(i) instanceof Character && (char) list.get(i) == 34) {
//				quoteList.add(i);
//				System.out.println("Quote " + quoteList.add(i));
//			}
//			if (list.get(i) instanceof Character
//					&& (char) list.get(i) == Main_Class.univocityDetetced_delimiter.charAt(0)) {
//				seperator = i;
//				System.out.println("Separtor " + seperator);
//			}
//		}
//
//		int minIndex = quoteList.indexOf(Collections.min(quoteList));
//		int maxIndex = quoteList.indexOf(Collections.max(quoteList));
//		System.out.println(minIndex + "           " + maxIndex + "            " + seperator);
//		if (seperator > minIndex && seperator < maxIndex)
//			return true;
//		else
//			return false;
//
//	}
}
