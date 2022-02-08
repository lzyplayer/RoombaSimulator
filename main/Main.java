package main;

import java.awt.Dimension;

import javax.swing.JFrame;

import world.Point2D;
import world.Room;
import world.RoombaGUI;
import world.RoombaSimulator;

public class Main {
	
	public static void main(String[] args) {
		int width = 800;
		int height = 600;
		int botRadius = 10;
		Room r = new Room(width, height, 20);
		Point2D startPos = r.getStartPos(botRadius);
		MyRoomba robo = new MyRoomba(startPos.x, startPos.y, botRadius);
		RoombaSimulator sim = new RoombaSimulator(robo, r);
		RoombaGUI gui = new RoombaGUI(sim);
		gui.setSize(new Dimension(width+400,height+50));
		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		sim.setGui(gui);
		sim.simulate();
	}
}