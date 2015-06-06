/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nikhil
 */


package process;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import com.itextpdf.text.pdf.parser.RenderListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.MainFrame1;


public class PdfProcessor {
    
    String fileName;
    String pdfText;;

    public PdfProcessor() {
    }
    
    public PdfProcessor(String fileName){
        this.fileName=fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
    
    public static void main(String args[]){
        
        PdfProcessor obj=new PdfProcessor(MainFrame1.TEMP_PDF_FILE_NAME);
        obj.extractText();
        //System.out.println(obj.pdfText);       
        StudentResult res=obj.parseResult();
        res.quickPrint();
 
    }
    
    public StudentResult getStudentResult(){
        extractText();
        return parseResult();
    }
    
    public String extractText(){
        String s=null;
        try {
            ByteArrayOutputStream o=new ByteArrayOutputStream(1024);
            PdfReader reader = new PdfReader(fileName);
            RenderListener listener = new TextRenderListener(o);
            PdfContentStreamProcessor processor = new PdfContentStreamProcessor(listener);
            PdfDictionary pageDic = reader.getPageN(1);
            PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
            processor.processContent(ContentByteUtils.getContentBytesForPage(reader, 1), resourcesDic);
            o.flush();
            s=o.toString();
            reader.close();
            
        } catch (IOException ex) {
            Logger.getLogger(PdfProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        pdfText=s;
        return s;
    }
    
    public StudentResult parseResult(){
        boolean subjectSection=false;
        StudentResult result=new StudentResult();
        String[] lines=pdfText.split("\\n");
        for(int i=0;i<lines.length;i++){
            
            if(subjectSection){
                if(lines[i].length()==0)
                {
                    subjectSection=false;
                    continue;
                }
                //System.out.println(lines[i]);
                result.addSubjectResult(lines[i], lines[i+1], lines[i+2], lines[i+3], lines[i+4], lines[i+5]);
                i=i+5;
            }
            else{
                
                if(lines[i].contains("NAME OF CANDIDATE")){
                    i++;
                    result.setStudentName(lines[i].trim());
                }
                else if(lines[i].contains("REGISTER No.")){
                    i++;
                    result.setRegNo(lines[i].trim());
                }
                else if(lines[i].contains("EXAMINATION CENTER")){
                    i++;
                    result.setCollegeName(lines[i].trim());
                    i=i+2;
                    subjectSection=true;
                }
                else if(lines[i].contains("Semester Grade Point Average")){
                    String[] parts=lines[i].split(":");
                    String rawPoint=parts[1].trim();
                    result.setAverageGradePoint(rawPoint);
                }
            }
        }
        return result;
    }
}
