import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
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

import javafx.scene.image.ImageView;

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
	private final int SIZE = 40; 
	private final int STRING = 1, DOUBLE = 0;
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
		cmdRestart = new JButton("Reset");

		cmdStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.start();					
			}
		});
		cmdRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmdStart.setText("Start");
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
				p.gravitation = p.grav_backup = (double) modelG.getValue();
			}
		});

		elasticitySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				p.elasticity = (double) modelE.getValue();
			}
		});

		heightSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				p.set_y((int)modelH.getValue());
				p.repaint();
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
		private double v0 = 0;
		private double grav_backup;
		private int start_height;
		private double SCREEN;
		private int meter;
		public Play()
		{
			timer = new Timer(100, this);
			initializtion();
			place = "C:\\Users\\ItamarSharify\\workspace\\GravitationGame\\src\\";
		}
		public void set_y(int height)
		{
			y = this.getHeight()-height*meter;
		}
		private void initializtion()
		{
			height = defaultHeight;
			start_height = this.getHeight()-height*meter;
			backgroundPicture = "earth.jpg"; 
			gravitation = G;
			grav_backup = gravitation;
			elasticity = COR;
		}
		public void start() 
		{ 
			time = 0;
			timer.start();
		}
		public void restart() //Back to default 
		{
			timer.stop();
			timer = new Timer(100, this);
			initializtion();
			initSpinners("Earth", G, COR, defaultHeight);

			x = (this.getWidth()-SIZE)/2; //middle of the screen
			y = start_height;
			velocity = 0;
			v0 = 0;
			time = 0; 
			repaint(); 
		}
		private void initSpinners(String planet, double G, double COR, int defaultHeight)
		{
			backgroundComboBox.setSelectedItem(planet);
			gravitationSpinner.setValue(G);
			elasticitySpinner.setValue(COR);
			heightSpinner.setValue(defaultHeight);
			grav_backup = G;
		}
		public void changeBackground()
		{
			restart();
			backgroundPicture = picture + ".jpg";
			switch(picture)
			{
			case "Earth":
				initSpinners("Earth", 9.8, elasticity, defaultHeight);
				break;
			case "Moon":
				initSpinners("Moon", 1.6, elasticity, defaultHeight);
				break;
			case "Mars":
				initSpinners("Mars", 3.8, elasticity, defaultHeight);
				break;
			case "Saturn":
				initSpinners("Saturn", 5, elasticity, defaultHeight);
				break;
			}

			repaint(); 

		}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			//case the ball goes down
			if (gravitation>0)
			{
				y += (int)(v0*time + 0.5*(gravitation*time*time));//calc the next place
				if(y > SCREEN-SIZE)//the ball has hit the floor
				{
					y = this.getHeight() - SIZE;//reset the height - because not always it'll reach the floor perfectly 
					gravitation = -grav_backup;//reverse the gravitaion
					v0 = Math.abs((velocity-grav_backup)*elasticity);
					System.out.println("END OF SCREEN");
					time = 0;
					velocity = v0;
				}
			}
			//case the ball goes up
			else if(gravitation<0)
			{
				y -= (int)(v0*time + 0.5*(gravitation*time*time));//calc the next place
				if(velocity <=0)
				{
					gravitation = grav_backup;
					System.out.println("changed direction");
					time = 0;
					v0 = 0;
				}
			}
			System.out.println("Time: "+time+" Y: "+y+ ", V0: "+v0+" V: "+velocity+" A: "+gravitation);
			velocity += gravitation; 
			time += 1;
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
				SCREEN = this.getHeight();
				meter = (int) Math.round((SCREEN-(double)SIZE)/100);
				start_height = this.getHeight()-height*meter;
				y = start_height;
				firstRun = false;
			}

			Color c = new Color(73, 245, 0, 240); //Green
			g2d.setColor(c);
//			Image img = Toolkit.getDefaultToolkit().getImage(place + backgroundPicture); // the default method to open images
			Image img = new ImageIcon(this.getClass().getResource(backgroundPicture)).getImage();//relative opening
			g2d.drawImage(img, 0, 0, this);	

			g2d.fillOval(x, y, SIZE, SIZE);

		}
	}

}
