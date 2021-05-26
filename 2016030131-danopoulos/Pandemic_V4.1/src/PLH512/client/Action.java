package PLH512.client;

import java.util.ArrayList;

import PLH512.server.Board;

public class Action {
	
	private String stringAction;
	private Board newBoardState;
	private Boolean oeMove;
	private ArrayList<String> prevCities;

	
	public Action(String stringAction,Board newBoardState,ArrayList<String> prevCities){
		this.stringAction = stringAction;
		this.newBoardState = newBoardState;
		this.oeMove=false;
		this.prevCities = prevCities;
	}
	public Action(String stringAction,Board newBoardState,Boolean oeMove,ArrayList<String> prevCities) {
		this.stringAction = stringAction;
		this.newBoardState = newBoardState;
		this.oeMove = oeMove;
		this.prevCities = prevCities;
	}
	

	public String getStringAction() {
		return stringAction;
	}

	public void setStringAction(String stringAction) {
		this.stringAction = stringAction;
	}

	public Board getNewBoardState() {
		return newBoardState;
	}

	public void setNewBoardState(Board newBoardState) {
		this.newBoardState = newBoardState;
	}

	public Boolean getOeMove() {
		return oeMove;
	}

	public void setOeMove(Boolean oeMove) {
		this.oeMove = oeMove;
	}
	public ArrayList<String> getPrevCities() {
		return prevCities;
	}
	public void setPrevCities(ArrayList<String> prevCities) {
		this.prevCities = prevCities;
	}
	public void addCity(String city){
		this.prevCities.add(city);
	}
	public boolean existCity(String city){
		if(this.prevCities!=null) {
			for(int i=0;i<this.prevCities.size();i++)
				if(this.prevCities.get(i).equals(city))
					return true;
		}
		return false;
	}
}
