package editor.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import framework.Image;
import framework.Level;
import framework.Utils;
import framework.imageFilters.BasicVariance;
import framework.imageFilters.BlurEdges;
import framework.imageFilters.DarkenFrom;
import framework.imageFilters.ImageFilter;
import framework.imageFilters.LightenFrom;
import framework.imageFilters.Outline;
import framework.imageFilters.ShadeDir;

/**
 * Contains information for both game and editor information
 */
public class EditorJoint {
	/** Name of this joint */
	private String name;
	/** Frame that this belongs to */
	private EditorFrame frame;
	/** Level that this Joint belongs to */
	private Level level;
	/** X offset from SpriteSheet position */
	private double xOffset;
	/** Y offset from SpriteSheet position */
	private double yOffset;
	/** Locations for points of shape */
	private List<Point2D> points;
	private Path2D path;
	/** The {@link framework.Image Image} for this joint */
	private Image img;
	/** Image filters for the image */
	private List<ImageFilter> filters;
	/** Base {@link java.awt.Color Color} of the image */
	private Color color;
	/** Faded color for editor to indicated that this object is not selected */
	private Color fadedColor;
	/** Order in which joints are rendered, highest first */
	private int renderOrder; 
	private EditorJoint parent;
	private List<EditorJoint> children;
	
	public EditorJoint(EditorFrame frame, String name)
	{
		this.frame = frame;
		this.level = frame.getLevel();
		this.name = name;
		this.points = new ArrayList<Point2D>();
		this.filters = new ArrayList<ImageFilter>();
		this.xOffset = 0;
		this.yOffset = 0;
		this.setColor(Color.BLACK);
		this.children = new ArrayList<EditorJoint>();
	}
	
	public EditorJoint copy()
	{
		EditorJoint newJoint = new EditorJoint(this.frame, this.name);
		newJoint.setColor(new Color(this.color.getRGB()));
		newJoint.setPath(new Path2D.Double());
		for (int i = 0; i < this.points.size(); i++)
		{
			newJoint.addPoint(this.points.get(i).getX(), this.points.get(i).getY());
		}
		
		return newJoint;
	}
	
	public void saveToFile(PrintWriter out)
	{
		out.write("JOINT:" + name + "\n");
		out.write("POSITION:" + xOffset + ";" + yOffset + "\n");
		out.write("ORDER" + renderOrder + "\n");
		out.write("POINTS:");
		for (int i = 0; i < points.size(); i++)
		{
			out.write(points.get(i).getX() + "," + points.get(i).getY());
			if (i < points.size() - 1)
			{
				out.write(";");
			}
		}
		out.write("\n");
		out.write("COLOR:");
		out.write(Utils.colorToString(color, ";"));
		out.write("\n");
		if (filters.size() > 0)
		{
			out.write("FILTERS:");
			for (int i = 0; i < filters.size(); i++)
			{
				out.write(filters.get(i).toString());
				if (i < filters.size() - 1)
				{
					out.write(";");
				}
			}
		}
		out.write("\n");
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	public Level getLevel()
	{
		return level;
	}
	public String getFileString()
	{
		return "";
	}
	
	/**
	 * Get the X offset from the SpriteSheet position 
	 * @return Distance from the SpriteSheet that this joint belongs to
	 */
	public double getX() {
		return xOffset;
	}
	public void setX(double x) {
		this.xOffset = x;
	}
	/**
	 * Get the Y offset from the SpriteSheet position 
	 * @return Distance from the SpriteSheet that this joint belongs to
	 */
	public double getY() {
		return yOffset;
	}
	public void setY(double y) {
		this.yOffset = y;
	}
	public Image getImage() {
		return img;
	}
	public void setImg(Image img) {
		this.img = img;
	}
	public EditorFrame getFrame()
	{
		return frame;
	}
	public List<ImageFilter> getFilters()
	{
		return filters;
	}
	public void addFilter(ImageFilter filter)
	{
		filters.add(filter);
	}
	public void addNewFilter(String filterName)
	{
		if (filterName == "Basic Variance")
		{
			addFilter(new BasicVariance());
		}
		else if (filterName == "Blur Edges")
		{
			addFilter(new BlurEdges());
		}
		else if (filterName == "Darken From")
		{
			addFilter(new DarkenFrom(ShadeDir.TOP, 1));
		}
		else if (filterName == "Lighten From")
		{
			addFilter(new LightenFrom(ShadeDir.TOP, 1));
		}
		else if (filterName == "Outline")
		{
			addFilter(new Outline(Color.BLACK));
		}
		
		getFrame().addNewJointFilter(filterName, this.name);
	}
	public void swapFilters(int a, int b)
	{
		if (a >= 0 && b >= 0 && a < filters.size() && b < filters.size())
		{
			ImageFilter temp = filters.get(a);
			filters.set(a, filters.get(b));
			filters.set(b, temp);
		}
		
		getFrame().swapJointFilters(this.name, a, b);
	}
	
	public void deleteFilter(int i)
	{
		getFrame().deleteJointFilter(this.name, i);
		filters.remove(i);
	}
	
	public Point2D getPoint(int i)
	{
		return points.get(i);
	}
	public void setPoint(int point, Point2D newPos)
	{
		points.set(point, newPos); 
		Path2D newPath = new Path2D.Double();
		
		newPath.moveTo(points.get(0).getX(), points.get(0).getY());
		for (int i = 0; i < points.size(); i++)
		{
			newPath.lineTo(points.get(i).getX(), points.get(i).getY());
		}
		path = newPath;
	}
	public void addPoint(double x, double y)
	{
		points.add(new Point2D.Double(x, y));
		Path2D newPath = new Path2D.Double();
		
		newPath.moveTo(points.get(0).getX(), points.get(0).getY());
		for (int i = 1; i < points.size(); i++)
		{
			newPath.lineTo(points.get(i).getX(), points.get(i).getY());
		}
		path = newPath;
	}
	public List<Point2D> getPoints()
	{
		return points;
	}
	public void setPoints(List<Point2D> newPoints)
	{
		points = newPoints;
	}
	public void setPath(Path2D path)
	{
		this.path = path;
	}
	public Path2D getPath()
	{
		return path;
	}
	public void setColor(Color col)
	{
		color = col;
		Color faded = new Color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha() / 2);
		fadedColor = faded;
	}
	public Color getColor()
	{
		return color;
	}
	public Color getFadedColor()
	{
		return fadedColor;
	}
	
	public int getRenderOrder() {
		return renderOrder;
	}

	public void setRenderOrder(int renderOrder) {
		this.renderOrder = renderOrder;
	}

	public EditorJoint getParent() {
		return parent;
	}
	public void setParent(EditorJoint parent) {
		if (this.parent != null)
		{
			if (this.parent.children.contains(this))
			{
				this.parent.children.remove(this);
			}
		}
		this.parent = parent;
		if (this.parent != null)
		{
			this.parent.addChild(this);
		}
		
		frame.setParentToChildren(this, parent);
	}

	public List<EditorJoint> getChildren() {
		return children;
	}
	public void addChild(EditorJoint child){
		children.add(child);
	}

	public void makeImage()
	{
		double xPos = path.getBounds2D().getX();
		double yPos = path.getBounds2D().getY();
		Path2D imgPath = new Path2D.Double();
		imgPath.moveTo(points.get(0).getX(), points.get(0).getY());
		for (int i = 1; i < points.size(); i++)
		{
			imgPath.lineTo(points.get(i).getX(), points.get(i).getY());
		}
		AffineTransform transform = new AffineTransform();
		transform.translate(-xPos, -yPos); // Move to 0/0
		imgPath.transform(transform);
		img = new Image(imgPath, color, level);
		img.setX(xPos * 0.25);
		img.setY(yPos * 0.25);
		for (int i = 0; i < filters.size(); i++)
		{
			img.addFilter(filters.get(i));
		}
	}
	
	public void render(Graphics2D g2d, boolean debug)
	{
		double xPos = frame.getAnimation().getSpriteSheet().getX() + xOffset;
		double yPos = frame.getAnimation().getSpriteSheet().getY() + yOffset;
		img.setCenterX(xPos);
		img.setCenterY(yPos);
		img.render(g2d, debug);
	}
	
	public void moveJoint(double xTranslate, double yTranslate)
	{
		AffineTransform transform = new AffineTransform();
		transform.translate(xTranslate, yTranslate);
		setX(getX() + xTranslate);
		setY(getY() + yTranslate);
		if (getChildren().size() > 0)
		{
			for (int i = 0; i < getChildren().size(); i++)
			{
				getChildren().get(i).moveJoint(xTranslate, yTranslate);
			}
		}
	}
}
