package com.ra4king.snake_old;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.ra4king.snake_old.networking.Packet;
import com.ra4king.snake_old.networking.PacketIO;
import com.ra4king.snake_old.networking.SocketPacketIO;


public class Menus extends JComponent {
	private static final long serialVersionUID = -3254613031844225253L;
	
	private RoundRectangle2D rects[] = new RoundRectangle2D[3];
	private RoundRectangle2D hsRects[] = new RoundRectangle2D[3];
	private RoundRectangle2D hsButton;
	private RoundRectangle2D mmButton;
	private boolean highlights[] = new boolean[3];
	private boolean hsHighlights[] = new boolean[3];
	private boolean hsHighlight;
	private boolean mmHighlight;
	private int hsLevelSelected = 0;
	private String slugHighScores[][] = new String[10][2];
	private String wormHighScores[][] = new String[10][2];
	private String pythonHighScores[][] = new String[10][2];
	private boolean mainMenu = true;
	private String playerName = "";
	private int highestScore;
	private boolean hasShownUpdate = false;
	private final double version = 1.7;
	
	public Menus() {
		for(int a = 0; a < 10; a++) {
			Arrays.fill(slugHighScores[a],"");
			Arrays.fill(wormHighScores[a],"");
			Arrays.fill(pythonHighScores[a],"");
		}
		
		slugHighScores[0][0] = wormHighScores[0][0] = pythonHighScores[0][0] = "Fetching.....";
	}
	
	public void mouseMoved(int x, int y) {
		int a = determineButton(x,y);
		
		if(mainMenu) {
			for(int b = 0; b < highlights.length; b++)
				highlights[b] = false;
			
			hsHighlight = false;
			
			if(a > 0)
				highlights[a-1] = true;
			else if(a == -1)
				hsHighlight = true;
		}
		else {
			for(int b = 0; b < hsHighlights.length; b++)
				hsHighlights[b] = false;
			
			hsHighlights[hsLevelSelected] = true;
			
			mmHighlight = false;
			
			if(a < -2)
				hsHighlights[(-a)-3] = true;
			if(a == -2)
				mmHighlight = true;
		}
		
		repaint();
	}
	
	public int mouseClicked(int x, int y) {
		switch(determineButton(x,y)) {
			case 1:
				highlights[0] = false;
				repaint();
				return Snake.SLUG;
			case 2:
				highlights[1] = false;
				repaint();
				return Snake.WORM;
			case 3:
				highlights[2] = false;
				repaint();
				return Snake.PYTHON;
			case -1:
				hsHighlights[0] = hsHighlights[1] = hsHighlights[2] = false;
				hsHighlight = false;
				mainMenu = false;
				fetchHighScores("Slug");
				hsLevelSelected = 0;
				repaint();
				return -1;
			case -2:
				highlights[0] = highlights[1] = highlights[2] = false;
				mmHighlight = false;
				mainMenu = true;
				repaint();
				return -2;
			case -3:
				hsHighlights[1] = hsHighlights[2] = false;
				hsHighlights[0] = false;
				fetchHighScores("Slug");
				hsLevelSelected = 0;
				repaint();
				return -3;
			case -4:
				hsHighlights[0] = hsHighlights[2] = false;
				hsHighlights[1] = false;
				hsLevelSelected = 1;
				fetchHighScores("Worm");
				repaint();
				return -4;
			case -5:
				hsHighlights[0] = hsHighlights[1] = false;
				hsHighlights[2] = false;
				hsLevelSelected = 2;
				fetchHighScores("Python");
				repaint();
				return -5;
		}
		
		return 0;
	}
	
	private int determineButton(int x, int y) {
		if(mainMenu) {
			if(rects[0].contains(x,y))
				return 1;
			else if(rects[1].contains(x,y))
				return 2;
			else if(rects[2].contains(x,y))
				return 3;
			else if(hsButton.contains(x,y))
				return -1;
		}
		else {
			if(mmButton != null && mmButton.contains(x,y))
				return -2;
			else if(hsRects[0] != null && hsRects[0].contains(x,y))
				return -3;
			else if(hsRects[1] != null && hsRects[1].contains(x,y))
				return -4;
			else if(hsRects[2] != null && hsRects[2].contains(x,y))
				return -5;
		}
		
		return 0;
	}
	
	public void submit(final int points, final String level) {
		mainMenu = false;
		
		if(level.equals("Slug"))
			hsLevelSelected = 0;
		else if(level.equals("Worm"))
			hsLevelSelected = 1;
		else
			hsLevelSelected = 2;
		
		if(points == 0) {
			fetchHighScores(level);
			return;
		}
		
		while(playerName == null || playerName.equals("")) {
			playerName = JOptionPane.showInputDialog(this,"Name:");
			
			if(playerName == null)
				return;
			else
				playerName = playerName.trim();
		}
		
		JOptionPane.showMessageDialog(this,playerName + " got " + points + " points!");
		
		if(points > highestScore)
			highestScore = points;
		
		(new Thread() {
			public void run() {
				PacketIO io = null;
				try{
					io = new SocketPacketIO("www.ra4king.com",5051);
					
					Packet packet = new Packet();
					packet.writeString("Snake game");
					io.write(packet);
					
					packet = new Packet();
					packet.writeInt(0);
					io.write(packet);
					
					try{
						double serverVer = io.read().readDouble();
						if(version < serverVer) System.out.println("NEW UPDATE!");
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
					
					packet = new Packet();
					packet.writeInt(1);
					packet.writeString(playerName);
					packet.writeInt(points);
					packet.writeString(level);
					io.write(packet);
					
					packet = new Packet();
					packet.writeInt(-1);
					io.write(packet);
				}
				catch(Exception exc) {
					exc.printStackTrace();
				}
				finally {
					try{
						io.close();
					}
					catch(Exception exc) {}
					
					repaint();
					
					try{
						Thread.sleep(500);
					}
					catch(Exception exc) {}
					fetchHighScores(level);
				}
			}
		}).start();
	}
	
	private void fetchHighScores(final String level) {
		(new Thread() {
			public void run() {
				PacketIO io = null;
				try{
					io = new SocketPacketIO("www.ra4king.com",5051);
					
					Packet packet = new Packet();
					packet.writeString("Snake game");
					io.write(packet);
					
					packet = new Packet();
					packet.writeInt(0);
					io.write(packet);
					
					try{
						double serverVer = io.read().readDouble();
						if(version < serverVer && !hasShownUpdate) {
							JOptionPane.showMessageDialog(Menus.this,"<html>New Update! New version: " + serverVer + "<br/>Current version: " + version);
							hasShownUpdate = true;
						}
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
					
					packet = new Packet();
					packet.writeInt(2);
					packet.writeString(level);
					io.write(packet);
					
					String scores[][];
					if(level.equals("Slug"))
						scores = slugHighScores;
					else if(level.equals("Worm"))
						scores = wormHighScores;
					else
						scores = pythonHighScores;
					
					packet = io.read();
					for(int a = 0; a < 10; a++) {
						String sections[] = packet.readString().split("<:>");
						scores[a][0] = sections[0].trim();
						scores[a][1] = sections[1].trim();
					}
					
					packet = new Packet();
					packet.writeInt(-1);
					io.write(packet);
				}
				catch(Exception exc) {
					slugHighScores[0][0] = "Connection Error";
					wormHighScores[0][0] = "Connection Error";
					pythonHighScores[0][0] = "Connection Error";
					
					exc.printStackTrace();
				}
				finally {
					try{
						io.close();
					}
					catch(Exception exc) {}
					
					repaint();
				}
			}
		}).start();
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(Color.black);
		g2.fillRect(0,0,getWidth(),getHeight());
		
		if(mainMenu)
			drawMainMenu(g2);
		else
			drawHighScoreBoard(g2);
	}
	
	private void drawMainMenu(Graphics2D g) {
		g.setColor(Color.blue);
		g.setFont(new Font(Font.MONOSPACED,Font.BOLD,100));
		
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds("Snake",g);
		g.drawString("Snake",(int)((getWidth()-200-rect.getWidth())/2+100)
									,(int)((100-rect.getHeight())/2+fm.getAscent()+100));
		
		g.setColor(Color.orange);
		g.setFont(new Font(Font.MONOSPACED,Font.ROMAN_BASELINE,40));
		
		String s = "Slug";
		fm = g.getFontMetrics();
		rect = fm.getStringBounds(s,g);
		g.drawString(s,(int)(((getWidth()/3)-rect.getWidth())/2)
							,(int)((200-rect.getHeight())/2+fm.getAscent()+getHeight()-250));
		
		s = "Worm";
		fm = g.getFontMetrics();
		rect = fm.getStringBounds(s,g);
		g.drawString(s,(int)(((getWidth()/3)-rect.getWidth())/2+(getWidth()/3))
							,(int)((200-rect.getHeight())/2+fm.getAscent()+getHeight()-250));
		
		s = "Python";
		fm = g.getFontMetrics();
		rect = fm.getStringBounds(s,g);
		g.drawString(s,(int)(((getWidth()/3)-rect.getWidth())/2+(2*(getWidth()/3)))
							,(int)((200-rect.getHeight())/2+fm.getAscent()+getHeight()-250));
		
		for(int a = 0; a < rects.length; a++)
			rects[a] = new RoundRectangle2D.Double(a*(getWidth()/3),getHeight()-250,getWidth()/3,200,20,20);
		
		for(int a = 0; a < highlights.length; a++) {
			g.setColor(Color.white);
			g.draw(rects[a]);
		}
		
		for(int a = 0; a < highlights.length; a++) {
			if(highlights[a]) {
				g.setColor(new Color(255,255,255,100));
				g.fill(rects[a]);
				g.setColor(Color.orange);
				g.draw(rects[a]);
			}
		}
		
		g.setFont(new Font(Font.MONOSPACED,Font.BOLD,20));
		
		s = "High Scores";
		fm = g.getFontMetrics();
		rect = fm.getStringBounds(s,g);
		g.setColor(Color.orange);
		g.drawString(s,(int)((getWidth()-rect.getWidth())/2),(int)((50-rect.getHeight())/2)+getHeight()-25);
		
		hsButton = new RoundRectangle2D.Double((getWidth()-rect.getWidth())/2,470,rect.getWidth(),20,10,10);
		g.setColor(Color.white);
		g.draw(hsButton);
		
		if(hsHighlight) {
			g.setColor(new Color(255,255,255,100));
			g.fill(hsButton);
			g.setColor(Color.orange);
			g.draw(hsButton);
		}
	}
	
	private void drawHighScoreBoard(Graphics2D g) {
		g.setColor(Color.blue);
		g.setFont(new Font(Font.MONOSPACED,Font.BOLD,100));
		
		String s = "Snake";
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(s,g);
		g.drawString(s,(int)((getWidth()-200-rect.getWidth())/2+100)
									,(int)((100-rect.getHeight())/2+fm.getAscent()));
		
		g.setColor(Color.orange);
		g.setFont(new Font(Font.MONOSPACED,Font.ROMAN_BASELINE,30));
		
		s = "Slug";
		fm = g.getFontMetrics();
		rect = fm.getStringBounds(s,g);
		hsRects[0] = new RoundRectangle2D.Double(((getWidth()/3)-rect.getWidth())/2,
															(100-rect.getHeight())/2+100,
															rect.getWidth(),rect.getHeight(),10,10);
		g.drawString(s,(int)hsRects[0].getX(),(int)(hsRects[0].getY()+hsRects[0].getHeight())-fm.getDescent());
		
		s = "Worm";
		fm = g.getFontMetrics();
		rect = fm.getStringBounds(s,g);
		hsRects[1] = new RoundRectangle2D.Double(((getWidth()/3)-rect.getWidth())/2+(getWidth()/3),
															(100-rect.getHeight())/2+100,
															rect.getWidth(),rect.getHeight(),10,10);
		g.drawString(s,(int)hsRects[1].getX(),(int)(hsRects[1].getY()+hsRects[1].getHeight())-fm.getDescent());
		
		s = "Python";
		fm = g.getFontMetrics();
		rect = fm.getStringBounds(s,g);
		hsRects[2] = new RoundRectangle2D.Double(((getWidth()/3)-rect.getWidth())/2+(2*(getWidth()/3)),
															(100-rect.getHeight())/2+100,
															rect.getWidth(),rect.getHeight(),10,10);
		g.drawString(s,(int)hsRects[2].getX(),(int)(hsRects[2].getY()+hsRects[2].getHeight())-fm.getDescent());
		
		for(int a = 0; a < hsHighlights.length; a++) {
			g.setColor(Color.white);
			g.draw(hsRects[a]);
		}
		
		for(int a = 0; a < hsHighlights.length; a++) {
			if(hsHighlights[a] || hsLevelSelected == a) {
				g.setColor(new Color(255,255,255,100));
				g.fill(hsRects[a]);
				g.setColor(Color.orange);
				g.draw(hsRects[a]);
			}
		}
		
		g.setColor(Color.white);
		g.setFont(new Font(Font.SANS_SERIF,Font.TRUETYPE_FONT,25));
		
		String scores[][];
		if(hsLevelSelected == 0)
			scores = slugHighScores;
		else if(hsLevelSelected == 1)
			scores = wormHighScores;
		else
			scores = pythonHighScores;
		
		for(int a = 0; a < scores.length; a++) {
			g.drawString(""+(a+1),50,220+(a*25));
			g.drawString(scores[a][0],100,220+(a*25));
			g.drawString(scores[a][1],300,220+(a*25));
		}
		
		g.setFont(new Font(Font.MONOSPACED,Font.BOLD,20));
		
		s = "Main Menu";
		fm = g.getFontMetrics();
		rect = fm.getStringBounds(s,g);
		g.setColor(Color.orange);
		g.drawString(s,(int)((getWidth()-rect.getWidth())/2),(int)((50-rect.getHeight())/2)+getHeight()-25);
		
		mmButton = new RoundRectangle2D.Double((getWidth()-rect.getWidth())/2,470,rect.getWidth(),20,10,10);
		g.setColor(Color.white);
		g.draw(mmButton);
		
		if(mmHighlight) {
			g.setColor(new Color(255,255,255,100));
			g.fill(mmButton);
			g.setColor(Color.orange);
			g.draw(mmButton);
		}
		
		if(highestScore > 0 && playerName != null) {
			g.setColor(Color.magenta);
			g.setFont(new Font(Font.MONOSPACED,Font.BOLD,20));
			g.drawString(playerName + " " + highestScore,350,490);
		}
	}
}