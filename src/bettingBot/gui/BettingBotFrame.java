package bettingBot.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class BettingBotFrame extends JFrame{
	
	private JLabel fundsLabel;
	private JScrollPane runningBetsPane;
	private JTextArea runningBetsTextArea;
	private JScrollPane eventPane;
	private JTextArea eventTextArea;
	
	public BettingBotFrame(){
		setTitle("Betting GUI");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setSize(1000, 700);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setSize(906, 504);
		mainPanel.setLocation(20, 78);
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		mainPanel.setBackground(Color.BLACK);
		add(mainPanel);
		
		fundsLabel = new JLabel();
		add(fundsLabel);
		fundsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		fundsLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		fundsLabel.setLocation(20, 20);
		fundsLabel.setSize(200, 60);
		fundsLabel.setText("<html>Current Funds:<br></html>");
		
		runningBetsTextArea = new JTextArea();
		runningBetsTextArea.setEditable(false);
		runningBetsPane = new JScrollPane(runningBetsTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(runningBetsPane);
		runningBetsPane.setSize(450,  500);
		runningBetsPane.setLocation(2,  2);
		runningBetsPane.setBorder(null);
		
		eventTextArea = new JTextArea();
		eventTextArea.setEditable(false);
		eventTextArea.setLineWrap(true);
		eventTextArea.setText("Events:");
		eventPane = new JScrollPane(eventTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(eventPane);
		eventPane.setSize(450,  500);
		eventPane.setLocation(runningBetsPane.getX() + runningBetsPane.getWidth() + 2,  2);
		eventPane.setBorder(null);
	}
	
	public void setFunds(double funds){
		fundsLabel.setText("<html>Current Funds:<br>" + funds + " GBP</html>");
	}
	
	public void addEvent(String event){
		eventTextArea.setText(eventTextArea.getText() + "\n" + event);
	}
	
	public static void main(String[] args) {
		BettingBotFrame frame = new BettingBotFrame();
		frame.setVisible(true);
	}
}
