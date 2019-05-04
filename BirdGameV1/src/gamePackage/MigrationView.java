package gamePackage;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class MigrationView extends View {
	private Image g1, map;
	public int imgVelY = 0;
	private int scaledImageWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private int scaledImageHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	private BufferedImage airplaneImg = super.createImage("src/images/airplane.png");
	private BufferedImage blockImg = super.createImage("src/images/block.png");
	private BufferedImage fishImg = super.createImage("src/images/fish.png");
	private BufferedImage healthImg = super.createImage("src/images/health.png");
	private BufferedImage healthIcon = super.createImage("src/images/birdHealth.png");
			
	double birdX, birdY, planeX, planeY, blockX, blockY, fishX, fishY;
	
	private int imgWidth = 150;
	private String birdImagePath = "src/images/osprey_frames.png";
	private int birdFrameCount = 22;
	private BufferedImage[] bird_imagesBufferedImage;
	private int picNum = 0;
	
	private int health;
	private int healthCount;
	
	public MigrationView() {
		super();
		this.loadImage();
	}
	
	public MigrationView(ActionListener actionListener) {
		super();

		JButton buttonNext = new JButton("press the SPACE bar to go to the next game");

		buttonNext.addActionListener(actionListener);
		buttonNext.setMnemonic(KeyEvent.VK_SPACE);

		this.add(buttonNext);

		this.loadImage();
	}

	//Shows the GameObjects that the bird must avoid
	public void displayGameObjects() 
	{
		
	}
	
	//Draws the background for the Migration minigame
	@Override
	public void drawBackground() 
	{
		
	}
	
	private void loadImage() {
		ImageIcon grass_1 = new ImageIcon("src/images/beach.png");
		ImageIcon miniMap = new ImageIcon("src/images/minimap.png");
		g1 = grass_1.getImage().getScaledInstance(scaledImageWidth, scaledImageHeight, Image.SCALE_DEFAULT);
		map = miniMap.getImage().getScaledInstance(scaledImageWidth/4, scaledImageHeight, Image.SCALE_DEFAULT);
		BufferedImage birdFrames = super.createImage(birdImagePath);
		bird_imagesBufferedImage = new BufferedImage[birdFrameCount];
		
		for (int i = 0; i < birdFrameCount; i++) {
			bird_imagesBufferedImage[i] = birdFrames.getSubimage(imgWidth * i, 0, imgWidth, 150);
		}
		
		
		setDoubleBuffered(true);
	}
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		
		picNum = (picNum + 1) % birdFrameCount;
		imgVelY-=1;
		
		
		if (imgVelY % scaledImageHeight == 0) {
			imgVelY = 0;
		}
		
		g2.drawImage(g1, 0, (-imgVelY % scaledImageHeight), null); // draws image in the window
		g2.drawImage(g1,  0,(-(imgVelY % scaledImageHeight)-scaledImageHeight), null); // draws image in the window, had to make second image the same as the first for continuity
		
		g2.drawImage(bird_imagesBufferedImage[picNum], (int)birdX, (int)birdY, null);
		g2.drawImage(blockImg, (int)blockX, (int)blockY, null);

		g2.drawImage(airplaneImg, (int)planeX, (int)planeY, null);
		g2.drawImage(fishImg, (int)fishX, (int)fishY, null);

		g2.drawImage(healthImg, scaledImageWidth - 390, 20, null);
		g2.drawImage(map, 0, 0, null);
		
		for (int i = 0; i < healthCount; i++) {
			g2.drawImage(healthIcon, (scaledImageWidth - 350) + (30 * i), 20, null);
		}
		
		
	}

	@Override
	public void update(ArrayList<GameObject> list) {
		
		Bird b = (Bird) list.get(0);
		GameObject plane = list.get(1);
		GameObject block = list.get(2);
		GameObject questionBlock = list.get(3);
		GameObject food = list.get(4);
		
		birdX = b.getX();
		birdY = b.getY();
		planeX = plane.getX();
		planeY = plane.getY();
		blockX = block.getX();
		blockY = block.getY();
		fishX = food.getX();
		fishY = food.getY();
		health = b.getHealth();
		healthCount = b.getHealthCount();
		//System.out.println(b.getHealthCount() + "(MigrationView.java)");
		
	}
}
	




