package com.ra4king.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import com.ra4king.gameutils.MenuPage;
import com.ra4king.gameutils.Screen;
import com.ra4king.gameutils.gui.Button;
import com.ra4king.gameutils.gui.Widget;
import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.SocketPacketIO;
import com.ra4king.snake.SnakeBoard.SnakeType;

public class Highscores extends Widget {
	private Button slugButton, wormButton, pythonButton;
	
	private Entry[] current;
	private Entry[] slugEntries, wormEntries, pythonEntries;
	
	private Entry user;
	
	private final double version = 2.0;
	private boolean hasShownUpdate;
	
	public Highscores(SnakeType type) {
		slugEntries = new Entry[10];
		wormEntries = new Entry[10];
		pythonEntries = new Entry[10];
		
		for(int a = 0; a < 10; a++) {
			slugEntries[a] = new Entry("",0);
			wormEntries[a] = new Entry("",0);
			pythonEntries[a] = new Entry("",0);
		}
		
		showScores(type);
	}
	
	public Entry getUserScore() {
		return user;
	}
	
	@Override
	public void init(Screen screen) {
		super.init(screen);
		
		MenuPage highscores = (MenuPage)screen;
		
		slugButton = (Button)highscores.add(new Button("Slug",30,Snake.WIDTH/6,Snake.HEIGHT/2-150,20,20,true,new Button.Action() {
			public void doAction(Button button) {
				showScores(SnakeType.SLUG);
			}
		}));
		slugButton.setTextPaint(Color.white);
		slugButton.setBackground(Color.black);
		slugButton.setBackgroundHighlight(Color.gray);
		slugButton.setBackgroundPressed(Color.darkGray);
		slugButton.setBorder(Color.yellow);
		slugButton.setBorderHighlight(Color.yellow);
		slugButton.setBorderPressed(Color.yellow);
		wormButton = (Button)highscores.add(new Button("Worm",30,Snake.WIDTH/2-10,Snake.HEIGHT/2-150,20,20,true,new Button.Action() {
			public void doAction(Button button) {
				showScores(SnakeType.WORM);
			}
		}));
		wormButton.setTextPaint(Color.black);
		wormButton.setBackground(Color.white);
		wormButton.setBackgroundHighlight(Color.gray);
		wormButton.setBackgroundPressed(Color.darkGray);
		wormButton.setBorder(Color.yellow);
		wormButton.setBorderHighlight(Color.yellow);
		wormButton.setBorderPressed(Color.yellow);
		pythonButton = (Button)highscores.add(new Button("Python",30,5*Snake.WIDTH/6,Snake.HEIGHT/2-150,20,20,true,new Button.Action() {
			public void doAction(Button button) {
				showScores(SnakeType.PYTHON);
			}
		}));
		pythonButton.setTextPaint(Color.white);
		pythonButton.setBackground(Color.black);
		pythonButton.setBackgroundHighlight(Color.gray);
		pythonButton.setBackgroundPressed(Color.darkGray);
		pythonButton.setBorder(Color.yellow);
		pythonButton.setBorderHighlight(Color.yellow);
		pythonButton.setBorderPressed(Color.yellow);
	}
	
	public void showScores(final SnakeType type) {
		if(slugButton != null) {
			slugButton.setBackground(Color.black);
			slugButton.setTextPaint(Color.white);
			wormButton.setBackground(Color.black);
			wormButton.setTextPaint(Color.white);
			pythonButton.setBackground(Color.black);
			pythonButton.setTextPaint(Color.white);
		}
		
		switch(type) {
			case SLUG:
				if(slugButton != null) {
					slugButton.setBackground(Color.white);
					slugButton.setTextPaint(Color.black);
				}
				current = slugEntries;
				break;
			case WORM:
				if(wormButton != null) {
					wormButton.setBackground(Color.white);
					wormButton.setTextPaint(Color.black);
				}
				current = wormEntries;
				break;
			case PYTHON:
				if(pythonButton != null) {
					pythonButton.setBackground(Color.white);
					pythonButton.setTextPaint(Color.black);
				}
				current = pythonEntries;
				break;
		}
		
		current[0].name = "Fetching...";
		
		new Thread() {
			public void run() {
				Entry[] current = Highscores.this.current;
				
				SocketPacketIO io = null;
				try {
					io = new SocketPacketIO("www.ra4king.com",5051);
					
					Packet packet = new Packet();
					packet.writeString("Snake game");
					io.write(packet);
					
					packet = new Packet();
					packet.writeInt(0);
					io.write(packet);
					
					double serverVer = io.read().readDouble();
					if(version < serverVer && !hasShownUpdate) {
						JOptionPane.showMessageDialog(getParent().getGame(),"<html>New Update! New version: " + serverVer + "<br/>Current version: " + version);
						hasShownUpdate = true;
					}
					
					packet = new Packet();
					packet.writeInt(2);
					packet.writeString(type.toString().toLowerCase());
					io.write(packet);
					
					packet = io.read();
					for(int a = 0; a < 10; a++) {
						String[] entry = packet.readString().split("<:>");
						current[a].name = entry[0].trim();
						current[a].score = Integer.parseInt(entry[1].trim());
					}
					
					packet = new Packet();
					packet.writeInt(-1);
					io.write(packet);
				}
				catch(Exception exc) {
					exc.printStackTrace();
					
					current[0].name = "Connection error.";
				}
				finally {
					try {
						io.close();
					}
					catch(Exception exc) {}
				}
			}
		}.start();
	}
	
	public void submit(final int score, final SnakeType type) {
		if(score <= 0)
			return;
		
		if(user == null) {
			String name;
			while("".equals(name = JOptionPane.showInputDialog(getParent().getGame(),"Enter name:"))) {
				if(name == null)
					return;
			}
			
			user = new Entry(name,score);
		}
		
		if(score > user.score)
			user.score = score;
		
		new Thread() {
			public void run() {
				String name = user.name;
				
				SocketPacketIO io = null;
				try {
					io = new SocketPacketIO("www.ra4king.com",5051);
					
					Packet packet = new Packet();
					packet.writeString("Snake game");
					io.write(packet);
					
					packet = new Packet();
					packet.writeInt(0);
					io.write(packet);
					
					if(version < io.read().readDouble())
						System.out.println("NEW UPDATE!");
					
					packet = new Packet();
					packet.writeInt(1);
					packet.writeString(name);
					packet.writeInt(score);
					packet.writeString(type.toString().toLowerCase());
					io.write(packet);
					
					packet = new Packet();
					packet.writeInt(-1);
					io.write(packet);
				}
				catch(Exception exc) {
					exc.printStackTrace();
				}
				finally {
					try {
						io.close();
					}
					catch(Exception exc) {}
				}
			}
		}.start();
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.white);
		g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,30));
		for(int a = 0; a < 10; a++) {
			g.drawString(String.valueOf(a+1), 50, 210 + (a*33));
			g.drawString(current[a].name, 100, 210 + (a*33));
			if(current[a].score > 0)
				g.drawString(String.valueOf(current[a].score), Snake.WIDTH-100, 210 + (a*33));
		}
	}
	
	public static class Entry {
		private String name;
		private int score;
		
		public Entry(String name, int score) {
			this.name = name;
			this.score = score;
		}
		
		public String getName() {
			return name;
		}
		
		public int getScore() {
			return score;
		}
	}
}
