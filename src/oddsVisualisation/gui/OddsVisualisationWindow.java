package oddsVisualisation.gui;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class OddsVisualisationWindow extends JFrame{

	private JList<String> list = new JList<String>();
	private JScrollPane scrollPane;
	DefaultListModel<String> model;
	
	public void setData(String[] data){
		model = new DefaultListModel<String>();
		for(int i = 0; i < data.length; i++){
			model.addElement(data[i]);
		}
		list.setModel(model);
		repaint();
	}
	
	public OddsVisualisationWindow(){
		setSize(400, 700);
		scrollPane = new JScrollPane(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		add(scrollPane);
	}
}
