import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;


//This class provides functionality for various audio manipulation functions, all of which are run in bash.
public class Audio {
	ProcessBuilder builder;

	//The line will be the typed down line the user will enter
	public void saveCom(String line, String fileName, String address) {
		SaveCommentary save = new SaveCommentary(line, fileName, address);
		save.execute();
	}
	public void mergeCom(String line, String fileName) {
		MergeCommentary merge = new MergeCommentary(line, fileName);
		merge.execute();
	}
	public void addAud(String audioPath, String audioName, String audioSimple) {
		AddAudio aAud = new AddAudio(audioPath, audioName, audioSimple);
		aAud.execute();
	}

	
	/*==========================================================================
	 * FOR ALL NESTED CLASSES, EACH BASHCOMMAND IS SET NUMERICALLY (cmd1,cmd2...)
	 * AND THEN THEY ARE EXECUTED IN THAT ORDER.
	 * =========================================================================
	 */
	class SaveCommentary extends SwingWorker {
		private String line;
		private String fileName;
		private String address;

		protected SaveCommentary (String line, String fileName, String address) {
			this.line = line;
			this.fileName = fileName;
			this.address = address;
		}

		@Override
		protected Object doInBackground() throws Exception {
			String cmd1 = ("echo " + line + " | text2wave -o .temp.wav");
			String cmd2 = ("ffmpeg -i .temp.wav -f mp3 " + fileName);
			String cmd3 = ("rm .temp.wav");
			String cmd4 = ("mv $HOME/vidivoxtemp/" + fileName + " " + address);
			try {
				builder = new ProcessBuilder("/bin/bash","-c", cmd1);
				builder.start();
				/*threads need to sleep when making/copying files as the next command is executed immediately after,
				 * where this may cause errors when bash tries to convert a non-existant file.
				 */				
				Thread.sleep(1000);

				builder = new ProcessBuilder("/bin/bash","-c", cmd2);
				builder.start();
				Thread.sleep(1000);

				builder = new ProcessBuilder("/bin/bash","-c", cmd3);
				builder.start();
				Thread.sleep(1000);

				builder = new ProcessBuilder("/bin/bash","-c", cmd4);
				builder.start();

			} catch(IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	//Process for merging the file in the background
	class MergeCommentary extends SwingWorker {
		private String line;
		private String fileName;

		protected MergeCommentary (String line, String fileName) {
			this.line = line;
			this.fileName = fileName;
		}

		@Override
		protected Object doInBackground() throws Exception {
			String cmd1 = ("echo " + line + " | text2wave -o .temp.wav");
			String cmd2 = ("ffmpeg -i .temp.wav -f mp3 " + fileName);
			String cmd3 = ("rm .temp.wav");
			String cmd4 = ("ffmpeg -i "+ Media.videoFileName +" -i temp.mp3 -filter_complex amix=inputs=2 Merged_"+Media.videoFileName);
			String cmd5 = ("rm temp.mp3");

			try {
				builder = new ProcessBuilder("/bin/bash","-c", cmd1);
				builder.start();
				Thread.sleep(1000);

				builder = new ProcessBuilder("/bin/bash","-c", cmd2);
				builder.start();
				Thread.sleep(1000);

				builder = new ProcessBuilder("/bin/bash","-c", cmd3);
				builder.start();
				Thread.sleep(100);

				builder = new ProcessBuilder("/bin/bash","-c", cmd4);
				builder.start();
				Thread.sleep(1000);

				builder = new ProcessBuilder("/bin/bash","-c", cmd5);
				builder.start();

			} catch(IOException e) {
				e.printStackTrace();
			}
			return null;
			
		}
		
	}

	//Background process for adding the audio to the video.
	class AddAudio extends SwingWorker {
		private String audioPath;
		private String audioName;
		private String audioSimple;

		protected AddAudio (String audioPath, String audioName, String audioSimple) {
			this.audioPath = audioPath;
			this.audioName = audioName;
			this.audioSimple = audioSimple;
		}

		@Override
		protected Object doInBackground() throws Exception {
			String cmd1 = ("cp "+audioPath+ " $HOME/vidivoxtemp");
			String cmd2 = ("ffmpeg -i "+Media.videoFileName+" -i "+audioName+" -filter_complex amix=inputs=2 Merged_"+audioSimple+"_with_"+Media.videoFileName);
			String cmd3 = ("rm "+audioName);


			try {
				builder = new ProcessBuilder("/bin/bash","-c", cmd1);
				builder.start();
				Thread.sleep(1000);

				builder = new ProcessBuilder("/bin/bash","-c", cmd2);
				builder.start();
				Thread.sleep(1000);

				builder = new ProcessBuilder("/bin/bash","-c", cmd3);
				builder.start();
				Thread.sleep(100);

				JOptionPane.showMessageDialog(null, "The file Merged_"+audioSimple+"_with_"+Media.videoFileName+" was saved in /Home/vidivoxtemp.");

			} catch(IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
