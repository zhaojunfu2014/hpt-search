package com.hpt.search.cluster.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;

import com.hpt.search.common.SearchGlobal;

public class SocketUtils {
	
	public static BufferedReader getReaderFromSocket(Socket client){
		Reader reader = null;
		BufferedReader br=null;
		try {
		      //接受客户端传来的数据
		      reader = new InputStreamReader(client.getInputStream(),SearchGlobal.encode);  
		      br = new BufferedReader(reader);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return br;
	}
	
	public static PrintWriter getWriterFromSocket(Socket client) throws UnsupportedEncodingException, IOException{
		Writer writer = null;
		PrintWriter pw = null;
		writer = new OutputStreamWriter(client.getOutputStream(),SearchGlobal.encode);
		pw = new PrintWriter(writer);
		return pw;
	}
	
	public static boolean close(BufferedReader br,Socket client){
		try{
		     br.close();
		     client.close();
	   	 }catch (Exception e) {
			e.printStackTrace();
			return false;
	   	 }
		return true;
	}
	
	public static boolean close(PrintWriter pw,Socket client){
		try{
	    	 pw.flush();
		     pw.close();
		     client.close();
	   	 }catch (Exception e) {
			e.printStackTrace();
			return false;
	   	 }
		return true;
	}
}
