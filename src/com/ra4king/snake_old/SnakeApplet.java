package com.ra4king.snake_old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JApplet;
import javax.swing.Timer;

public class SnakeApplet extends JApplet {
	private static final long serialVersionUID = 1990562856049928295L;
	
	private Menus menu;
	private MouseAdapter mouseListener;
	private Snake snake;
	private Timer t;
	private String level;
	
	public void init() {
		setSize(500,500);
		
		menu = (Menus)add(new Menus());
		
		mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				int a = menu.mouseClicked(me.getX(),me.getY());
				
				if(a > 0) {
					removeMouseListener(this);
					remove(menu);
					newGame(a);
				}
			}
			
			public void mouseReleased(MouseEvent me) {
				menu.mouseMoved(me.getX(),me.getY());
			}
		};
		
		addMouseListener(mouseListener);
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent me) {
				menu.mouseMoved(me.getX(),me.getY());
			}
		});
	}
	
	public void newGame(int snakeType) {
		switch(snakeType) {
			case Snake.SLUG:
				level = "Slug";
				break;
			case Snake.WORM:
				level = "Worm";
				break;
			case Snake.PYTHON:
				level = "Python";
		}
		
		snake = (Snake)add(new Snake(snakeType));
		snake.setSize(500,500);
		
		snake.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent key) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_RIGHT:
						snake.setDirection(1);
						break;
					case KeyEvent.VK_UP:
						snake.setDirection(2);
						break;
					case KeyEvent.VK_LEFT:
						snake.setDirection(-1);
						break;
					case KeyEvent.VK_DOWN:
						snake.setDirection(-2);
				}
			}
		});
		
		snake.requestFocusInWindow();
		
		(t = new Timer(40,new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				snake.update();
				
				if(snake.hasLost()) {
					t.stop();
					showGameOver();
				}
				
				repaint();
			}
		})).start();
	}
	
	public void showGameOver() {
		t = null;
		menu.submit(snake.getPoints(),level);
		remove(snake);
		add(menu);
		addMouseListener(mouseListener);
		
		menu.requestFocusInWindow();
	}
}