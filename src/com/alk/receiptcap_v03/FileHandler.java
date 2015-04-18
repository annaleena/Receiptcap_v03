package com.alk.receiptcap_v03;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.widget.EditText;

public class FileHandler {

	public boolean saveData(String recognizedText, EditText input)
			throws IOException {

		// write text data to internal memory file
		String filepath = "/storage/emulated/0/receiptcapture/Receiptfiles/";
		File myFile = new File(filepath + input.getText() + ".txt");
		File dir = new File(filepath);

		if (dir.mkdir()) {
			System.out.println("Directory created");
		} else {
			System.out.println("Directory is not created");
		}

		if (!myFile.exists()) {
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(recognizedText);
			myOutWriter.close();
			fOut.close();
			return true;
		} else {
			return false;
		}
	}
}
