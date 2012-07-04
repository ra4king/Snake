package com.ra4king.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.ra4king.gameutils.BasicScreen;
import com.ra4king.snake.SnakeBoard.SnakeType;

public class Countdown extends BasicScreen {
	private final long MS = (long)1e6;
	
	private SnakeBoard board;
	private long startTime, length = 2400 * MS;
	
	public Countdown(SnakeType type) {
		board = new SnakeBoard(type);
	}
	
	@Override
	public void show() {
		startTime = System.nanoTime();
	}
	
	@Override
	public void update(long deltaTime) {
		if(System.nanoTime() - startTime >= length)
			getGame().setScreen("Snake Board",board);
	}
	
	@Override
	public void draw(Graphics2D g) {
		board.draw(g);
		
		g.setColor(Color.blue);
		g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 50));
		if(System.nanoTime() - startTime < 1800 * MS)
			g.drawString((3 - ((System.nanoTime()-startTime)/MS)/600) + "", Snake.WIDTH/2 - 10, Snake.HEIGHT/2 + 20);
		else
			g.drawString("GO!", Snake.WIDTH/2 - 30, Snake.HEIGHT/2 + 20);
	}
}
