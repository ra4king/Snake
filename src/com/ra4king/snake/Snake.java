package com.ra4king.snake;

import java.awt.Color;
import java.awt.Font;

import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.MenuPage;
import com.ra4king.gameutils.Menus;
import com.ra4king.gameutils.gui.Button;
import com.ra4king.gameutils.gui.Label;
import com.ra4king.snake.SnakeBoard.SnakeType;

public class Snake extends Game {
	private static final long serialVersionUID = 4863640150633571015L;
	
	private static final int WIDTH = 500, HEIGHT = 600;
	
	public Snake() {
		super(WIDTH,HEIGHT,60,2.0);
	}
	
	@Override
	protected void initGame() {
		Menus menus = new Menus();
		
		MenuPage mainMenu = menus.addPage("Main Menu", new MenuPage(menus));
		mainMenu.setBackground(Color.black);
		
		Font font = new Font(Font.DIALOG_INPUT,Font.BOLD,45);
		Button b;
		b = (Button)mainMenu.add(new Button("SLUG",5,0,2*HEIGHT/3,20,20,false,new Button.Action() {
			public void doAction(Button button) {
				setScreen("Snake Board", new SnakeBoard(SnakeType.SLUG));
			}
		}));
		b.setFont(font);
		b.setTextPaint(Color.red);
		b = (Button)mainMenu.add(new Button("WORM",5,b.getIntWidth()+1,2*HEIGHT/3,20,20,false,new Button.Action() {
			public void doAction(Button button) {
				setScreen("Snake Board", new SnakeBoard(SnakeType.WORM));
			}
		}));
		b.setFont(font);
		b.setTextPaint(Color.red);
		b = (Button)mainMenu.add(new Button("PYTHON",5,b.getIntX()+b.getIntWidth()+1,2*HEIGHT/3,20,20,false,new Button.Action() {
			public void doAction(Button button) {
				setScreen("Snake Board", new SnakeBoard(SnakeType.PYTHON));
			}
		}));
		b.setFont(font);
		b.setTextPaint(Color.red);
		
		Label l = (Label)mainMenu.add(new Label("SNAKE",new Font(Font.DIALOG_INPUT,Font.BOLD,100),WIDTH/2,200,true));
		l.setTextPaint(Color.blue);
		
		setScreen("Menus",menus);
	}
}
