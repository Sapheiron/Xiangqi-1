package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import de.tuberlin.sese.swtpp.gameserver.model.*;
//TODO: more imports from JVM allowed here


import java.io.Serializable;

public class XiangqiGame extends Game implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5424778147226994452L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign red and black player
	private Player blackPlayer;
	private Player redPlayer;
	
	// internal representation of the game state
	// TODO: insert additional game data here
	/************************
	 * constructors
	 ***********************/

	public XiangqiGame() {
		super();

		// TODO: initialization of game state can go here
	}

	public String getType() {
		return "xiangqi";
	}

	/*******************************************
	 * Game class functions already implemented
	 ******************************************/

	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);

			// game starts with two players
			if (players.size() == 2) {
				started = true;
				this.redPlayer = players.get(0);
				this.blackPlayer= players.get(1);
				nextPlayer = redPlayer;
			}
			return true;
		}

		return false;
	}

	@Override
	public String getStatus() {
		if (error)
			return "Error";
		if (!started)
			return "Wait";
		if (!finished)
			return "Started";
		if (surrendered)
			return "Surrendered";
		if (draw)
			return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if (started) {
			if (blackGaveUp())
				gameInfo = "black gave up";
			else if (redGaveUp())
				gameInfo = "red gave up";
			else if (didRedDraw() && !didBlackDraw())
				gameInfo = "red called draw";
			else if (!didRedDraw() && didBlackDraw())
				gameInfo = "black called draw";
			else if (draw)
				gameInfo = "draw game";
			else if (finished)
				gameInfo = blackPlayer.isWinner() ? "black won" : "red won";
		}

		return gameInfo;
	}

	@Override
	public String nextPlayerString() {
		return isRedNext() ? "r" : "b";
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}

	@Override
	public boolean callDraw(Player player) {

		// save to status: player wants to call draw
		if (this.started && !this.finished) {
			player.requestDraw();
		} else {
			return false;
		}

		// if both agreed on draw:
		// game is over
		if (players.stream().allMatch(Player::requestedDraw)) {
			this.draw = true;
			finish();
		}
		return true;
	}

	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.redPlayer == player) {
				redPlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				redPlayer.setWinner();
			}
			surrendered = true;
			finish();

			return true;
		}

		return false;
	}

	/* ******************************************
	 * Helpful stuff
	 ***************************************** */

	/**
	 *
	 * @return True if it's red player's turn
	 */
	public boolean isRedNext() {
		return nextPlayer == redPlayer;
	}

	/**
	 * Ends game after regular move (save winner, finish up game state,
	 * histories...)
	 *
	 * @param winner player who won the game
	 * @return true if game was indeed finished
	 */
	public boolean regularGameEnd(Player winner) {
		// public for tests
		if (finish()) {
			winner.setWinner();
			winner.getUser().updateStatistics();
			return true;
		}
		return false;
	}

	public boolean didRedDraw() {
		return redPlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean redGaveUp() {
		return redPlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/

	@Override
	public void setBoard(String state) {
		// Note: This method is for automatic testing. A regular game would not start at some artificial state.
		//       It can be assumed that the state supplied is a regular board that can be reached during a game.
		// TODO: implement
	}

	@Override
	public String getBoard() {
		// TODO: implement
		return "rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR";
	}

	@Override
	public boolean tryMove(String moveString, Player player) {
		// TODO: implement

		return false;
	}	
	
	//turns FEN-Notation into 9x10 matrix
	public char[][] matrizise(String state) {
		char [][] result = new char[9][10];
		Integer c = 0;
		Integer r = 0;
		for(Integer i = 0; i < state.length(); i++) {
			if(state.charAt(i) == '/') {
				r++;
				c = 0;
			} else if("gaehrcsGAEHRCS".contains(state.substring(i,1))) {
				result[c][r] = state.charAt(i);
				c++;
			} else if("0123456789".contains(state.substring(i,1))) {
				c += (int) state.charAt(i);
			}
		}
		return result;
	}

	//turns the board matrix into compact FEN-String
	public String stringify(char[][] matrix) {
		String result = "";
		Integer i = 0;
		for(Integer r=0;r<10;r++) {
			for(Integer c=0;c<9;c++) {
				if("gaehrcsGAEHRCS".contains("" + matrix[c][r])) {
					result = result + matrix[c][r];
				} else if("gaehrcsGAEHRCS".contains("" + matrix[c+1][r]) || c == 9) {
					result = result + i;
					i = 0;
				} else {
					i++;
				}
			}
		}
		return result;
	}
	
	public Integer[] fieldInt(String posString) {
		Integer c = (int) posString.charAt(0) - 97;
		Integer r = (int) posString.charAt(1);
		Integer [] result = {c, r};
		return result;
	}
	
	public String fieldString(Integer column, Integer row) {
		String result = "";
		if(column == 0) {
			result = result + "a";
		}
		if(column == 1) {
			result = result + "b";
		}
		if(column == 2) {
			result = result + "c";
		}
		if(column == 3) {
			result = result + "d";
		}
		if(column == 4) {
			result = result + "e";
		}
		if(column == 5) {
			result = result + "f";
		}
		if(column == 6) {
			result = result + "g";
		}
		if(column == 7) {
			result = result + "h";
		}
		if(column == 8) {
			result = result + "i";
		}
		return result + row;
	}
	
	//lists fields red general can move to only considering movement pattern, palace and friendly pieces
	public String possibleFieldsg(String posString, char[][] board) {
		String result = "";
		Integer[] pos = fieldInt(posString.substring(0,2));
		if(pos[1] > 0 && !"gaehrcs".contains(""+board[pos[0]][pos[1]-1])) {
			result = result + fieldString(pos[0], pos[1]-1);
		}
		if(pos[1] < 2 && !"gaehrcs".contains(""+board[pos[0]][pos[1]+1])) {
			result = result + fieldString(pos[0], pos[1]+1);
		}
		if(pos[0] > 3 && !"gaehrcs".contains(""+board[pos[0]-1][pos[1]])) {
			result = result + fieldString(pos[0]-1, pos[1]);
		}
		if(pos[0] < 5 && !"gaehrcs".contains(""+board[pos[0]+1][pos[1]])) {
			result = result + fieldString(pos[0]+1, pos[1]);
		}
		return result;
	}
	
	//lists fields red advisor can move to only considering movement pattern, palace and friendly pieces
	public String possibleFieldsa(Integer[] pos, char[][] board) {
		String result = "";
		if(pos[0] > 3 && pos[1] > 0 && !"gaehrcs".contains(""+board[pos[0]-1][pos[1]-1])) {
			result = result + fieldString(pos[0]-1, pos[1]-1);
		}
		if(pos[0] > 3 && pos[1] < 2 && !"gaehrcs".contains(""+board[pos[0]-1][pos[1]+1])) {
			result = result + fieldString(pos[0]-1, pos[1]+1);
		}
		if(pos[0] < 5 && pos[1] > 0 && !"gaehrcs".contains(""+board[pos[0]+1][pos[1]-1])) {
			result = result + fieldString(pos[0]+1, pos[1]-1);
		}
		if(pos[0] < 5 && pos[1] < 2 && !"gaehrcs".contains(""+board[pos[0]+1][pos[1]+1])) {
			result = result + fieldString(pos[0]+1, pos[1]+1);
		}
		return result;
	}
	
	//lists fields red advisor can move to only considering movement pattern, river and friendly pieces
	public String possibleFieldse(Integer[] pos, char[][] board) {
		String result = "";
		if(pos[0] > 1 && pos[1] > 1 && !"gaehrcsGAEHRCS".contains(""+board[pos[0]-1][pos[1]-1]) && !"gaehrcs".contains(""+board[pos[0]-2][pos[1]-2])) {
			result = result + fieldString(pos[0]-2, pos[1]-2);
		}
		if(pos[0] > 1 && pos[1] < 4 && !"gaehrcsGAEHRCS".contains(""+board[pos[0]-1][pos[1]+1]) && !"gaehrcs".contains(""+board[pos[0]-2][pos[1]+2])) {
			result = result + fieldString(pos[0]-2, pos[1]+2);
		}
		if(pos[0] < 7 && pos[1] > 1 && !"gaehrcsGAEHRCS".contains(""+board[pos[0]+1][pos[1]-1]) && !"gaehrcs".contains(""+board[pos[0]+2][pos[1]-2])) {
			result = result + fieldString(pos[0]+2, pos[1]-2);
		}
		if(pos[0] < 7 && pos[1] < 4 && !"gaehrcsGAEHRCS".contains(""+board[pos[0]+1][pos[1]+1]) && !"gaehrcs".contains(""+board[pos[0]+2][pos[1]+2])) {
			result = result + fieldString(pos[0]+2, pos[1]+2);
		}
		return result;
	}
	
	//lists fields red horse can move to only considering movement pattern and friendly pieces
	public String possibleFieldsh(Integer[] pos, char[][] board) {
		String result = "";
		if(pos[0] > 1 && !"gaehrcsGAEHRCS".contains(""+board[pos[0]-1][pos[1]])) {
			if(pos[1] > 0 && !"gaehrcs".contains(""+board[pos[0]-2][pos[1]-1])) {
				result = result + fieldString(pos[0]-2,pos[1]-1);
			}
			if(pos[1] < 9 && !"gaehrcs".contains(""+board[pos[0]-2][pos[1]+1])) {
				result = result + fieldString(pos[0]-2,pos[1]+1);
			}
		}
		if(pos[0] < 7 && !"gaehrcsGAEHRCS".contains(""+board[pos[0]+1][pos[1]])) {
			if(pos[1] > 0 && !"gaehrcs".contains(""+board[pos[0]+2][pos[1]-1])) {
				result = result + fieldString(pos[0]+2,pos[1]-1);
			}
			if(pos[1] < 9 && !"gaehrcs".contains(""+board[pos[0]+2][pos[1]+1])) {
				result = result + fieldString(pos[0]+2,pos[1]+1);
			}
		}
		if(pos[1] > 1 && !"gaehrcsGAEHRCS".contains(""+board[pos[0]][pos[1]-1])) {
			if(pos[0] > 0 && !"gaehrcs".contains(""+board[pos[0]-1][pos[1]-2])) {
				result = result + fieldString(pos[0]-1,pos[1]-2);
			}
			if(pos[0] < 8 && !"gaehrcs".contains(""+board[pos[0]+1][pos[1]-2])) {
				result = result + fieldString(pos[0]+1,pos[1]-2);
			}
		}
		if(pos[1] < 8 && !"gaehrcsGAEHRCS".contains(""+board[pos[0]][pos[1]+1])) {
			if(pos[0] > 0 && !"gaehrcs".contains(""+board[pos[0]-1][pos[1]+2])) {
				result = result + fieldString(pos[0]-1,pos[1]+2);
			}
			if(pos[0] < 8 && !"gaehrcs".contains(""+board[pos[0]+1][pos[1]+2])) {
				result = result + fieldString(pos[0]+1,pos[1]+2);
			}
		}
		return result;
	}
}
