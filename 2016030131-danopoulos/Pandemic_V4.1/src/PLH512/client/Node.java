package PLH512.client;

import java.util.ArrayList;
import PLH512.server.Board;

public class Node {	
	
	private Board curBoardState;
	private Node parent;
	private ArrayList<Node> childs;
	private boolean expanded;
	private String prevMovement;
	private int whoAmI;
	private int whoIsPlayingInitialy;
	private int timesVisited;
	private int reward;
	private int depth;
	
	public Node(Board curBoardState,int whoAmI){ //  Root Node - Solo Search 
		curBoardState.setWhoIsPlaying(whoAmI);
		this.curBoardState = curBoardState;
		this.parent = null;
		this.childs = null;
		this.prevMovement = "";
		this.whoAmI = whoAmI;
		this.whoIsPlayingInitialy = -1;
		this.timesVisited = 0;
		this.reward = 0;
		this.depth=0;
		this.expanded=false;
	}
	public Node(Board curBoardState,int whoAmI,int whoIsPlayingInitialy){ // Root Node - Suggestion Search
		curBoardState.setWhoIsPlaying(whoIsPlayingInitialy);
		this.curBoardState = curBoardState;
		this.parent = null;
		this.childs = null;
		this.prevMovement = "";
		this.whoAmI = whoAmI;
		this.whoIsPlayingInitialy = whoIsPlayingInitialy;
		this.timesVisited = 0;
		this.reward = 0;
		this.depth=0;
		this.expanded=false;
	}
	public Node(Board curBoardState,Node parent,String prevMovement){ // Child Node
		this.curBoardState = curBoardState;
		this.parent = parent;
		this.childs = null;
		this.prevMovement = prevMovement;
		this.timesVisited = 0;
		this.reward = 0;
		this.depth = parent.getDepth() + 1;
		this.expanded=false;
		this.whoAmI = parent.getWhoAmI();
		this.whoIsPlayingInitialy = parent.getWhoIsPlayingInitialy();
	}
	
	public Boolean isExpanded() {
		return this.expanded;
	}
	public void expand(ArrayList<Action> allPossibleNewActions){
		this.expanded=true;
		if(allPossibleNewActions!=null) {
			Action curAction;
			Node newChild;
			Board curBoard;
			this.childs = new ArrayList<Node>();
			for(int i=0;i<allPossibleNewActions.size();i++){
				curAction = allPossibleNewActions.get(i);
				curBoard = curAction.getNewBoardState();
				// Choose Next Player
				if (curBoard.getWhoIsPlaying() == 3)
					curBoard.setWhoIsPlaying(0); // Back to first player
				else
					curBoard.setWhoIsPlaying(curBoard.getWhoIsPlaying() + 1); // Next player
				newChild = new Node(curBoard,this,curAction.getStringAction());
				this.childs.add(newChild);
			}
		}
	}
	public void expand2(ArrayList<Action> allPossibleNewActions){
		this.expanded=true;
		if(allPossibleNewActions!=null){
			Action curAction;
			Node newChild;
			Board curBoard;
			this.childs = new ArrayList<Node>();
			for(int i=0;i<allPossibleNewActions.size();i++){
				//System.out.println("child = "+i);
				curAction = allPossibleNewActions.get(i);
				curBoard = curAction.getNewBoardState();
				// Simulate 4 rounds with circular moves
				for(int j=0;j<4;j++){
					curBoard.drawCards2(curBoard.getWhoIsPlaying(), 2);
					if (!curBoard.getIsQuietNight())
						curBoard.infectCities2(curBoard.getInfectionRate(),1);
					else 
						curBoard.setIsQuietNight(false);
					if (curBoard.getWhoIsPlaying() == 3)
						curBoard.setWhoIsPlaying(0); // Back to first player
					else
						curBoard.setWhoIsPlaying(curBoard.getWhoIsPlaying() + 1); // Next player
				}
				// Choose Next Player
				curBoard.setWhoIsPlaying(this.whoAmI);
				newChild = new Node(curBoard,this,curAction.getStringAction());
				this.childs.add(newChild);
			}
		}
	}
	public void expand3(ArrayList<Action> allPossibleNewActions){
		this.expanded=true;
		int nextplayer=-1;
		if(allPossibleNewActions!=null){
			Action curAction;
			Node newChild;
			Board curBoard;
			this.childs = new ArrayList<Node>();
			for(int i=0;i<allPossibleNewActions.size();i++){
				curAction = allPossibleNewActions.get(i);
				curBoard = curAction.getNewBoardState();
				if(curBoard.getWhoIsPlaying()==this.whoAmI)
					nextplayer = this.whoIsPlayingInitialy;
				else if(curBoard.getWhoIsPlaying()==this.whoIsPlayingInitialy)
					nextplayer = this.whoAmI;
				while(curBoard.getWhoIsPlaying()!=nextplayer){
					curBoard.drawCards2(curBoard.getWhoIsPlaying(), 2);
					if (!curBoard.getIsQuietNight())
						curBoard.infectCities2(curBoard.getInfectionRate(),1);
					else 
						curBoard.setIsQuietNight(false);
					// set next player
					if (curBoard.getWhoIsPlaying() == 3)
						curBoard.setWhoIsPlaying(0);
					else
						curBoard.setWhoIsPlaying(curBoard.getWhoIsPlaying() + 1);
				}
				// Choose Next Player
				curBoard.setWhoIsPlaying(nextplayer);
				newChild = new Node(curBoard,this,curAction.getStringAction());
				this.childs.add(newChild);
			}
		}
	}
	public Boolean doWeKnowTheCards(int player){
		if(this.whoIsPlayingInitialy==-1) {
			// we do solo search
			if(this.whoAmI == player)
				return true;
			else 
				return false;
		}else {
			// we do suggestion search
			if(this.whoAmI == player || this.whoIsPlayingInitialy == player)
				return true;
			else 
				return false;
		}
	}
	public boolean isTerminalNode(){
		if(this.depth==4 || (this.expanded==true && this.childs==null) )
			return true;
		return false;
	}
	public boolean isTerminalNode2(){
		if(this.depth==3 || (this.expanded==true && this.childs==null) )
			return true;
		return false;
	}
	public boolean isTerminalNode3(){
		if(this.depth==6 || (this.expanded==true && this.childs==null) )
			return true;
		return false;
	}
	public ArrayList<String> getCards(int player){
		//System.out.println("The next player playing is  = "+player +" who am i = "+this.whoAmI + " initial = "+this.whoIsPlayingInitialy);
		if(doWeKnowTheCards(player)){
			return this.curBoardState.getHandOf(player);
		}
		return null;
	}
	public Board getCurBoardState() {
		return curBoardState;
	}
	public void setCurBoardState(Board curBoardState) {
		this.curBoardState = curBoardState;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public ArrayList<Node> getChilds() {
		return childs;
	}
	public void setChilds(ArrayList<Node> childs) {
		this.childs = childs;
	}
	public String getPrevMovement() {
		return prevMovement;
	}
	public void setPrevMovement(String prevMovement) {
		this.prevMovement = prevMovement;
	}
	public int getNextPlayerPlaying() {
		return this.curBoardState.getWhoIsPlaying();
	}
	public int getTimesVisited() {
		return timesVisited;
	}
	public void setTimesVisited(int timesVisited) {
		this.timesVisited = timesVisited;
	}
	public void addVisit() {
		this.timesVisited++;
	}
	public int getReward() {
		return reward;
	}
	public void setReward(int reward) {
		this.reward = reward;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getWhoAmI() {
		return whoAmI;
	}
	public void setWhoAmI(int whoAmI) {
		this.whoAmI = whoAmI;
	}
	public int getWhoIsPlayingInitialy() {
		return whoIsPlayingInitialy;
	}
	public void setWhoIsPlayingInitialy(int whoIsPlayingInitialy) {
		this.whoIsPlayingInitialy = whoIsPlayingInitialy;
	}
	
}
