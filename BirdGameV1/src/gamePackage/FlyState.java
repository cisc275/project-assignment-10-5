package gamePackage;

public enum FlyState {
	UP("up"),
	DOWN("down"),
	RIGHT("right"),
	LEFT("left"),
	STILL("still");
	
	private String state = null;
	
	private FlyState(String state) {
		this.state = state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}
}
