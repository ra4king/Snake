package com.ra4king.snake_old;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;

public class Snake extends JComponent {
	private static final long serialVersionUID = -6342130263815388767L;
	
	private volatile ArrayList<Point> blocks;
	private volatile Image head;
	private volatile Image head2;
	private volatile Point food;
	private volatile boolean countDown = true;
	private volatile int countDownNum;
	private volatile boolean hasLost;
	private volatile boolean addBlock;
	private volatile boolean dirChanged;
	private volatile int currentX;
	private volatile int currentY;
	private volatile int direction;
	private volatile int nextDir;
	private volatile int speed;
	private volatile int count;
	private volatile int points;
	private volatile int foodPoints;
	public static final int SLUG = 3, WORM = 2, PYTHON = 1;
	
	public Snake(int snakeType) {
		if(snakeType != SLUG && snakeType != WORM && snakeType != PYTHON)
			throw new IllegalArgumentException("Invalid snake type: " + snakeType);
		
		speed = snakeType;
		
		direction = -2;
		
		blocks = new ArrayList<Point>(4);
		
		int midx = currentX = 8;
		int midy = currentY = 8;
		
		try{
			head = javax.imageio.ImageIO.read(getClass().getResource("/res/snake_head.png"));
			head2 = javax.imageio.ImageIO.read(getClass().getResource("/res/snake_head2.png"));
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		blocks.add(new Point(midx*25+50,(midy-3)*25+50));
		blocks.add(new Point(midx*25+50,(midy-2)*25+50));
		blocks.add(new Point(midx*25+50,(midy-1)*25+50));
		blocks.add(new Point(midx*25+50,midy*25+50));
		
		generateFood();
	}
	
	private void generateFood() {
		int x = (int)Math.round(Math.random()*15);
		int y = (int)Math.round(Math.random()*15);
		
		for(Point p : blocks)
			if((p.x-50)/25 == x && (p.y-50)/25 == y) {
				generateFood();
				return;
			}
		
		food = new Point(x*25+50,y*25+50);
		
		foodPoints = 90;
	}
	
	public void setDirection(int dir) {
		if(!countDown) {
			if(dir > -3 && dir < 3 && dir != 0 && Math.abs(direction) != Math.abs(dir)) {
				if(dirChanged)
					nextDir = dir;
				else {
					direction = dir;
					dirChanged = true;
				}
			}
		}
	}
	
	public boolean hasLost() {
		return hasLost;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void update() {
		if(countDown) {
			countDownNum++;
			
			if(countDownNum == 60)
				countDown = false;
		}
		else {
			count++;
			
			if(count%speed == 0) {
				foodPoints--;
				
				if(!addBlock)
					blocks.remove(0);
				else
					addBlock = false;
				
				switch(direction) {
					case 1:
						currentX++;
						break;
					case 2:
						currentY--;
						break;
					case -1:
						currentX--;
						break;
					case -2:
						currentY++;
				}
				
				dirChanged = false;
				
				if(nextDir != 0) {
					direction = nextDir;
					nextDir = 0;
				}
				
				blocks.add(new Point(currentX*25+50,currentY*25+50));
			}
			
			if(currentX > (getWidth()-100)/25-1 || currentX < 0 || currentY > (getHeight()-100)/25-1 || currentY < 0)
				hasLost = true;
			
			for(int a = 0; a < blocks.size()-1; a++) {
				Point p = blocks.get(a);
				if((p.x-50)/25 == currentX && (p.y-50)/25 == currentY)
					hasLost = true;
			}
			
			if(currentX == (food.x-50)/25 && currentY == (food.y-50)/25) {
				addBlock = true;
				
				if(foodPoints > 0)
					points += foodPoints;
				else
					points++;
				
				generateFood();
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(Color.gray);
		g2.fill(new Rectangle2D.Double(37,43,10,getHeight()-80));
		g2.fill(new Rectangle2D.Double(37,43,getWidth()-80,10));
		g2.fill(new Rectangle2D.Double(getWidth()-53,43,10,getHeight()-80));
		g2.fill(new Rectangle2D.Double(37,getHeight()-47,getWidth()-80,10));
		
		g2.setColor(Color.black);
		g2.fill(new Rectangle2D.Double(40,40,10,getHeight()-80));
		g2.fill(new Rectangle2D.Double(40,40,getWidth()-80,10));
		g2.fill(new Rectangle2D.Double(getWidth()-50,40,10,getHeight()-80));
		g2.fill(new Rectangle2D.Double(40,getHeight()-50,getWidth()-80,10));
		
		
		g2.setColor(Color.gray);
		g2.fill(new RoundRectangle2D.Double(food.x,food.y+5,20,20,10,10));
		
		g2.setColor(Color.black);
		g2.fill(new RoundRectangle2D.Double(food.x+5,food.y,20,20,10,10));
		
		for(Point p : blocks) {
			if(blocks.get(blocks.size()-1) == p) {
				switch(speed) {
					case SLUG:
					case PYTHON:
						g2.drawImage(head,p.x,p.y,null);
						break;
					case WORM:
						g2.drawImage(head2,p.x,p.y,null);
				}
				continue;
			}
			
			g2.setColor(Color.gray);
			g2.fill(new RoundRectangle2D.Double(p.x,p.y+2,23,23,10,10));
			
			g2.setColor(Color.black);
			g2.fill(new RoundRectangle2D.Double(p.x+2,p.y,23,23,10,10));
		}
		
		if(countDown) {
			g2.setColor(Color.blue);
			g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,100));
			
			switch((int)(countDownNum/15)) {
				case 0:
					g2.drawString("3",225,350);
					break;
				case 1:
					g2.drawString("2",225,350);
					break;
				case 2:
					g2.drawString("1",225,350);
					break;
				case 3:
					g2.drawString("GO!",200,350);
			}
		}
		
		g2.setColor(Color.green);
		g2.setFont(new Font(Font.MONOSPACED,Font.BOLD,30));
		
		String s = "Points " + points + "    ";
		switch(speed) {
			case SLUG:
				s += "Slug";
				break;
			case WORM:
				s += "Worm";
				break;
			case PYTHON:
				s += "Python";
		}
		
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(s,g2);
		g2.drawString(s,(int)((getWidth()-rect.getWidth())/2)
									,(int)((50-rect.getHeight())/2+fm.getAscent()+getHeight()-50));
	}
}