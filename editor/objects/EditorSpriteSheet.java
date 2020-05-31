package editor.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import framework.GameObject;
import framework.KeyState;
import framework.Level;
import framework.Utils;
import framework.imageFilters.BasicVariance;
import framework.imageFilters.BlurEdges;
import framework.imageFilters.DarkenFrom;
import framework.imageFilters.ImageFilter;
import framework.imageFilters.LightenFrom;
import framework.imageFilters.Outline;
import framework.imageFilters.ShadeDir;


public class EditorSpriteSheet extends GameObject{
	/** Level that this SpriteSheet belongs to */
	private Level level;
	private List<EditorAnimation> animations;
	private String name;
	private double x;
	private double y;
	private int curAnim = 0;
	
	public EditorSpriteSheet(String name, Level level)
	{
		this.name = name;
		this.level = level;
		this.animations = new ArrayList<EditorAnimation>();
	}
	
	public EditorSpriteSheet(Scanner in, Level level)
	{
		this.animations = new ArrayList<EditorAnimation>();
		this.level = level;
		x = 100;
		y = 100;
		
		in.useDelimiter(Pattern.compile("(\\n)")); // Regex. IDK
		
		// Temp objects
		EditorAnimation newAnim;
		EditorFrame newFrame;
		EditorJoint newJoint;
		List<EditorFrame> frames;
		List<EditorJoint> joints;
		
		while(in.hasNext())
		{
			String line = in.next();
			if (line.strip() != "")
			{
				String[] split = line.split(":");
				String token = split[0];
				String value = split[1];
				
				switch (token)
				{
					case "SPRITE":
						this.name = value;
						break;
					case "ANIM":
						newAnim = new EditorAnimation(this, value);
						animations.add(newAnim);
						break;
					case "FRAME":
						newFrame = new EditorFrame(animations.get(animations.size() - 1), value);
						frames = animations.get(animations.size() - 1).getFrames();
						frames.add(newFrame);
						if (frames.size() > 1)
						{
							frames.get(frames.size() - 2).setEditorChild(newFrame);
						}
						break;
					case "TIME":
						frames = animations.get(animations.size() - 1).getFrames();
						frames.get(frames.size() - 1).setFrameTime(Integer.parseInt(value));
						break;
					case "JOINT":
						frames = animations.get(animations.size() - 1).getFrames();
						newJoint = new EditorJoint(frames.get(frames.size() - 1), value);
						frames.get(frames.size() - 1).addJoint(newJoint);
						break;
					case "POSITION":
						String[] loc = value.split(";");
						frames = animations.get(animations.size() - 1).getFrames();
						joints = frames.get(frames.size() - 1).getJoints();
						joints.get(joints.size() - 1).setX(Double.parseDouble(loc[0]));
						joints.get(joints.size() - 1).setY(Double.parseDouble(loc[1]));
						break;
					case "ORDER":
						frames = animations.get(animations.size() - 1).getFrames();
						joints = frames.get(frames.size() - 1).getJoints();
						joints.get(joints.size() - 1).setRenderOrder(Integer.parseInt(value));
						break;
					case "POINTS":
						String[] points = value.split(";");
						for (int i = 0; i < points.length; i++)
						{
							String[] coords = points[i].split(",");
							frames = animations.get(animations.size() - 1).getFrames();
							joints = frames.get(frames.size() - 1).getJoints();
							joints.get(joints.size() - 1).addPoint(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
						}
						break;
					case "COLOR":
						Color newColor = Utils.stringToColor(value, ";");
						frames = animations.get(animations.size() - 1).getFrames();
						joints = frames.get(frames.size() - 1).getJoints();
						joints.get(joints.size() - 1).setColor(newColor);
						break;
					case "FILTERS":
						String[] filters = value.split(";");
						frames = animations.get(animations.size() - 1).getFrames();
						joints = frames.get(frames.size() - 1).getJoints();
						EditorJoint curItem = joints.get(joints.size() - 1);
						for (int i = 0; i < filters.length; i++)
						{
							String filter = filters[i];
							String[] filterParts = filter.split(ImageFilter.delimiter);
							switch (filterParts[0])
							{
								case BasicVariance.fileTitle:
									BasicVariance basicVar = new BasicVariance(Integer.parseInt(filterParts[1]));
									curItem.addFilter(basicVar);
									break;
								case BlurEdges.fileTitle:
									curItem.addFilter(new BlurEdges());
									break;
								case DarkenFrom.fileTitle:
									ShadeDir dir = ShadeDir.createFromString(filterParts[1]);
									DarkenFrom dark = new DarkenFrom(dir, Double.parseDouble(filterParts[2]));
									curItem.addFilter(dark);
									break;
								case LightenFrom.fileTitle:
									ShadeDir alsoDir = ShadeDir.createFromString(filterParts[1]);
									LightenFrom light = new LightenFrom(alsoDir, Double.parseDouble(filterParts[2]));
									curItem.addFilter(light);
									break;
								case Outline.fileTitle:
									Color col = Utils.stringToColor(filterParts[1], ",");
									curItem.addFilter(new Outline(col));
									break;
							}
						}
						break;
				}
			}
		}
		
		for(int i = 0; i < animations.size(); i++)
		{
			animations.get(i).generateImages();
		}
	}
	
	public void saveToFile()
	{
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream("Test/" + name + ".txt"));
			out.write("SPRITE:" + name);
			out.write("\n");
			for(int i = 0; i < animations.size(); i++)
			{
				animations.get(i).saveToFile(out);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getName()
	{
		return name;
	}
	public Level getLevel()
	{
		return level;
	}
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public EditorAnimation getAnimation(int anim)
	{
		return animations.get(anim);
	}
	
	public List<EditorAnimation> getAnimations()
	{
		return animations;
	}
	public EditorAnimation getAnimByName(String name)
	{
		for (int i = 0; i < animations.size(); i++)
		{
			if (animations.get(i).getName().equals(name))
			{
				return animations.get(i);
			}
		}
		return null; // No object found
	}
	/** 
	 * Get an array of the animation names for use in the editor (requires data in array rather than list)
	 * @return String[] containing all Animation names
	 */
	public String[] getAnimNames()
	{
		String[] names = new String[animations.size()];
		for (int i = 0; i < animations.size(); i++)
		{
			names[i] = animations.get(i).getName();
		}
		return names;
	}
	
	public void addNewAnimation(String animName)
	{
		animations.add(new EditorAnimation(this, animName));
	}
	
	@Override
	public void render(Graphics2D g2d, boolean debug)
	{
		this.animations.get(curAnim).render(g2d, debug);
	}
	
	@Override
	public void update(double dt, KeyState keys)
	{
		this.animations.get(curAnim).update(dt, keys);
	}

	@Override
	public void rescale() {
		// TODO Auto-generated method stub
		
	}
}
