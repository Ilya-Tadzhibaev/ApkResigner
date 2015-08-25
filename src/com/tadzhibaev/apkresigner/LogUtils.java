package com.tadzhibaev.apkresigner;

public final class LogUtils {
	
	public static void newLine(String string){
		Interface.log.append(string + Interface.newline);
		Interface.log.setCaretPosition(Interface.log.getDocument().getLength());
	}

}
