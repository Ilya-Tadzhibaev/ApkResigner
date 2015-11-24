package com.tadzhibaev.apkresigner;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.util.ArrayList;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;

public final class FileUtils {

	private static File fileName;
	private static ArrayList<File> notModifiedFiles;
	private static ArrayList<File> renamedFiles;

	private static String aliasName;
	private static String pswd;

	/*public void DeleteFiles(){
	    
		 FileUtils.DeleteFolder(FileUtils.getFileName() + File.separator + "META-INF");
	} - not used*/

	public static void newArrayInstance() {
		renamedFiles = new ArrayList<File>();
		notModifiedFiles = new ArrayList<File>();
	}

	public static void DeleteFolder(String string) {

		try {
			TFile zipFile = new TFile(string);
			if (zipFile.isDirectory()) {

				TFile.rm_r(zipFile);

				LogUtils.newLine("Deleted: " + zipFile.getAbsolutePath());

				TVFS.umount();

			} else {
				LogUtils.newLine("METTA-INF directory doesn't exist");
				TVFS.umount();
			}

		} catch (IOException e) {
			System.out.println("Can't Delete");
		}

	}

	public static void rename(File file) {

		int index = file.getAbsolutePath().lastIndexOf(".");
		String dontChange = file.getAbsolutePath().substring(0, index);
		File renamed = (new File(dontChange + ".zip"));

		boolean isRenamed = file.renameTo(new File(dontChange + ".zip"));
		if (isRenamed) {
			setFileName(renamed);
			LogUtils.newLine("Renamed to: " + renamed);
		} else {
			LogUtils.newLine("Something gone wrong");
		}

	}

	public static void renameBack(File zip) {

		int indexAPK = zip.getAbsolutePath().lastIndexOf(".");

		String dontChangeAPK = zip.getAbsolutePath().substring(0, indexAPK);
		File renamedtoAPK = (new File(dontChangeAPK + ".apk"));

		boolean isRenamed = zip.renameTo(new File(dontChangeAPK + ".apk"));
		if (isRenamed) {
			setFileName(renamedtoAPK);
			addToAray(renamedtoAPK);
			LogUtils.newLine("Renamed back to: " + renamedtoAPK);
		} else {
			LogUtils.newLine("Something gone wrong");

		}
	}

	public static void isCredentialsFileExists() {
		boolean checkFile = new File(System.getProperty("user.dir") + File.separator + "1.txt").exists();
		LogUtils.newLine("Credential file exists? " + checkFile);
		if (checkFile) {
			AlignPlusPSWD.setFileExists(true);
		} else {
			AlignPlusPSWD.setFileExists(false);
		}
	}

	public static void copySourceFile(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath());

	}

	public static File getFileName() {
		return fileName;
	}

	public static void setFileName(File fileName) {
		FileUtils.fileName = fileName;
	}

	public static ArrayList<File> getRenamedFileName() {
		return renamedFiles;
	}

	public static void addToAray(File fileName) {
		FileUtils.renamedFiles.add(fileName);
	}

	public static String getAliasName() {
		return aliasName;
	}

	public static void setAliasName(String string) {
		FileUtils.aliasName = string;
	}

	public static String getPswd() {
		return pswd;
	}

	public static void setPswd(String pswd) {
		FileUtils.pswd = pswd;
	}

	public static ArrayList<File> getNotModifiedFiles() {
		return notModifiedFiles;
	}

	public static void setNotModifiedFiles(File notModifiedFiles) {
		FileUtils.notModifiedFiles.add(notModifiedFiles);
	}

	public static void clearModifiedFilesArray() {
		FileUtils.renamedFiles.clear();
	}

	public static void clearNotModifiedFilesArray() {
		FileUtils.notModifiedFiles.clear();
	}

}
