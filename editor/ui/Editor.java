package editor.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import editor.objects.Options;
import editor.objects.SpriteSheet;
import framework.Level;
import framework.LevelManager;

public class Editor {
	private SpriteSheet activeSprite;
	private LevelManager manager;
	private int timeStep = 10;
	private boolean playing = false;
	private boolean shouldReRender = false;
	private Level demoLevel;
	private Options options;
	
	private JFrame frame;
	private DrawingArea drawing;
	private RenderArea render;
	private FilePanel file;
	private AnimationLists animLists;
	private EditOptions editOptions;
	private RenderControls renderControls;
	private ObjectInfo objectInfo;
	private FilterEdit filters;
	private AnimTimer animTimer;

	public void run() {
		activeSprite = new SpriteSheet();
		manager = new LevelManager();
		demoLevel = new Level(manager, 0);
		options = new Options();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		frame = new JFrame("Editor");
		
		drawing = new DrawingArea(this);
		render = new RenderArea(this);
		file = new FilePanel(this);
		animLists = new AnimationLists(this);
		editOptions = new EditOptions(this);
		renderControls = new RenderControls(this);
		objectInfo = new ObjectInfo(this);
		filters = new FilterEdit(this);
		animTimer = new AnimTimer(this);
		
		// Events
	    file.createEvents();
	    animLists.createEvents();
	    editOptions.createEvents();
	    drawing.createEvents();
	    renderControls.createEvents();
	    objectInfo.createEvents();
		
	    frame.addWindowListener(new WindowAdapter(){
	    	public void windowClosing(WindowEvent e){
	    		animTimer.getTimer().stop();
	    		System.exit(0);
	    	}
	    });
	    
		frame.add(drawing.getPanel());
		frame.add(file.getPanel());
		frame.add(animLists.getAnimPanel());
		frame.add(animLists.getFramePanel());
		frame.add(animLists.getObjectPanel());
		frame.add(objectInfo.getPanel());
		frame.add(editOptions.getPanel());
		frame.add(renderControls.getPanel());
		frame.add(render.getPanel());
		frame.add(filters.getPanel());
		
		frame.setSize(1500,800);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null); 
		frame.setVisible(true);
	}

	public SpriteSheet getActiveSprite() {
		return activeSprite;
	}
	public LevelManager getManager() {
		return manager;
	}
	public int getTimeStep() {
		return timeStep;
	}
	public boolean getPlaying() {
		return playing;
	}
	public boolean getShouldReRender() {
		return shouldReRender;
	}
	public Level getDemoLevel() {
		return demoLevel;
	}
	public Options getOptions() {
		return options;
	}
	public JFrame getFrame() {
		return frame;
	}
	public DrawingArea getDrawing() {
		return drawing;
	}
	public RenderArea getRender() {
		return render;
	}
	public FilePanel getFile() {
		return file;
	}
	public AnimationLists getAnimLists() {
		return animLists;
	}
	public EditOptions getEditOptions() {
		return editOptions;
	}
	public RenderControls getRenderControls() {
		return renderControls;
	}
	public ObjectInfo getObjectInfo() {
		return objectInfo;
	}
	public FilterEdit getFilters() {
		return filters;
	}

	public void setShouldReRender(Boolean render)
	{
		shouldReRender = render;
	}
	public void setActiveSprite(SpriteSheet sprite)
	{
		activeSprite = sprite;
	}
	public void setPlaying(Boolean play)
	{
		playing = play;
	}
}
