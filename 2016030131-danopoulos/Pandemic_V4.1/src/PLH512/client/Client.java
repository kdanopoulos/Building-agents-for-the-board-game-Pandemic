package PLH512.client;

import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.lang.Math;
import PLH512.server.Board;
import PLH512.server.City;

public class Client  
{
    final static int ServerPort = 64240;
    final static String username = "myName";
  
    public static void main(String args[]) throws UnknownHostException, IOException, ClassNotFoundException  
    { 
    	int numberOfPlayers;
    	int myPlayerID;
    	String myUsername;
    	String myRole;
    	
        
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
                        	
                        	// Initializing variables for current round
                        	
                        	Board myBoard = currentBoard[0];
                        	
                        	String myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                        	City myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        	
                        	ArrayList<String> myHand = myBoard.getHandOf(myPlayerID);
                        	
                        	int[] myColorCount = {0, 0, 0, 0};
                        	
                        	for (int i = 0 ; i < 4 ; i++)
                        		myColorCount[i] =  cardsCounterOfColor(myBoard, myPlayerID, myBoard.getColors(i));
                        	
                        	ArrayList<citiesWithDistancesObj> distanceMap = new ArrayList<citiesWithDistancesObj>();
                        	distanceMap = buildDistanceMap(myBoard, myCurrentCity, distanceMap);
                        	
                        	
                        	String myAction = "";
                        	String mySuggestion = "";
                        	
                        	int myActionCounter = 0;
                        	
                        	// Printing out my current hand
                        	System.out.println("\nMy current hand...");
                        	printHand(myHand);
                        	
                        	// Printing out current color count
                        	System.out.println("\nMy hand's color count...");
                        	for (int i = 0 ; i < 4 ; i++)
                        		System.out.println(myBoard.getColors(i) + " cards count: " + myColorCount[i]);
                        	
                        	// Printing out distance map from current city
                        	//System.out.println("\nDistance map from " + myCurrentCity);
                        	//printDistanceMap(distanceMap);
                        	
                        	// ADD YOUR CODE FROM HERE AND ON!! 
                        	
                        	boolean tryToCure = false;
                        	String colorToCure = null;
                        	
                        	boolean tryToTreatHere = false;
                        	String colorToTreat = null;
                        	
                        	boolean tryToTreatClose = false;
                        	String destinationClose = null;
                        	
                        	boolean tryToTreatMedium = false;
                        	String destinationMedium = null;
                        	
                        	String destinationRandom = null;
                        	
                        	if (myColorCount[0] > 4 || myColorCount[1] > 4 || myColorCount[2] > 4 || myColorCount[3] > 4)
                        	{
                        		if (myActionCounter < 4)
                        			tryToCure = true;
                        		
                        		if (myColorCount[0] > 4)
                        			colorToCure = "Black";
                        		else if (myColorCount[1] > 4)
                        			colorToCure = "Yellow";
                        		else if (myColorCount[2] > 4)
                        			colorToCure = "Blue";
                        		else if (myColorCount[3] > 4)
                        			colorToCure = "Red";
                        	}
                        	
                        	if (tryToCure)
                        	{
                        		System.out.println("I want to try and cure the " + colorToCure + " disease!");
                        		myAction = myAction + toTextCureDisease(myPlayerID, colorToCure);
                        		myBoard.cureDisease(myPlayerID, colorToCure);
                        		myActionCounter++;
                        		
                        	}
                        	
                        	if (myCurrentCityObj.getBlackCubes() != 0 || myCurrentCityObj.getYellowCubes() != 0  || myCurrentCityObj.getBlueCubes() != 0  || myCurrentCityObj.getRedCubes() != 0)
                        	{
                        		if (myActionCounter < 4)
                        			tryToTreatHere = true;
                        		
                        		if (myCurrentCityObj.getBlackCubes() > 0)
                        			colorToTreat = "Black";
                        		else if ( myCurrentCityObj.getYellowCubes() > 0)
                        			colorToTreat = "Yellow";
                        		else if (myCurrentCityObj.getBlueCubes() > 0)
                        			colorToTreat = "Blue";
                        		else if (myCurrentCityObj.getRedCubes() > 0)
                        			colorToTreat = "Red";
                        	}
                        	
                        	if (tryToTreatHere) 
                        	{
                        		while (myCurrentCityObj.getMaxCube() != 0 && myActionCounter < 4)
                        		{
                        			colorToTreat = myCurrentCityObj.getMaxCubeColor();
                    				
                    				System.out.println("I want to try and treat one " + colorToTreat + " cube from " + myCurrentCity + "!");
                    				
                    				myAction = myAction + toTextTreatDisease(myPlayerID, myCurrentCity, colorToTreat);
                            		myActionCounter++;
                            		
                            		myBoard.treatDisease(myPlayerID, myCurrentCity, colorToTreat);
                        		}
                        	}
                        	
                        	if (myActionCounter < 4 )
                        	{
                        		destinationClose = getMostInfectedInRadius(1, distanceMap, myBoard);
                        		
                        		if(!destinationClose.equals(myCurrentCity))
                        			tryToTreatClose = true;
                    		}
                        	
                        	if (tryToTreatClose)
                        	{
                        		System.out.println("Hhhmmmmmm I could go and try to treat " + destinationClose);
                        		
                        		myAction = myAction + toTextDriveTo(myPlayerID, destinationClose);
                        		myActionCounter++;
                        		
                        		myBoard.driveTo(myPlayerID, destinationClose);
                        		
                        		myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                            	myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        		
                        		while (myCurrentCityObj.getMaxCube() != 0 && myActionCounter < 4)
                        		{
                        			colorToTreat = myCurrentCityObj.getMaxCubeColor();
                    				
                    				System.out.println("I want to try and treat one " + colorToTreat + " cube from " + myCurrentCity + "!");
                    				
                    				myAction = myAction + toTextTreatDisease(myPlayerID, myCurrentCity, colorToTreat);
                            		myActionCounter++;
                            		
                            		myBoard.treatDisease(myPlayerID, myCurrentCity, colorToTreat);
                        		}
                        	}
                        	
                        	
                        	if (myActionCounter < 4 )
                        	{
                        		destinationMedium = getMostInfectedInRadius(2, distanceMap, myBoard);
                        		
                        		if(!destinationMedium.equals(myCurrentCity))
                        			tryToTreatMedium = true;
                    		}
                        	
                        	if (tryToTreatMedium)
                        	{
                        		System.out.println("Hhhmmmmmm I could go and try to treat " + destinationMedium);
                        		
                        		String driveFirstTo = getDirectionToMove(myCurrentCity, destinationMedium, distanceMap, myBoard);
                        		
                        		myAction = myAction + toTextDriveTo(myPlayerID, driveFirstTo);
                        		myActionCounter++;
                        		myAction = myAction + toTextDriveTo(myPlayerID, destinationMedium);
                        		myActionCounter++;
                        		
                        		myBoard.driveTo(myPlayerID, driveFirstTo);
                        		
                        		myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                            	myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        		
                        		myBoard.driveTo(myPlayerID, destinationMedium);
                        		
                        		myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                            	myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        		
                        		while (myCurrentCityObj.getMaxCube() != 0 && myActionCounter < 4)
                        		{
                        			colorToTreat = myCurrentCityObj.getMaxCubeColor();
                    				
                    				System.out.println("I want to try and treat one " + colorToTreat + " cube from " + myCurrentCity + "!");
                    				
                    				myAction = myAction + toTextTreatDisease(myPlayerID, myCurrentCity, colorToTreat);
                            		myActionCounter++;
                            		
                            		myBoard.treatDisease(myPlayerID, myCurrentCity, colorToTreat);
                        		}
                        	}
                        	
                        	Random rand = new Random();
                        	
                        	while (myActionCounter < 4)
                        	{
                        		int upperBound;
                        		int randomNumber;
                        		String randomCityToGo;
                        		
                        		upperBound = myCurrentCityObj.getNeighboursNumber();
                        		randomNumber = rand.nextInt(upperBound);
                        		randomCityToGo = myCurrentCityObj.getNeighbour(randomNumber);
                        		
                        		System.out.println("Moving randomly to " + randomCityToGo);
                        		
                        		myAction = myAction + toTextDriveTo(myPlayerID, randomCityToGo);
                        		myActionCounter++;
                        		
                        		myBoard.driveTo(myPlayerID, randomCityToGo);
                        		
                        		myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                            	myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                    		}
                        	
                        	
                        	// UP TO HERE!! DON'T FORGET TO EDIT THE "msgToSend"
                        	
                        	// Message type 
                        	// toTextShuttleFlight(0,Atlanta)+"#"+etc
                        	String msgToSend;
                        	if (myBoard.getWhoIsPlaying() == myPlayerID)
                        		msgToSend = myAction;
                        		
                        		//msgToSend = "AP,"+myPlayerID+"#AP,"+myPlayerID+"#AP,"+myPlayerID+"#C,"+myPlayerID+",This was my action#AP,"+myPlayerID+"#C,"+myPlayerID+",This should not be printed..";//"Action";
                            else 
                        		msgToSend = "#C,"+myPlayerID+",This was my recommendation"; //"Recommendation"
                        	
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
        	if (currentBoard[0].getGameEnded() == true) {
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
    /*public static ArrayList<Action> findAllPossibleMovesForThisRoundWithCards(Board currentBoard,int whoIsPlaying,String prevActions,Boolean oeMove){
    	int[] myColorCount = {0, 0, 0, 0};
    	for (int i = 0 ; i < 4 ; i++)
    		myColorCount[i] =  cardsCounterOfColor(currentBoard, whoIsPlaying, currentBoard.getColors(i));
    	ArrayList<Action> allPossibleActions = new ArrayList<Action>();
    	Board newBoard;
    	ArrayList<String> newHand;
    	String playerCityPossitionName = currentBoard.getPawnsLocations(whoIsPlaying);
    	City playerCityPossition = currentBoard.searchForCity(playerCityPossitionName);
    	int neighbNum = playerCityPossition.getNeighboursNumber();
    	// General Moves
    	// drive/ferry
    	for(int i=0;i<neighbNum;i++) {
    		String curNeighbCityName = playerCityPossition.getNeighbour(i);
    		//City curNeighbCity = currentBoard.searchForCity(curNeighbCityName);
    		newBoard = currentBoard;
    		newBoard.driveTo(whoIsPlaying, curNeighbCityName);
    		allPossibleActions.add( new Action(prevActions+toTextDriveTo(whoIsPlaying,curNeighbCityName),newBoard) );
    	}
    	// direct flight
    	for(int i=0;i<hand.size();i++){
    		if(hand.get(i)!=playerCityPossitionName){
    			newBoard = currentBoard;
    			newBoard.directFlight(whoIsPlaying, hand.get(i));
    			newHand = hand;
    			newHand.remove(i);
    			allPossibleActions.add( new Action(prevActions+toTextDirectFlight(whoIsPlaying,hand.get(i)),newBoard ) );
    		}
    		else { // we have a card of the city we are already there
    			// Charter Flight
    			for(int l=0;l<currentBoard.getCitiesCount();l++) {
    				newBoard = currentBoard;
    				newBoard.charterFlight(whoIsPlaying, currentBoard.searchForCity(l).getName());
    				newHand = hand;
        			newHand.remove(i);
        			allPossibleActions.add( new Action(prevActions+toTextCharterFlight(whoIsPlaying,currentBoard.searchForCity(l).getName()),newBoard) );
    			}
    			//Build RS if we are not Operations Expert
    			boolean isOperationsExpert = currentBoard.getRoleOf(whoIsPlaying).equals("Operations Expert");
    			if(isOperationsExpert==false) {
    				newHand = hand;
        			newHand.remove(i);
    				if(currentBoard.getResearchStationsBuild()==currentBoard.getResearchStationsLimit()){
        				ArrayList<String> RSLocations = currentBoard.getRSLocations();
        	    		for(int m=0;m<RSLocations.size();m++){
        	    			newBoard = currentBoard;
        	    			newBoard.removeRS(whoIsPlaying, RSLocations.get(m));
        	    			newBoard.buildRS(whoIsPlaying, playerCityPossitionName);
        	    			allPossibleActions.add( new Action(prevActions+ toTextRemoveRS(whoIsPlaying,RSLocations.get(m))+toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard) );
        	    		}
        			}else {
        				newBoard = currentBoard;
            			newBoard.buildRS(whoIsPlaying, playerCityPossitionName);
            			newBoard.setResearchStationsBuild(newBoard.getResearchStationsBuild()+1);
            			allPossibleActions.add( new Action(prevActions+ toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard) );
        			}
    			}
    		}
    	}
    	
    	if(playerCityPossition.getHasReseachStation()){ // the player is in a city with a research station
    		// Shuttle Flight
    		ArrayList<String> RSLocations = currentBoard.getRSLocations();
    		for(int i=0;i<RSLocations.size();i++){
    			if(RSLocations.get(i)!=playerCityPossitionName){
    				newBoard = currentBoard;
    				newBoard.shuttleFlight(whoIsPlaying, RSLocations.get(i));
    				allPossibleActions.add( new Action(prevActions+toTextShuttleFlight(whoIsPlaying,RSLocations.get(i)),newBoard) );
    			}
    		}
    		//Discover a Cure
    		boolean isScientist = currentBoard.getRoleOf(whoIsPlaying).equals("Scientist");
    		String colorToCure="";
    		Boolean justEqual = false;
    		int cardsNeededForCure;
    		if(isScientist) {
    			cardsNeededForCure = 3;
    		}else {
    			cardsNeededForCure = 4;
    		}
    		if (myColorCount[0] >= cardsNeededForCure || myColorCount[1] >= cardsNeededForCure || myColorCount[2] >= cardsNeededForCure || myColorCount[3] >= cardsNeededForCure)
        	{	
        		if (myColorCount[0] >= cardsNeededForCure) {
        			colorToCure = "Black";
        			if(myColorCount[0]==cardsNeededForCure)
        				justEqual=true;
        		}
        		else if (myColorCount[1] >= cardsNeededForCure) {
        			colorToCure = "Yellow";
        			if(myColorCount[1]==cardsNeededForCure)
        				justEqual=true;
        		}
        		else if (myColorCount[2] >= cardsNeededForCure) {
        			colorToCure = "Blue";
        			if(myColorCount[2]==cardsNeededForCure)
        				justEqual=true;
        		}
        		else if (myColorCount[3] >= cardsNeededForCure) {
        			colorToCure = "Red";
        			if(myColorCount[3]==cardsNeededForCure)
        				justEqual=true;
        		}	
        		newHand = hand;
        		String [] cards = new String[4];
        		cards[0]=null;
        		cards[1]=null;
        		cards[2]=null;
        		cards[3]=null;
        		int count=0;
        		if(justEqual) {
        			for(int h=0;h<newHand.size();h++) {
            			if( currentBoard.searchForCity(newHand.get(h)).getColour().equals(colorToCure)) {
            				cards[count] = newHand.get(h);
            				newHand.remove(h);
            				h--;
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}else {
        			for(int h=0;h<newHand.size();h++) {
            			if( currentBoard.searchForCity(newHand.get(h)).getColour().equals(colorToCure) && newHand.get(h)!=playerCityPossitionName) {
            				cards[count] = newHand.get(h);
            				newHand.remove(h);
            				h--;
            				count++;
            			}
            			if(count==cardsNeededForCure)
            				break;
            		}
        		}
        		newBoard = currentBoard;
        		newBoard.cureDisease(whoIsPlaying, colorToCure, cards[0], cards[1], cards[2], cards[3]);
        		allPossibleActions.add( new Action( prevActions+toTextCureDisease(whoIsPlaying,colorToCure,cards[0],cards[1],cards[2],cards[3]),newBoard ) );
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
    	newBoard = currentBoard;
    	newBoard.treatDisease(whoIsPlaying, playerCityPossitionName, colorToTreat);
    	allPossibleActions.add( new Action(prevActions+toTextTreatDisease(whoIsPlaying,playerCityPossitionName,colorToTreat),newBoard) );
    	
    	// Special Abilities
    	//---Operations Expert---
    	// build RS
    	boolean isOperationsExpert = currentBoard.getRoleOf(whoIsPlaying).equals("Operations Expert");
    	if(playerCityPossition.getHasReseachStation()==false){
    		if(isOperationsExpert){
    			if(currentBoard.getResearchStationsBuild()==currentBoard.getResearchStationsLimit()) {
    				ArrayList<String> RSLocations = currentBoard.getRSLocations();
    	    		for(int i=0;i<RSLocations.size();i++){
    	    			newBoard = currentBoard;
    	    			newBoard.removeRS(whoIsPlaying, RSLocations.get(i));
    	    			newBoard.buildRS(whoIsPlaying, playerCityPossitionName);
    	    			allPossibleActions.add( new Action(prevActions+ toTextRemoveRS(whoIsPlaying,RSLocations.get(i))+toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard) );
    	    		}
    			}else {
    				newBoard = currentBoard;
        			newBoard.buildRS(whoIsPlaying, playerCityPossitionName);
        			newBoard.setResearchStationsBuild(newBoard.getResearchStationsBuild()+1);
        			allPossibleActions.add( new Action(prevActions+ toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard) );
    			}
    		}
    	}else {
    		if(oeMove==false) {
    			// once per turn, move from a research station to any city by discarding any City card
    			for(int i=0;i<hand.size();i++) {
    				newHand=hand;
    				newHand.remove(i);
    				for(int j=0;j<currentBoard.getCitiesCount();j++) {
    					newBoard = currentBoard;
    					newBoard.operationsExpertTravel(whoIsPlaying, currentBoard.searchForCity(j).getName(), hand.get(i));
    					allPossibleActions.add( new Action(prevActions+toTextOpExpTravel(whoIsPlaying,currentBoard.searchForCity(j).getName(),hand.get(i)),newBoard,true) );
    				}
    			}
    		}
    	}
    	// No action
    	//allPossibleActions.add( new Action( toTextActionPass(whoIsPlaying),currentBoard) );
    	return allPossibleActions;
    }
    public static ArrayList<Action> findAllPossibleMovesForThisRoundWithoutCards(Board currentBoard,int whoIsPlaying,String prevActions){
    	ArrayList<Action> allPossibleActions = new ArrayList<Action>();
    	Board newBoard;
    	String playerCityPossitionName = currentBoard.getPawnsLocations(whoIsPlaying);
    	City playerCityPossition = currentBoard.searchForCity(playerCityPossitionName);
    	int neighbNum = playerCityPossition.getNeighboursNumber();
    	// General Moves
    	// drive/ferry all possible neighbours cities
    	for(int i=0;i<neighbNum;i++) {
    		String curNeighbCityName = playerCityPossition.getNeighbour(i);
    		//City curNeighbCity = currentBoard.searchForCity(curNeighbCityName);
    		newBoard = currentBoard;
    		newBoard.driveTo(whoIsPlaying, curNeighbCityName);
    		allPossibleActions.add( new Action(prevActions+toTextDriveTo(whoIsPlaying,curNeighbCityName),newBoard) );
    	}
    	// Shuttle Flight
    	if(playerCityPossition.getHasReseachStation()){
    		// the player is in a city with a research station
    		ArrayList<String> RSLocations = currentBoard.getRSLocations();
    		for(int i=0;i<RSLocations.size();i++){
    			if(RSLocations.get(i)!=playerCityPossitionName){
    				newBoard = currentBoard;
    				newBoard.shuttleFlight(whoIsPlaying, RSLocations.get(i));
    				allPossibleActions.add( new Action(prevActions+toTextShuttleFlight(whoIsPlaying,RSLocations.get(i)),newBoard) );
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
    	newBoard = currentBoard;
    	newBoard.treatDisease(whoIsPlaying, playerCityPossitionName, colorToTreat);
    	allPossibleActions.add( new Action(prevActions+toTextTreatDisease(whoIsPlaying,playerCityPossitionName,colorToTreat),newBoard) );
    	// Special Abilities
    	//---Operations Expert---
    	// build RS
    	if(playerCityPossition.getHasReseachStation()==false){
    		boolean isOperationsExpert = currentBoard.getRoleOf(whoIsPlaying).equals("Operations Expert");
    		if(isOperationsExpert){
    			if(currentBoard.getResearchStationsBuild()==currentBoard.getResearchStationsLimit()) {
    				ArrayList<String> RSLocations = currentBoard.getRSLocations();
    	    		for(int i=0;i<RSLocations.size();i++){
    	    			newBoard = currentBoard;
    	    			newBoard.removeRS(whoIsPlaying, RSLocations.get(i));
    	    			newBoard.buildRS(whoIsPlaying, playerCityPossitionName);
    	    			allPossibleActions.add( new Action(prevActions+ toTextRemoveRS(whoIsPlaying,RSLocations.get(i))+toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard) );
    	    		}
    			}else {
    				newBoard = currentBoard;
        			newBoard.buildRS(whoIsPlaying, playerCityPossitionName);
        			newBoard.setResearchStationsBuild(newBoard.getResearchStationsBuild()+1);
        			allPossibleActions.add( new Action(prevActions+ toTextBuildRS(whoIsPlaying,playerCityPossitionName) ,newBoard) );
    			}
    		}
    	}
    	// once per turn, move from a research station to any city by discarding any City card - not implemented in this method
    	// No action
    	//allPossibleActions.add( new Action( toTextActionPass(whoIsPlaying),currentBoard) );
    	return allPossibleActions;
    }
    public ArrayList<Action> getAllNewStatesWithoutCards(Board currentBoard,int whoIsPlaying){
    	ArrayList<Action> curActioList = findAllPossibleMovesForThisRoundWithoutCards(currentBoard,whoIsPlaying,"");
    	for(int i=0;i<curActioList.size();i++){
    		ArrayList<Action> curActioList2 = findAllPossibleMovesForThisRoundWithoutCards(curActioList.get(i).getNewBoardState(),whoIsPlaying,curActioList.get(i).getStringAction());
    		for(int j=0;j<curActioList2.size();j++){
    			ArrayList<Action> curActioList3 = findAllPossibleMovesForThisRoundWithoutCards(curActioList2.get(j).getNewBoardState(),whoIsPlaying,curActioList2.get(j).getStringAction());
    			for(int k=0;k<curActioList3.size();k++){
    				ArrayList<Action> curActioList4 = findAllPossibleMovesForThisRoundWithoutCards(curActioList3.get(k).getNewBoardState(),whoIsPlaying,curActioList3.get(k).getStringAction());
    				return curActioList4;
    			}
    		}
    	}
    	return null;
    }
    //findAllPossibleMovesForThisRoundWithCards(Board currentBoard,ArrayList<String> hand,int whoIsPlaying,String prevActions,Boolean oeMove)
    public ArrayList<Action> getAllNewStatesWithCards(Board currentBoard,int whoIsPlaying,ArrayList<String> hand){
    	ArrayList<Action> curActioList = findAllPossibleMovesForThisRoundWithCards(currentBoard,hand,whoIsPlaying,"",false);
    	for(int i=0;i<curActioList.size();i++) {
    		ArrayList<Action> curActioList2 = findAllPossibleMovesForThisRoundWithCards(curActioList.get(i).getNewBoardState(),whoIsPlaying,curActioList.get(i).getStringAction(),curActioList.get(i).getOeMove());
    		for(int j=0;j<curActioList2.size();j++) {
    			ArrayList<Action> curActioList3 = findAllPossibleMovesForThisRoundWithCards(curActioList2.get(j).getNewBoardState(),whoIsPlaying,curActioList2.get(j).getStringAction(),curActioList2.get(j).getOeMove());
    			for(int k=0;k<curActioList3.size();k++) {
    				ArrayList<Action> curActioList4 = findAllPossibleMovesForThisRoundWithCards(curActioList3.get(k).getNewBoardState(),whoIsPlaying,curActioList3.get(k).getStringAction(),curActioList3.get(k).getOeMove());
    				return curActioList4;
    			}
    		}
    	}
    	return null;
    }
    public Node utcSearchSolo(Board currentBoard,int whoIsPlaying,ArrayList<String> hand,long timeStarted,long budgetInMilliseconds){
    	Node v0 = new Node(currentBoard,whoIsPlaying);
    	Node v,chld;
    	int delta = 0;
    	boolean justExpanded;
    	while(System.currentTimeMillis()-timeStarted<budgetInMilliseconds){	
    		// Tree Policy
    		justExpanded=false;
    		v = v0;
    		while(v.isTerminalNode())
    		{ 
    				if(v.isExpanded())// v is fully expanded
    				{ 
    					v = bestChild(v,true);
    				}
    				else // v is not fully expanded
    				{ 
    					expand(v);
    					justExpanded=true;
    					if(v.getChilds()!=null) {
    						for(int d=0;d<v.getChilds().size();d++) {
        						chld = v.getChilds().get(d);
        						delta = defaultPolicy(chld.getCurBoardState(),chld.getPrevMovement());
        						backUp(chld,delta);
        					}
    					}
    					break;
    				}
    		}
    		if(!justExpanded) 
    		{
    			delta = defaultPolicy(v.getCurBoardState(),v.getPrevMovement());
        		backUp(v,delta);
    		}
    	}
    	return bestChild(v0,false);
    }
    public Node utcSearchSuggestion(Board currentBoard,int whoIsPlaying,int whoAmI,ArrayList<String> hand,long timeStarted,long budgetInMilliseconds){
    	Node v0 = new Node(currentBoard,whoAmI,whoIsPlaying);
    	Node v,chld;
    	int delta = 0;
    	boolean justExpanded;
    	while(System.currentTimeMillis()-timeStarted<budgetInMilliseconds){	
    		// Tree Policy
    		justExpanded=false;
    		v = v0;
    		while(v.isTerminalNode())
    		{ 
    				if(v.isExpanded())// v is fully expanded
    				{ 
    					v = bestChild(v,true);
    				}
    				else // v is not fully expanded
    				{ 
    					expand(v);
    					justExpanded=true;
    					if(v.getChilds()!=null) {
    						for(int d=0;d<v.getChilds().size();d++) {
        						chld = v.getChilds().get(d);
        						delta = defaultPolicy(chld.getCurBoardState(),chld.getPrevMovement());
        						backUp(chld,delta);
        					}
    					}
    					break;
    				}
    		}
    		if(!justExpanded) 
    		{
    			delta = defaultPolicy(v.getCurBoardState(),v.getPrevMovement());
        		backUp(v,delta);
    		}
    	}
    	return bestChild(v0,false);
    }
    public void expand(Node v){
    	ArrayList<String> hand = v.getCards(v.getNextPlayerPlaying());
    	ArrayList<Action> rootChilds;
    	if(hand==null) {
    		rootChilds = getAllNewStatesWithoutCards(v.getCurBoardState(),v.getNextPlayerPlaying());
    	}else {
    		rootChilds = getAllNewStatesWithCards(v.getCurBoardState(),v.getNextPlayerPlaying(),hand);
    	}
    	v.expand(rootChilds);
    }
    public Node bestChild(Node v,boolean hasCP){
    	ArrayList<Node> childs = v.getChilds();
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
    	return bestChild;
    }
    public void backUp(Node v,int reward){
    	while(v!=null) {
    		v.addVisit();
    		v.setReward( v.getReward() + reward );
    		v = v.getParent();
    	}
    }
    
    public int defaultPolicy(Board preActionBoard,String action){
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
				reward+=1;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " drives to " + variables[2]);
				//board.driveTo(Integer.parseInt(variables[1]), variables[2]);
			}
				
			else if (variables[0].equals("DF")) // Direct Flight
			{
				reward+=1;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a direct flight to " + variables[2]);
				//board.directFlight(Integer.parseInt(variables[1]), variables[2]);
			}
				
			else if (variables[0].equals("CF"))// Charter Flight
			{
				reward+=1;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a charter flight to " + variables[2]);
				//board.charterFlight(Integer.parseInt(variables[1]), variables[2]);
			}
				
			else if (variables[0].equals("SF")) // Shuttle Flight
			{
				reward+=1;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a shuttle flight to " + variables[2]);
				//board.shuttleFlight(Integer.parseInt(variables[1]), variables[2]);
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
				String color = variables[3];
				City treatCity =  preActionBoard.searchForCity(variables[2]);
				String maxCubColorOfThisCity = treatCity.getMaxCubeColor();
				if(color==maxCubColorOfThisCity) {
					// we treat the right one
					reward+= treatCity.getMaxCube()*4;
					if(treatCity.getMaxCube()==2) {
						String neighrName;
						for(int g=0;g<treatCity.getNeighboursNumber();g++){
							neighrName = treatCity.getNeighbour(g);
							if(preActionBoard.searchForCity(neighrName).getMaxCube()==2){
								reward+=5;
							}
						}
					}
				}
				else {
					// wrong color
					reward-=8;
				}
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is treating the " + variables[3] + " disease from " + variables[2]);
				//board.treatDisease(Integer.parseInt(variables[1]), variables[2], variables[3]);
			}
			else if (variables[0].equals("CD1")) // Cure Disease 1
			{
				String color = variables[2];
				int colorID=-1;
				if(color=="Black") {
					colorID=0;
				}else if(color=="Yellow"){
					colorID=1;
				}else if(color=="Blue"){
					colorID=2;
				}else if(color=="Red"){
					colorID=3;
				}
				if(preActionBoard.getCured(colorID)) {
					reward-=12;
				}else {
					reward += (-( preActionBoard.getCubesLeft(colorID)-24 ))+12;
				}
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is curing the " + variables[2] + " disease");
				//board.cureDisease(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("CD2")) // Cure Disease 2
			{
				String color = variables[2];
				int colorID=-1;
				if(color=="Black") {
					colorID=0;
				}else if(color=="Yellow"){
					colorID=1;
				}else if(color=="Blue"){
					colorID=2;
				}else if(color=="Red"){
					colorID=3;
				}
				if(preActionBoard.getCured(colorID)) {
					reward-=12;
				}else {
					reward += (-( preActionBoard.getCubesLeft(colorID)-24 ))+12;
				}
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is curing the " + variables[2] + " disease and throws " + variables[3] + variables[4] + variables[5] + variables[6] );
				//board.cureDisease(Integer.parseInt(variables[1]), variables[2], variables[3], variables[4], variables[5], variables[6]);
			}
			else if (variables[0].equals("AP")) // Action Pass
			{
				reward+=0;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " decided to pass this action");
				//board.actionPass(Integer.parseInt(variables[1]));
			}/*
			else if (variables[0].equals("C"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " sends the following message: " + variables[2]);
				//board.chatMessage(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("PA"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " plays Airlift. Moving player " + Integer.parseInt(variables[2]) + " to " + variables[3]);
				
			}
			else if (variables[0].equals("OET")) // Operation Expert Travel
			{
				reward+=1;
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " travels to " + variables[2] + " as the Operations Expert");
				//board.operationsExpertTravel(Integer.parseInt(variables[1]), variables[2], variables[3]);
			}
		}
		return reward;
    }
    */
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