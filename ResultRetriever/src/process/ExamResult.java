/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package process;

/**
 *
 * @author Nikhil
 */
public class ExamResult {
    
    String subCode;
    String subName;
    String credit;
    String grade;
    String date;
    String type;
    
    
    ExamResult(){
        
    }
    ExamResult(String subCode,String subName,String credit,String grade,String date,String type){
        this.subCode=subCode;
        this.subName=subName;
        this.credit=credit;
        this.grade=grade;
        this.date=date;
        this.type=type;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void quickPrint(){
        System.out.println(subCode+" "+subName+" "+credit+" "+grade+" "+type);
    }
}
