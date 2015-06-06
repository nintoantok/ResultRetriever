/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package process;

import com.extentech.ExtenXLS.WorkBookHandle;
import com.extentech.ExtenXLS.WorkSheetHandle;
import com.extentech.formats.XLS.CellNotFoundException;
import com.extentech.formats.XLS.WorkSheetNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.MainFrame1;
import jxl.*;
import jxl.write.*;

/**
 *
 * @author 
 */
public class SpreadSheetCreator {
    
    GroupResult<StudentResult> results;
    String collegeName;
    String examName;
    Map<String,Integer> subjectToColoumnNoMap=new HashMap<String,Integer>();
    Map<String,Integer> gradeToRowNoMap=new HashMap<String,Integer>();
   
    public SpreadSheetCreator(GroupResult<StudentResult> results){
        this.results=results;
        collegeName="College Name Not specified";
        examName="Exam Name nto available";
    }

    public SpreadSheetCreator(GroupResult<StudentResult> results, String collegeName, String examName) {
        this.results=results;
        this.collegeName=collegeName;
        this.examName=examName;
    }
    
    public void createXLS(String fileName,boolean userJxl){
        try {
            if(userJxl==false){
                this.createXLS(fileName);
                return;
            }
            
            WritableFont boldfont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false); 
            WritableCellFormat boldfontformat = new WritableCellFormat (boldfont);
            boldfontformat.setAlignment(jxl.format.Alignment.CENTRE);
            boldfontformat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            boldfontformat.setWrap(true);
            boldfontformat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            
            WritableFont normalfont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false); 
            WritableCellFormat normalfontformat = new WritableCellFormat (normalfont);
            normalfontformat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            
            WritableWorkbook workbook = Workbook.createWorkbook(new File("../Result.xls"));
            WritableSheet sheet1 = workbook.createSheet("Sheet1", 0);
            
            int totalColumns;
            int currentRow=0;
            int currentColumn;
            int underSessions;
            jxl.write.Number number;
            Label label;
            
            sheet1.mergeCells(0, currentRow, 11, currentRow);
            label = new Label(0, currentRow, this.collegeName, boldfontformat);
            sheet1.addCell(label);
            currentRow++;
            sheet1.mergeCells(0, currentRow, 11, currentRow);
            label = new Label(0, currentRow, this.examName,boldfontformat);
            sheet1.addCell(label);
            currentRow++;
            
            
            label = new Label(0, currentRow, "Register No.",boldfontformat); 
            sheet1.addCell(label);
            label = new Label(1, currentRow, "Name of Student",boldfontformat); 
            sheet1.addCell(label);
            currentColumn=2;
            for(String subCode:results.getSubjects()){
                subjectToColoumnNoMap.put(subCode, currentColumn);
                label = new Label(currentColumn, currentRow, subCode,boldfontformat);
                sheet1.addCell(label);
                currentColumn++;
            }
            label = new Label(currentColumn, currentRow, "SGPA",boldfontformat); 
            sheet1.addCell(label);
            currentColumn++;
            label = new Label(currentColumn, currentRow, "No. of Under-sessions",boldfontformat); 
            sheet1.addCell(label);
            totalColumns=currentColumn;
            
            for(StudentResult i:(List<StudentResult>)results){
                currentRow++;
                label = new Label(0, currentRow, i.regNo,normalfontformat);
                sheet1.addCell(label);

                label = new Label(1, currentRow, i.studentName,normalfontformat);
                sheet1.addCell(label);
                underSessions=0;
                for(ExamResult j:i.subjectResults){
                    currentColumn=subjectToColoumnNoMap.get(j.subCode);
                    label = new Label(currentColumn, currentRow, j.grade,normalfontformat);
                    sheet1.addCell(label);
                    if(MainFrame1.UNDER_SESION_GRADES.contains(j.grade)){
                        underSessions++;
                    } 
                }
                currentColumn=2;
                currentColumn+=results.getSubjects().size();
                label = new Label(currentColumn, currentRow, i.averageGradePoint,normalfontformat);
                sheet1.addCell(label);
                currentColumn++;
                number = new jxl.write.Number(currentColumn, currentRow, underSessions,normalfontformat);
                //label = new Label(currentColumn, currentRow, String.valueOf(underSessions),normalfontformat);
                sheet1.addCell(number); 
            }
            
            currentRow++;
            currentRow++;
            //Pass %
            
            sheet1.mergeCells(0, currentRow, 1, currentRow);
            label = new Label(0, currentRow, "Pass Percentage",boldfontformat);
            sheet1.addCell(label); 
            
            int toalNumberOfStudents=results.size();
            Map<String, Integer> passCount = results.getPassCount();
            
            for(String subCode:passCount.keySet()){
                currentColumn=subjectToColoumnNoMap.get(subCode);
                Integer passcount = passCount.get(subCode);
                if(passcount==null){
                    passcount=new Integer(0);
                }
                double passPer=((double)passcount/(double)toalNumberOfStudents)*100.0;
                number = new jxl.write.Number(currentColumn, currentRow, passPer,normalfontformat);
                    //label = new Label(currentColumn, currentRow, count.toString(),normalfontformat);
                sheet1.addCell(number); 
            }
            currentRow++;
            
            //Summary of grades
            int rowTostartSummary = currentRow;
            Map<String, Map<String, Integer>> subjectGradeCount = results.getSubjectGradeCounts();
            
            boolean donePrintingHeadings=false;
            for(String subject:subjectGradeCount.keySet()){
                currentRow=rowTostartSummary;
                currentColumn=subjectToColoumnNoMap.get(subject);
                Map<String, Integer> gradeCount = subjectGradeCount.get(subject);
                for(String grade:results.grades)
                {
                    if(!donePrintingHeadings)
                    {
                        gradeToRowNoMap.put(grade,currentRow);
                        sheet1.mergeCells(0, currentRow, 1, currentRow);
                        label = new Label(0, currentRow, "No of "+grade+" grades",boldfontformat);
                        sheet1.addCell(label); 
                    }
                    else{
                      currentRow=gradeToRowNoMap.get(grade);
                    }
                    Integer count=gradeCount.get(grade);
                    if(count==null){
                        count=new Integer(0);
                    }
                    number = new jxl.write.Number(currentColumn, currentRow, count,normalfontformat);
                    //label = new Label(currentColumn, currentRow, count.toString(),normalfontformat);
                    sheet1.addCell(number); 
                    currentRow++;
                }
                donePrintingHeadings=true;
            }
            currentRow++;
            //Print pass and fail counts
            sheet1.mergeCells(0, currentRow, 1, currentRow);
            label = new Label(0, currentRow, "Passed Students",boldfontformat);
            sheet1.addCell(label); 
            number = new jxl.write.Number(currentColumn, currentRow, results.getTotalPass(),normalfontformat);
            sheet1.addCell(number); 
            currentRow++;
            
            
            sheet1.mergeCells(0, currentRow, 1, currentRow);
            label = new Label(0, currentRow, "Failed Students",boldfontformat);
            sheet1.addCell(label); 
             number = new jxl.write.Number(currentColumn, currentRow, results.getTotalFail(),normalfontformat);
            sheet1.addCell(number);
            currentRow++;
            
            
            
            //Adjusting columns widths
            for(int x=0;x<12;x++)
            {
                int size;
                if(x==0)
                    size=150;
                else if(x==1)
                    size=200;
                else 
                    size=80;
                CellView cell = sheet1.getColumnView(x);
                cell.setSize(size*20);
                sheet1.setColumnView(x, cell);
            }
            workbook.write(); 
            workbook.close();
            
        } catch (IOException ex) {
            Logger.getLogger(SpreadSheetCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriteException ex) {
            Logger.getLogger(SpreadSheetCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    //Deprected method not being used
    //No new features are added to this method
    public void createXLS(String fileName){
        FileOutputStream fo = null;
        int totalColumns;
        try {
            WorkBookHandle h=new WorkBookHandle();
            WorkSheetHandle sheet1 = h.getWorkSheet("Sheet1");
            
            int currentRow=1;
            char currentColumn;
            int underSessions;
            for(StudentResult i:(List<StudentResult>)results){
                if(results.indexOf(i)==0){
                    sheet1.add("Register No.", "A1");
                    sheet1.add("Name of Student", "B1");
                    currentColumn='C';
                    for(ExamResult j:i.subjectResults){
                        sheet1.add(j.subCode, String.valueOf(currentColumn)+String.valueOf(currentRow));
                        currentColumn++;
                    }
                    sheet1.add("SGPA", String.valueOf(currentColumn)+String.valueOf(currentRow));
                    currentColumn++;
                    sheet1.add("No. of Under-sessions", String.valueOf(currentColumn)+String.valueOf(currentRow));
                    totalColumns=currentColumn;
                }
                
                currentRow++;
                sheet1.add(i.regNo,"A"+String.valueOf(currentRow));
                sheet1.add(i.studentName,"B"+String.valueOf(currentRow));
                currentColumn='C';
                underSessions=0;
                for(ExamResult j:i.subjectResults){
                    sheet1.add(j.grade, String.valueOf(currentColumn)+String.valueOf(currentRow));
                    if(MainFrame1.UNDER_SESION_GRADES.contains(j.grade)){
                        underSessions++;
                    } 
                    currentColumn++;
                }
                sheet1.add(i.averageGradePoint, String.valueOf(currentColumn)+String.valueOf(currentRow));
                currentColumn++;
                sheet1.add(underSessions, String.valueOf(currentColumn)+String.valueOf(currentRow)); 
            }
            fo = new FileOutputStream("Result.xls");
            h.writeBytes(fo);
            fo.close();
        } catch (IOException ex) {
            Logger.getLogger(SpreadSheetCreator.class.getName()).log(Level.SEVERE, null, ex);
        }  catch (WorkSheetNotFoundException ex) {
            Logger.getLogger(SpreadSheetCreator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fo.close();
            } catch (IOException ex) {
                Logger.getLogger(SpreadSheetCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
     //For unit testing only
    public static void main(String args[]) throws WorkSheetNotFoundException, CellNotFoundException, FileNotFoundException, IOException{
        /*
        //Test 1 - Creating empty XLS
        FileOutputStream fo=new FileOutputStream("Result.xls");
        WorkBookHandle h=new WorkBookHandle();
        WorkSheetHandle y = h.getWorkSheet("Sheet1");
        y.add("aa", "A1");
        h.writeBytes(fo);
        fo.close();
        */  
        
    }
    
}
