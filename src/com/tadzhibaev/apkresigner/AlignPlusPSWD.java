package com.tadzhibaev.apkresigner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public final class AlignPlusPSWD {
	
	private static boolean isFileExists;
	private static final String filePath = System.getProperty("user.dir") + File.separator + "1.txt";
	
	public static void createNewIfNotPresented(){
		
		if (!AlignPlusPSWD.isFileExists()){
			try {
				PrintWriter fileTXT = new PrintWriter(filePath);
				LogUtils.newLine("File created:" + filePath);
				LogUtils.newLine("Please, write alias_name and password in it like that:");
				LogUtils.newLine("alias_name");
				LogUtils.newLine("password");
				fileTXT.close();
			} catch (FileNotFoundException e1) {
				LogUtils.newLine("can't create file");
			}
			
		} else {
			try {
				BufferedReader br = new BufferedReader(new FileReader(filePath));
				
				String aliasName = br.readLine();
				String pswd = br.readLine();
				if (aliasName != null){
					FileUtils.setAliasName(aliasName);
				}
				if (pswd != null){
					FileUtils.setPswd(pswd);
				}
				
				
				br.close();
			} catch (FileNotFoundException e) {
				LogUtils.newLine("Something gone wrong");
			} catch (IOException e) {
				LogUtils.newLine("alias_name or password are incorrect or absent. Please, fill the file correctly");
				
			}
			
		}
		
	}
	
	public static boolean isFileExists() {
		return isFileExists;
	}

	public static void setFileExists(boolean isFileExists) {
		AlignPlusPSWD.isFileExists = isFileExists;
	}

	
}
