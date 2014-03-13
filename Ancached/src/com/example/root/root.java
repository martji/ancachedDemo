package com.example.root;

import java.io.DataOutputStream;

public class root {
	
	public static boolean upgradeRootPermission(String pkgCodePath) {  
	    Process process = null;  
	    DataOutputStream os = null;  
	    try {  
	        String cmd="chmod 777 " + pkgCodePath;  
	        process = Runtime.getRuntime().exec("su"); //«–ªªµΩroot’ ∫≈  
	        os = new DataOutputStream(process.getOutputStream());  
	        os.writeBytes(cmd + "\n");  
	        os.writeBytes("exit\n");  
	        os.flush();  
	        process.waitFor();  
	    } catch (Exception e) {  
	        return false;  
	    } finally {  
	        try {  
	            if (os != null) {  
	                os.close();  
	            }  
	            process.destroy();  
	        } catch (Exception e) {  
	        }  
	    }  
	    return true;  
	}  
	
}
