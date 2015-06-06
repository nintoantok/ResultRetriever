/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import ui.MainFrame1;

/**
 *
 * @author 
 */
public class GroupResult<T> extends ArrayList{
    Set<String> subjects=new TreeSet<String>();
    Set<String> grades=new TreeSet<String>();
    Map<String,Integer> passCount=new HashMap<String,Integer>();
    int totalPass=0;
    int totalFail=0;

    
    Map<String,Map<String,Integer>> subjectGradeCounts=new HashMap<String,Map<String,Integer>>();
    
    @Override
    public boolean add(Object e) {
        if(e instanceof StudentResult)
        {
            StudentResult sr=(StudentResult)e;
            Set<String> sub = sr.getAllSubjects();  
            subjects.addAll(sub);
            List<ExamResult> subResults = sr.getSubjectResults();
            boolean failed=false;
            for(ExamResult er:subResults)
            {
                String code = er.getSubCode();
                String grade = er.getGrade();
                Map<String, Integer> gradeCount = subjectGradeCounts.get(code);
                if(gradeCount==null){
                    gradeCount=new HashMap<String, Integer>();
                }
                Integer count = gradeCount.get(grade);
                if(count==null){
                    count=new Integer(0);
                }
                count=count+1;
                grades.add(grade);
                gradeCount.put(grade, count);
                subjectGradeCounts.put(code, gradeCount);
                
                if(!MainFrame1.UNDER_SESION_GRADES.contains(grade))
                {
                    Integer passcount=passCount.get(code);
                    if(passcount==null){
                        passcount=new Integer(0);
                    }
                    passcount++;
                    passCount.put(code, passcount);
                }
                else
                {
                    failed=true;
                }
            }
            if(!failed){
                totalFail++;
            }
            else{
                totalPass++;
            }
        }
        return super.add(e); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int getTotalPass() {
        return totalPass;
    }

    public void setTotalPass(int totalPass) {
        this.totalPass = totalPass;
    }

    public int getTotalFail() {
        return totalFail;
    }

    public void setTotalFail(int totalFail) {
        this.totalFail = totalFail;
    }

    public Set<String> getGrades() {
        return grades;
    }

    public void setGrades(Set<String> grades) {
        this.grades = grades;
    }

    public Map<String, Integer> getPassCount() {
        return passCount;
    }

    public void setPassCount(Map<String, Integer> passCount) {
        this.passCount = passCount;
    }

    public Map<String, Map<String, Integer>> getSubjectGradeCounts() {
        return subjectGradeCounts;
    }

    public void setSubjectGradeCounts(Map<String, Map<String, Integer>> subjectGradeCounts) {
        this.subjectGradeCounts = subjectGradeCounts;
    }

    public Set<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<String> subjects) {
        this.subjects = subjects;
    }
    
}
