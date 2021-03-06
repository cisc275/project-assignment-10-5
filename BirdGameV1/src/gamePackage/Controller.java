package gamePackage;
import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class Controller implements Serializable {
	private boolean wam = true;
	
	private SideSwiperModel sideSwiperModel;
	private SideSwiperView sideSwipeView;

	private WhackAMoleView whackViewTut;
	private WhackAMoleView whackViewGame;
	private WhackAMoleModel whackModel;
	private final int WHACK_A_MOLE_MODSWITCH = 3;
	
	private MigrationModel migrationModel;
	private MigrationView migrationView;
	
	private StartView startViewOsprey;
	private StartView startViewNorthernHarrier;
	
	private OspreyWinView ospreyWinView;
	private LoseView loseView;
	private WinView winView;
	
	private GameState state;
	
	private BirdType birdType;
	private JFrame frame;
	private JPanel masterPanel;
	private CardLayout cardLayout;
	
	private boolean ssvPaused = false;
	private boolean mmvPaused = false;
	private boolean sideSwiperGameOver = false;
	private boolean migrationGameOver = false;
	private boolean whackWillWin;
	private boolean whackWillNotWin;
	
	private final int MIGRATIONMAPNUM = 9;
	private final int DELAWAREMAPNUM = 13;

	private final int STARTING_HEALTH = 3;
	
	private int whackSwitch = 1;
	
	private boolean whackWinner = false; //change to int to handle if win from osprey or northern harrier
	
	private final int FPS = 15;
	private int count = 1;
	private ArrayList<Integer> whackUserSequence = new ArrayList<Integer>();
	
	public Controller() {
		init();
	}
	/**
	 * Initiate the models, views, JFrame, cardLayout, and Keybindings for the game.
	 */
	public void init() {
		this.birdType = BirdType.OSPREY;
		this.state = GameState.START;
		
		instantiateModels();
		instantiateViews();
		createJFrame();
		addPanelsToCardLayout();
		initializeKeyBindings();
		
		frame.setVisible(true);
	}
	
	/**
	 * Instantiate the models that the game will use.
	 */
	private void instantiateModels() {
		sideSwiperModel = new SideSwiperModel();
		migrationModel = new MigrationModel();
		whackModel = new WhackAMoleModel();
	}
	
	/**
	 * Instantiate the views that the game will use.
	 */
	private void instantiateViews() {
		// Code to run SideSwiper Game
		sideSwipeView = new SideSwiperView();
		
		// Code to run Whack a Mole Game Tutorial
		whackViewTut = new WhackAMoleView();
		
		// Code to run Whack a Mole Game
		whackViewGame = new WhackAMoleView();
		
		// Code to run Migration Game
		migrationView = new MigrationView();
		
		//Code to display Start screen
		startViewOsprey = new StartView(BirdType.OSPREY);
		startViewNorthernHarrier = new StartView(BirdType.NORTHERNHARRIER);
		
		//Code to display Win screen
		ospreyWinView = new OspreyWinView();
				
		//Code to display End screen
		loseView = new LoseView();
		winView = new WinView();
	}
	
	/**
	 * Instantiate the main JFrame.
	 */
	private void createJFrame() {
		// Creates the frame and selects settings
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); // sets screen to full screen
	}
	
	/**
	 * Set up the main panel for cardLayout and add add subviews to the deck for cardLayout.
	 */
	private void addPanelsToCardLayout() {
		this.cardLayout = new CardLayout();
		masterPanel = new JPanel(cardLayout);
		frame.add(masterPanel);
		
		masterPanel.add(startViewOsprey, "startOsprey");
		masterPanel.add(sideSwipeView, "sideSwiper");	
		masterPanel.add(whackViewTut, "whackAMoleTut");
		masterPanel.add(whackViewGame, "whackViewGame");
		masterPanel.add(ospreyWinView, "ospreywin");
		masterPanel.add(startViewNorthernHarrier, "startNorthernHarrier");
		masterPanel.add(migrationView, "migration");
		masterPanel.add(loseView, "lose");
		masterPanel.add(winView, "win");
	}
	
	/**
	 * Set up keyBindings for the starting/Osprey View, sideSwiperView, whackAMole, starting/Migration View, and the Win/Lose View
	 */
	private void initializeKeyBindings() {
		
		//Goes from startOsprey to side swiper tutorial
		addKeyBinding(startViewOsprey, KeyEvent.VK_T, "next panel from start", (e) -> {
			this.state = GameState.SIDESWIPER;
			this.cardLayout.show(this.masterPanel, "sideSwiper");
			System.out.println("should start tuturial now");
		
		}, false);
		
		
		//Goes from side swiper game to whack a mole tutorial
		addKeyBinding(sideSwipeView, KeyEvent.VK_0, "next panel from ssv", (e) -> {
			this.state = GameState.WHACKAMOLE;
			this.cardLayout.show(this.masterPanel, "whackAMoleTut");
			whackModel.randomizeSequence();
			whackViewTut.updateSequence(whackModel.getSequence());
			whackViewTut.setIsWackView(true);
			whackViewTut.initTimers();
			whackViewTut.setShowTutBox(true);
		}, false);
		
		//Goes from whackViewTut to whackView game
		addKeyBinding(whackViewTut, KeyEvent.VK_SPACE, "next panel from ssv", (e) -> {
			this.state = GameState.WHACKAMOLE;
			this.cardLayout.show(this.masterPanel, "whackViewGame");
			whackModel.randomizeSequence();
			whackViewGame.updateSequence(whackModel.getSequence());
			whackViewGame.setIsWackView(true);
			whackViewGame.setShowTutBox(false);
			whackViewGame.initTimers();
		}, false);
		
		//Goes from whack a mole game to win screen if player wins
		addKeyBinding(whackViewGame, KeyEvent.VK_0, "next panel from wmv", (e) -> { 	
			if (count % WHACK_A_MOLE_MODSWITCH == 1) {
				this.state = GameState.OSPREYWIN;
				this.cardLayout.show(this.masterPanel, "ospreywin");
				count++;
			}
			else if (count % WHACK_A_MOLE_MODSWITCH == 2) {
				this.state = GameState.WIN;
				this.cardLayout.show(this.masterPanel, "win");
				count++;
			}
			else {
				this.state = GameState.LOSE;
				this.cardLayout.show(this.masterPanel, "lose");
				count++;
			}
		}, false);
		
		//Goes from win screen to migration tutorial
		addKeyBinding(ospreyWinView, KeyEvent.VK_SPACE, "next panel from wmv", (e) -> { 
			
			this.state = GameState.START;
			this.birdType = BirdType.NORTHERNHARRIER;
			this.cardLayout.show(this.masterPanel, "startNorthernHarrier");
			setBindingsToWhackAMoleNULL();
			whackViewTut.resetTimers();
			whackModel.setKeyState(0);
			whackViewTut.resetIndex();
			
			whackViewGame.resetTimers();
			whackViewGame.resetIndex();
			
		}, false);
		
		addKeyBinding(startViewNorthernHarrier, KeyEvent.VK_T, "next panel from start", (e) -> {

			this.state = GameState.MIGRATION;
			this.cardLayout.show(this.masterPanel, "migration");
			

		}, false);

		
		addKeyBinding(migrationView, KeyEvent.VK_0, "next panel from mmv", (e) -> {
			
			this.state = GameState.WHACKAMOLE;
			this.cardLayout.show(this.masterPanel, "whackViewGame");
			whackModel.randomizeSequence();
			whackViewGame.updateSequence(whackModel.getSequence());
			whackViewGame.setIsWackView(true);
			whackViewGame.initTimers();
			
		}, false);

		addKeyBinding(loseView, KeyEvent.VK_SPACE, "next panel from end", (e) -> {
			this.state = GameState.START;
			this.cardLayout.show(this.masterPanel, "startOsprey");
			setBindingsToWhackAMoleNULL();
			//whackViewTut.resetTimers();
			whackModel.setKeyState(0);
			//whackViewTut.resetIndex();
			
			whackViewGame.resetTimers();
			whackViewGame.resetIndex();
			
		}, false);
		
		addKeyBinding(winView, KeyEvent.VK_SPACE, "next panel from end", (e) -> {
			this.state = GameState.START;
			this.cardLayout.show(this.masterPanel, "startOsprey");
			this.birdType = BirdType.OSPREY;
			setBindingsToWhackAMoleNULL();
			whackViewTut.resetTimers();
			whackViewGame.resetTimers();
			whackModel.setKeyState(0);
		}, false);
		
		setBindingsToSideSwiper();
		setBindingsToMigration();
	}

	/**
	 * starts our game, initializes the beginning View.
	 */
	public void start() {
		while (repeat()) {
			try {
				Thread.sleep(FPS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * update the sideSwiper logic for the game, if the bird's health gets to 0, gameOver, update the MiniMap
	 */
	public void updateSideSwiperModel() {
		if (sideSwiperModel.getPicNumMap() > 1)
			sideSwiperModel.setIsFirstFrame(false);
		
		sideSwipeView.setState(sideSwiperModel.getState());
		sideSwipeView.setPicNumMap(sideSwiperModel.getPicNumMap());
		ssvPaused = sideSwiperModel.updateLocationAndDirectionForOsprey();
		ArrayList<GameObject> list2 = sideSwiperModel.getUpdatableGameObjectsForOsprey();
		
		if (sideSwiperModel.getOsprey().getHealthCount() <= 0) {
			sideSwiperGameOver = true;
			this.state = GameState.LOSE;
			sideSwiperModel.getOsprey().setFlyState(FlyState.STILL);
			for (int i = 1; i < list2.size(); i++) {
				sideSwiperModel.resetGameObjectLocation(list2.get(i));
			}
			
			gameOver();
		}
		
		if (!sideSwiperModel.getIsFirstFrame() && sideSwiperModel.getPicNumMap() % MIGRATIONMAPNUM == 0) {
			this.state = GameState.WHACKAMOLE;
			this.cardLayout.show(this.masterPanel, "whackAMoleTut");
			whackViewTut.setShowTutBox(true);
			whackModel.randomizeSequence();
			whackViewTut.updateSequence(whackModel.getSequence());
			whackViewTut.setIsWackView(true);
			whackViewTut.initTimers();
			sideSwiperModel.getOsprey().setFlyState(FlyState.STILL);
			for (int i = 1; i < list2.size(); i++) {
				sideSwiperModel.resetGameObjectLocation(list2.get(i));
			}
			sideSwiperModel.setIsFirstFrame(true);
			
		}
		
		if(sideSwiperModel.getIsHit() == true) {
			sideSwipeView.setTimeForRectangle(true);
			System.out.println("It is time to draw the red rectangle");
		}
		
		sideSwipeView.update(list2);
	}
	
	/**
	 * update the MigrationModel logic for the game, if the bird's health gets to 0, gameOver, update the MiniMap
	 */
	public void updateMigrationModel() {
		if (migrationModel.getPicNumMap() > 1) {
			migrationModel.setFirstFrame(false);
		}
		
		migrationView.setState(migrationModel.getState());
		migrationView.setPicNumMap(migrationModel.getPicNumMap());
		mmvPaused = migrationModel.updateLocationAndDirectionForNorthernHarrier();
		ArrayList<GameObject> list3 = migrationModel.getUpdatableGameObjectsForNorthernHarrier();
		
		if (migrationModel.getNorthernHarrier().getHealthCount() <= 0) {
			migrationGameOver = true;
			this.state = GameState.LOSE;
			migrationModel.getNorthernHarrier().setFlyState(FlyState.STILL);
			for (int i = 1; i < list3.size(); i++) {
				migrationModel.resetGameObjectLocation(list3.get(i));
			}
			gameOver();
		}
		
		if (!migrationModel.getFirstFrame() && migrationModel.getPicNumMap() % DELAWAREMAPNUM == 0) {
			this.state = GameState.WHACKAMOLE;
			this.cardLayout.show(this.masterPanel, "whackAMoleTut");
			whackViewTut.setShowTutBox(true);
			whackModel.randomizeSequence();
			whackViewTut.updateSequence(whackModel.getSequence());
			whackViewTut.setIsWackView(true);
			whackViewTut.initTimers();
			migrationModel.getNorthernHarrier().setFlyState(FlyState.STILL);
			for (int i = 1; i < list3.size(); i++) {
				migrationModel.resetGameObjectLocation(list3.get(i));
			}
			migrationModel.setFirstFrame(true);
			
		}
		
		migrationView.update(list3);
	}
	
	/**
	 * Switch cardLayout to show gameOver/Lose view, reset the corresponding model logic and variables.
	 */
	public void gameOver() {
		
		if (sideSwiperGameOver) {
			sideSwiperModel.init();
			migrationModel.init();
			sideSwiperModel.getOsprey().setHealthCount(STARTING_HEALTH);
			this.cardLayout.show(this.masterPanel, "lose");
			sideSwiperGameOver = false;
		}
		if (migrationGameOver) {
			sideSwiperModel.init();
			migrationModel.init();
			migrationModel.getNorthernHarrier().setHealthCount(STARTING_HEALTH);
			this.cardLayout.show(this.masterPanel, "lose");
			migrationGameOver = false;
		}

	}
	
	/**
	 * determine the win screen after user wins the whackAMole game.
	 */
	public void winner() {
		whackWillNotWin = false;
		whackWillWin = false;
		if (whackUserSequence.size() == whackViewGame.getEXPECTED_PATTERN_SIZE()) {
			System.out.println("user pattern reached 4");
			this.wam = true;
			for (int i = 0; i < whackViewTut.getEXPECTED_PATTERN_SIZE(); i++) {
				if (whackModel.getSequence().get(i) == whackUserSequence.get(i)) {
					whackWillWin = true;
					System.out.println("correct index");
				}
				else {
					whackWillNotWin = true;
					System.out.println("False index!");
				}
			}
			whackWinner = whackWillWin && !whackWillNotWin;
			if (whackWinner) {
				System.out.println("Winner!!!");
				if (this.whackSwitch % 2 != 0) {
					this.state = GameState.OSPREYWIN;
					this.cardLayout.show(this.masterPanel, "ospreywin");
					this.whackSwitch++;
				}
				else {
					this.state = GameState.WIN;
					this.cardLayout.show(this.masterPanel, "win");
					this.whackSwitch++;
					sideSwiperModel.init();
					migrationModel.init();
				}
			}
			if (!whackWinner && this.birdType == BirdType.OSPREY) {
				System.out.println("Loser!!!");
				
				this.whackSwitch = 1;
				this.cardLayout.show(this.masterPanel, "lose");
				this.state = GameState.LOSE;
				sideSwiperModel.init();
				migrationModel.init();
			} else if (!whackWinner && this.birdType == BirdType.NORTHERNHARRIER) {
				System.out.println("Loser!!!");
				
				this.whackSwitch = 1;
				this.cardLayout.show(this.masterPanel, "lose");
				this.state = GameState.LOSE;
				sideSwiperModel.init();
				migrationModel.init();
			}
		}
	}

    /**
     * repeat()
     * Calls an overloaded version of repeat() which is conditional on the bird type.
     * @return a boolean to represent if the game should repeat
     */
    public boolean repeat() {
        
        boolean shouldRepeat = false;;
        if (this.state == GameState.WHACKAMOLE && whackViewTut.getDrawed() && this.wam) {
			setBindingsToWhackAMole();
			this.wam = false;
		}
        
        switch(birdType) {
            case OSPREY:
                shouldRepeat = repeat(birdType, this.sideSwiperModel, this.sideSwiperModel.getOsprey());
                break;
            case NORTHERNHARRIER:
                shouldRepeat = repeat(birdType, this.migrationModel, this.migrationModel.getNorthernHarrier());
                break;
        }
        
        return shouldRepeat;
    }
    
    /**
     * repeat()
     * Updates the current mode and view if the game is not paused.
     * Displays a question when the game is paused. If the question is answered correctly, health is rewarded.
     * If the question is answered incorrectly, health is depleted.
     * @param birdType, the current birdType of the game
     * @param currentModel, the current model the game is playing with
     * @param bird, the current bird the game is using
     * @return true if the game should repeat; false otherwise
     */
    public boolean repeat(BirdType birdType, Model currentModel, Bird bird) {
        if (currentModel.getPauseGameFlag() == false) {
            updateMode();
            drawView();
            return true;
        } else {
            Question q = new Question(birdType);
            q.displayQuestion();
            while(currentModel.getPauseGameFlag() == true) {
                if(q.isCorrect()) {
                    bird.increaseHealthCount(4);
                    q.setCorrect(false);
                } else {
                    bird.decreaseHealthCount();
                    q.setCorrect(false);
                }
                currentModel.changePauseGameFlag();
                start();
            }
            return false;
        }
    }
	
    /**
     * uses swingWorker to split up model updating tasks to be in the background for better performance.
     */
	public void updateMode() {
		SwingWorker<Void, Void> updateModelWorker = new SwingWorker<Void, Void>() {
			@SuppressWarnings("incomplete-switch")
			@Override
			protected Void doInBackground() throws Exception {
				switch (state) {
				case SIDESWIPER:
					serializeGame(sideSwiperModel, "sideSwiperModel.txt");
					if (!ssvPaused)
						updateSideSwiperModel();
					break;
				case MIGRATION:
					serializeGame(migrationModel, "migrationModel.txt");
					if (!mmvPaused)
						updateMigrationModel();
					break;
				case WHACKAMOLE:
					serializeGame(whackModel, "whackModel.txt");
					updateWhackKeyState();
					winner();
					break;
				case START:
					break;
				case OSPREYWIN:
					whackUserSequence.clear();
					break;
				case LOSE:
					whackUserSequence.clear();
					break;
				case WIN:
					whackUserSequence.clear();
				}
				return null;
			}
		};
		
		updateModelWorker.execute();
	}
	
	/**
	 * update the whackAMole view's keys state to be the whackModel's keyState.
	 */
	public void updateWhackKeyState() {
		whackViewGame.setKeyState(whackModel.getKeyState());
	}
	
	/**
	 * repaint the respected game views based on the game state.
	 */
	@SuppressWarnings("incomplete-switch")
	public void drawView() {
		switch (state) {
		case SIDESWIPER:
			SwingUtilities.invokeLater(() ->  this.sideSwipeView.repaint());
			break;
		case MIGRATION:
			SwingUtilities.invokeLater(() ->  this.migrationView.repaint());
			break;
		case WHACKAMOLE:
			SwingUtilities.invokeLater(() ->  this.ospreyWinView.repaint());
			break;
		case START:
			SwingUtilities.invokeLater(() ->  this.startViewOsprey.repaint());
			SwingUtilities.invokeLater(() ->  this.startViewNorthernHarrier.repaint());
			break;
		case OSPREYWIN:
			SwingUtilities.invokeLater(() ->  this.ospreyWinView.repaint());
			break;
		case LOSE:
			SwingUtilities.invokeLater(() ->  this.loseView.repaint());
			break;
		case WIN:
			SwingUtilities.invokeLater(() -> this.winView.repaint());
		}
	}
	
	/**
	 * add keyBindings and actionListeners to specified JComponent
	 * @param comp
	 * @param keyCode
	 * @param id
	 * @param actionListener
	 * @param isReleased
	 */
	public static void addKeyBinding(JComponent comp, int keyCode, String id, ActionListener actionListener, boolean isReleased) {
		InputMap inputMap = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = comp.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, isReleased), id);
		actionMap.put(id, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(e);

			}
		});
	}
	
	/**
	 * set keyBindings to whackAMoleView
	 */
	public void setBindingsToWhackAMole() {
		addKeyBinding(whackViewGame, KeyEvent.VK_RIGHT, "go right", (evt) -> {
			whackModel.setKeyState(3);
			whackUserSequence.add(4);
			
			@SuppressWarnings("rawtypes")
			Iterator i = whackUserSequence.iterator();
			while (i.hasNext()) {
				System.out.print("User Sequence: ");
				System.out.println(i.next());
			}
			
			System.out.println("User Sequence size: ");
			System.out.println(whackUserSequence.size());

		}, false);
		
		addKeyBinding(whackViewGame, KeyEvent.VK_LEFT, "go left", (evt) -> {
			whackModel.setKeyState(4);
			whackUserSequence.add(3);
			
			@SuppressWarnings("rawtypes")
			Iterator i = whackUserSequence.iterator();
			while (i.hasNext()) {
				System.out.print("User Sequence: ");
				System.out.println(i.next());
			}
			
			System.out.println("User Sequence size: ");
			System.out.println(whackUserSequence.size());
		}, false);
		
		addKeyBinding(whackViewGame, KeyEvent.VK_UP, "go up", (evt) -> {
			whackModel.setKeyState(1);
			whackUserSequence.add(1);
			
			@SuppressWarnings("rawtypes")
			Iterator i = whackUserSequence.iterator();
			while (i.hasNext()) {
				System.out.print("User Sequence: ");
				System.out.println(i.next());
			}
			
			System.out.println("User Sequence size: ");
			System.out.println(whackUserSequence.size());
		}, false);
		
		addKeyBinding(whackViewGame, KeyEvent.VK_DOWN, "go down", (evt) -> {
			whackModel.setKeyState(2);
			whackUserSequence.add(2);

			@SuppressWarnings("rawtypes")
			Iterator i = whackUserSequence.iterator();
			while (i.hasNext()) {
				System.out.print("User Sequence: ");
				System.out.println(i.next());
			}
			
			System.out.println("User Sequence size: ");
			System.out.println(whackUserSequence.size());
		}, false);
	}
	
	/**
	 * set NULL Key Bindings to whackAMole view to reset the keyBindings.
	 */
	public void setBindingsToWhackAMoleNULL() {
		addKeyBinding(whackViewTut, KeyEvent.VK_RIGHT, "go right", (evt) -> {
		}, false);
		
		addKeyBinding(whackViewTut, KeyEvent.VK_LEFT, "go left", (evt) -> {
		}, false);
		
		addKeyBinding(whackViewTut, KeyEvent.VK_UP, "go up", (evt) -> {
		}, false);
		
		addKeyBinding(whackViewTut, KeyEvent.VK_DOWN, "go down", (evt) -> {
		}, false);
	}
	
	/**
	 * set Key Bindings to Migration View
	 */
	public void setBindingsToMigration() {
		addKeyBinding(migrationView, KeyEvent.VK_RIGHT, "go right", (evt) -> {
			System.out.println("right pressed");
			migrationModel.getNorthernHarrier().setFlyState(FlyState.RIGHT);
		}, false);
		
		addKeyBinding(migrationView, KeyEvent.VK_RIGHT, "go right release", (evt) -> {
			System.out.println("right released");
			migrationModel.getNorthernHarrier().setFlyState(FlyState.STILL);
		}, true);
		
		addKeyBinding(migrationView, KeyEvent.VK_LEFT, "go left", (evt) -> {
			migrationModel.getNorthernHarrier().setFlyState(FlyState.LEFT);
			
		}, false);
		
		addKeyBinding(migrationView, KeyEvent.VK_LEFT, "go left release", (evt) -> {
			migrationModel.getNorthernHarrier().setFlyState(FlyState.STILL);
			
		}, true);
	}
	
	/**
	 * set Key Bindings to Side Swiper View.
	 */
	public void setBindingsToSideSwiper() {
		addKeyBinding(sideSwipeView, KeyEvent.VK_UP, "go up", (evt) -> {
			sideSwiperModel.getOsprey().setFlyState(FlyState.UP);
		}, false);
		
		addKeyBinding(sideSwipeView, KeyEvent.VK_UP, "go up release", (evt) -> {
			sideSwiperModel.getOsprey().setFlyState(FlyState.STILL);
		}, true);
		
		addKeyBinding(sideSwipeView, KeyEvent.VK_DOWN, "go down", (evt) -> {
			sideSwiperModel.getOsprey().setFlyState(FlyState.DOWN);
		}, false);
		
		addKeyBinding(sideSwipeView, KeyEvent.VK_DOWN, "go down release", (evt) -> {
			sideSwiperModel.getOsprey().setFlyState(FlyState.STILL);
		}, true);
	}
	
	/**
	 * serializeGame()
	 * Automatically serializes the game by writing to text files based on the model that is currently being played.
	 * Set up such that it creates three text files, one for SideSwiperModel, MigrationModel, and WhackAMoleModel
	 * @param m, the model to serialize
	 * @param currentGame, the text for the name of the file to serialize (based on the game model)
	 * @throws IOException
	 */
	public static void serializeGame(Model m, String currentGame) throws IOException {
		try {
			FileOutputStream output = new FileOutputStream(currentGame);
			ObjectOutputStream outputStream = new ObjectOutputStream(output);
			outputStream.writeObject(m);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SideSwiperModel getSideSwiperModel() {
		return sideSwiperModel;
	}
	
	public SideSwiperView getSideSwipeView() {
		return sideSwipeView;
	}

	public MigrationModel getMigrationModel() {
		return migrationModel;
	}

	public MigrationView getMigrationView() {
		return migrationView;
	}
	
	public GameState getState() {
		return state;
	}
}
