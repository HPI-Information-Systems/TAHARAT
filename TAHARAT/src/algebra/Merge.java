package algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Merge{

	public List<Object> apply(List<Object> input, List<Object> output) {
		// TODO Auto-generated method stub
		List<Object> results = new ArrayList<Object>();
		
		
		return results;
	}

	public String shiftFIX (String input, String univocityDetetced_QUOTE, String univocityDetetced_ESCAPE)
	{
		
		// use ReQuote, ReEscape, ReDelimit and ReLine for inserting or replacing dialect member characters, while use Drop for deleting them from the incorrect positions
		StringBuilder sb = new StringBuilder();
		boolean flagQuoteAppear = false;
		
		//========================== if Quote and Escape are the same===================
		
		if(univocityDetetced_ESCAPE.contentEquals(univocityDetetced_QUOTE))
		{
			if(input.toString().matches("^[-\"%.+,0-9]+$")) //==============if input contains no letters=================
			{
				// i is starting from 1 as first index has "univocityDetetced_QUOTE"
				sb.append("\""); // starting quote
				for(int i = 1; i<input.length()-1; i++)
				{
				   if(input.charAt(i) != univocityDetetced_QUOTE.charAt(0))
					{
						sb.append(input.charAt(i));
					}
				}
				sb.append("\""); // ending quote		
			}
			else //==================== if input contains letters===================
			{
				// i is starting from 1 as first index has "univocityDetetced_QUOTE"
				sb.append("\""); // starting quote
				for(int i = 1; i<input.length()-1; i++)
				{
					if(flagQuoteAppear == false && input.charAt(i) == univocityDetetced_QUOTE.charAt(0))
					{
						sb.append("\"");
						sb.append("\"");
						flagQuoteAppear = true;
					}
					else if(input.charAt(i) != univocityDetetced_QUOTE.charAt(0))
					{
						flagQuoteAppear = false;
						sb.append(input.charAt(i));
					}
				}
				sb.append("\""); // ending quote	
			}
			
		}
		//========================== if Quote and Escape are different===================
		else
		{
			if(input.toString().matches("^[-\"%.+,0-9]+$")) //==============if input contains no letters=================
			{
				// i is starting from 1 as first index has "univocityDetetced_QUOTE"
				if(univocityDetetced_ESCAPE.length()>1)
				{
					sb.append("\""); // starting quote
					for(int i = 1; i<input.length()-1; i++)
					{
					   if(i+1 <input.length()-1 && input.charAt(i) != univocityDetetced_ESCAPE.charAt(0) && input.charAt(i+1) != univocityDetetced_ESCAPE.charAt(1))
						{
							sb.append(input.charAt(i));
						}
					}
					sb.append("\""); // ending quote
				}
				else
				{
					sb.append("\""); // starting quote
					for(int i = 1; i<input.length()-1; i++)
					{
					   if(input.charAt(i) != univocityDetetced_ESCAPE.charAt(0))
						{
							sb.append(input.charAt(i));
						}
					}
					sb.append("\""); // ending quote	
				}
					
			}
			else //==================== if input contains letters===================
			{
				// i is starting from 1 as first index has "univocityDetetced_QUOTE"
				if(univocityDetetced_ESCAPE.length()>1)
				{
					sb.append("\""); // starting quote
					for(int i = 1; i<input.length()-1; i++)
					{
						if(i+1 <input.length()-1 && flagQuoteAppear == false && input.charAt(i) == univocityDetetced_ESCAPE.charAt(0) && input.charAt(i+1) == univocityDetetced_ESCAPE.charAt(1))
						{
							sb.append("\"");
							sb.append("\"");
							flagQuoteAppear = true;
						}
						else if(i+1 <input.length()-1 && input.charAt(i) != univocityDetetced_ESCAPE.charAt(0) && input.charAt(i+1) != univocityDetetced_ESCAPE.charAt(1))
						{
							flagQuoteAppear = false;
							sb.append(input.charAt(i));
						}
					}
					sb.append("\""); // ending quote
				}
				else
				{
					sb.append("\""); // starting quote
					for(int i = 1; i<input.length()-1; i++)
					{
						if(flagQuoteAppear == false && input.charAt(i) == univocityDetetced_ESCAPE.charAt(0))
						{
							sb.append("\"");
							sb.append("\"");
							flagQuoteAppear = true;
						}
						else if(input.charAt(i) != univocityDetetced_ESCAPE.charAt(0))
						{
							flagQuoteAppear = false;
							sb.append(input.charAt(i));
						}
					}
					sb.append("\""); // ending quote
				}
					
			}
	
		}
		
		return sb.toString();
	}
}
