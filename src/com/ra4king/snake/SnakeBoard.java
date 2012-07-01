package com.ra4king.snake;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import com.ra4king.gameutils.BasicScreen;
import com.ra4king.gameutils.util.SafeInteger;

public class SnakeBoard extends BasicScreen {
	private final int BLOCK_SIZE = 20;
	
	public enum SnakeType {
		SLUG(120), WORM(80), PYTHON(40);
		
		private int delay;
		
		private SnakeType(int delay) {
			this.delay = delay;
		}
		
		public int getDelay() {
			return delay;
		}
	}
	
	private enum Direction {
		LEFT(-1), RIGHT(1), UP(-2), DOWN(2);
		
		private int dir;
		
		private Direction(int dir) {
			this.dir = dir;
		}
		
		public int getValue() {
			return dir;
		}
		
		public int getVX() {
			return (dir & 0x1) == 1 ? dir : 0;
		}
		
		public int getVY() {
			return (dir & 0x1) == 0 ? dir/2 : 0;
		}
		
		public boolean isOppositeOf(Direction other) {
			return Math.abs(getValue()) == Math.abs(other.getValue());
		}
		
		public static Direction getDirection(int key) {
			switch(key) {
				case KeyEvent.VK_LEFT: return Direction.LEFT;
				case KeyEvent.VK_RIGHT: return Direction.RIGHT;
				case KeyEvent.VK_UP: return Direction.UP;
				case KeyEvent.VK_DOWN: return Direction.DOWN;
				default: return null;
			}
		}
	}
	
	private ArrayList<Direction> nextDirs;
	
	private LinkedList<Point> body;
	private Point food;
	
	private SnakeType type;
	
	private SafeInteger score;
	private int possiblePoints = 90;
	
	private long elapsedTime;
	
	public SnakeBoard(SnakeType type) {
		this.type = type;
		
		score = new SafeInteger();
		
		nextDirs = new ArrayList<Direction>();
		nextDirs.add(Direction.DOWN);
		
		body = new LinkedList<Point>();
		
		for(int a = 0; a < 3; a++)
			body.add(new Point(12,13-a));
		
		generateFood();
	}
	
	private void generateFood() {
		int x = (int)(Math.random() * 25);
		int y = (int)(Math.random() * 25);
		
		Point f = new Point(x,y);
		
		for(Point p : body)
			if(p.equals(f)) {
				generateFood();
				return;
			}
		
		food = f;
	}
	
	private void die() {
		getGame().setScreen("Menus");
	}
	
	@Override
	public void update(long deltaTime) {
		elapsedTime += deltaTime;
		
		if(elapsedTime >= type.getDelay()*1e6) {
			elapsedTime -= type.getDelay()*1e6;
			
			if(nextDirs.size() > 1)
				nextDirs.remove(0);
			
			Direction dir = nextDirs.get(0);
			
			Point head = body.getFirst();
			Point newHead = new Point(head.x + dir.getVX(), head.y + dir.getVY());
			
			if(newHead.x < 0 || newHead.x >= 25 || newHead.y < 0 || newHead.y >= 25) {
				die();
				return;
			}
			
			if(newHead.equals(food)) {
				score.set(score.get() + possiblePoints);
				generateFood();
			}
			else
				body.removeLast();
			
			for(Point p : body)
				if(p.equals(newHead)) {
					die();
					return;
				}
			
			body.addFirst(newHead);
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		boolean head = true;
		
		for(Point p : body) {
			g.setColor(head ? Color.red : Color.black);
			g.fillRoundRect(p.x*BLOCK_SIZE,p.y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE/2,BLOCK_SIZE/2);
			g.setColor(Color.white);
			g.drawRoundRect(p.x*BLOCK_SIZE,p.y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE/2,BLOCK_SIZE/2);
			
			head = false;
		}
		
		g.setColor(Color.blue);
		g.fillRoundRect(food.x*BLOCK_SIZE,food.y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE/2,BLOCK_SIZE/2);
		g.setColor(Color.white);
		g.drawRoundRect(food.x*BLOCK_SIZE,food.y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE/2,BLOCK_SIZE/2);
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		Direction d = Direction.getDirection(key.getKeyCode());
		if(d == null)
			return;
		
		Direction last = nextDirs.get(nextDirs.size()-1);
		if(!last.equals(d) && !last.isOppositeOf(d))
			nextDirs.add(d);
	}
}
