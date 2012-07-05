package com.ra4king.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.MenuPage;
import com.ra4king.gameutils.Menus;
import com.ra4king.gameutils.gui.Button;
import com.ra4king.gameutils.gui.Label;
import com.ra4king.snake.Highscores.Entry;
import com.ra4king.snake.SnakeBoard.SnakeType;

public class Snake extends Game {
	private static final long serialVersionUID = 4863640150633571015L;
	
	public static void main(String[] args) {
		Snake snake = new Snake();
		snake.setupFrame("Snake", false);
		snake.start();
	}
	
	private Highscores scores;
	
	public static final int WIDTH = 500, HEIGHT = 600;
	
	public Snake() {
		super(WIDTH,HEIGHT,60,2.0);
	}
	
	@Override
	protected void initGame() {
		showFPS(false);
		
		final Menus menus = new Menus();
		setScreen("Menus",menus);
		
		MenuPage mainMenu = menus.addPage("Main Menu", new MenuPage(menus));
		mainMenu.setBackground(Color.black);
		
		Font font = new Font(Font.DIALOG_INPUT,Font.BOLD,45);
		Button b;
		b = (Button)mainMenu.add(new Button("SLUG",5,0,2*HEIGHT/3,20,20,false,new Button.Action() {
			public void doAction(Button button) {
				setScreen("Snake Board", new Countdown(SnakeType.SLUG));
			}
		}));
		b.setFont(font);
		b.setTextPaint(Color.red);
		b = (Button)mainMenu.add(new Button("WORM",5,b.getIntWidth()+1,2*HEIGHT/3,20,20,false,new Button.Action() {
			public void doAction(Button button) {
				setScreen("Snake Board", new Countdown(SnakeType.WORM));
			}
		}));
		b.setFont(font);
		b.setTextPaint(Color.red);
		b = (Button)mainMenu.add(new Button("PYTHON",5,b.getIntX()+b.getIntWidth()+1,2*HEIGHT/3,20,20,false,new Button.Action() {
			public void doAction(Button button) {
				setScreen("Snake Board", new Countdown(SnakeType.PYTHON));
			}
		}));
		b.setFont(font);
		b.setTextPaint(Color.red);
		b = (Button)mainMenu.add(new Button("Highscores",30,WIDTH/2,HEIGHT-60,20,20,true,new Button.Action() {
			public void doAction(Button button) {
				menus.setMenuPageShown("Highscores");
			}
		}));
		b.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
		b.setTextPaint(Color.red);
		Label l = (Label)mainMenu.add(new Label("SNAKE",new Font(Font.DIALOG_INPUT,Font.BOLD,100),WIDTH/2,200,true));
		l.setTextPaint(Color.blue);
		
		
		MenuPage highscores = menus.addPage("Highscores",new MenuPage(menus));
		highscores.setBackground(Color.black);
		
		scores = (Highscores)highscores.add(new Highscores(SnakeType.WORM));
		
		l = (Label)highscores.add(new Label("SNAKE",new Font(Font.DIALOG_INPUT,Font.BOLD,100),Snake.WIDTH/2,30,true));
		l.setTextPaint(Color.blue);
		b = (Button)highscores.add(new Button("Main Menu",30,WIDTH/2,HEIGHT-60,20,20,true,new Button.Action() {
			public void doAction(Button button) {
				menus.setMenuPageShown("Main Menu");
			}
		}));
		b.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
		b.setTextPaint(Color.red);
	}
	
	@Override
	public void paint(Graphics2D g) {
		super.paint(g);
		
		Entry user = scores.getUserScore();
		if(user != null) {
			g.setColor(Color.magenta);
			g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,20));
			g.drawString(user.getName() + " - " + user.getScore(), 10, Snake.HEIGHT-10);
		}
		
		g.setColor(Color.black);
	}
}
