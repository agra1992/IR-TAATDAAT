/*
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CSE 535 Assignment~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
								SUBMITTED BY:
								=============
Name: Agradeep Khanra
UB ID Name: agradeep
Person No.: 50169196			
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/

import java.io.*;
import java.util.*;

public class CSE535Assignment {

	public static void main(String[] args){
		String content = new String();
		
		/*
		 * args[0] = term.idx
		 * args[1] = output.log
		 * args[2] = <Top K>
		 * args[3] = query_file.txt
		 */
		
		int listCounter = 0;
		int i = 0;
		
		File indexFile = new File(args[0]);
		try {
			//Using System.setOut to redirect all console output to output.log file
			System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(args[1])), true));
			BufferedReader reader = new BufferedReader(new FileReader(indexFile));
			//Counting the total number of lines (or Terms) in the indexFile
			while (reader.readLine() != null){
				listCounter ++;
			}
			reader.close();
		}
		catch(FileNotFoundException fnf){
			fnf.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("\nProgram terminated Safely...");
		}

		/*Creating an array of Linked Lists
		 *  listDocIDArray is going to store all terms with increasing Doc IDs in Linked Lists
		 *  listTermFreqArray is going to store all terms with decreasing Term Frequencies in Linked Lists 
		 */ 
		@SuppressWarnings("unchecked")
		LinkedList<String>[] listDocIDArray = new LinkedList[listCounter];
		@SuppressWarnings("unchecked")
		LinkedList<String>[] listTermFreqArray = new LinkedList[listCounter];
		
		//Parsing term.idx to input values into listDocIDArray 
		try {
			Scanner sc = new Scanner(new FileInputStream(indexFile), "UTF-8");
			while (sc.hasNextLine()){
				
				content = sc.nextLine();
				String[] strArray = content.split("\\\\");
					listDocIDArray[i] = new LinkedList<String>();
				for(int j = 0; j < strArray.length; j++){
					if(j == 1){
						strArray[j] = strArray[j].replace("c", "");
					}
					if(strArray[j].indexOf("/") > 0 & j != 0){
						String[] strPostingArray = strArray[j].split(",");
						for(int k = 0; k < strPostingArray.length; k++){
								if(strPostingArray[k].indexOf("m[") >= 0){
									strPostingArray[k] = strPostingArray[k].substring(2);
									if(strPostingArray[k].indexOf("]") >= 0)
										strPostingArray[k] = strPostingArray[k].replaceAll("]", "");
								}
								else if(strPostingArray[k].indexOf("]") >= 0){
									strPostingArray[k] = strPostingArray[k].replaceAll("]", "");
								}
								
								strPostingArray[k] = strPostingArray[k].replaceAll(" ", "");
								listDocIDArray[i].add(strPostingArray[k]);
						}
						break;
					}
					else
						listDocIDArray[i].add(strArray[j]);
				}
				i++;
		}
		sc.close();
		}
		catch(FileNotFoundException fnf){
			fnf.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("\nProgram terminated Safely...");
		}
		
		/*As we already have listDocIDArray, I'm temporarily setting 
		 * listTermFreqArray equal to listDocIDArray
		 * and I'll perform sorting on listTermFreqArray later on
		 */
		for(int j = 0;j < listDocIDArray.length; j++){
			listTermFreqArray[j] = new LinkedList<String>();
			
			for(int k = 0; k < listDocIDArray[j].size(); k++){
				listTermFreqArray[j].add(k, listDocIDArray[j].get(k));
			}
		}
		
		//We will now sort the listDocIDArray using Efficient Bubble Sort
		LinkedList<String> tempArray = new LinkedList<String>();
		String Temp = new String();
		
		for(int j = 0;j < listDocIDArray.length; j++){	//Perform Sort on each Term in listDocIDArray
			tempArray = listDocIDArray[j];
			boolean flag = true;
			int lastSwap = tempArray.size();
			while(flag){
				flag = false;
				for(int p = 1;p < tempArray.size(); p++){	//Perform Sort on each Doc ID in a Term
					int currentSwap = -1;
					for(int q = 2; q < lastSwap - 1; q++){
						int pos1 = tempArray.get(q).lastIndexOf("/");
						int intVal1 = Integer.parseInt(tempArray.get(q).substring(0, pos1));
						int pos2 = tempArray.get(q+1).lastIndexOf("/");
						int intVal2 = Integer.parseInt(tempArray.get(q+1).substring(0, pos2));
						
						if(intVal1 > intVal2){
							Temp = tempArray.get(q);
							tempArray.set(q, tempArray.get(q+1));
							tempArray.set(q+1, Temp);
							currentSwap = q;
							flag = true;
						}
					}
					lastSwap = currentSwap + 2;
				}
			}
			listDocIDArray[j] = tempArray;	//Input sorted tempArray into listDocIDArray
		}
		
		//We will now sort the listTermFreqArray using Efficient Bubble Sort
		//based in decreasing Term Frequencies
		
		tempArray = new LinkedList<String>();
		Temp = new String();
		for(int j = 0;j < listTermFreqArray.length; j++){	//Perform Sort on each Term in listDocIDArray
			tempArray = listTermFreqArray[j];
			boolean flag = true;
			int lastSwap = tempArray.size();
			while(flag){
				flag = false;
				for(int p = 1;p < tempArray.size(); p++){	//Perform Sort on each Doc ID in a Term
					int currentSwap = -1;
					for(int q = 2; q < lastSwap - 1; q++){
						int pos1 = tempArray.get(q).lastIndexOf("/");
						int intVal1 = Integer.parseInt(tempArray.get(q).substring(pos1 + 1));
						int pos2 = tempArray.get(q+1).lastIndexOf("/");
						int intVal2 = Integer.parseInt(tempArray.get(q+1).substring(pos2 + 1));
						
						if(intVal1 < intVal2){
							Temp = tempArray.get(q);
							tempArray.set(q, tempArray.get(q+1));
							tempArray.set(q+1, Temp);
							currentSwap = q;
							flag = true;
						}
					}
					lastSwap = currentSwap + 2;
				}
			}
			listTermFreqArray[j] = tempArray;	//Input sorted tempArray into listDocIDArray
		}
		
		/*As both indexes are created, we can now perform getTopK*/
		//Method to get Top K Terms
		getTopK(args[2], listDocIDArray);
		
		File queryFile = new File(args[3]);
		int N = 0;
		String[] arrQueries = new String[100];
		String queryTerm = new String();
		
		try {
			//For each Query line in query_file.txt, perform the 5 other modules
			
			Scanner sc = new Scanner(new FileInputStream(queryFile), "UTF-8");
			while (sc.hasNextLine()){
				content = sc.nextLine();
				boolean blnGlobalFound = false;		//Use this Flag to skip TAAT And or DAAT And operations if Terms are not present in the index
				arrQueries = content.split(" ");
				N = arrQueries.length;
				/*Checking each Term in Query file against
				 * terms in index to check if term is
				 * present in the index 
				 */
				for(int j = 0; j < N; j++){
					queryTerm = arrQueries[j].replace(" ", "");
					for(int k = 0; k < listDocIDArray.length; k++){
						if(listDocIDArray[k].get(0).equals(queryTerm)){
							blnGlobalFound = true;
						}
					}
					if(!blnGlobalFound){
						blnGlobalFound = false;		// Indicates a term in query is not present in the index
						break;
					}
				}
				
				//Get Postings lists for the terms in 1 line in query list:
				getPostings(listDocIDArray, listTermFreqArray, arrQueries);
				
				/*If blnGlobalFound Flag is false, means
				 * that a term is not found in index.
				 * In this case, do not perform TAAT And
				 * as there will be no common Documents.
				 * Else, perform TAAT And.
				 */
				if(!blnGlobalFound){
					System.out.print("\nFUNCTION: termAtATimeQueryAnd " + content);
					System.out.print("\n0 documents are found");
					System.out.print("\n0 comparisons are made");
					System.out.print("\n0 seconds are used");
					System.out.print("\n0 comparisons are made with optimization");
					System.out.print("\nResult: terms not found");
				}
				else
					termAtATimeQueryAnd(content, listTermFreqArray);
				
				//Perform TAAT Or
				termAtATimeQueryOr(content, listTermFreqArray);
				
				/*If blnGlobalFound Flag is false, means
				 * that a term is not found in index.
				 * In this case, do not perform DAAT And
				 * as there will be no common Documents.
				 * Else, perform DAAT And.
				 */
				if(!blnGlobalFound){
					System.out.print("\nFUNCTION: termAtATimeQueryAnd " + content);
					System.out.print("\n0 documents are found");
					System.out.print("\n0 comparisons are made");
					System.out.print("\n0 seconds are used");
					System.out.print("\nResult: terms not found");
				}
				else
					documentAtATimeQueryAnd(content, listDocIDArray);
				//Perform DAAT Or
				documentAtATimeQueryOr(content, listDocIDArray);
			}
		sc.close();		
		}
		catch(FileNotFoundException fnf){
			fnf.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("\nProgram terminated Safely...");
		}
	}
	

	public static void getTopK(String strTotal, LinkedList<String>[] listDocIDArray){
		int intTotal = Integer.parseInt(strTotal), iCounter = 0, intVal, intLarge = 0;
		String[] topTerms = new String[100];
		String strTemp = new String();
		String strFinal = new String();

		while(iCounter < intTotal){
			intVal = 0;
			intLarge = 0;
			for(int j = 0; j < listDocIDArray.length; j++){
				intVal = Integer.parseInt(listDocIDArray[j].get(1));
				if(intVal > intLarge){
					strTemp = listDocIDArray[j].get(0);
					if(!Arrays.asList(topTerms).contains(strTemp)){
						strFinal = strTemp;
						intLarge = intVal;
					}
				}
			}
			topTerms[iCounter] = strFinal;
			iCounter ++;
		}
		
		int intLength = 0;
		for(int j = 0; j < topTerms.length; j++){
			if(topTerms[j] == null)
				break;
			else
				intLength++;
		}
		System.out.println("FUNCTION: getTopK " + intTotal);
		System.out.print("Result: ");
		for(int j = 0; j < intLength; j++){
			if(j == intLength - 1)
				System.out.print(topTerms[j]);
			else	
				System.out.print(topTerms[j] + ", ");
		}
		
	}
	
	public static void getPostings(LinkedList<String>[] listDocIDArray, LinkedList<String>[] listTermFreqArray, String[] arrQueries){
		boolean blnFound = false;
		int N = arrQueries.length;
		String queryTerm;
		for(int j = 0; j < N; j++){
			blnFound = false;
			queryTerm = arrQueries[j].replace(" ", "");
			System.out.print("\nFUNCTION: getPostings " + queryTerm);
			for(int k = 0; k < listDocIDArray.length; k++){
				if(listDocIDArray[k].get(0).equals(queryTerm)){
						blnFound = true;
					}
				}
			if(!blnFound){
				System.out.print("\nterm not found");
				continue;
			}
			System.out.print("\nOrdered by doc IDs: ");
			for(int k = 0; k < listDocIDArray.length; k++){
				if(listDocIDArray[k].get(0).equals(queryTerm)){
					for(int p = 2; p < listDocIDArray[k].size(); p++){
						if(p == listDocIDArray[k].size() - 1){
							int pos1 = listDocIDArray[k].get(p).lastIndexOf("/");
							System.out.print(listDocIDArray[k].get(p).substring(0, pos1));
						}
						else{
							int pos1 = listDocIDArray[k].get(p).lastIndexOf("/");
							System.out.print(listDocIDArray[k].get(p).substring(0, pos1) + ", ");
						}
					}
					break;
				}
			}
			System.out.print("\nOrdered by TF: ");
			for(int k = 0; k < listTermFreqArray.length; k++){
				if(listTermFreqArray[k].get(0).equals(queryTerm)){
					for(int p = 2; p < listTermFreqArray[k].size(); p++){
						if(p == listTermFreqArray[k].size() - 1){
							int pos1 = listTermFreqArray[k].get(p).lastIndexOf("/");
							System.out.print(listTermFreqArray[k].get(p).substring(0, pos1));
						}
						else{
							int pos1 = listTermFreqArray[k].get(p).lastIndexOf("/");
							System.out.print(listTermFreqArray[k].get(p).substring(0, pos1) + ", ");
						}
					}
					break;
				}
			}
			
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void termAtATimeQueryAnd(String content, LinkedList<String>[] listTermFreqArray){
		
		//startTime stores the time at which the function starts execution
		long startTime = System.currentTimeMillis();
		
		String tempContent = content.replace(" ", ", ");
		System.out.print("\nFUNCTION: termAtATimeQueryAnd " + tempContent);
		
		String[] arrQueries;
		int intTotalTerms = 0;
		
		arrQueries = content.split(" ");
		intTotalTerms = arrQueries.length;
		LinkedList<String>[] listTermQueries = new LinkedList[intTotalTerms];

		/*In this step, we are creating a listTermQueries Linked List Array
		 * which will store ONLY the terms that we need
		 * from all the terms in listTermFreqArray
		 */
		
		for(int i = 0; i < arrQueries.length; i++){
			for(int j = 0; j < listTermFreqArray.length; j++){
				listTermQueries[i] = new LinkedList();
				if(arrQueries[i].equals(listTermFreqArray[j].get(0))){
					for(int k = 0; k < listTermFreqArray[j].size(); k++)
						listTermQueries[i].add(k, listTermFreqArray[j].get(k));
					break;
				}
			}
		}
		
		//Creating a HashMap to store the temporary results after parsing each Term		
		HashMap<String, LinkedList> partialScores = new HashMap<String, LinkedList>(1000);
		LinkedList oLink = new LinkedList();
		LinkedList oTemp = new LinkedList();
		List<String> oKeys;
		boolean intersection = false;

		int intVal = 0;
		int totalComps = 0;
		String DocID = new String();
		String[] arrVals = new String[2];

		/*In this step, we are inputting all DocIDs into
		 *  partialScores. partialScores stores a DocID-intersection pair
		 *  where DocID is a String and intersection is a boolean value.
		 *  Intersection is used to indicate if a Doc ID in partialScores
		 *  matched with a DocID in the next query Term.
		 */

		for(int i = 0; i < listTermQueries.length; i++){
			
			if(i != 0){	//assign all intersections to false for each Term, after the first Term
				for(int l = 2; l < listTermQueries[i - 1].size(); l++){
					arrVals = listTermQueries[i - 1].get(l).split("/");
					DocID = arrVals[0];
					
					if(partialScores.containsKey(DocID)){
						/*Using .contains as we are just getting values
						 * in the PartialScores HashMap and changing all intersection to "False".
						 * No Document ID comparisons are being made at this point.
						 */
						oTemp = partialScores.get(DocID);
						oTemp.set(1, false);
						partialScores.put(DocID, oTemp);
					}
				}
			}
			//parse each Doc ID in a Term
			for(int j = 2; j < listTermQueries[i].size(); j++){
				oLink = new LinkedList();
				arrVals = listTermQueries[i].get(j).split("/");
				DocID = arrVals[0];
				intVal = Integer.parseInt(arrVals[1]);
				
				/*At i = 0, since it is the first Term, we are inputting
				 * all Doc IDs into partialScores and updating intersection as "true"
				 * for all Doc IDs.
				 */
				if(i == 0){
					intersection = true;
					oLink.add(intVal);
					oLink.add(intersection);
					partialScores.put(DocID, oLink);
				}
				else{	//Perform these steps if its not the First Term in the query
					int intTemp = 0;
					oKeys = new ArrayList<String>(partialScores.keySet());
					for(int p = 0; p < oKeys.size(); p++){
						/* Parsing all documents of current Term and 
						 * comparing with documents in partialScores.
						 * In case of match, update intersection of ONLY those Doc IDs
						 * which matched.
						 */
						totalComps++;
						if(oKeys.get(p).equals(DocID)){
							
							oTemp = new LinkedList<String>();
							oTemp = partialScores.get(DocID);
							intTemp = Integer.parseInt(oTemp.get(0).toString());
							intTemp = intTemp + intVal;
							intersection = true;
							oTemp.set(0, intTemp);
							oTemp.set(1, intersection);
							partialScores.put(DocID, oTemp);	//Input the updated intersection value in partialScores.
						}
					}
				}
			}
			/*Now, we will remove all elements in HashMap where intersection = false.
			 * This essentially means that the DocIDs with intersection = false were 
			 * not present in the partialScores and the current Term's Doc IDs
			 */
			oKeys = new ArrayList(partialScores.keySet());
			for (Object key : oKeys){
				oTemp = new LinkedList();
				oTemp = partialScores.get(key);
				String finalVal = oTemp.get(1).toString();
				
				if(finalVal == "false"){
					partialScores.remove(key);
				}
			}
		}
		
		//Now we will the Doc IDs in partialScores based on increasing DocID value
		oKeys = new ArrayList(partialScores.keySet());
		Collections.sort(oKeys);
		
		//As function has come to an end, we record the endTime of the execution.
		long endTime = System.currentTimeMillis();
		
		//Calculating total Duration of execution
		float duration = (float)(endTime - startTime)/1000;
		
		System.out.print("\n" + partialScores.size() + " documents are found");
		System.out.print("\n" + totalComps + " comparisons are made");
		System.out.print("\n" + duration + " seconds are used");
		//Perform optimized TAAT And at this step
		System.out.print("\n" + termAtATimeQueryAnd_Optimize(content, listTermFreqArray) + " comparisons are made with optimization");
				
		System.out.print("\nResult: ");
		//Print all DocIDs in partialScores.
		for(int i = 0; i < oKeys.size(); i++){
			if(i == oKeys.size() - 1)
				System.out.print(oKeys.get(i));
			else
				System.out.print(oKeys.get(i) + ", ");
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void termAtATimeQueryOr(String content, LinkedList<String>[] listTermFreqArray){
		//startTime stores the time at which the function starts execution
		long startTime = System.currentTimeMillis();
		
		String tempContent = content.replace(" ", ", ");
		System.out.print("\nFUNCTION: termAtATimeQueryOr " + tempContent);
		String[] arrQueries;
		int intTermsFound = 0;
		arrQueries = content.split(" ");
		
		/*In this step we are checking how many terms in the Query file are
		 * are actually present in the index
		 */
		
		for(int i = 0; i < arrQueries.length; i++){
			for(int j = 0; j < listTermFreqArray.length; j++){
				if(listTermFreqArray[j].get(0).equals(arrQueries[i])){
					intTermsFound++;	//Store total number of Terms found in index
				}
			}
		}
		
		if(intTermsFound == 0){ //If no terms were present, then perform not operations
			System.out.print("\n0 documents are found");
			System.out.print("\n0 comparisons are made");
			System.out.print("\n0 seconds are used");
			System.out.print("\n0 comparisons are made with optimization");
			System.out.print("\nResult: terms not found");
			return;	//Exit function
		}
		
		/*Store Terms and their Doc IDs that are found in index in
		 *  listTermQueries
		 */
		LinkedList<String>[] listTermQueries = new LinkedList[intTermsFound];
		int intC = 0;
		for(int i = 0; i < arrQueries.length; i++){
			for(int j = 0; j < listTermFreqArray.length; j++){
				
				if(listTermFreqArray[j].get(0).equals(arrQueries[i])){
					listTermQueries[intC] = new LinkedList<String>();
					for(int k = 0; k < listTermFreqArray[j].size(); k++){
						listTermQueries[intC].add(k, listTermFreqArray[j].get(k));
					}
					intC++;	
					break;
				}
			}
		}
		
		HashMap<String, LinkedList> partialScores = new HashMap<String, LinkedList>(1000);
		LinkedList oLink = new LinkedList();
		LinkedList oTemp = new LinkedList();
		List<String> oKeys;
		boolean intersection = false;

		int intVal = 0;
		int totalComps = 0;
		String DocID = new String();
		String[] arrVals = new String[2];
		
		/*In this step, we are inputting all DocIDs into
		 *  partialScores. partialScores stores a DocID-intersection pair
		 *  where DocID is a String and intersection is a boolean value.
		 *  Intersection is used to indicate if a Doc ID in partialScores
		 *  matched with a DocID in the next query Term.
		 */
		for(int i = 0; i < listTermQueries.length; i++){
			//parse each term
			for(int j = 2; j < listTermQueries[i].size(); j++){
				oLink = new LinkedList();
				arrVals = listTermQueries[i].get(j).split("/");
				DocID = arrVals[0];
				intVal = Integer.parseInt(arrVals[1]);
				
				/*At i = 0, since it is the first Term, we are inputting
				 * all Doc IDs into partialScores and updating intersection as "true"
				 * for all Doc IDs.
				 */
				if(i == 0){
					intersection = true;
					oLink.add(intVal);
					oLink.add(intersection);
					partialScores.put(DocID, oLink);
				}
				else{	//Perform following operations if not First Term
					int intTemp = 0;
					//Getting all DocIDs in partialScores
					oKeys = new ArrayList<String>(partialScores.keySet());
					for(int p = 0; p < oKeys.size(); p++){
						/*Comparing each DocID in Term to Doc IDs in partialScores
						 *  to check if term is already present in partialScores
						 */
						totalComps++;
						if(oKeys.get(p).equals(DocID)){	//If Doc ID in Term is found in DocIDs in partialScores
							oTemp = new LinkedList();
							oTemp = partialScores.get(DocID);
							intTemp = Integer.parseInt(oTemp.get(0).toString());
							intTemp = intTemp + intVal;
							intersection = true;
							oTemp.set(0, intTemp);
							oTemp.set(1, intersection);
							partialScores.put(DocID, oTemp);	//Update partialScores with new intTemp and intersection values
						}
						else{	/*Add DocID in term not found in partialScores
						* then add the Doc ID to partialScores
						*/
							oTemp = new LinkedList();
							intersection = true;
							oTemp.add(intVal);
							oTemp.add(intersection);
							partialScores.put(DocID, oTemp);
						}
					}
				}
			}
		}
		
		//Now we will the Doc IDs in partialScores based on increasing DocID value
		oKeys = new ArrayList<String>(partialScores.keySet());
		Collections.sort(oKeys);
		
		//As function has come to an end, we record the endTime of the execution.
		long endTime = System.currentTimeMillis();
		
		//Calculating total Duration of execution
		float duration = (float)(endTime - startTime)/1000;
		
		System.out.print("\n" + partialScores.size() + " documents are found");
		System.out.print("\n" + totalComps + " comparisons are made");
		System.out.print("\n" + duration + " seconds are used");
		//Perform optimized TAAT Or at this step
		System.out.print("\n" + termAtATimeQueryOr_Optimize(content, listTermFreqArray) + " comparisons are made with optimization");
		System.out.print("\nResult: ");
		
		//Print all DocIDs in partialScores
		for(int i = 0; i < oKeys.size(); i++){
			if(i == oKeys.size() - 1)
				System.out.print(oKeys.get(i));
			else
				System.out.print(oKeys.get(i) + ", ");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void documentAtATimeQueryAnd(String content, LinkedList<String>[] listDocIDArray){
		
		long startTime = System.currentTimeMillis();
		
		String tempContent = content.replace(" ", ", ");
		System.out.print("\nFUNCTION: documentAtATimeQueryAnd " + tempContent);
		String[] arrQueries;
		int intTotalTerms = 0;
		String strQueryTerm;
		
		arrQueries = content.split(" ");
		intTotalTerms = arrQueries.length;
		int[] pointers = new int[intTotalTerms];
		LinkedList<String>[] listTermQueries = new LinkedList[intTotalTerms];
		
		/*In this step, we are creating a listTermQueries Linked List Array
		 * which will store ONLY the terms that we need
		 * from all the terms in listTermFreqArray
		 */
		
		for(int i = 0; i < arrQueries.length; i++){
			for(int j = 0; j < listDocIDArray.length; j++){
				listTermQueries[i] = new LinkedList<String>();
				if(arrQueries[i].equals(listDocIDArray[j].get(0))){
					for(int k = 0; k < listDocIDArray[j].size(); k++)
						listTermQueries[i].add(k, listDocIDArray[j].get(k));
					break;
				}
			}
		}
		
		LinkedList<String>[] oList = new LinkedList[intTotalTerms];
		int intVal = 0;
		int totalComps = 0;
		String DocID = new String();
		String[] arrVals = new String[2];
		
		//Creating an Array Linked List to store all Query Term's corresponding DocIDs in an array
		for(int i = 0; i < listTermQueries.length; i++){
			oList[i] = new LinkedList<String>();
			strQueryTerm = arrQueries[i];
			oList[i].add(strQueryTerm);
			for(int j = 2; j < listTermQueries[i].size(); j++){
				DocID = listTermQueries[i].get(j).toString();
				oList[i].add(DocID);		//oList stores ONLT Doc IDs. No other values like "Term" or "Postings list length" are stored
			}
		}
		
		/*
		 * We are using a pointer array called "pointers"
		 * which will store the pointers to each term's postings list
		 * at each step
		 */
		
		//Initialize all pointers to the first document of each term
		for(int i = 0; i < pointers.length; i++)
			pointers[i] = 1;
		
		boolean blnCont = true;
		int intLarge = 0, intDocID, intC = 0, intFinalCounter = 0;
		String[] finalAnswer = new String[1000];	//Array to store all our final DocIDs
		String strDocID = new String();
		//Parsing oList 1 document at a time
		
		while(blnCont){
			intC = 0;
			for(int i = 0; i < oList.length; i++){
				totalComps++;
				arrVals = oList[i].get(pointers[i]).split("/");
				intDocID = Integer.parseInt(arrVals[0]);
				strDocID = oList[i].get(pointers[i]);
				//Retrieving the Largest Doc ID for all Query Terms
				if(intDocID >= intLarge){
					/*
					 * check if Current DocID is larger than intLarge
					 * If true then set large as intDocID
					 */
					intLarge = intDocID;
					++intC;	//A counter to check if the DocIDs in all the terms are the same
				}
			}
			
			if(intC == intTotalTerms){	//if all DocIDs are same in all the terms
				finalAnswer[intFinalCounter] = strDocID;	//input the DocID in the finalAnswer array
				intFinalCounter++;
				for(int i = 0; i < oList.length; i++){	//increment all pointers of each postings list by 1
					pointers[i] = pointers[i] + 1;
					if(pointers[i] > oList[i].size() - 1){	//Check if a postings list has ended
						blnCont = false;	//End execution as soon as this step executes
						break;
					}
				}
			}
			else{	
				/*
				 * If all docIDs are not same, increment pointers of all
				 * DocIDs which are lesser than intLarge.
				 */
				for(int i = 0; i < oList.length; i++){
					if(blnCont == false)
						break;
					intVal = pointers[i];
					totalComps++;
					arrVals = oList[i].get(pointers[i]).split("/");
					intDocID = Integer.parseInt(arrVals[0]);
					if(intDocID < intLarge){
						intVal++;	//Increment pointers of each postings list ONE DOCUMENT AT A TIME
						pointers[i] = intVal;						
						if(pointers[i] > oList[i].size() - 1){	//Check if end of a postings list has been reached
							blnCont = false;	//End execution at this step
							break;
						}
					}
				}
			}
		}
		
		//Display all Doc IDs
		long endTime = System.currentTimeMillis();
		
		float duration = (float)(endTime - startTime)/1000;  //divide by 1000000 to get milliseconds.
		
		System.out.print("\n" + intFinalCounter + " documents are found");
		System.out.print("\n" + totalComps + " comparisons are made");
		System.out.print("\n" + duration + " seconds are used");
		System.out.print("\nResult: ");
		
		for(int i = 0; i < intFinalCounter; i++){
			if(i == intFinalCounter - 1){
				arrVals = finalAnswer[i].split("/");
				DocID = arrVals[0];
				System.out.print(DocID);
			}
			else{
				arrVals = finalAnswer[i].split("/");
				DocID = arrVals[0];
				System.out.print(DocID + ", ");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void documentAtATimeQueryOr(String content, LinkedList<String>[] listDocIDArray){
		
		long startTime = System.currentTimeMillis();
		
		String tempContent = content.replace(" ", ", ");
		System.out.print("\nFUNCTION: documentAtATimeQueryOr " + tempContent);
		String[] arrQueries;
		int intTotalTerms = 0, intTermsFound = 0;
		String strQueryTerm;
		
		arrQueries = content.split(" ");
		intTotalTerms = arrQueries.length;
		int[] pointers = new int[intTotalTerms];
		
		/*In this step we are checking how many terms in the Query file are
		 * are actually present in the index
		 */
		
		for(int i = 0; i < arrQueries.length; i++){
			for(int j = 0; j < listDocIDArray.length; j++){
				if(listDocIDArray[j].get(0).equals(arrQueries[i])){
					intTermsFound++;	//Store total number of Terms found in index
				}
			}
		}
		
		if(intTermsFound == 0){	//If no terms present in index then exit function
			System.out.print("\n0 documents are found");
			System.out.print("\n0 comparisons are made");
			System.out.print("\n0 seconds are used");
			System.out.print("\nResult: terms not found");
			return;
		}
		
		boolean[] blnPointers = new boolean[intTermsFound];
		LinkedList<String>[] listTermQueries = new LinkedList[intTermsFound];
		int intC = 0;
		for(int i = 0; i < arrQueries.length; i++){
			for(int j = 0; j < listDocIDArray.length; j++){
				
				if(listDocIDArray[j].get(0).equals(arrQueries[i])){
					listTermQueries[intC] = new LinkedList<String>();
					for(int k = 0; k < listDocIDArray[j].size(); k++){
						listTermQueries[intC].add(k, listDocIDArray[j].get(k));
					}
					intC++;	
					break;
				}
			}
		}
		
		LinkedList<String>[] oList = new LinkedList[intTermsFound];
		int totalComps = 0;
		String DocID = new String();
		
		//Creating a Array Linked List to store all Query Term's corresponding DocIDs in an array
		for(int i = 0; i < listTermQueries.length; i++){
			oList[i] = new LinkedList<String>();
			strQueryTerm = arrQueries[i];
			oList[i].add(strQueryTerm);
			for(int j = 2; j < listTermQueries[i].size(); j++){
				DocID = listTermQueries[i].get(j).toString();
				oList[i].add(DocID);	//oList will store DocIDs ONLY
			}
		}
		
		//initialize all pointers to the first document of each term
		for(int i = 0; i < pointers.length; i++)
			pointers[i] = 1;
		
		/*
		 * blnPointers is a boolean Array to indicate whether
		 * a particular postings list has reached it's end.
		 */
		//initialize all flags to true
		//Change values to false ONLY if the corresponding term postings list is done parsing
		for(int i = 0; i < blnPointers.length; i++)
			blnPointers[i] = true;
		
		boolean blnCont = true;
		int intDocID, intChecker;
		int intFinalCounter = 0;
		String[] finalAnswer = new String[1000];
		String strDocID = new String();
		int intMin = 0;
		//Parsing oList 1 document at a time
		String arrVal[];
		
		while(blnCont){
			intMin = 0;
			intChecker = 0;	
			for(int i = 0; i < oList.length; i++){
				if(!blnPointers[i])		//If a particular postings list has reached its end, then continue to next term
					continue;
				arrVal = oList[i].get(pointers[i]).split("/");
				strDocID = arrVal[0];
				intDocID = Integer.parseInt(arrVal[0]);
				totalComps++;
				
				//Setting intMin to the lowest DocID
				if(intMin == 0){	
					intMin = intDocID;
					intChecker = i;
				}
					
				else if(intDocID < intMin){
					intMin = intDocID;
					intChecker = i;	//intChecker store the index of the posting List at which the lowest DocID was found
				}
			}
			
			for(int i = 0; i < oList.length; i++){	//Parse DocIDs in the postings list pointed to by pointers Array
				if(!blnPointers[i])		//If a particular postings list has reached its end, then continue to next term
					continue;
				arrVal = oList[i].get(pointers[i]).split("/");
				strDocID = arrVal[0];
				intDocID = Integer.parseInt(arrVal[0]);
				totalComps++;
				
				/*
				 * if the current Doc ID is the lowest
				 * and it matched the postings list at which the lowest Doc ID was found
				 * then input the Doc ID in finalAnswer array and increment it's pointer
				 */
				if(intDocID == intMin && i == intChecker){
					finalAnswer[intFinalCounter] = strDocID;
					intFinalCounter++;
					pointers[i]++;
					
					if(pointers[i] == oList[i].size()){	//If pointer reaches the end of a postings list
						blnPointers[i] = false;	//Set the correspoiding blnPointers[i] to false.
						//At this step, a postings list with blnPointers[i] = false will never be parsed.
						//As we have reached it's end.
					}
				}
				/*
				 * If current Doc ID is equal to minimum
				 * but it's current postings list doesn't match the one with intChecker
				 * then just increment the current postings list's pointer
				 */
				else if(intDocID == intMin && i != intChecker){
					pointers[i]++;
					
					if(pointers[i] == oList[i].size()){
						blnPointers[i] = false;
					}
				}
			}
			
			/*
			 * After each parse, we check if all blnPointers are false.
			 * If so, we will exit execution.
			 */
			for(int i = 0; i < blnPointers.length; i++){
				if(!blnPointers[i])
					blnCont = false;
				else{
					blnCont = true;
					break;
				}
					
			}
		}
		long endTime = System.currentTimeMillis();
		
		float duration = (float)(endTime - startTime)/1000;
		
		//Finally, print all Doc IDs in finalAnswer array
		System.out.print("\n" + intFinalCounter + " documents are found");
		System.out.print("\n" + totalComps + " comparisons are made");
		System.out.print("\n" + duration + " seconds are used");
		System.out.print("\nResult: ");
		
		for(int i = 0; i < intFinalCounter; i++){
			if(i == intFinalCounter - 1)
				System.out.print(finalAnswer[i]);
			else
				System.out.print(finalAnswer[i] + ", ");
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public static int termAtATimeQueryAnd_Optimize(String content, LinkedList<String>[] listTermFreqArray){
		/*
		 * This Function is same as termAtATimeQueryAnd except for 2 things:
		 * 1) It performs a sort on terms based on increasing postings list length
		 * 2) It returns the value of optimized comparisons. 
		 */
		String tempContent = content.replace(" ", ", ");
		
		String[] arrQueries;
		int intTotalTerms = 0;
		
		arrQueries = content.split(" ");
		intTotalTerms = arrQueries.length;
		LinkedList<String>[] listTermQueries = new LinkedList[intTotalTerms];
		LinkedList<String> objTemp = new LinkedList<String>();
		
		for(int i = 0; i < arrQueries.length; i++){
			for(int j = 0; j < listTermFreqArray.length; j++){
				listTermQueries[i] = new LinkedList<String>();
				if(arrQueries[i].equals(listTermFreqArray[j].get(0))){
					for(int k = 0; k < listTermFreqArray[j].size(); k++)
						listTermQueries[i].add(k, listTermFreqArray[j].get(k));
					break;
				}
			}
		}
		
		int intVal1, intVal2;
		boolean flag = true;
		//Performing Sort on listTermQueries based on Postings Lists length
		while(flag){
			flag = false;
			for(int i = 0; i < listTermQueries.length; i++){
				intVal1 = Integer.parseInt(listTermQueries[i].get(1));	//Store the postings list length in intVal1
				for(int j = i; j < listTermQueries.length; j++){
					intVal2 = Integer.parseInt(listTermQueries[j].get(1));	//Store the postings list length in intVal1
					if(intVal2 < intVal1){
						objTemp = listTermQueries[j];
						listTermQueries[j] = listTermQueries[i];
						listTermQueries[i] = objTemp;
						flag = true;
					}
				}
			}
		}
		
		HashMap<String, LinkedList<String>> partialScores = new HashMap<String, LinkedList<String>>(1000);
		LinkedList oLink = new LinkedList();
		LinkedList oTemp = new LinkedList();
		List<String> oKeys;
		boolean intersection = false;

		int intVal = 0;
		int totalComps = 0;
		String DocID = new String();
		String[] arrVals = new String[2];

		
		for(int i = 0; i < listTermQueries.length; i++){
			//assign all intersections to false
			if(i != 0){
				for(int l = 2; l < listTermQueries[i - 1].size(); l++){
					arrVals = listTermQueries[i - 1].get(l).split("/");
					DocID = arrVals[0];
					/*Using .contains as we are just updating values
					 * in the PartialScores HashMap.
					 * No Document ID comparisons are being made.
					 */
					if(partialScores.containsKey(DocID)){
						oTemp = partialScores.get(DocID);
						oTemp.set(1, false);
						partialScores.put(DocID, oTemp);
					}
				}
			}
			//parse each term
			for(int j = 2; j < listTermQueries[i].size(); j++){
				oLink = new LinkedList();
				arrVals = listTermQueries[i].get(j).split("/");
				DocID = arrVals[0];
				intVal = Integer.parseInt(arrVals[1]);
				
				
				if(i == 0){
					intersection = true;
					oLink.add(intVal);
					oLink.add(intersection);
					partialScores.put(DocID, oLink);
				}
				else{
					int intTemp = 0;
					oKeys = new ArrayList<String>(partialScores.keySet());
					for(int p = 0; p < oKeys.size(); p++){
						totalComps++;
						if(oKeys.get(p).equals(DocID)){
							
							oTemp = new LinkedList<String>();
							oTemp = partialScores.get(DocID);
							intTemp = Integer.parseInt(oTemp.get(0).toString());
							intTemp = intTemp + intVal;
							intersection = true;
							oTemp.set(0, intTemp);
							oTemp.set(1, intersection);
							partialScores.put(DocID, oTemp);
						}
					}
				}
			}
			//Remove all elements in Hash table where intersection = false
			oKeys = new ArrayList<String>(partialScores.keySet());
			for (Object key : oKeys){
				oTemp = new LinkedList<String>();
				oTemp = partialScores.get(key);
				String finalVal = oTemp.get(1).toString();
				
				if(finalVal == "false"){
					totalComps++;
					partialScores.remove(key);
				}
			}
		}
		//Return the total optimized comparisons count
		return totalComps;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int termAtATimeQueryOr_Optimize(String content, LinkedList<String>[] listTermFreqArray){
		/*
		 * This Function is same as termAtATimeQueryOr except for 2 things:
		 * 1) It performs a sort on terms based on increasing postings list length
		 * 2) It returns the value of optimized comparisons. 
		 */
		String[] arrQueries;
		int intTermsFound = 0;
		arrQueries = content.split(" ");
		
		for(int i = 0; i < arrQueries.length; i++){
			for(int j = 0; j < listTermFreqArray.length; j++){
				if(listTermFreqArray[j].get(0).equals(arrQueries[i])){
					intTermsFound++;
				}
			}
		}
		
		LinkedList<String>[] listTermQueries = new LinkedList[intTermsFound];
		int intC = 0;
		for(int i = 0; i < arrQueries.length; i++){
			for(int j = 0; j < listTermFreqArray.length; j++){
				
				if(listTermFreqArray[j].get(0).equals(arrQueries[i])){
					listTermQueries[intC] = new LinkedList<String>();
					for(int k = 0; k < listTermFreqArray[j].size(); k++){
						listTermQueries[intC].add(k, listTermFreqArray[j].get(k));
					}
					intC++;	
					break;
				}
			}
		}
		
		int intVal1, intVal2;
		boolean flag = true;
		LinkedList<String> objTemp = new LinkedList<String>();
		
		//Performing Sort on listTermQueries based on Postings Lists length
		while(flag){
			flag = false;
			for(int i = 0; i < listTermQueries.length; i++){
				intVal1 = Integer.parseInt(listTermQueries[i].get(1));	//Store the postings list length in intVal1
				for(int j = i; j < listTermQueries.length; j++){
					intVal2 = Integer.parseInt(listTermQueries[j].get(1));	//Store the postings list length in intVal1
					if(intVal2 < intVal1){
						objTemp = listTermQueries[j];
						listTermQueries[j] = listTermQueries[i];
						listTermQueries[i] = objTemp;
						flag = true;
					}
				}
			}
		}
		
		HashMap<String, LinkedList<String>> partialScores = new HashMap<String, LinkedList<String>>(1000);
		LinkedList oLink = new LinkedList();
		LinkedList oTemp = new LinkedList();
		List<String> oKeys;
		boolean intersection = false;

		int intVal = 0;
		int totalComps = 0;
		String DocID = new String();
		String[] arrVals = new String[2];
		
		for(int i = 0; i < listTermQueries.length; i++){
			//parse each term
			for(int j = 2; j < listTermQueries[i].size(); j++){
				oLink = new LinkedList<String>();
				arrVals = listTermQueries[i].get(j).split("/");
				DocID = arrVals[0];
				intVal = Integer.parseInt(arrVals[1]);
				
				
				if(i == 0){
					intersection = true;
					oLink.add(intVal);
					oLink.add(intersection);
					partialScores.put(DocID, oLink);
				}
				else{
					int intTemp = 0;
					oKeys = new ArrayList<String>(partialScores.keySet());
					for(int p = 0; p < oKeys.size(); p++){
						totalComps++;
						if(partialScores.containsKey(DocID) == true){
							oTemp = new LinkedList<String>();
							oTemp = partialScores.get(DocID);
							intTemp = Integer.parseInt(oTemp.get(0).toString());
							intTemp = intTemp + intVal;
							intersection = true;
							oTemp.set(0, intTemp);
							oTemp.set(1, intersection);
							partialScores.put(DocID, oTemp);
						}
						else{
							oTemp = new LinkedList<String>();
							intersection = true;
							oTemp.add(intVal);
							oTemp.add(intersection);
							partialScores.put(DocID, oTemp);
						}
					}
				}
			}
		}
		//Return the total optimized comparisons count
		return totalComps;
	}
}
