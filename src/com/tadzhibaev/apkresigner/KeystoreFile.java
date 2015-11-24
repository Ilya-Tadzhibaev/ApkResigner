package com.tadzhibaev.apkresigner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public final class KeystoreFile {

	private static boolean isKeystoreFileExists;
	private static boolean toOpenDialog;
	private static final String filePath = System.getProperty("user.dir") + File.separator + "keystore_path.txt";

	private static String KeystorePath;

	public static void isKeystoreExists() {

		KeystoreFile.setKeystoreFileExists((new File(filePath).exists() == true) ? true : false);

	}

	public static void ReadKeystorePath() {

		try {
			BufferedReader pathReader = new BufferedReader(new FileReader(filePath));
			String path = pathReader.readLine();
			if (path != null) {
				if (new File(path).exists()) {
					KeystoreFile.setToOpenDialog(false);

					if (!path.equalsIgnoreCase(KeystoreFile.getKeystorePath())) {
						KeystoreFile.setKeystorePath(path);
						LogUtils.newLine("Path to keystore is set to: " + path);
						LogUtils.newLine("If you want to modify a path, delete " + filePath);
						LogUtils.newLine("Or modify the path in that file");
					} else {
						LogUtils.newLine("Path is already set");
					}

				} else {
					LogUtils.newLine("Path is invalid. Choose another file");
					KeystoreFile.setToOpenDialog(true);
				}
			} else {
				LogUtils.newLine("File is empty. Set keystore path in file: " + KeystoreFile.filePath);
				KeystoreFile.setToOpenDialog(true);
			}
			pathReader.close();

		} catch (FileNotFoundException e) {
			LogUtils.newLine("Keystore file not found. Open Keystore file once again");
			KeystoreFile.setToOpenDialog(true);
		} catch (IOException e) {
			LogUtils.newLine("Something gone wrong");
			KeystoreFile.setToOpenDialog(true);
		}

	}

	public static void writeKeystorePathToFile(File file) {

		try {
			PrintWriter writeKeystorePath = new PrintWriter(filePath);
			writeKeystorePath.write(file.getAbsolutePath());
			KeystoreFile.setKeystorePath(file.getAbsolutePath());
			LogUtils.newLine("File created: " + filePath);
			LogUtils.newLine("Path to keystore is set to: " + file.getAbsolutePath());
			KeystoreFile.setToOpenDialog(false);
			writeKeystorePath.close();
		} catch (FileNotFoundException e1) {
			LogUtils.newLine("can't create file: " + e1);
			KeystoreFile.setToOpenDialog(true);
		}

	}

	public static String getKeystorePath() {
		return KeystorePath;
	}

	public static void setKeystorePath(String keystorePath) {
		KeystorePath = keystorePath;
	}

	public static boolean isKeystoreFileExists() {
		return isKeystoreFileExists;
	}

	public static void setKeystoreFileExists(boolean isKeystoreFileExists) {
		KeystoreFile.isKeystoreFileExists = isKeystoreFileExists;
	}

	public static boolean isToOpenDialog() {
		return toOpenDialog;
	}

	public static void setToOpenDialog(boolean toOpenDialog) {
		KeystoreFile.toOpenDialog = toOpenDialog;
	}

}
