/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

/**
 *
 * @author Nikhil
 */
public class ExamComboItem {
    private String url;
    private String label;
    
    ExamComboItem(String u,String l){
        url=u;
        label=l;
    }

    ExamComboItem(String line){
        System.out.println(line);
        int urlStart=line.indexOf("href=")+6;
        int urlEnd=line.indexOf(">", urlStart)-1;
        url=line.substring(urlStart, urlEnd);
        System.out.println(url);
        int labelStart=line.indexOf("<b>")+3;
        int labelEnd=line.indexOf("<", labelStart)-1;
        label=line.substring(labelStart, labelEnd);
    }
 
 @Override
    public String toString(){
        return label;
    }
 
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
}
