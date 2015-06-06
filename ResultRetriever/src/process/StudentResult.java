/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package process;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Nikhil
 */
public class StudentResult {

    String regNo;
    String studentName;
    String collegeName;
    String examName;
    List<ExamResult> subjectResults=new ArrayList<ExamResult>();
    String averageGradePoint;
    Set<String> allSubjects=new TreeSet<String>();
    
    public void addSubjectResult(String subCode,String subName,String credit,String grade,String date,String type){
        allSubjects.add(subCode);
        subjectResults.add(new ExamResult(subCode,subName,credit,grade,date,type));
    }

    public Set<String> getAllSubjects() {
        return allSubjects;
    }

    public void setAllSubjects(Set<String> allSubjects) {
        this.allSubjects = allSubjects;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public List<ExamResult> getSubjectResults() {
        return subjectResults;
    }

    public void setSubjectResults(List<ExamResult> subjectResults) {
        this.subjectResults = subjectResults;
    }
     public String getAverageGradePoint() {
        return averageGradePoint;
    }

    public void setAverageGradePoint(String averageGradePoint) {
        this.averageGradePoint = averageGradePoint;
    }
    
    public void quickPrint(){
        System.out.println("Name: "+studentName);
        System.out.println("Reg No: "+regNo);
        System.out.println("College: "+collegeName);
        for(ExamResult i:subjectResults){
            i.quickPrint();
        }
        System.out.println("Average Points: "+averageGradePoint);
    }
}

