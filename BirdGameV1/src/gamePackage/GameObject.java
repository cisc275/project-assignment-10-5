package gamePackage;
import java.awt.geom.Point2D;

public class GameObject extends Point2D {
	protected HitBox GameObjectBox;
	private ObjectType type;
	
	private double xPosition;
	private double yPosition;
	
	private final int SPEED = 10;
	
	public GameObject(BirdType b, double startingCoord, ObjectType t, int imgWidth, int imgHeight){
		if (b == BirdType.OSPREY) {
			this.xPosition = startingCoord;
			
			int maxWidth = Model.scaledImageWidth + this.GameObjectBox.width;
			int minWidth = 0;
			int randY = (int)(Math.random() * (maxWidth - minWidth + 1) + minWidth);
			
			this.yPosition = randY;
		} else {
			this.yPosition = startingCoord;
			
			int maxHeight = 0 - this.GameObjectBox.height;
			int minHeight = Model.scaledImageHeight;
			int randX = (int)(Math.random() * (maxHeight - minHeight + 1) + minHeight);
			
			this.xPosition = randX;
		}
		
		this.type = t;
		this.GameObjectBox = new HitBox((int)this.getX(), (int)this.getY(), imgWidth, imgHeight);
		
		
	}
	
	/**
	 * getX()
	 * This method overrides the Point2D getX() method
	 * It returns the current x-position of the GameObject.
	 */
	@Override
	public double getX() {
		return this.xPosition;
	}

	/**
	 * getY()
	 * This method overrides the Point2D getY() method
	 * It returns the current y-position of the GameObject.
	 */
	@Override
	public double getY() {
		return this.yPosition;
	}
	
	/**
	 * setLocation()
	 * This method overrides the Point2D setLocation() method.
	 * It sets the x-position and y-position of the GameObject.
	 */
	@Override
	public void setLocation(double x, double y) {
		this.xPosition = x;
		this.yPosition = y;
	}
	
	/**
	 * getGameObjectSpeed()
	 * @return SPEED, the final GameObject speed
	 */
	public int getGameObjectSpeed() {
		return this.SPEED;
	}

	/**
	 * getType()
	 * 
	 * @return type, the GameObject type
	 */
	public ObjectType getType() {
		return type;
	}
}
