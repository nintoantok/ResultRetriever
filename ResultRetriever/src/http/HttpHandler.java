/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package http;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ui.MainFrame1;

/**
 *
 * @author 
 */
public class HttpHandler {
    String url;
    String response;
    String textip="110.77.227.246";
    InetAddress proxyHost;
    int proxyPort=3128;

    
    public HttpHandler(String url){
        proxyHost=null;
        try {
            this.url=url;
            proxyHost=InetAddress.getByName(textip);
        } catch (UnknownHostException ex) {
            Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getResponse(){
        String res=null;
        boolean retry=true;
        int retrys=0;
        while(retry)
        {
            try {

//                InetSocketAddress address = new InetSocketAddress(proxyHost, proxyPort);
//                Proxy proxy = new Proxy(Proxy.Type.SOCKS, address);
//                Socket connection = new Socket(proxy);
//                InetSocketAddress inet = new InetSocketAddress(MainFrame1.RESULT_DOMAIN, MainFrame1.PORT);
//                connection.connect(inet);
                Socket connection = new Socket(MainFrame1.RESULT_DOMAIN, MainFrame1.PORT);
                
                OutputStream con_out = connection.getOutputStream();
                InputStream con_in = connection.getInputStream();
                PrintWriter out_writer = new PrintWriter(con_out, false);
                out_writer.print("GET "+url+" HTTP/1.1\r\n");
                out_writer.print("Host: nikhil-pc\r\n");
                out_writer.print("\r\n");
                out_writer.flush();

                InputStreamReader isr_reader = new InputStreamReader(con_in);
                char[] streamBuf = new char[8192];
                int amountRead;
                StringBuilder receivedData = new StringBuilder();
                while((amountRead = isr_reader.read(streamBuf)) > 0){
                        receivedData.append(streamBuf, 0, amountRead);
                }
                res=receivedData.toString();
            } catch (UnknownHostException ex) {
                Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(res==null && retrys<3){
                retrys++;
            }
            else {
                retry=false;
            }
        }
        if(res==null){
            //Code to write log file   
        }
        this.response=res;
        return res;
    }
    
    
    public void saveResponseToFile(String fileName){
        FileOutputStream fstream=null;
        try {
//            InetSocketAddress address = new InetSocketAddress(proxyHost, proxyPort);
//            Proxy proxy = new Proxy(Proxy.Type.SOCKS, address);
//            Socket connection = new Socket(proxy);
//            InetSocketAddress inet = new InetSocketAddress(MainFrame1.RESULT_DOMAIN, MainFrame1.PORT);
//            connection.connect(inet);
            
            Socket connection = new Socket(MainFrame1.RESULT_DOMAIN, MainFrame1.PORT);
            OutputStream con_out = connection.getOutputStream();
            InputStream con_in = connection.getInputStream();
            PrintWriter out_writer = new PrintWriter(con_out, false);
            out_writer.print("GET "+url+" HTTP/1.1\r\n");
            out_writer.print("Host: "+MainFrame1.HOST_NAME+"\r\n");
            out_writer.print("\r\n");
            out_writer.flush();
            
             // 1. Read the response header from server separately beforehand.
            byte data;
            String temp_char = "";
            while (!"\r\n\r\n".equals(temp_char)) {
                data = (byte) con_in.read();
                if (((char) data) == '\r' || ((char) data) == '\n') {
                    temp_char += String.valueOf((char) data);
                } else {
                    temp_char = "";
                }
            }

            // 2. Recieving the actual data, be it text or binary
            byte[] streamBuf = new byte[8192];
            int amountRead;
            fstream=new FileOutputStream(fileName);

            while((amountRead = con_in.read(streamBuf,0,streamBuf.length)) > 0){
                    fstream.write(streamBuf, 0, amountRead);
           }
        } catch (UnknownHostException ex) {
            Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(fstream!=null) {
                try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }
    }
    
    
    public String getDestinationUrl(){
        if(response==null) {
            return "";
        }else{
            String destinationUrl;
            Pattern pt1=Pattern.compile(MainFrame1.REGEX_FORM_START);
            Matcher matcher1 = pt1.matcher(response);
            matcher1.find();
            int urlStartIndex = matcher1.end();
            int urlEndUrlIndex=min(response.indexOf("\"",urlStartIndex),response.indexOf("\'",urlStartIndex));
            destinationUrl=response.substring(urlStartIndex, urlEndUrlIndex);
            if(destinationUrl.startsWith("./")) {
                destinationUrl=destinationUrl.substring(2);
            }
            if(destinationUrl.startsWith("/")){
                int firstSepIndex = url.indexOf(MainFrame1.RESULT_DOMAIN)+MainFrame1.RESULT_DOMAIN.length();
                String urPrefix = url.substring(0, firstSepIndex);
                destinationUrl=urPrefix+"/"+destinationUrl;
            }
            else{
                int lastSepIndex = url.lastIndexOf("/");
                String urPrefix = url.substring(0, lastSepIndex);
                destinationUrl=urPrefix+"/"+destinationUrl;
            }
            int paramIndex=url.lastIndexOf("?")+1;
            String param = url.substring(paramIndex);
            destinationUrl=destinationUrl+"?"+param;
            return destinationUrl;
        }
    }
    
    public String getTextFieldName(){
        String fieldName;
        Pattern pt1=Pattern.compile(MainFrame1.REGEX_INPUT_START);
        Matcher matcher1 = pt1.matcher(response);
        matcher1.find();
        int inputStartIndex=matcher1.start();
        int nameStartIndex = response.indexOf("name=",inputStartIndex)+6;
        int nameEndIndex=min(response.indexOf("\"",nameStartIndex),response.indexOf("\'",nameStartIndex));
        fieldName=response.substring(nameStartIndex, nameEndIndex);
        return fieldName;
    }
    
    private int min(int a,int b){
        if(a<0) {
            return b;
        }
        if(b<0) {
            return a;
        }
        else {
            return (a<b?a:b);
        }
    }
    
}
