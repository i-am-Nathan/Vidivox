import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;


//The Commentary class implements the functionality for various text-to-speech commands
public class Commentary extends JFrame{
	public static boolean isPlaying = false;
	private JLabel instructionLabel;
	private JTextArea inputText;

	//The main commentary JFrame which allows saving/testing text-to-speech.
	public Commentary() {
		super("Add Commentary");
		setLocation(1050,100);
		setSize(700, 300);
		JPanel buttonsPane = new JPanel();

		instructionLabel = new JLabel("Write text for your commentary, maximum of 40 words");
		getContentPane().add(instructionLabel, BorderLayout.PAGE_START);

		inputText = new JTextArea();
		inputText.setLineWrap(true);;
		inputText.setWrapStyleWord(true);
		getContentPane().add(inputText, BorderLayout.CENTER);


		//This button automatically merges the text in the box (converted to an mp3) with the current video
		JButton addTextButton = new JButton("Merge Commentary");
		addTextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String line = inputText.getText();
				int wordCount = wordCount(line);
				//deny use if >40 words used
				if(wordCount > 40) {
					JOptionPane.showMessageDialog(null, "Sorry Your word count was " + wordCount + " which exceeded 40, please reduce it.");
				} else {
					Audio merge = new Audio();
					merge.mergeCom(line, "temp.mp3");
					JOptionPane.showMessageDialog(null, "The file Merged_"+Media.videoFileName+" was saved in /Home/vidivoxtemp.");
				}
			}

		});
		
		//This button saves the commentary individually as an mp3 file in the specified location.
		JButton saveCommentary = new JButton("Save Commentary");
		saveCommentary.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String line = inputText.getText();
				int wordCount = wordCount(line);
				if(wordCount > 40) {
					JOptionPane.showMessageDialog(null, "Sorry Your word count was " + wordCount + " which exceeded 40, please reduce it.");
				} else {
					
					JFileChooser locationChooser = new JFileChooser();
					locationChooser.setFileFilter(new FileNameExtensionFilter("mp3 Images", "mp3"));

					if(locationChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

						String address = locationChooser.getSelectedFile().getParent();
						String fileName = locationChooser.getSelectedFile().getName();
						if (!fileName.endsWith(".mp3")) {
							fileName = fileName + ".mp3";
						}

						Audio save = new Audio();				
						save.saveCom(line, fileName, address);
					}
				}

			}

		}); 

		//this button previews the text in the box when played with festival.
		JButton testButton = new JButton("Test Commentary");
		testButton.setBounds(19, 70, 63, 29);
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String comLine = inputText.getText();
				int wordCount = wordCount(comLine);
				if(wordCount > 40) {
					JOptionPane.showMessageDialog(null, "Sorry Your word count was " + wordCount + " which exceeded 40, please reduce it.");
				}
				else if(isPlaying == false) {
					isPlaying = true;
					Festival playText = new Festival();
					playText.festival(comLine);

				}
			}
		});

		buttonsPane.add(testButton);
		buttonsPane.add(addTextButton);
		buttonsPane.add(saveCommentary);

		getContentPane().add(buttonsPane, BorderLayout.PAGE_END);
	}

	//Counts the number of text in a file
	protected int wordCount(String text) {
		return text.split("\\s+").length;
	}


}
