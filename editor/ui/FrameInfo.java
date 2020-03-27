package editor.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FrameInfo {
	private Editor editor;
	private JPanel panel;
	private JLabel frameName;
	private JButton changeName;
	private JLabel frameCount;
	private JButton changeFrames;
	
	public FrameInfo(Editor edit)
	{
		editor = edit;
		panel = new JPanel();
		panel.setBounds(180, 680, 300, 50);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		frameName = new JLabel("Frame name");
		panel.add(frameName);
		changeName = new JButton("Change name");
		panel.add(changeName);
		frameCount = new JLabel("Frame time");
		panel.add(frameCount);
		changeFrames = new JButton("Change time");
		panel.add(changeFrames);
		
		panel.setLayout(new GridLayout(2, 2));
	}
	
	public void createEvents()
	{
		AnimationLists ani = editor.getAnimLists();
		changeName.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String frameName = (String)JOptionPane.showInputDialog(editor.getFrame(), "","Rename Frame",
	    				JOptionPane.PLAIN_MESSAGE, null, null, "Object");
	    		if (ani.getFrameList().getSelectedIndex() != -1)
	    		{
	    			int index = ani.getFrameList().getSelectedIndex();
	    			if (ani.getSelectedAnimation().getFrameByName(frameName) == null)
	    			{
	    				ani.getSelectedFrame().setName(frameName);
	    				ani.getFrameList().setListData(ani.getSelectedAnimation().getFrameNames());
	    				ani.getFrameList().setSelectedIndex(index);
	    			}
	    			else
	    			{
		    			JOptionPane.showMessageDialog(editor.getFrame(),
		    					"Animation with this name already exists", "Invalid input", JOptionPane.WARNING_MESSAGE);
	    			}
	    		}
	    	}
	    });
	}

	public JPanel getPanel()
	{
		return panel;
	}
}
