
import java.io.IOException;

import javax.swing.SwingWorker;

//The Festival class creates a festival that runs in the background
public class Festival {
	ProcessBuilder builder;

	//The line will be the sentence you want to say in tts.
	public void festival(String line) {
		BackgroundTask backTask = new BackgroundTask(line);
		backTask.execute();
	}

	class BackgroundTask extends SwingWorker {
		private String line;

		protected BackgroundTask(String line) {
			this.line = line;
		}

		@Override
		protected Object doInBackground() throws Exception {
			//Run the command and start it.
			String cmd = ("echo " + line + " | festival --tts");
			try {
				builder = new ProcessBuilder("/bin/bash","-c", cmd);
				builder.start();

			} catch(IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		//This makes sure that only 1 festival is playing at a time
		@Override
		protected void done() {
			Commentary.isPlaying = false;
		}
	}
}
