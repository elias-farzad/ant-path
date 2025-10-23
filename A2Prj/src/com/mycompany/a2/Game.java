package com.mycompany.a2;

import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.*;

/**
* Controller for GUI game.
* Replaces text-mode input with Buttons, Toolbar side menu, title-bar Help,
* and key bindings. Wires Commands to GameWorld methods.
*/
public class Game extends Form {
	private final GameWorld gw = new GameWorld();
	private final MapView mv = new MapView();
	private final ScoreView sv = new ScoreView();
	
	// Single instances of commands
	private final Command cmdAccelerate = new CmdAccelerate(gw);
	private final Command cmdBrake = new CmdBrake(gw);
	private final Command cmdLeft = new CmdLeft(gw);
	private final Command cmdRight = new CmdRight(gw);
	private final Command cmdCollideFlag = new CmdCollideFlag(gw);
	private final Command cmdCollideFood = new CmdCollideFood(gw);
	private final Command cmdCollideSpider = new CmdCollideSpider(gw);
	private final Command cmdTick = new CmdTick(gw);
	private final Command cmdAbout = new CmdAbout();
	private final Command cmdHelp = new CmdHelp();
	private final Command cmdExit = new CmdExit();
	
	public Game() {
		super("Ant-Path", new BorderLayout());

		// Views
		add(BorderLayout.NORTH, sv);
		add(BorderLayout.CENTER, mv);

		// Controls (left/right/bottom)
		Container west = sideCol();
		Container east = sideCol();
		Container south = new Container(new FlowLayout(CENTER));

		// Buttons reuse the same Command instances
		Button bA = new StyledButton(cmdAccelerate);
		Button bB = new StyledButton(cmdBrake);
		Button bL = new StyledButton(cmdLeft);
		Button bR = new StyledButton(cmdRight);
		Button bF = new StyledButton(cmdCollideFood);
		Button bG = new StyledButton(cmdCollideSpider);
		Button bT = new StyledButton(cmdTick);
		Button bC = new StyledButton(cmdCollideFlag);

		west.addAll(bA, bL);
		east.addAll(bB, bR);
		south.addAll(bC, bG, bF, bT);

		add(BorderLayout.WEST, west);
		add(BorderLayout.EAST, east);
		add(BorderLayout.SOUTH, south);
		
		// Toolbar / Side menu
		Toolbar tb = new Toolbar();
		setToolbar(tb);
		tb.addCommandToSideMenu(cmdAccelerate);

		CheckBox sound = new CheckBox("Sound");
		sound.addActionListener(e -> gw.setSound(sound.isSelected()));
		tb.addComponentToSideMenu(sound);

		tb.addCommandToSideMenu(cmdAbout);
		tb.addCommandToSideMenu(cmdExit);
		tb.addCommandToRightBar(cmdHelp);
		
		// Key bindings
		addKeyListener('a', e -> cmdAccelerate.actionPerformed(null));
		addKeyListener('b', e -> cmdBrake.actionPerformed(null));
		addKeyListener('l', e -> cmdLeft.actionPerformed(null));
		addKeyListener('r', e -> cmdRight.actionPerformed(null));
		addKeyListener('c', e -> cmdCollideFlag.actionPerformed(null));
		addKeyListener('f', e -> cmdCollideFood.actionPerformed(null));
		addKeyListener('g', e -> cmdCollideSpider.actionPerformed(null));
		addKeyListener('t', e -> cmdTick.actionPerformed(null));

		// Observer wiring
		gw.addObserver(mv);
		gw.addObserver(sv);

		show(); // ensure MapView has size
		// Update dynamic world size
		GameWorld.WORLD_WIDTH = Math.max(1, mv.getWidth());
		GameWorld.WORLD_HEIGHT = Math.max(1, mv.getHeight());
		gw.setWorldSize(GameWorld.WORLD_WIDTH, GameWorld.WORLD_HEIGHT);
		gw.init();
	}
	
	private static Container sideCol() {
		Container c = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		return c;
	}

	private static class StyledButton extends Button {
		StyledButton(Command cmd) {
			super(cmd);
			getAllStyles().setBgTransparency(255);
			getAllStyles().setBgColor(0x0000ff);  // blue background
			getAllStyles().setFgColor(0xffffff);  // white text
		}
	}

	// Commands
	private class CmdAccelerate extends Command {
		CmdAccelerate(GameWorld gw){
			super("Accelerate"); 
			this.gw=gw;
		}
		
		private final GameWorld gw;
		public void actionPerformed(ActionEvent e) {
			gw.accelerate(); 
		} 
	}

	private class CmdBrake extends Command {
		CmdBrake(GameWorld gw){
			super("Brake");
			this.gw=gw; 
		}
		private final GameWorld gw;
		public void actionPerformed(ActionEvent e) {
			gw.brake(); 
		}
	}
	
	private class CmdLeft extends Command {
		CmdLeft(GameWorld gw){
			super("Left"); 
			this.gw=gw; 
		}
		private final GameWorld gw; 
		public void actionPerformed(ActionEvent e){
			gw.turnLeft(); 
		}
	}
	
	private class CmdRight extends Command {
		CmdRight(GameWorld gw) {
			super("Right"); 
			this.gw=gw;
		}
		private final GameWorld gw; 
		public void actionPerformed(ActionEvent e){
			gw.turnRight(); 
		}
	}
	
	private class CmdCollideFlag extends Command {
		CmdCollideFlag(GameWorld gw){
			super("Collide With Flag"); 
			this.gw=gw; 
		}
		
		private final GameWorld gw;
		public void actionPerformed(ActionEvent e) {
		    TextField tf = new TextField("", "Flag number", 3, TextField.NUMERIC);
		    Command ok = new Command("OK");
		    Command cancel = new Command("Cancel");

		    Command result = Dialog.show("Flag", tf, new Command[]{ok, cancel});

		    if (result == ok) {
		        try {
		            int n = Integer.parseInt(tf.getText().trim());
		            gw.collideFlag(n);
		        } catch (Exception ex) {
		            Dialog.show("Error", "Invalid flag number", "OK", null);
		        }
		    }
		}
	}
	
	private class CmdCollideFood extends Command { 
		CmdCollideFood(GameWorld gw){ 
			super("Collide with Food Station"); 
			this.gw=gw; 
		}
		private final GameWorld gw;
		public void actionPerformed(ActionEvent e){
			gw.collideFoodStation(); 
		}
	}
	
	private class CmdCollideSpider extends Command {
		CmdCollideSpider(GameWorld gw){
			super("Collide with Spider"); 
			this.gw=gw; 
		}
		private final GameWorld gw;
		public void actionPerformed(ActionEvent e){
			gw.collideSpider();
		}
	}
	
	private class CmdTick extends Command {
		CmdTick(GameWorld gw){
			super("Tick");
			this.gw=gw; 
		}
		private final GameWorld gw;
		public void actionPerformed(ActionEvent e){
			gw.tick(); 
		}
	}
	
	private class CmdAbout extends Command {
		CmdAbout(){
			super("About");
		}
		public void actionPerformed(ActionEvent e){
			Dialog.show("About", "Ant Path A2 — Elias Farzad CSC 133 — Fall 2025", "OK", null); 
		}
	}
	
	private class CmdHelp extends Command {
		CmdHelp(){
			super("Help"); 
		}
		public void actionPerformed(ActionEvent e){
			Dialog.show("Help", "Keys: "
					+ "a - Accelerate"
					+ "b - Brake"
					+ "l - Turn Left"
					+ "r - Turn Right"
					+ "c - Set Food Consumption"
					+ "f - Collide with a Food Station"
					+ "g - Collide with a Spider"
					+ "t - Tick",
					"OK",
					null); 
		}
	}
	
	private class CmdExit extends Command {
		CmdExit(){ 
			super("Exit"); 
		}
		public void actionPerformed(ActionEvent e){
			boolean ok = Dialog.show("Confirm", "Quit the game?", "Yes", "No"); 
			if (ok) Display.getInstance().exitApplication(); 
		}
	}
}
