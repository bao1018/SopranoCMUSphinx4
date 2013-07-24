package com.pwc.labs.soprano.transcript;

import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.ConfidenceResult;
import edu.cmu.sphinx.result.ConfidenceScorer;
import edu.cmu.sphinx.result.Path;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * A simple example that shows how to transcribe a continuous audio file that
 * has multiple utterances in it.
 */
public class Transcriber {
	private static DecimalFormat format = new DecimalFormat("#.#####");
	public static void main(String[] args) throws IOException,
			UnsupportedAudioFileException {
		URL audioURL;
		String strResultTextFilePath = null;
		if (args.length > 0) {
			audioURL = new File(args[0]).toURI().toURL();

		} else {
			audioURL = Transcriber.class.getResource("10001-90210-01803.wav");
		}

		if (args.length > 1)
			strResultTextFilePath = args[1];

		URL configURL = Transcriber.class.getResource("config.xml");

		ConfigurationManager cm = new ConfigurationManager(configURL);
		Recognizer recognizer = (Recognizer) cm.lookup("recognizer");

		/* allocate the resource necessary for the recognizer */
		recognizer.allocate();

		// configure the audio input for the recognizer
		AudioFileDataSource dataSource = (AudioFileDataSource) cm
				.lookup("audioFileDataSource");
		dataSource.setAudioFile(audioURL, null);

		// Loop until last utterance in the audio file has been decoded, in
		// which case the recognizer will return null.
		Result result;

		StringBuffer sbSpeech = new StringBuffer();

		long beforeRun = System.currentTimeMillis();
		while ((result = recognizer.recognize()) != null) {
			 ConfidenceScorer cs = (ConfidenceScorer) cm.lookup("confidenceScorer");
			 ConfidenceResult cr = cs.score(result);
             Path best = cr.getBestHypothesis();
             System.out.println(best.getTranscription());
             System.out.println
             ("     (confidence: " +
                     format.format(best.getLogMath().logToLinear
                             ((float) best.getConfidence()))
                     + ')');
//			String resultText = result.getBestResultNoFiller();
//			sbSpeech.append(resultText + ". ");
		}
		double timeDiff = (System.currentTimeMillis() - beforeRun) / 1000.0;
		if (strResultTextFilePath == null)
			System.out.println(sbSpeech.toString());
		else{
			PrintWriter pw = new PrintWriter(strResultTextFilePath, "UTF-8");
			pw.print(sbSpeech.toString());
			pw.flush();
			pw.close();
		}
			

		System.out.println("Takes " + timeDiff + " seconds");
	}
}