package com.ra4king.snake;

import com.ra4king.gameutils.Game;
import com.ra4king.snake.SnakeBoard.SnakeType;

public class Snake extends Game {
	private static final long serialVersionUID = 4863640150633571015L;
	
	public Snake() {
		super(500,500,60,2.0);
	}
	
	@Override
	protected void initGame() {
		setScreen("Snake Board", new SnakeBoard(SnakeType.WORM));
	}
}
