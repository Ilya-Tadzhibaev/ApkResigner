package com.tadzhibaev.apkresigner;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Interface extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static String newline = "\n";
	JButton openAPK, openKeystore, resign, alignAPK, test;
	JFileChooser fileChooser, KeystoreChooser;

	static JTextArea log;
	private static boolean isKeystoreDisabled = true;
	private static boolean isResignDisabled = true;
	private static boolean isAlignDisabled = true;

	public Interface() {
		super(new BorderLayout());

		log = new JTextArea(10, 40);
		log.setMargin(new Insets(10, 10, 10, 10));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		openAPK = new JButton("Open APK file");
		openAPK.addActionListener(this);

		openKeystore = new JButton("Open Keystore file");
		openKeystore.addActionListener(this);
		if (!isKeystoreDisabled) {
			openKeystore.setEnabled(false);

		}

		resign = new JButton("Re-Sign!");
		resign.addActionListener(this);
		if (!isResignDisabled) {
			resign.setEnabled(false);
		}

		alignAPK = new JButton("Align APK");
		alignAPK.addActionListener(this);
		if (!isAlignDisabled) {
			alignAPK.setEnabled(false);
		}

		test = new JButton("Clear files chosen");
		test.addActionListener(this);

		JPanel newPanel = new JPanel();
		newPanel.add(openAPK);
		newPanel.add(openKeystore);
		newPanel.add(resign);
		newPanel.add(alignAPK);
		newPanel.add(test);

		add(newPanel, BorderLayout.NORTH);
		add(logScrollPane, BorderLayout.CENTER);

		FileFilter filterAPK = new FileNameExtensionFilter("APK files", "apk");

		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileFilter(filterAPK);

		FileFilter filterKeystore = new FileNameExtensionFilter("Keysore files", "keystore");

		KeystoreChooser = new JFileChooser();
		KeystoreChooser.setMultiSelectionEnabled(false);
		KeystoreChooser.setFileFilter(filterKeystore);

		FileUtils.newArrayInstance();

	}

	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == openAPK) {
			int retInt = fileChooser.showOpenDialog(Interface.this);

			if (retInt == JFileChooser.APPROVE_OPTION) {
				File[] fileSelected = fileChooser.getSelectedFiles();

				for (File file : fileSelected) {
					LogUtils.newLine("File selected: " + file);
					FileUtils.setNotModifiedFiles(file);

				}

			} else {
				LogUtils.newLine("Open APK file cancelled by user.");
			}

		} else if (event.getSource() == openKeystore) {
			KeystoreFile.isKeystoreExists();
			if (KeystoreFile.isToOpenDialog()) {

				int retInt = KeystoreChooser.showOpenDialog(this);

				if (retInt == JFileChooser.APPROVE_OPTION) {
					File fileSelected = KeystoreChooser.getSelectedFile();
					KeystoreFile.writeKeystorePathToFile(fileSelected);
				} else {
					LogUtils.newLine("Open Keystore file cancelled by user.");
				}
			} else {
				KeystoreFile.ReadKeystorePath();
			}

		} else if (event.getSource() == resign) {

			Thread resigning = new Thread() {

				public void run() {

					try {
						
						isAlignDisabled = true;
						isKeystoreDisabled = true;
						isResignDisabled = true;

						KeystoreFile.ReadKeystorePath();
						FileUtils.isCredentialsFileExists();
						AlignPlusPSWD.createNewIfNotPresented();

						if (FileUtils.getAliasName() != null && FileUtils.getPswd() != null && AlignPlusPSWD.isFileExists()) {

							if (!FileUtils.getNotModifiedFiles().isEmpty()) {

								for (File notModifiedFile : FileUtils.getNotModifiedFiles()) {
									int index = notModifiedFile.getAbsolutePath().lastIndexOf(".");
									String dontChange = notModifiedFile.getAbsolutePath().substring(0, index);
									String destString = dontChange + "_merc" + notModifiedFile.getAbsolutePath().substring(index, notModifiedFile.getAbsolutePath().length());

									File dest = new File(destString);
									try {
										FileUtils.copySourceFile(notModifiedFile, dest);
									} catch (IOException e) {
										LogUtils.newLine(e.toString());
									}
									FileUtils.rename(dest);
									FileUtils.DeleteFolder(FileUtils.getFileName() + File.separator + "META-INF");
									FileUtils.renameBack(FileUtils.getFileName());

								}

								if (!FileUtils.getRenamedFileName().isEmpty()) {

									if (!KeystoreFile.getKeystorePath().isEmpty()) {

										for (File renamedFile : FileUtils.getRenamedFileName()) {

											String resignCommand = "jarsigner -sigalg SHA1withRSA -digestalg SHA1 -keystore " + KeystoreFile.getKeystorePath() + " " + renamedFile + " " + FileUtils.getAliasName() + " -storepass "
											        + FileUtils.getPswd();

											try {
												LogUtils.newLine(resignCommand);
												Process process = Runtime.getRuntime().exec(resignCommand);

												BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
												String line = reader.readLine();
												while (line != null) {
													LogUtils.newLine(line);
													line = reader.readLine();

												}

												reader.close();
												process.destroy();
											} catch (IOException e) {
												LogUtils.newLine("Something gone wrong");
											}
										}

									} else {
										LogUtils.newLine("Set Keystore file");
									}

								} else {
									LogUtils.newLine("Choose files first");
								}
							} else {
								LogUtils.newLine("Choose files first");
							}

						} else {
							LogUtils.newLine("Set \"alias_name\" or \"password\" first");
						}
					} catch (Exception e) {

					}
				}
			};
			resigning.start();
			

		} else if (event.getSource() == alignAPK) {

			if (!FileUtils.getRenamedFileName().isEmpty()) {

				if (!KeystoreFile.getKeystorePath().isEmpty()) {

					for (File alignFile : FileUtils.getRenamedFileName()) {

						int indexAPK = alignFile.getAbsolutePath().lastIndexOf(".");

						String dontChangeAPK = alignFile.getAbsolutePath().substring(0, indexAPK);
						File renamedToAligned = (new File(dontChangeAPK + "_aligned.apk"));

						String alignCommand = "zipalign -v 4 " + alignFile + " " + renamedToAligned;

						try {
							LogUtils.newLine(alignCommand);
							Process process = Runtime.getRuntime().exec(alignCommand);

							BufferedReader readerAlign = new BufferedReader(new InputStreamReader(process.getInputStream()));
							String line = readerAlign.readLine();
							while (line != null) {
								line = readerAlign.readLine();
								if (line.equalsIgnoreCase("Verification succesful")) {
									LogUtils.newLine(line);
									LogUtils.newLine("If you want to re-sign other files - press \"Clear files chosen\" button");
									alignFile.delete();
									break;
								}

							}

							readerAlign.close();
							process.destroy();
						} catch (IOException e) {
							LogUtils.newLine("Something gone wrong");
						}
					}
				} else {
					LogUtils.newLine("Set Keystore file");
				}
			} else {
				LogUtils.newLine("Choose files first");
			}

		} else if (event.getSource() == test) {
			if (!FileUtils.getNotModifiedFiles().isEmpty()) {
				FileUtils.clearNotModifiedFilesArray();
				LogUtils.newLine("Chosen files array cleared");
				if (!FileUtils.getRenamedFileName().isEmpty()) {
					FileUtils.clearModifiedFilesArray();
					LogUtils.newLine("Modified files array cleared");
				} else {
					LogUtils.newLine("No files modified");
				}
			} else {
				LogUtils.newLine(">No files selected");
			}

		}
	}

	private static void createAndShowGUI() {

		JFrame frame = new JFrame("APK Resigner");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new Interface());
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});

	}

}
