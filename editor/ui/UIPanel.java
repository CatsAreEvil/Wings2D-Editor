package editor.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public abstract class UIPanel {
	protected JPanel panel;
	protected Editor editor;
	protected List<UIElement> elements;
	
	public UIPanel(Editor edit)
	{
		this.editor = edit;
		panel = new JPanel();
		panel.setLayout(null);
		elements = new ArrayList<UIElement>();
	}
	
	public void resize(JPanel parent, double scale)
	{
		panel.setLocation(0, 0);
		panel.setSize(parent.getWidth(), parent.getHeight());
		for (int i = 0; i < elements.size(); i++)
		{
			elements.get(i).resize(scale);
		}
	}
	
	public void initElements()
	{
		for (int i = 0; i < elements.size(); i++)
		{
			elements.get(i).createEvents();
			panel.add(elements.get(i).getPanel());
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}
