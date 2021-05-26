package PLH512.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import PLH512.server.Board;
import PLH512.server.City;

public class MCTSv1 {
	final static int ServerPort = 64240;
    final static String username = "myName";
  
    public static void main(String args[]) throws UnknownHostException, IOException, ClassNotFoundException  
    {
    	int numberOfPlayers;
    	int myPlayerID;
    	String myUsername;
    	String myRole;
    	int[] extraPoints = {0,0,0,0};
    	int[] counter = {-1,-1,-1,-1};
    	
        
        // Getting localhost ip 
        InetAddress ip = InetAddress.getByName("localhost"); 
          
        // Establish the connection 
        Socket s = new Socket(ip, ServerPort); 
        System.out.println("\nConnected to server!");
        
        // Obtaining input and out streams 
        ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream dis = new ObjectInputStream(s.getInputStream());  
        
        // Receiving the playerID from the Server
        myPlayerID = (int)dis.readObject();
        myUsername = "User_" + myPlayerID;
        System.out.println("\nHey! My username is " + myUsername);
        
        // Receiving number of players to initialize the board
        numberOfPlayers = (int)dis.readObject();
        
        // Receiving my role for this game
        myRole = (String)dis.readObject();
        System.out.println("\nHey! My role is " + myRole);
        
        // Sending the username to the Server
        dos.reset();
        dos.writeObject(myUsername);
        
        // Setting up the board
        Board[] currentBoard = {new Board(numberOfPlayers)};
        
        // Creating sendMessage thread 
        Thread sendMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() {
            	
            	boolean timeToTalk = false;
            	
            	//MPOREI NA GINEI WHILE  TRUE ME BREAK GIA SINTHIKI??
                while (currentBoard[0].getGameEnded() == false) 
                { 	
                	timeToTalk = ((currentBoard[0].getWhoIsTalking() == myPlayerID)  && !currentBoard[0].getTalkedForThisTurn(myPlayerID));
                	
                	try {
						TimeUnit.MILLISECONDS.sleep(15);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                	
                    try { 
                        // Executing this part of the code once per round
                        if (timeToTalk)
                        {
                        	System.out.println("Time to talk!!");
                        	
                        	// Initializing variables for current round                       	
                        	Board myBoard = currentBoard[0];                       	
                        	//String myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                        	//City myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        	ArrayList<String> myHand = myBoard.getHandOf(myPlayerID);	
                        	int[] myColorCount = {0, 0, 0, 0};                     	
                        	for (int i = 0 ; i < 4 ; i++)
                        		myColorCount[i] =  cardsCounterOfColor(myBoard, myPlayerID, myBoard.getColors(i));
                        	// Printing out my current hand
                        	System.out.println("\nMy current hand...");
                        	printHand(myHand);
                        	// Printing out current color count
                        	System.out.println("\nMy hand's color count...");
                        	for (int i = 0 ; i < 4 ; i++)
                        		System.out.println(myBoard.getColors(i) + " cards count: " + myColorCount[i]);
                        	System.out.println("");
                        	
                        	
                        	String msgToSend;
                        	Node mySoloDecision,mySuggestionDecision;
                        	if (myBoard.getWhoIsPlaying() == myPlayerID){
                        		System.out.println("I am playing !");
                        		//---------------------test-zone---------------------
                        		/*int whoIsPlaying = myPlayerID;
                        		int counter=0;
                        		ArrayList<Action> list = new ArrayList<Action>();
                        		ArrayList<Action> curActioList = findAllPossibleMovesForThisRoundWithCards(myBoard,whoIsPlaying,"",false,new ArrayList<String>());
                        		//System.out.println("The list of action 1st class has "+curActioList.size()+" different possible actions");
                            	for(int i=0;i<curActioList.size();i++) {
                            		ArrayList<Action> curActioList2 = findAllPossibleMovesForThisRoundWithCards(curActioList.get(i).getNewBoardState(),whoIsPlaying,curActioList.get(i).getStringAction(),curActioList.get(i).getOeMove(),curActioList.get(i).getPrevCities());
                            		//System.out.println("The list of action 2nd class has "+curActioList2.size()+" different possible actions");
                            		for(int j=0;j<curActioList2.size();j++) {
                            			ArrayList<Action> curActioList3 = findAllPossibleMovesForThisRoundWithCards(curActioList2.get(j).getNewBoardState(),whoIsPlaying,curActioList2.get(j).getStringAction(),curActioList2.get(j).getOeMove(),curActioList2.get(j).getPrevCities());
                            			//System.out.println("The list of action 3rd class has "+curActioList3.size()+" different possible actions");
                            			for(int k=0;k<curActioList3.size();k++) {
                            				ArrayList<Action> curActioList4 = findAllPossibleMovesForThisRoundWithCards(curActioList3.get(k).getNewBoardState(),whoIsPlaying,curActioList3.get(k).getStringAction(),curActioList3.get(k).getOeMove(),curActioList3.get(k).getPrevCities());
                            				//System.out.println("The list of action 4th class has "+curActioList4.size()+" different possible actions");
                            				counter+= curActioList4.size();
                            				for(int m=0;m<curActioList4.size();m++)
                            					list.add(curActioList4.get(m));
                            				if(counter>100)
                            					break;
                            			}
                            		}
                            	}
                        		System.out.println("The total possible action for this round with cards are: "+counter);
                        		Random rn = new Random();
                        		int min = 0;
                        		int max = counter-1;
                        		int ran  = rn.nextInt((max+1) - min) + min;*/
                        		//---------------------test-zone---------------------\\ end
                   
                        		//mySoloDecision = utcSearchSolo(myBoard,myPlayerID,System.currentTimeMillis(),5000);
                        		//if(mySoloDecision==null) {
                        		//	System.out.println("it is null");
                        		//}
                        		mySoloDecision = utcSearchSolo2(myBoard,myPlayerID,System.currentTimeMillis(),10000);
                        		int[] rewards = new int[4];
                        		int max = -1000;
                        		int maxPlayerId=-1;
                        		String maxAction="";
                        		String tempStr="";
                        		System.out.println("I am deciding");
                        		for(int i=0;i<4;i++) {
                        			if(i==myPlayerID) {
                        				System.out.println("I thought to do: "+mySoloDecision.getPrevMovement());
                        				rewards[i] = defaultPolicy(myBoard,mySoloDecision.getPrevMovement(),myPlayerID);
                        				rewards[i]+=extraPoints[i];
                        				tempStr = mySoloDecision.getPrevMovement();
                        			}else {
                        				System.out.println("The player User_"+i+" thought to do: "+myBoard.getActions(i));
                        				rewards[i] = defaultPolicy(myBoard,"#"+myBoard.getActions(i),myPlayerID);
                        				rewards[i]+=extraPoints[i];
                        				tempStr = "#"+myBoard.getActions(i);
                        			}
                        			if(rewards[i]>max) {
                        				max=rewards[i];
                        				maxPlayerId=i;
                        				maxAction = tempStr;
                        			}
                        		}
                        		if(maxPlayerId!=myPlayerID) {
                        			extraPoints[maxPlayerId]=5; // give extra points to the player that just helped me
                        			counter[maxPlayerId]=0; // update the counter that this player helped at the current round
                        		}
                        	
                        		msgToSend=maxAction;
                        		System.out.println("I decide to do: "+maxAction);
                        		
                        		for(int i=0;i<4;i++){
                        			// run all my co-players that didn't helped me this round
                        			// i!=myPlayerID exist to find all my co-players
                        			// counter[i]!=-1 means that this player had help recently and has extra points
                        			// i!=maxPlayerId skips the player that just helped me
                        			if(i!=myPlayerID && counter[i]!=-1 && i!=maxPlayerId){
                        				counter[i]++;
                        				if(counter[i]==5){// this player did not helped me the last 5 rounds
                        					counter[i]=-1; // reset the counter that this player did not helped my recently 
                        					extraPoints[i]=0; // delete all the extra points from this player
                        				}
                        			}
                        		}
                        		
                        		
                        	}
                        		
                        		//msgToSend = "AP,"+myPlayerID+"#AP,"+myPlayerID+"#AP,"+myPlayerID+"#C,"+myPlayerID+",This was my action#AP,"+myPlayerID+"#C,"+myPlayerID+",This should not be printed..";//"Action";
                            else {
                            	System.out.println("I give suggestion!");
                            	mySuggestionDecision = utcSearchSuggestion2(myBoard,myBoard.getWhoIsPlaying(),myPlayerID,System.currentTimeMillis(),8000);
                            	//mySuggestionDecision = utcSearchSuggestion(myBoard,myBoard.getWhoIsPlaying(),myPlayerID,System.currentTimeMillis(),1000);
                            	//msgToSend = mySuggestionDecision.getPrevMovement();
                            	//msgToSend = "#C,"+myPlayerID+",This was my recommendation"; //"Recommendation"
                            	msgToSend = mySuggestionDecision.getPrevMovement();
                            }
                        	
                        	// NO EDIT FROM HERE AND ON (EXEPT FUNCTIONS OUTSIDE OF MAIN() OF COURSE)
                        	
                        	// Writing to Server
                        	dos.flush();
                        	dos.reset();
                        	if (msgToSend != "")
                        		msgToSend = msgToSend.substring(1); // Removing the initial delimeter
                        	dos.writeObject(msgToSend);
                        	System.out.println(myUsername + " : I've just sent my " + msgToSend);
                        	currentBoard[0].setTalkedForThisTurn(true, myPlayerID);
                        }
                    } catch (IOException e) { 
                        e.printStackTrace(); 
					}
                } 
            } 
        }); 
          
        // Creating readMessage thread 
        Thread readMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
            	
            	
                while (currentBoard[0].getGameEnded() == false) { 
                    try { 
                        
                    	// Reading the current board
                    	//System.out.println("READING!!!");
                    	currentBoard[0] = (Board)dis.readObject();
                    	//System.out.println("READ!!!");
                    	
                    	// Read and print Message to all clients
                    	String prtToScreen = currentBoard[0].getMessageToAllClients();
                    	if (!prtToScreen.equalsIgnoreCase(""))
                    		System.out.println(prtToScreen);
                    	
                    	// Read and print Message this client
                    	prtToScreen = currentBoard[0].getMessageToClient(myPlayerID);
                    	if (!prtToScreen.equalsIgnoreCase(""))
                    		System.out.println(prtToScreen);
                    	
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    } catch (ClassNotFoundException e) {
						e.printStackTrace();
					} 
                } 
            } 
        }); 
        
        // Starting the threads
        readMessage.start();
        sendMessage.start(); 
        
        // Checking if the game has ended
        while (true) 
        {
        	if (currentBoard[0].getGameEnded() == true){
        		System.out.println("\nGame has finished. Closing resources.. \n");
        		//scn.close();
            	s.close();
            	System.out.println("Recources closed succesfully. Goodbye!");
            	System.exit(0);
            	break;
        	}
        
        }
    } 
    
    // --> Useful functions <--
    public static boolean existCity(ArrayList<String> prevCities,String city){
		if(prevCities!=null) {
			for(int i=0;i<prevCities.size();i++)
				if(prevCities.get(i).equals(city))
					return true;
		}
		return false;
	}
    
    public static Board copyBoard (Board boardToCopy)
    {
    	Board copyOfBoard;
    	
    	try {
    	     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	     ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
    	     outputStrm.writeObject(boardToCopy);
    	     ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    	     ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
    	     copyOfBoard = (Board)objInputStream.readObject();
    	     return copyOfBoard;
    	   }
    	   catch (Exception e) {
    	     e.printStackTrace();
    	     return null;
    	   }
    }
    
    public static String getDirectionToMove (String startingCity, String goalCity, ArrayList<citiesWithDistancesObj> distanceMap, Board myBoard)
    {
    	City startingCityObj = myBoard.searchForCity(startingCity);
    	
    	int minDistance = distanceFrom(goalCity, distanceMap);
    	int testDistance = 999;
    	
    	String directionToDrive = null;
    	String testCity = null;
    	
    	for (int i = 0 ; i < startingCityObj.getNeighboursNumber() ; i++)
    	{
    		ArrayList<citiesWithDistancesObj> testDistanceMap = new ArrayList<citiesWithDistancesObj>();
    		testDistanceMap.clear();
    		
    		testCity = startingCityObj.getNeighbour(i);
    		testDistanceMap = buildDistanceMap(myBoard, testCity, testDistanceMap);
    		testDistance = distanceFrom(goalCity, testDistanceMap);
    		
    		if (testDistance < minDistance)
    		{
    			minDistance = testDistance;
    			directionToDrive = testCity;
    		}
    	}
    	return directionToDrive;
    }
    
    
    public static String getMostInfectedInRadius(int radius, ArrayList<citiesWithDistancesObj> distanceMap, Board myBoard)
    {
    	int maxCubes = -1;
    	String mostInfected = null;
    	
    	for (int i = 0 ; i < distanceMap.size() ; i++)
    	{
    		if (distanceMap.get(i).getDistance() <= radius)
    		{
    			City cityToCheck = myBoard.searchForCity(distanceMap.get(i).getName());
    			
    			if (cityToCheck.getMaxCube() > maxCubes)
    			{
    				mostInfected = cityToCheck.getName();
    				maxCubes = cityToCheck.getMaxCube();
    			}
    		}
    	}
    	
    	return mostInfected;
    }
    
    // Count how many card of the color X player X has
    public static int cardsCounterOfColor(Board board, int  playerID, String color)
    {
    	int cardsCounter = 0;
    	
    	for (int i = 0 ; i < board.getHandOf(playerID).size() ; i++)
    		if (board.searchForCity(board.getHandOf(playerID).get(i)).getColour().equals(color))
    			cardsCounter++;
    	
    	return cardsCounter;
    }
    
    public static void printHand(ArrayList<String> handToPrint)
    {
    	for (int i = 0 ; i < handToPrint.size() ; i++)
    		System.out.println(handToPrint.get(i));
    }
    
    public static boolean alredyInDistanceMap(ArrayList<citiesWithDistancesObj> currentMap, String cityName)
    {
    	for (int i = 0 ; i < currentMap.size() ; i++)
    		if (currentMap.get(i).getName().equals(cityName))
    			return true;
    	
    	return false;
    }
    
    public static boolean isInDistanceMap (ArrayList<citiesWithDistancesObj> currentMap, String cityName)
    {
    	for (int i = 0 ; i < currentMap.size() ; i++)
    	{
    		if (currentMap.get(i).getName().equals(cityName))
    			return true;
    	}
    	return false;
    }
    
    public static void printDistanceMap(ArrayList<citiesWithDistancesObj> currentMap)
    {
    	for (int i = 0 ; i < currentMap.size() ; i++)
    		System.out.println("Distance from " + currentMap.get(i).getName() + ": " + currentMap.get(i).getDistance());
    }
    
    public static int distanceFrom(String cityToFind, ArrayList<citiesWithDistancesObj> currentDistanceMap)
    {
    	int result = -1;
    	
    	for (int i = 0 ; i < currentDistanceMap.size() ; i++)
    		if (currentDistanceMap.get(i).getName().equals(cityToFind))
    			result = currentDistanceMap.get(i).getDistance();
    	
    	return result;
    }
    
    public static int numberOfCitiesWithDistance(int distance, ArrayList<citiesWithDistancesObj> currentDistanceMap)
    {
    	int count = 0;
    	
    	for (int i = 0 ; i < currentDistanceMap.size() ; i++)
    		if (currentDistanceMap.get(i).getDistance() == distance)
    			count++;
    	
    	return count;
    }
    
    public static ArrayList<citiesWithDistancesObj> buildDistanceMap(Board myBoard, String currentCityName, ArrayList<citiesWithDistancesObj> currentMap)
    {
    	currentMap.clear();
    	currentMap.add(new citiesWithDistancesObj(currentCityName, myBoard.searchForCity(currentCityName), 0));

    	for (int n = 0 ; n < 15 ; n++)
    	{
        	for (int i = 0 ; i < currentMap.size() ; i++)
        	{
        		if (currentMap.get(i).getDistance() == (n-1))
        		{
        			for (int j = 0 ; j < currentMap.get(i).getCityObj().getNeighboursNumber() ; j++)
        			{
        				String nameOfNeighbor = currentMap.get(i).getCityObj().getNeighbour(j);
        				
        				if (!(alredyInDistanceMap(currentMap, nameOfNeighbor)))
        					currentMap.add(new citiesWithDistancesObj(nameOfNeighbor, myBoard.searchForCity(nameOfNeighbor), n));
        			}
        		}
        	}
    	}
    	
    	return currentMap;
    }
    
    
    // --> Actions <--
    public static int[] moveCounter(String prevActions){
    	String delimiterActions = "#";
		String delimiterVariables = ",";
		String[] actions;
		String[] variables;
		int dt=0;
		int df=0;
		int cf=0;
		int sf=0;
		int oet=0;
		actions = prevActions.split(delimiterActions);
		for (int i = 0 ; i < actions.length; i++){
			variables = actions[i].split(delimiterVariables);
			if (variables[0].equals("DT")){
				dt++;
			}
			else if (variables[0].equals("DF")){
				df++;
			}
			else if (variables[0].equals("CF")){
				cf++;
			}	
			else if (variables[0].equals("SF")){
				sf++;
			}
			else if (variables[0].equals("OET")){
				oet++;
			}
		}
		int[] moveCounter= {dt,df,cf,sf,oet};
		return moveCounter;
    }
    
    public static ArrayList<Action> findAllPossibleMovesForThisRoundWithCards(Board currentBoard,int whoIsPlaying,String prevActions,Boolean oeMove,ArrayList<String> prevCities){
    	int[] myColorCount = {0, 0, 0, 0};
    	int[] specificMoveCount = moveCounter(prevActions);
    	boolean[] moves = {false,false,false,false,false};
    	Random ran = new Random();
    	int num;
    	Action temp;
    	boolean found=false;
    	for (int i = 0 ; i < 4 ; i++)
    		myColorCount[i] =  cardsCounterOfColor(currentBoard, whoIsPlaying, currentBoard.getColors(i));
    	ArrayList<Action> allPossibleActions = new ArrayList<Action>();
    	ArrayList<String> hand = currentBoard.getHandOf(whoIsPlaying);
    	Board newBoard;
    	String playerCityPossitionName = currentBoard.getPawnsLocations(whoIsPlaying);
    	City playerCityPossition = currentBoard.searchForCity(playerCityPossitionName);
    	// possibilities
    	// 0 -> drive/ferry
    	// 1 -> direct flight
    	// 2 -> Charter Flight
    	// 3 -> Shuttle Flight
    	for(int i=0;i<4;i++){
    		if(specificMoveCount[i]>0){
        		num=ran.nextInt(101);
        		if(specificMoveCount[i]==1 && num<=50)
        			moves[i]=true;
        		else if(specificMoveCount[i]==2 && num<=30)
        			moves[i]=true;
        		else if(specificMoveCount[i]==3 & num<10)
        			moves[i]=true;
        	}else {
        		moves[i]=true;
        	}
    	}
    	// General Moves
    	// drive/ferry
    	// all neighbors
    	if(moves[0]) {
    		for(int i=0;i<playerCityPossition.getNeighboursNumber();i++){ // run for all the neighbors of my city
        		String curNeighbCityName = playerCityPossition.getNeighbour(i);
        		// remove circle moves -> it prevents you to go back to a city that you were before
        		found=existCity(prevCities,curNeighbCityName);
        		if(!found) {
        			newBoard = currentBoard.deepClone();
            		newBoard.driveTo2(whoIsPlaying, curNeighbCityName);
            		temp = new Action(prevActions+toTextDriveTo(whoIsPlaying,curNeighbCityName),newBoard,prevCities);
            		temp.addCity(playerCityPossitionName);
            		allPossibleActions.add(temp);
        		}
        		}
    	}
    	// run my hand
    	for(int i=0;i<hand.size();i++) // run for all the cards of the hand
    	{	
    			found=existCity(prevCities,hand.get(i));
    			if(hand.get(i)!=playerCityPossitionName){ // if the card it isn't the city we are already in 
    				// direct flight
    				if(!found && moves[1]==true){
    					newBoard = currentBoard.deepClone();
        				newBoard.directFlight2(whoIsPlaying, hand.get(i));
        				temp = new Action(prevActions+toTextDirectFlight(whoIsPlaying,hand.get(i)),newBoard,prevCities);
        				temp.addCity(playerCityPossitionName);
        				allPossibleActions.add( temp );
    				}
    				}
    			else{ 
    				// we have a card of our current location 
    				// Charter Flight
    				//run all cities
    				for(int l=0;l<currentBoard.getCitiesCount();l++){
    					if(!found && moves[2]==true){
    						newBoard = currentBoard.deepClone();
        					newBoard.charterFlight2(whoIsPlaying, currentBoard.searchForCity(l).getName());
        					temp = new Action(prevActions+toTextCharterFlight(whoIsPlaying,currentBoard.searchForCity(l).getName()),newBoard,prevCities);
        					temp.addCity(playerCityPossitionName);
        					allPossibleActions.add(temp);
    					}
    			    	}
    				//Build RS if we are not Operations Expert
    				//System.out.println("We have the card that we are aleready there!!!--------------------------------------------------------------------");
    				boolean isOperationsExpert = currentBoard.getRoleOf(whoIsPlaying).equals("Operations Expert");
    				//System.out.println("isOperationsExpert = "+isOperationsExpert);
    				//if(playerCityPossition.getHasReseachStation()==false)
    				//	System.out.println("we don't have RS here!!!!+++++++");
    				//else
    				//	System.out.println("We have a RS already here.....");
    				if(isOperationsExpert==false && playerCityPossition.getHasReseachStation()==false){ // do we have a RS in our current location?
    					//System.out.println("!!!!!!!!!!!I am not an OE and here we don't have RS!!!!!!!!!!!");
    					if(currentBoard.getResearchStationsBuild()==currentBoard.getResearchStationsLimit()){ // we have already 6 RS
    						ArrayList<String> RSLocations = currentBoard.getRSLocations();
    						for(int m=0;m<RSLocations.size();m++){
    							newBoard = currentBoard.deepClone();
    							newBoard.removeRS2(whoIsPlaying, RSLocations.get(m));
    							newBoard.buildRS2(whoIsPlaying, playerCityPossitionName);
    							allPossibleActions.add( new Action(prevActions+ toTextRemoveRS(whoIsPlaying,RSLocations.get(m))+toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard,prevCities) );
    							}
    						}
    					else{
    						newBoard = currentBoard.deepClone();
    						newBoard.buildRS2(whoIsPlaying, playerCityPossitionName);
    						newBoard.setResearchStationsBuild(newBoard.getResearchStationsBuild()+1);
    						allPossibleActions.add( new Action(prevActions+ toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard,prevCities) );
    						}
    				}
    			}
    	}
    	
    	if(playerCityPossition.getHasReseachStation())// the player is in a city with a research station
    	{
    		// Shuttle Flight
    		//travel to another RS
    		ArrayList<String> RSLocations = currentBoard.getRSLocations();
    		for(int i=0;i<RSLocations.size();i++){
    			found=existCity(prevCities,RSLocations.get(i));
    			if(RSLocations.get(i)!=playerCityPossitionName && found==false && moves[3]==true){
    				newBoard = currentBoard.deepClone();
    				newBoard.shuttleFlight2(whoIsPlaying, RSLocations.get(i));
    				temp = new Action(prevActions+toTextShuttleFlight(whoIsPlaying,RSLocations.get(i)),newBoard,prevCities);
    				temp.addCity(playerCityPossitionName);
    				allPossibleActions.add( temp );
    			    }}
    		
    		//Discover a Cure
    		boolean isScientist = currentBoard.getRoleOf(whoIsPlaying).equals("Scientist");
    		Boolean justEqual = false;
    		int cardsNeededForCure;
    		String [] cards = new String[4];
    		if(isScientist){
    			cardsNeededForCure = 3;
    		}else {
    			cardsNeededForCure = 4;
    		}
    		
    		// BLACK
    		if(myColorCount[0] >= cardsNeededForCure && currentBoard.getCured("Black")==false){
    			cards[0]=null;
        		cards[1]=null;
        		cards[2]=null;
        		cards[3]=null;
        		justEqual = false;
    			if(myColorCount[0]==cardsNeededForCure)
    				justEqual=true;
    			int count=0;
        		if(justEqual){
        			for(int h=0;h<hand.size();h++) {
            			if( currentBoard.searchForCity(hand.get(h)).getColour().equals("Black")){
            				cards[count] = hand.get(h);
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}else{
        			for(int h=0;h<hand.size();h++) {
            			if( currentBoard.searchForCity(hand.get(h)).getColour().equals("Black") && hand.get(h)!=playerCityPossitionName){
            				cards[count] = hand.get(h);
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}
        		newBoard = currentBoard.deepClone();
        		newBoard.cureDisease2(whoIsPlaying, "Black", cards[0], cards[1], cards[2], cards[3]);
        		allPossibleActions.add( new Action( prevActions+toTextCureDisease(whoIsPlaying,"Black",cards[0],cards[1],cards[2],cards[3]),newBoard,prevCities ) );
    		}
    		//YELLOW
    		if(myColorCount[1] >= cardsNeededForCure && currentBoard.getCured("Yellow")==false){
    			cards[0]=null;
        		cards[1]=null;
        		cards[2]=null;
        		cards[3]=null;
        		justEqual = false;
    			if(myColorCount[1]==cardsNeededForCure)
    				justEqual=true;
    			int count=0;
        		if(justEqual){
        			for(int h=0;h<hand.size();h++) {
            			if( currentBoard.searchForCity(hand.get(h)).getColour().equals("Yellow")){
            				cards[count] = hand.get(h);
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}else{
        			for(int h=0;h<hand.size();h++) {
            			if( currentBoard.searchForCity(hand.get(h)).getColour().equals("Yellow") && hand.get(h)!=playerCityPossitionName){
            				cards[count] = hand.get(h);
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}
        		newBoard = currentBoard.deepClone();
        		newBoard.cureDisease2(whoIsPlaying, "Yellow", cards[0], cards[1], cards[2], cards[3]);
        		allPossibleActions.add( new Action( prevActions+toTextCureDisease(whoIsPlaying,"Yellow",cards[0],cards[1],cards[2],cards[3]),newBoard,prevCities ) );
    		}
    		//BLUE
    		if(myColorCount[2] >= cardsNeededForCure && currentBoard.getCured("Blue")==false){
    			cards[0]=null;
        		cards[1]=null;
        		cards[2]=null;
        		cards[3]=null;
        		justEqual = false;
    			if(myColorCount[2]==cardsNeededForCure)
    				justEqual=true;
    			int count=0;
        		if(justEqual){
        			for(int h=0;h<hand.size();h++) {
            			if( currentBoard.searchForCity(hand.get(h)).getColour().equals("Blue")){
            				cards[count] = hand.get(h);
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}else{
        			for(int h=0;h<hand.size();h++) {
            			if( currentBoard.searchForCity(hand.get(h)).getColour().equals("Blue") && hand.get(h)!=playerCityPossitionName){
            				cards[count] = hand.get(h);
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}
        		newBoard = currentBoard.deepClone();
        		newBoard.cureDisease2(whoIsPlaying, "Blue", cards[0], cards[1], cards[2], cards[3]);
        		allPossibleActions.add( new Action( prevActions+toTextCureDisease(whoIsPlaying,"Blue",cards[0],cards[1],cards[2],cards[3]),newBoard,prevCities ) );
    		}
    		//RED
    		if(myColorCount[3] >= cardsNeededForCure && currentBoard.getCured("Red")==false){
    			cards[0]=null;
        		cards[1]=null;
        		cards[2]=null;
        		cards[3]=null;
        		justEqual = false;
    			if(myColorCount[3]==cardsNeededForCure)
    				justEqual=true;
    			int count=0;
        		if(justEqual){
        			for(int h=0;h<hand.size();h++) {
            			if( currentBoard.searchForCity(hand.get(h)).getColour().equals("Red")){
            				cards[count] = hand.get(h);
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}else{
        			for(int h=0;h<hand.size();h++) {
            			if( currentBoard.searchForCity(hand.get(h)).getColour().equals("Red") && hand.get(h)!=playerCityPossitionName){
            				cards[count] = hand.get(h);
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}
        		newBoard = currentBoard.deepClone();
        		newBoard.cureDisease2(whoIsPlaying, "Red", cards[0], cards[1], cards[2], cards[3]);
        		allPossibleActions.add( new Action( prevActions+toTextCureDisease(whoIsPlaying,"Red",cards[0],cards[1],cards[2],cards[3]),newBoard,prevCities ) );
    		}
    		
    		
    	}
    	// In our current location
    	// Treat disease
    	
    	if(playerCityPossition.getBlackCubes() > 0 || playerCityPossition.getYellowCubes() > 0 || playerCityPossition.getBlueCubes() > 0 || playerCityPossition.getRedCubes() > 0) {
    		String colorToTreat = playerCityPossition.getMaxCubeColor();
    		//System.out.println("We can treat! = "+colorToTreat+" city = "+playerCityPossition.getName());
    		newBoard = currentBoard.deepClone();
        	newBoard.treatDisease2(whoIsPlaying, playerCityPossitionName, colorToTreat);
        	allPossibleActions.add( new Action(prevActions+toTextTreatDisease(whoIsPlaying,playerCityPossitionName,colorToTreat),newBoard,prevCities) );
    	}
    	/*colorToTreat = playerCityPossition.getMaxCubeColor();
    	if (playerCityPossition.getBlackCubes() > 0)
			colorToTreat = "Black";
		else if ( playerCityPossition.getYellowCubes() > 0)
			colorToTreat = "Yellow";
		else if (playerCityPossition.getBlueCubes() > 0)
			colorToTreat = "Blue";
		else if (playerCityPossition.getRedCubes() > 0)
			colorToTreat = "Red";
    	if(colorToTreat!=null) {
    		System.out.println("We can treat the color = "+ colorToTreat);
    		newBoard = currentBoard.deepClone();
        	newBoard.treatDisease2(whoIsPlaying, playerCityPossitionName, colorToTreat);
        	allPossibleActions.add( new Action(prevActions+toTextTreatDisease(whoIsPlaying,playerCityPossitionName,colorToTreat),newBoard,prevCities) );
    	}*/
    	
    	// Special Abilities
    	//---Operations Expert---
    	// build RS
    	boolean isOperationsExpert = currentBoard.getRoleOf(whoIsPlaying).equals("Operations Expert");
    	if(playerCityPossition.getHasReseachStation()==false){
    		if(isOperationsExpert){
    			if(currentBoard.getResearchStationsBuild()==currentBoard.getResearchStationsLimit()) {
    				ArrayList<String> RSLocations = currentBoard.getRSLocations();
    	    		for(int i=0;i<RSLocations.size();i++){
    	    			newBoard = currentBoard.deepClone();
    	    			newBoard.removeRS2(whoIsPlaying, RSLocations.get(i));
    	    			newBoard.buildRS2(whoIsPlaying, playerCityPossitionName);
    	    			allPossibleActions.add( new Action(prevActions+ toTextRemoveRS(whoIsPlaying,RSLocations.get(i))+toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard,prevCities) );
    	    		}
    			}else {
    				newBoard = currentBoard.deepClone();
        			newBoard.buildRS2(whoIsPlaying, playerCityPossitionName);
        			newBoard.setResearchStationsBuild(newBoard.getResearchStationsBuild()+1);
        			allPossibleActions.add( new Action(prevActions+ toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard,prevCities) );
    			}
    		}
    	}else{
    		if(oeMove==false && isOperationsExpert==true) {
    			// once per turn, move from a research station to any city by discarding any City card
    			for(int i=0;i<hand.size();i++) {
    				for(int j=0;j<currentBoard.getCitiesCount();j++){
    					String curCity = currentBoard.searchForCity(j).getName();
    					found=existCity(prevCities, curCity  );
    					if(!found){
    						newBoard = currentBoard.deepClone();
        					newBoard.operationsExpertTravel2(whoIsPlaying, curCity, hand.get(i));
        					temp = new Action(prevActions+toTextOpExpTravel(whoIsPlaying,curCity,hand.get(i)),newBoard,true,prevCities);
        					temp.addCity(playerCityPossitionName);
        					allPossibleActions.add( temp );
    					}
    				}
    			}
    		}
    	}
    	// No action
    	//allPossibleActions.add( new Action( prevActions+toTextActionPass(whoIsPlaying),currentBoard,prevCities) );
    	return allPossibleActions;
    }
    public static ArrayList<Action> findAllPossibleMovesForThisRoundWithoutCards(Board currentBoard,int whoIsPlaying,String prevActions,ArrayList<String> prevCities){
    	//System.out.println("Inside of find all actions without cards");
    	ArrayList<Action> allPossibleActions = new ArrayList<Action>();
    	Board newBoard;
    	boolean found;
    	Action temp;
    	String playerCityPossitionName = currentBoard.getPawnsLocations(whoIsPlaying);
    	City playerCityPossition = currentBoard.searchForCity(playerCityPossitionName);
    	//System.out.println("does my city have a RS? = "+ playerCityPossition.getHasReseachStation());
    	int neighbNum = playerCityPossition.getNeighboursNumber();
    	// General Moves
    	// drive/ferry all possible neighbors cities
    	for(int i=0;i<neighbNum;i++){
    		String curNeighbCityName = playerCityPossition.getNeighbour(i);
    		found=existCity(prevCities,curNeighbCityName);
    		if(!found){
    			newBoard = currentBoard.deepClone();
        		newBoard.driveTo2(whoIsPlaying, curNeighbCityName);
        		temp = new Action(prevActions+toTextDriveTo(whoIsPlaying,curNeighbCityName),newBoard,prevCities);
        		temp.addCity(playerCityPossitionName);
        		allPossibleActions.add( temp );
    		}
    	}
    	// Shuttle Flight
    	if(playerCityPossition.getHasReseachStation()){
    		// the player is in a city with a research station
    		ArrayList<String> RSLocations = currentBoard.getRSLocations();
    		for(int i=0;i<RSLocations.size();i++){
    			if(RSLocations.get(i)!=playerCityPossitionName){
    				found=existCity(prevCities,RSLocations.get(i));
    				if(!found) {
    					newBoard = currentBoard.deepClone();
        				newBoard.shuttleFlight2(whoIsPlaying, RSLocations.get(i));
        				temp = new Action(prevActions+toTextShuttleFlight(whoIsPlaying,RSLocations.get(i)),newBoard,prevCities);
        				temp.addCity(playerCityPossitionName);
        				allPossibleActions.add( temp );
    				}
    			}
    		}
    	}
    	// Treat disease
    	String colorToTreat = null;
    	if (playerCityPossition.getBlackCubes() > 0)
			colorToTreat = "Black";
		else if ( playerCityPossition.getYellowCubes() > 0)
			colorToTreat = "Yellow";
		else if (playerCityPossition.getBlueCubes() > 0)
			colorToTreat = "Blue";
		else if (playerCityPossition.getRedCubes() > 0)
			colorToTreat = "Red";
    	if(colorToTreat!=null) {
    		newBoard = currentBoard.deepClone();
        	newBoard.treatDisease2(whoIsPlaying, playerCityPossitionName, colorToTreat);
        	allPossibleActions.add( new Action(prevActions+toTextTreatDisease(whoIsPlaying,playerCityPossitionName,colorToTreat),newBoard,prevCities) );
    	}
    	// Special Abilities
    	//---Operations Expert---
    	// build RS
    	boolean isOperationsExpert = currentBoard.getRoleOf(whoIsPlaying).equals("Operations Expert");
    	if(playerCityPossition.getHasReseachStation()==false && isOperationsExpert==true){
    			if(currentBoard.getResearchStationsBuild()==currentBoard.getResearchStationsLimit()) {
    				ArrayList<String> RSLocations = currentBoard.getRSLocations();
    	    		for(int i=0;i<RSLocations.size();i++){
    	    			newBoard = currentBoard.deepClone();
    	    			newBoard.removeRS2(whoIsPlaying, RSLocations.get(i));
    	    			newBoard.buildRS2(whoIsPlaying, playerCityPossitionName);
    	    			allPossibleActions.add( new Action(prevActions+ toTextRemoveRS(whoIsPlaying,RSLocations.get(i))+toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard,prevCities) );
    	    		}
    			}else {
    				newBoard = currentBoard.deepClone();
        			newBoard.buildRS2(whoIsPlaying, playerCityPossitionName);
        			newBoard.setResearchStationsBuild(newBoard.getResearchStationsBuild()+1);
        			allPossibleActions.add( new Action(prevActions+ toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard,prevCities) );
    			}
    	}
    	// once per turn, move from a research station to any city by discarding any City card - not implemented in this method
    	// No action
    	//allPossibleActions.add( new Action( toTextActionPass(whoIsPlaying),currentBoard) );
    	return allPossibleActions;
    }
    public static ArrayList<Action> getAllNewStatesWithoutCards(Board currentBoard,int whoIsPlaying){
    	ArrayList<Action> list = new ArrayList<Action>();
    	ArrayList<Action> curActioList = findAllPossibleMovesForThisRoundWithoutCards(currentBoard,whoIsPlaying,"",new ArrayList<String>());
    	for(int i=0;i<curActioList.size();i++){
    		ArrayList<Action> curActioList2 = findAllPossibleMovesForThisRoundWithoutCards(curActioList.get(i).getNewBoardState(),whoIsPlaying,curActioList.get(i).getStringAction(),curActioList.get(i).getPrevCities());
    		for(int j=0;j<curActioList2.size();j++){
    			ArrayList<Action> curActioList3 = findAllPossibleMovesForThisRoundWithoutCards(curActioList2.get(j).getNewBoardState(),whoIsPlaying,curActioList2.get(j).getStringAction(),curActioList2.get(j).getPrevCities());
    			for(int k=0;k<curActioList3.size();k++){
    				ArrayList<Action> curActioList4 = findAllPossibleMovesForThisRoundWithoutCards(curActioList3.get(k).getNewBoardState(),whoIsPlaying,curActioList3.get(k).getStringAction(),curActioList3.get(k).getPrevCities());
    				for(int f=0;f<curActioList4.size();f++)
    					list.add(curActioList4.get(f));
    			}
    		}
    	}
    	return list;
    }
    //findAllPossibleMovesForThisRoundWithCards(Board currentBoard,ArrayList<String> hand,int whoIsPlaying,String prevActions,Boolean oeMove)
    public static ArrayList<Action> getAllNewStatesWithCards(Board currentBoard,int whoIsPlaying){
    	ArrayList<Action> list = new ArrayList<Action>();
    	ArrayList<Action> curActioList = findAllPossibleMovesForThisRoundWithCards(currentBoard,whoIsPlaying,"",false,new ArrayList<String>());
    	for(int i=0;i<curActioList.size();i++) {
    		ArrayList<Action> curActioList2 = findAllPossibleMovesForThisRoundWithCards(curActioList.get(i).getNewBoardState(),whoIsPlaying,curActioList.get(i).getStringAction(),curActioList.get(i).getOeMove(),curActioList.get(i).getPrevCities());
    		for(int j=0;j<curActioList2.size();j++) {
    			ArrayList<Action> curActioList3 = findAllPossibleMovesForThisRoundWithCards(curActioList2.get(j).getNewBoardState(),whoIsPlaying,curActioList2.get(j).getStringAction(),curActioList2.get(j).getOeMove(),curActioList2.get(j).getPrevCities());
    			for(int k=0;k<curActioList3.size();k++) {
    				ArrayList<Action> curActioList4 = findAllPossibleMovesForThisRoundWithCards(curActioList3.get(k).getNewBoardState(),whoIsPlaying,curActioList3.get(k).getStringAction(),curActioList3.get(k).getOeMove(),curActioList3.get(k).getPrevCities());
    				for(int f=0;f<curActioList4.size();f++)
    					list.add(curActioList4.get(f));
    			}
    		}
    	}
    	return list;
    }
    public static Node utcSearchSolo(Board currentBoard,int whoIsPlaying,long timeStarted,long budgetInMilliseconds){
    	Node v0 = new Node(currentBoard,whoIsPlaying);
    	Node v,chld;
    	int delta = 0;
    	while(System.currentTimeMillis()-timeStarted<budgetInMilliseconds){	
    		// Tree Policy
    		v = v0;
    		while(!v.isTerminalNode())
    		{ 
    				if(!v.isExpanded()){ // not expanded  
    					expand(v); // expand all , price them and backup them
    					if(v.getChilds()!=null){
    						for(int d=0;d<v.getChilds().size();d++) {
        						chld = v.getChilds().get(d);
        						delta = defaultPolicy(chld.getCurBoardState(),chld.getPrevMovement(),v.getNextPlayerPlaying());
        						backUp(chld,delta);
        					}
    					}
    				}
    				else{ // is expanded
    					v = bestChild(v,true);
    				}
    		}
    		delta = defaultPolicy(v.getCurBoardState(),v.getPrevMovement(),v.getParent().getNextPlayerPlaying());
        	backUp(v,delta);
    	}
    	return bestChild(v0,false);
    }
    public static Node utcSearchSolo2(Board currentBoard,int whoIsPlaying,long timeStarted,long budgetInMilliseconds){
    	Node v0 = new Node(currentBoard,whoIsPlaying);
    	Node v,chld;
    	int delta = 0;
    	while(System.currentTimeMillis()-timeStarted<budgetInMilliseconds){
    		// Tree Policy
    		v = v0;
    		while(!v.isTerminalNode2())
    		{ 
    				if(!v.isExpanded()){ // not expanded  
    					expand2(v,whoIsPlaying); // expand all , price them and backup them
    					if(v.getChilds()!=null){
    						for(int d=0;d<v.getChilds().size();d++) {
        						chld = v.getChilds().get(d);
        						delta = defaultPolicy(chld.getCurBoardState(),chld.getPrevMovement(),whoIsPlaying);
        						backUp(chld,delta);
        					}
    					}
    				}
    				else{ // is expanded
    					v = bestChild(v,true);
    				}
    		}
    		delta = defaultPolicy(v.getCurBoardState(),v.getPrevMovement(),whoIsPlaying);
        	backUp(v,delta);
    	}
    	return bestChild(v0,false);
    }
    public static Node utcSearchSuggestion(Board currentBoard,int whoIsPlaying,int whoAmI,long timeStarted,long budgetInMilliseconds){
    	Node v0 = new Node(currentBoard,whoAmI,whoIsPlaying);
    	Node v,chld;
    	int delta = 0;
    	while(System.currentTimeMillis()-timeStarted<budgetInMilliseconds){	
    		// Tree Policy
    		v = v0;
    		while(!v.isTerminalNode())
    		{ 
    				if(!v.isExpanded()){ // not expanded 
    					expand(v); // expand all , price them and backup them
    					if(v.getChilds()!=null) {
    						for(int d=0;d<v.getChilds().size();d++) {
        						chld = v.getChilds().get(d);
        						delta = defaultPolicy(chld.getCurBoardState(),chld.getPrevMovement(),v.getNextPlayerPlaying());
        						backUp(chld,delta);
        					}
    					}
    				}
    				else{ // is expanded
    					v = bestChild(v,true);
    				}
    		}
    		delta = defaultPolicy(v.getCurBoardState(),v.getPrevMovement(),v.getParent().getNextPlayerPlaying());
        	backUp(v,delta);
    	}
    	return bestChild(v0,false);
    }
    public static Node utcSearchSuggestion2(Board currentBoard,int whoIsPlaying,int whoAmI,long timeStarted,long budgetInMilliseconds){
    	Node v0 = new Node(currentBoard,whoAmI,whoIsPlaying);
    	Node v,chld;
    	int delta = 0;
    	while(System.currentTimeMillis()-timeStarted<budgetInMilliseconds){	
    		// Tree Policy
    		v = v0;
    		while(!v.isTerminalNode3())
    		{ 
    				if(!v.isExpanded()){ // not expanded 
    					expand3(v,whoIsPlaying,whoAmI); // expand all , price them and backup them 
    					if(v.getChilds()!=null){
    						for(int d=0;d<v.getChilds().size();d++){
        						chld = v.getChilds().get(d);
        						delta = defaultPolicy(chld.getCurBoardState(),chld.getPrevMovement(),v.getNextPlayerPlaying());
        						backUp(chld,delta);
        					}
    					}	
    				}
    				else{ // is expanded
    					v = bestChild(v,true);
    				}
    		}
    		delta = defaultPolicy(v.getCurBoardState(),v.getPrevMovement(),v.getParent().getNextPlayerPlaying());
        	backUp(v,delta);
    	}
    	return bestChild(v0,false);
    }
    
    public static void expand(Node v){
    	ArrayList<String> hand = v.getCards(v.getNextPlayerPlaying());
    	ArrayList<Action> rootChilds;
    	if(hand==null) {
    		rootChilds = getAllNewStatesWithoutCards(v.getCurBoardState(),v.getNextPlayerPlaying());
    	}else {
    		rootChilds = getAllNewStatesWithCards(v.getCurBoardState(),v.getNextPlayerPlaying());
    	}
    	v.expand(rootChilds);
    }
    public static void expand2(Node v,int whoAmI){
    	ArrayList<String> hand = null;
    	if(v.getNextPlayerPlaying()==whoAmI)
    		hand = v.getCurBoardState().getHandOf(whoAmI);
    	ArrayList<Action> rootChilds;
    	if(hand==null) {
    		rootChilds = null;
    	}else {
    		rootChilds = getAllNewStatesWithCards(v.getCurBoardState(),v.getNextPlayerPlaying());
    		//System.out.println(rootChilds.size()+" childs were created depth = "+v.getDepth());
    	}
    	v.expand2(rootChilds);
    }
    public static void expand3(Node v,int whoIsPlaying,int whoAmI){
    	ArrayList<String> hand = null;
    	if(v.getNextPlayerPlaying()==whoAmI)
    		hand = v.getCurBoardState().getHandOf(whoAmI);
    	else if(v.getNextPlayerPlaying()==whoIsPlaying)
    		hand = v.getCurBoardState().getHandOf(whoIsPlaying);
    	ArrayList<Action> rootChilds;
    	if(hand==null){
    		rootChilds = null;
    	}else{
    		rootChilds = getAllNewStatesWithCards(v.getCurBoardState(),v.getNextPlayerPlaying());
    		//System.out.println(rootChilds.size()+" childs were created depth = "+v.getDepth());
    	}
    	v.expand3(rootChilds);
    }
    public static Node bestChild(Node v,boolean hasCP){
    	//System.out.println("We choose the best child, current depth = "+v.getDepth());
    	ArrayList<Node> childs = v.getChilds();
    	//System.out.println("we have "+childs.size()+" childs at depth ="+childs.get(0).getDepth());
    	Node bestChild = null;
    	if(childs!=null) {
    		double max = -10000;
        	double curUTC,cp;
        	if(hasCP)
        		cp = 2 * ( 1/Math.sqrt(2) );
        	else
        		cp = 0;
        	int n,visits,reward;
        	n = v.getTimesVisited(); // parent visits
        	for(int i=0;i<childs.size();i++){
        		visits = childs.get(i).getTimesVisited(); // child visits
        		reward = childs.get(i).getReward();
        		curUTC = reward/visits;
        		if(hasCP) {
        			curUTC+= ( cp*Math.sqrt( ( (2*Math.log(n))/visits ) ) );
        		}
        		if(max<curUTC) {
        			max = curUTC;
        			bestChild = childs.get(i);
        		}
        	}
    	}
    	//System.out.println("The best child is = "+bestChild.getPrevMovement()+" and depth = "+bestChild.getDepth()+" reward = "+bestChild.getReward());
    	/*if(bestChild==null){
    		System.out.println("bestChild returned null!!");
    		System.out.println("Parent, expanded = "+v.isExpanded()+" depth = "+v.getDepth()+" nextPlayer = "+v.getNextPlayerPlaying()+" prevMOVE = "+v.getPrevMovement());
    		System.out.println("is Terminal  ? 1= "+v.isTerminalNode()+" 2= "+v.isTerminalNode2()+" 3= "+v.isTerminalNode3());
    	}*/
    	return bestChild;
    }
    public static void backUp(Node v,int reward){
    	while(v!=null) {
    		v.addVisit();
    		v.setReward( v.getReward() + reward );
    		v = v.getParent();
    	}
    }
    
    public static int defaultPolicy(Board preActionBoard,String action,int whoAmI){
    	int[] myColorCount = {0, 0, 0, 0};
    	for (int i = 0 ; i < 4 ; i++)
    		myColorCount[i] =  cardsCounterOfColor(preActionBoard,whoAmI,preActionBoard.getColors(i));
    	int reward = 0;
    	String delimiterActions = "#";
		String delimiterVariables = ",";
		String[] actions;
		String[] variables;
		actions = action.split(delimiterActions);
		for (int i = 0 ; i < actions.length; i++)
		{
			variables = actions[i].split(delimiterVariables);
			
			if (variables[0].equals("DT")) // Drive
			{
				reward+=0;
				if(preActionBoard.getRoleOf(whoAmI).equals("Medic")){
					City cur = preActionBoard.searchForCity(variables[2]);
					for(int m=0;m<4;m++) {
						if(preActionBoard.getCured(m)) {
							reward+=cur.getCubsOfColor(m);
						}
					}
				}
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " drives to " + variables[2]);
				//board.driveTo(Integer.parseInt(variables[1]), variables[2]);
			}
				
			else if (variables[0].equals("DF")) // Direct Flight
			{
				String color = preActionBoard.searchForCity(variables[2]).getColour();
				int colorID=-1;
				if(color.equals("Black")){
					colorID=0;
				}else if(color.equals("Yellow")){
					colorID=1;
				}else if(color.equals("Blue")){
					colorID=2;
				}else if(color.equals("Red")){
					colorID=3;
				}
				reward-= myColorCount[colorID] * 4;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a direct flight to " + variables[2]);
				//board.directFlight(Integer.parseInt(variables[1]), variables[2]);
				if(preActionBoard.getRoleOf(whoAmI).equals("Medic")){
					City cur = preActionBoard.searchForCity(variables[2]);
					for(int m=0;m<4;m++) {
						if(preActionBoard.getCured(m)) {
							reward+=cur.getCubsOfColor(m);
						}
					}
				}
			}
				
			else if (variables[0].equals("CF"))// Charter Flight
			{
				reward-=5;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a charter flight to " + variables[2]);
				//board.charterFlight(Integer.parseInt(variables[1]), variables[2]);
				if(preActionBoard.getRoleOf(whoAmI).equals("Medic")){
					City cur = preActionBoard.searchForCity(variables[2]);
					for(int m=0;m<4;m++) {
						if(preActionBoard.getCured(m)) {
							reward+=cur.getCubsOfColor(m);
						}
					}
				}
			}
				
			else if (variables[0].equals("SF")) // Shuttle Flight
			{
				reward+=0;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a shuttle flight to " + variables[2]);
				//board.shuttleFlight(Integer.parseInt(variables[1]), variables[2]);
				if(preActionBoard.getRoleOf(whoAmI).equals("Medic")){
					City cur = preActionBoard.searchForCity(variables[2]);
					for(int m=0;m<4;m++) {
						if(preActionBoard.getCured(m)) {
							reward+=cur.getCubsOfColor(m);
						}
					}
				}
			}
				
			else if (variables[0].equals("BRS")) // Build Research Station
			{
				reward+=10;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is building a Research Station to " + variables[2]);
				//board.buildRS(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("RRS")) // Remove Research Station
			{
				reward-=0;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is removing a Reseaerch Station from " + variables[2]);
				//board.removeRS(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("TD")) // Treat Disease 
			{
				if(preActionBoard.getRoleOf(whoAmI).equals("Medic")) {
					reward+=10;
				}
				String color = variables[3];
				int colorID=-1;
				if(color.equals("Black")){
					colorID=0;
				}else if(color.equals("Yellow")){
					colorID=1;
				}else if(color.equals("Blue")){
					colorID=2;
				}else if(color.equals("Red")){
					colorID=3;
				}
				if(preActionBoard.getCubesLeft(colorID)<3)
					reward+=20;
				City treatCity =  preActionBoard.searchForCity(variables[2]);
					reward+= (treatCity.getMaxCube()*4);
					if(treatCity.getMaxCube()>2) {
						reward-=( ( treatCity.getMaxCube()-2 )*4)+1;
					}
					else if(treatCity.getMaxCube()==2){
						reward+= 2*preActionBoard.getOutbreaksCount();
						String neighrName;
						for(int g=0;g<treatCity.getNeighboursNumber();g++){
							neighrName = treatCity.getNeighbour(g);
							if(preActionBoard.searchForCity(neighrName).getMaxCube()==2){
								reward+=5;
							}
						}
					}
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is treating the " + variables[3] + " disease from " + variables[2]);
				//board.treatDisease(Integer.parseInt(variables[1]), variables[2], variables[3]);
			}
			else if (variables[0].equals("CD1")) // Cure Disease 1
			{
				String color = variables[2];
				int colorID=-1;
				if(color.equals("Black")){
					colorID=0;
				}else if(color.equals("Yellow")){
					colorID=1;
				}else if(color.equals("Blue")){
					colorID=2;
				}else if(color.equals("Red")){
					colorID=3;
				}
				reward += (-( preActionBoard.getCubesLeft(colorID)-24 ))+20;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is curing the " + variables[2] + " disease");
				//board.cureDisease(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("CD2")) // Cure Disease 2
			{
				String color = variables[2];
				int colorID=-1;
				if(color.equals("Black")){
					colorID=0;
				}else if(color.equals("Yellow")){
					colorID=1;
				}else if(color.equals("Blue")){
					colorID=2;
				}else if(color.equals("Red")){
					colorID=3;
				}
				reward += (-( preActionBoard.getCubesLeft(colorID)-24 ))+20;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is curing the " + variables[2] + " disease and throws " + variables[3] + variables[4] + variables[5] + variables[6] );
				//board.cureDisease(Integer.parseInt(variables[1]), variables[2], variables[3], variables[4], variables[5], variables[6]);
			}
			else if (variables[0].equals("AP")) // Action Pass
			{
				reward+=0;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " decided to pass this action");
				//board.actionPass(Integer.parseInt(variables[1]));
			}
			else if (variables[0].equals("OET")) // Operation Expert Travel
			{
				String color = preActionBoard.searchForCity(variables[3]).getColour();
				int colorID=-1;
				if(color.equals("Black")){
					colorID=0;
				}else if(color.equals("Yellow")){
					colorID=1;
				}else if(color.equals("Blue")){
					colorID=2;
				}else if(color.equals("Red")){
					colorID=3;
				}
				reward-= myColorCount[colorID] * 3;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " travels to " + variables[2] + " as the Operations Expert throws"+variables[3]);
				//board.operationsExpertTravel(Integer.parseInt(variables[1]), variables[2], variables[3]);
			}
		}
		return reward;
    }
    
    // --> Coding functions <--
    
    public static String toTextDriveTo(int playerID, String destination)
    {
    	return "#DT,"+playerID+","+destination;
    }
    	
    public static String toTextDirectFlight(int playerID, String destination)
    {
    	return "#DF,"+playerID+","+destination;
    }
    
    public static String toTextCharterFlight(int playerID, String destination)
    {
    	return "#CF,"+playerID+","+destination;
    }
    
    public static String toTextShuttleFlight(int playerID, String destination)
    {
    	return "#SF,"+playerID+","+destination;
    }
    
    public static String toTextBuildRS(int playerID, String destination)
    {
    	return "#BRS,"+playerID+","+destination;
    }
    
    public static String toTextRemoveRS(int playerID, String destination)
    {
    	return "#RRS,"+playerID+","+destination;
    }
    
    public static String toTextTreatDisease(int playerID, String destination, String color)
    {
    	return "#TD,"+playerID+","+destination+","+color;
    }
    
    public static String toTextCureDisease(int playerID, String color)
    {
    	return "#CD1,"+playerID+","+color;
    }
    
    public static String toTextCureDisease(int playerID, String color, String card1, String card2, String card3, String card4)
    {
    	return "#CD2,"+playerID+","+color+","+card1+","+card2+","+card3+","+card4;
    }
    
    
    public static String toTextActionPass(int playerID)
    {
    	return "#AP,"+playerID;
    }
    
    public static String toTextChatMessage(int playerID, String messageToSend)
    {
    	return "#C,"+playerID+","+messageToSend;
    }
    
    public static String toTextPlayGG(int playerID, String cityToBuild)
    {
    	return "#PGG,"+playerID+","+cityToBuild;
    }
    
    public static String toTextPlayQN(int playerID)
    {
    	return "#PQN,"+playerID;
    }
    public static String toTextPlayA(int playerID, int playerToMove, String cityToMoveTo)
    {
    	return "#PA,"+playerID+","+playerToMove+","+cityToMoveTo;
    }
    public static String toTextPlayF(int playerID)
    {
    	return "#PF,"+playerID;
    }
    public static String toTextPlayRP(int playerID, String cityCardToRemove)
    {
    	return "#PRP,"+playerID+","+cityCardToRemove;
    }
    public static String toTextOpExpTravel(int playerID, String destination, String colorToThrow)
    {
    	return "#OET,"+playerID+","+destination+","+colorToThrow;
    }

}
