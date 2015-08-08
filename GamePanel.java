import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GamePanel extends JPanel 
{
	//Define constants
	private Play p;
	private final double G = 9.8;
	private final double COR = 0.8;
	private final int defaultHeight = 10;
	private final double minValue = 0.0;
	private final double maxValue = 100.0;
	private final double step = 0.1;
	private final int SIZE = 50; 

	//Define variables
	private JButton cmdStart, cmdRestart;
	private String[] backgroundStrings = {"Earth", "Moon", "Mars", "Saturn"}; 
	private JLabel gravityLabel, elasticityLabel, heightLabel, backgroundLabel; 
	private JComboBox backgroundComboBox, cb;
	private JPanel keysUp, keysDown;
	private SpinnerNumberModel modelG, modelE, modelH;
	private JSpinner gravitationSpinner, elasticitySpinner, heightSpinner;
	private String picture;


	public GamePanel()
	{
		p = new Play();
		//Creating Buttons and their listeners 
		//*****************************************************
		cmdStart = new JButton("Start");
		cmdRestart = new JButton("Restart");

		cmdStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.start();
			}
		});
		cmdRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.restart();
			}
		});

		//Creating combo box and Spinners 
		//******************************************************
		backgroundComboBox = new JComboBox(backgroundStrings);
		modelG = new SpinnerNumberModel(G, minValue, maxValue, step);
		modelE = new SpinnerNumberModel(COR, minValue, maxValue, step);
		modelH = new SpinnerNumberModel(defaultHeight, (int)minValue, (int)maxValue, 1);
		gravitationSpinner = new JSpinner(modelG);
		elasticitySpinner = new JSpinner(modelE);
		heightSpinner = new JSpinner(modelH);

		
		//Adding listeners to combo box and Spinners
		//******************************************************
		backgroundComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cb = (JComboBox)e.getSource();
				picture = (String)cb.getSelectedItem();
				p.changeBackground();
			}
		});

		gravitationSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				p.gravitation = (double) modelG.getValue();
				JOptionPane.showMessageDialog(null,"Gravitation changed to "+ p.gravitation);
			}
		});
		
		elasticitySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				p.elasticity = (double) modelE.getValue();
				JOptionPane.showMessageDialog(null,"Elasticity changed to "+ p.elasticity);
			}
		});

		heightSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				p.height = (int)modelH.getValue();
				JOptionPane.showMessageDialog(null,"Height changed to " + p.height);
			}
		});
		
		//Creating labels
		//******************************************************
		backgroundLabel = new JLabel("Background");
		gravityLabel = new JLabel("Gravitation");
		elasticityLabel = new JLabel("Elasticity");
		heightLabel = new JLabel("Height");

		//Set up key panels and Layout 
		//******************************************************
		keysUp = new JPanel();
		keysDown = new JPanel();

		keysUp.add(cmdStart);
		keysUp.add(cmdRestart);

		//line 1 -labels
		keysDown.setLayout(new GridLayout(2, 4));
		keysDown.add(backgroundLabel);
		backgroundLabel.setHorizontalAlignment(JLabel.CENTER);
		keysDown.add(gravityLabel);
		gravityLabel.setHorizontalAlignment(JLabel.CENTER);
		keysDown.add(elasticityLabel);
		elasticityLabel.setHorizontalAlignment(JLabel.CENTER);
		keysDown.add(heightLabel);
		heightLabel.setHorizontalAlignment(JLabel.CENTER);

		//line2 - spinners
		keysDown.add(backgroundComboBox);
		keysDown.add(gravitationSpinner);
		keysDown.add(elasticitySpinner);
		keysDown.add(heightSpinner);

		setLayout(new BorderLayout());
		add(keysUp, BorderLayout.NORTH);
		add(keysDown, BorderLayout.SOUTH);
		add(p, BorderLayout.CENTER);
		//**************************************************



	}

	//Class that playing the game 
	private class Play extends JPanel implements ActionListener
	{
		private String place;
		private String backgroundPicture;
		private double gravitation, elasticity, velocity, time;
		private int x, y, height;
		private Timer timer;
		private boolean firstRun = true;

		public Play()
		{
			timer = new Timer(10, this);
			backgroundPicture = "earth.jpg"; 
			height = defaultHeight;
			gravitation = G;
			elasticity = COR;
			place = "C:\\Users\\olesya\\workspace\\Ball\\src\\";
		}
		public void start() 
		{ 
			time = 0;
			timer.start();
		}
		public void restart() //Back to default 
		{
			timer.stop();
    		backgroundPicture = "earth.jpg"; 
    		height = defaultHeight;
    		gravitation = G;
    		elasticity = COR;
    		backgroundComboBox.setSelectedItem("Earth");
    		gravitationSpinner.setValue(G);
    		elasticitySpinner.setValue(COR);
    		heightSpinner.setValue(defaultHeight);
    		
    		
			x = (this.getWidth()-SIZE)/2; //middle of the screen
			y = 0;
			velocity = 0;
			time = 0; 
			repaint(); 
		}

		public void changeBackground()
		{
			backgroundPicture = picture + ".jpg";
			repaint(); 

		}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			int delta;
			time += 0.1;
			velocity += gravitation; 
			int beforeY = y;
			y = beforeY + (int)(velocity*time + 0.5*(gravitation*time*time));
			
			delta = Math.abs(y-beforeY);
			if(velocity <= 0)
			{
				gravitation = gravitation*-1;
			}
			if(y > this.getHeight() - SIZE)
			{
				y = this.getHeight() - SIZE;
				gravitation = gravitation*-1;
				velocity = velocity*elasticity;
			}
			if (delta < 1 )
				timer.stop();
	
			repaint(); 

		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
	
			//If running first time, define x,y at the top in the middle
			if(firstRun)
			{
				x = (getWidth()-SIZE)/2; //middle of the screen
				y = 0;
				firstRun = false;
			}
			
			Color c = new Color(73, 245, 0, 240); //Green
			g2d.setColor(c);
			Image img = Toolkit.getDefaultToolkit().getImage(place + backgroundPicture);
			g2d.drawImage(img, 0, 0, this);	
			
			g2d.fillOval(x, y, SIZE, SIZE);
			
		}
	}

}
