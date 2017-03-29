import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

/*This class is the main class for the video player, it shows the GUI of the video.
 * 
 */
public class Media extends JFrame{

	//The video file name
	public static String videoFileName;
	//The absolute path of the video.
	public String videoFilePath;

	static EmbeddedMediaPlayerComponent mediaPlayerComponent;
	final JSlider volumeSlider = new JSlider();


	//The boolean for seeing whether the video is playing, this field is used for the videoSlider
	private boolean videoActive = false;

	//Button fields for the bottom part
	JToggleButton forwardButton = new JToggleButton();
	JToggleButton rewindButton = new JToggleButton();
	JButton stopButton = new JButton();
	final JToggleButton playPauseButton = new JToggleButton();
	final JButton volumeButton = new JButton();

	//Button fields for the top part
	JButton addFileButton = new JButton("Add Audio");
	JButton openButton = new JButton("Open Video");
	JButton addCommentButton = new JButton("Add Commentary");

	//The path and name to the audio file
	public File audioFile;



	public static void main(final String[] args) {
		//finds the VLC files to use in the vidivox application	
		new NativeDiscovery().discover();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Media(args);
			}
		});
	}


	//This method is used for choosing an audio file.
	public File OpenFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("mp3 & wav Images", "wav", "mp3"));

		if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	//Constructor 
	public Media(String[] args) {
		super("Vidivox Media Player");
	

		//Create the area where the video will be played  
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayerComponent.setLocation(0, 44);
		mediaPlayerComponent.setSize(812, 600); 
		getContentPane().add(mediaPlayerComponent,BorderLayout.CENTER);

		//==============================START OF BUTTONS PANE================================================
		JPanel buttonsPane = new JPanel();

		buttonsPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		//Adding the buttons
		buttonsPane.add(stopButton);
		buttonsPane.add(rewindButton);
		buttonsPane.add(playPauseButton);
		buttonsPane.add(forwardButton);
		buttonsPane.add(volumeButton);
		buttonsPane.add(volumeSlider);

		//Mute and volume Button	     
		volumeButton.setIcon(new ImageIcon(Media.class.getResource("Images/volume.png")));

		//This action listener combines the mute and unmute buttons in one. So when the video is muted it will show a mute button vice versa
		volumeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(mediaPlayerComponent.getMediaPlayer().isMute() == false) {
					volumeButton.setIcon(new ImageIcon(Media.class.getResource("Images/mute.png")));
					mediaPlayerComponent.getMediaPlayer().mute(true);	
				} 
				else if (mediaPlayerComponent.getMediaPlayer().isMute() == true) {
					volumeButton.setIcon(new ImageIcon(Media.class.getResource("Images/volume.png")));
					mediaPlayerComponent.getMediaPlayer().mute(false);		
				}	     		
			}
		});

		//The class for making a rewind background task so that when user presses the rewind once it will continue rewinding the video
		class Rewind extends SwingWorker {
			private JToggleButton tBtn;

			protected Rewind (JToggleButton tBtn) {
				this.tBtn = tBtn;
			}
			@Override
			protected Object doInBackground() throws Exception {
				while (tBtn.isSelected()) {
					mediaPlayerComponent.getMediaPlayer().skip(-100);
					Thread.sleep(10);
				}
				return null;
			}	
		}

		//Rewind button, using the background task to rewind
		rewindButton.setIcon(new ImageIcon(Media.class.getResource("Images/rewind.png")));
		rewindButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JToggleButton tBtn = (JToggleButton)e.getSource();
				Rewind bGT = new Rewind(tBtn);
				if (tBtn.isSelected()) {		
					mediaPlayerComponent.getMediaPlayer().pause();
					bGT.execute();
					mediaPlayerComponent.getMediaPlayer().mute(true); // Mute the sound because it sounds weird when rewinding
				} else {
					bGT.cancel(true);
					mediaPlayerComponent.getMediaPlayer().play();
					mediaPlayerComponent.getMediaPlayer().mute(false);
				}
			}
		});



		//Making the stop button where when pressed goes to the start of the video and stops video from playing.
		stopButton.setIcon(new ImageIcon(Media.class.getResource("Images/stop.png")));
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mediaPlayerComponent.getMediaPlayer().stop(); //stops the video 

				//Toggles the play button so that it will wait for user to press it again to play it
				if(!playPauseButton.isSelected()) { 
					
					playPauseButton.setIcon(new ImageIcon(Media.class.getResource("Images/play.png")));
					playPauseButton.setSelected(!playPauseButton.isSelected());
				}
			}
		});


		//Fast forward button
		forwardButton.setIcon(new ImageIcon(Media.class.getResource("Images/ff.png")));
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JToggleButton tBtn = (JToggleButton)e.getSource();
				if (tBtn.isSelected()) {
					mediaPlayerComponent.getMediaPlayer().setRate(5);
				} else {
					mediaPlayerComponent.getMediaPlayer().setRate(1);
				}
			}
		});

		//Play and pause button
		playPauseButton.setIcon(new ImageIcon(Media.class.getResource("Images/pause.png")));
		playPauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JToggleButton tBtn = (JToggleButton)e.getSource();
				//Changes images depending on whether the video is playing or not.
				if (tBtn.isSelected()) {
					playPauseButton.setIcon(new ImageIcon(Media.class.getResource("Images/play.png")));
					mediaPlayerComponent.getMediaPlayer().pause();
				} else {
					playPauseButton.setIcon(new ImageIcon(Media.class.getResource("Images/pause.png")));
					mediaPlayerComponent.getMediaPlayer().play();
				}
			}
		});


		//The volume slider, it changes the volume if this is moved.
		volumeSlider.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				if (mediaPlayerComponent.getMediaPlayer().isMute() == false) {
					mediaPlayerComponent.getMediaPlayer().setVolume(volumeSlider.getValue());
				}
			}

		});


		//This panel combines the buttomsPane and the video slider together
		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new GridLayout(1, 1, 0, 0));

		//Adding the 2 panes in a structured manor
		bottomPane.add(buttonsPane);

		//add this whole section to the bottom of the frame     
		getContentPane().add(bottomPane, BorderLayout.PAGE_END);

		//=================================END OF BUTTONS============================================

		//======================Top Bar=========================
		JPanel commentaryPane = new JPanel();
		commentaryPane.setBounds(807, 102, 197, 600);
		commentaryPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		commentaryPane.add(openButton);
		commentaryPane.add(addFileButton);
		commentaryPane.add(addCommentButton);

		//The add file button lets the user find an audio and merges it with the current video
		addFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//If a video is not playing show a message.
				if(videoActive == false) {
					JOptionPane.showMessageDialog(null, "Please open a video before adding audio");
				}else {
					//Open the file 
					audioFile = OpenFile();
					
					String audioFilePath = audioFile.getAbsolutePath();
					String audioFileName = audioFile.getName();
					
					String audioFileSimple = null;
					//Get the extension of the file so that it can be stored as an mp3
					int pos = audioFileName.lastIndexOf(".");
					if (pos > 0) {
						audioFileSimple = audioFileName.substring(0, pos);
					}
					Audio addAudio = new Audio();
					addAudio.addAud(audioFilePath, audioFileName, audioFileSimple);
				}
			}
		});

		
		//open the file for the video
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("mp4 & avi Images", "mp4", "avi"));
				//Make sure that it is a mp4 or avi
				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					//Get some values for the fields so that we can access them globally
					videoFilePath = chooser.getSelectedFile().getAbsolutePath();
					videoFileName = chooser.getSelectedFile().getName();
					mediaPlayerComponent.getMediaPlayer().playMedia(videoFilePath);
					
					//Show that the video is playing
					videoActive = true;

					//Duplicated the video to the temporary folder
					ProcessBuilder duplicateVideo = new ProcessBuilder("/bin/bash","-c", "cp " + videoFilePath + " $HOME/vidivoxtemp");
					try {
						duplicateVideo.start();
					} catch (IOException e1) {
						System.out.println("Command was typed wrong");
						e1.printStackTrace();
					}

				}
			}
		});

		//Add the button for adding commentary
		addCommentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(videoActive == false) {
					JOptionPane.showMessageDialog(null,"Please open a video before adding commentary");
				} else {
					Commentary commentaryFrame = new Commentary();
					commentaryFrame.setVisible(true);
				}
			} 
		});
		


		getContentPane().add(commentaryPane,BorderLayout.PAGE_START); 

		//======================================END OF COMMENTARY STUFF=========================================


		setLocation(100, 100);
		setSize(1020, 740);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);



	}

}