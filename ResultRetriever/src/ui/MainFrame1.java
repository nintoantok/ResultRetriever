/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Test with  http://uoc.ac.in/exam/btech/creditbt1.php?id=2247
* VEAKECS001 to VEAKECS066
 */

package ui;

import http.HttpHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import process.GroupResult;
import process.PdfProcessor;
import process.SpreadSheetCreator;
import process.StudentResult;

/**
 *
 * @author 
 */
public class MainFrame1 extends javax.swing.JFrame {
    
    public static String COLLEGE_NAME="College Name Here.....";
    
    public static List<String> UNDER_SESION_GRADES=new ArrayList<String>(Arrays.asList("U","U(Absent)"));
    public static int PORT=80;
    public static String RESULT_DOMAIN="uoc.ac.in";
    public static String RESULT_URL="http://uoc.ac.in/exam/resultonline.php";
    public static String EXAMS_LIST_FILE_NAME="temp.txt";
    public static String BTECH_CREDIT_URL="btech/credit";
    public static String TEMP_PDF_FILE_NAME="temp.pdf";
    public static String HOST_NAME="nikhil-pc";
    
    public static String FILE_NEWLINE="\r\n";
    public static String EMPTY_STRING="";
    public static String THREE_DECIMAL_PLACE_FORMAT="%03d";
    
    public static String REGEX_TABLE_START="<table border=1([\\s\\S])*";
    public static String REGEX_LINK_START="<a href";
    public static String REGEX_LINK_END="</a>";
    public static String REGEX_FORM_START="<form(.)*action(\\s)*=(\\s)*(\"|\')";
    public static String REGEX_INPUT_START="<input(.)*type(\\s)*=(\\s)*(\"|\')text(\"|\')";
    public static String REGEX_NEW_LINE="\\n";
    
    public static String MSG_INV_REGNO="Enter Valid Register Number";
    public static String MSG_INV_REGNO_N_NO="Enter Valid Register Number and Number of Students";
    

    /**
     * Creates new form NewJFrame
     */
    public MainFrame1() {
        initComponents();
        fillExamsCombo();
        activateTextBoxes();
    }
    
    private void activateTextBoxes(){
        if(singleStudentRadio.isSelected()){
            regNoField.setEnabled(true);
            startRegNoField.setEnabled(false);
            noStudentsField.setEnabled(false);
        }
        else{
            regNoField.setEnabled(false);
            startRegNoField.setEnabled(true);
            noStudentsField.setEnabled(true);
        }
    }
    
    private void fillExamsCombo(){
        FileReader fr; 
        BufferedReader br;
        try {
            fr = new FileReader(EXAMS_LIST_FILE_NAME);
            br = new BufferedReader(fr);
            String s; 
            while((s = br.readLine()) != null) { 
                ExamComboItem item = new ExamComboItem(s);
                if(item.getUrl().contains(BTECH_CREDIT_URL)){
                    examCombo.addItem(item);
                }
            } 
            fr.close(); 
        } catch (IOException ex) {
            Logger.getLogger(MainFrame1.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch(Exception e){
            Logger.getLogger(MainFrame1.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void writeListToFile(List<String> l){
        FileWriter fstream = null;
        try {
            fstream = new FileWriter(EXAMS_LIST_FILE_NAME);
            BufferedWriter out = new BufferedWriter(fstream);
            for(String i:l){
                out.write(i+FILE_NEWLINE);
            }
            out.close();
            fstream.close();
        } catch (IOException ex) {
            Logger.getLogger(MainFrame1.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(MainFrame1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String getHttpResponse(){
        HttpHandler h=new HttpHandler(RESULT_URL);
        return (h.getResponse());
    }
    
   public List<String> parseHtml(String html){
       
       //Extracting main table from HTML
       Pattern pt1=Pattern.compile(REGEX_TABLE_START);
       Matcher matcher1 = pt1.matcher(html);
       matcher1.find();
       String table=matcher1.group();
       
       //Extracting all the Links from the table
       ArrayList<String> l=new ArrayList<String>();
       Pattern pt2=Pattern.compile(REGEX_LINK_START);
       Pattern pt3=Pattern.compile(REGEX_LINK_END);
       Matcher matcher2 = pt2.matcher(table);
       Matcher matcher3 = pt3.matcher(table);
       while (matcher2.find() && matcher3.find()) {
           String line=table.substring(matcher2.start(), matcher3.end());
           line=line.replaceAll(REGEX_NEW_LINE, EMPTY_STRING);
           l.add(line);
       }             
       return l;
   }
   
   public void fetchresults(String url,String regNoFieldName){
        FileWriter fw = null;
        try {
            List<String> studentRegNos=prepareListofRegs();
            GroupResult<StudentResult> results=new GroupResult<StudentResult>();
            if(studentRegNos==null){
                JOptionPane.showMessageDialog(this,MSG_INV_REGNO_N_NO);
                return;
            }
            String prefixUrl;
            if(url.contains("?")){
                 prefixUrl = url+"&"+regNoFieldName+"=";
            }else{
                prefixUrl = url+"?"+regNoFieldName+"=";
            }
            fw = new FileWriter("log.txt", true);
            fw.write("-----------------------------\r\n");
            Date now=new Date();
            fw.write("Log on: "+now.toString()+"\r\n");
            for(String i:studentRegNos){
                String finalUrl=prefixUrl+i;
                System.out.println(finalUrl);
                boolean retry=true;
                int retrys=0;
                
                StudentResult r=null;
                while(retry)
                {
                    r=null;
                    HttpHandler h=new HttpHandler(finalUrl);
                    h.saveResponseToFile(TEMP_PDF_FILE_NAME);
                    PdfProcessor p=new PdfProcessor(TEMP_PDF_FILE_NAME);
                    r=p.getStudentResult();
                    if(r.getRegNo()==null && retrys<5){
                        retrys++;
                        System.out.println("Retrying.............");
                    }
                    else
                    {
                        retry=false;
                    }
                }
                if(r.getRegNo()==null)
                {
                    //Code to write log
                    fw.write("Failed to retrieve result for reg no "+i+"\r\n");

                }
                else
                {
                     results.add(r);
                     r.quickPrint();
                }
             }
            ExamComboItem selectedItem=(ExamComboItem) examCombo.getSelectedItem();
            String examName=selectedItem.toString();
            SpreadSheetCreator sc=new SpreadSheetCreator(results,this.COLLEGE_NAME,examName);
            sc.createXLS("results.xls",true);
            System.out.println("Done!!!");
        } catch (IOException ex) {
            Logger.getLogger(MainFrame1.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(fw!=null)
                    fw.close();
            } catch (IOException ex) {
                Logger.getLogger(MainFrame1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
   }
   
   public List<String> prepareListofRegs(){
       List<String> listOfRegNo=new ArrayList<String>();
       if(singleStudentRadio.isSelected()){
            String regNo = regNoField.getText();
            if(regNo==null||regNo.equals(EMPTY_STRING)){
                return null;
            }
            listOfRegNo.add(regNo);
        }
        else{
            String regNo = startRegNoField.getText();
            String noStudents=noStudentsField.getText();
            if(regNo==null||regNo.equals(EMPTY_STRING)||noStudents==null||noStudents.equals(EMPTY_STRING)){
                return null;
            }
            int nos=Integer.parseInt(noStudents);
            String regNoSufix=regNo.substring(regNo.length()-3);
            String regNoPefix=regNo.substring(0,regNo.length()-3);
            int startingRegNo=Integer.parseInt(regNoSufix);
            for(int i=0;i<nos;i++){
                String finalRegNo=regNoPefix+String.format(THREE_DECIMAL_PLACE_FORMAT,startingRegNo+i);
                listOfRegNo.add(finalRegNo);
            }
        }
       return listOfRegNo;
   }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        startRegNoField = new javax.swing.JTextField();
        examCombo = new javax.swing.JComboBox();
        noStudentsField = new javax.swing.JTextField();
        singleStudentRadio = new javax.swing.JRadioButton();
        regNoField = new javax.swing.JTextField();
        multiStudentRadio = new javax.swing.JRadioButton();
        labelExam = new java.awt.Label();
        labelRegNo = new java.awt.Label();
        labelStartRegNo = new java.awt.Label();
        labelNoStudents = new java.awt.Label();
        retrieveButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        streamCombo = new javax.swing.JComboBox();
        labelStream = new javax.swing.JLabel();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Result Analysis");

        buttonGroup1.add(singleStudentRadio);
        singleStudentRadio.setSelected(true);
        singleStudentRadio.setText("Single Student");
        singleStudentRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleStudentRadioActionPerformed(evt);
            }
        });

        buttonGroup1.add(multiStudentRadio);
        multiStudentRadio.setText("Group of Students");
        multiStudentRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiStudentRadioActionPerformed(evt);
            }
        });

        labelExam.setText("Exam");

        labelRegNo.setText("Reg No");

        labelStartRegNo.setText("Starting Reg No");

        labelNoStudents.setText("No of Students");

        retrieveButton.setText("Retrieve");
        retrieveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retrieveButtonActionPerformed(evt);
            }
        });

        settingsButton.setText("...");
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });

        refreshButton.setText("Refresh List");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        streamCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "B Tech (Credit)" }));

        labelStream.setText("Stream");

        fileMenu.setText("File");

        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        mainMenuBar.add(fileMenu);

        helpMenu.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        mainMenuBar.add(helpMenu);

        setJMenuBar(mainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(settingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(multiStudentRadio)
                                .addComponent(singleStudentRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(labelRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(labelStartRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(labelNoStudents, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(104, 104, 104)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(startRegNoField)
                                                .addComponent(noStudentsField)
                                                .addComponent(regNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(labelExam, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(labelStream))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(examCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(streamCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGap(43, 43, 43)
                                    .addComponent(refreshButton)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(256, 256, 256)
                        .addComponent(retrieveButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(streamCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelStream))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(examCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(refreshButton))
                    .addComponent(labelExam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(singleStudentRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(regNoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(multiStudentRadio)
                        .addGap(22, 22, 22)
                        .addComponent(startRegNoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addComponent(labelStartRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noStudentsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNoStudents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addComponent(retrieveButton)
                .addGap(7, 7, 7)
                .addComponent(settingsButton)
                .addContainerGap())
        );

        labelExam.getAccessibleContext().setAccessibleName("examLabel");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void retrieveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retrieveButtonActionPerformed
        ExamComboItem selectedItem=(ExamComboItem) examCombo.getSelectedItem();
        String url = selectedItem.getUrl();
        System.out.println(url);
        HttpHandler h=new HttpHandler(url);
        h.getResponse();
        String destUrl = h.getDestinationUrl();
        String fieldName=h.getTextFieldName();
        fetchresults(destUrl, fieldName);
        
    }//GEN-LAST:event_retrieveButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        List l=parseHtml(getHttpResponse());
        writeListToFile(l);
        examCombo.removeAllItems();
        fillExamsCombo();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void multiStudentRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiStudentRadioActionPerformed
        activateTextBoxes();
    }//GEN-LAST:event_multiStudentRadioActionPerformed

    private void singleStudentRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleStudentRadioActionPerformed
        activateTextBoxes();
    }//GEN-LAST:event_singleStudentRadioActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SettingsDialog dialog = new SettingsDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        //
                    }
                });
                dialog.setVisible(true);
            }
        });
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
         java.awt.EventQueue.invokeLater(new Runnable() {
             @Override
            public void run() {
                AboutDialog dialog = new AboutDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                    }
                });
                dialog.setVisible(true);
            }
        });
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
@Override
            public void run() {
                new MainFrame1().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox examCombo;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private java.awt.Label labelExam;
    private java.awt.Label labelNoStudents;
    private java.awt.Label labelRegNo;
    private java.awt.Label labelStartRegNo;
    private javax.swing.JLabel labelStream;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JRadioButton multiStudentRadio;
    private javax.swing.JTextField noStudentsField;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTextField regNoField;
    private javax.swing.JButton retrieveButton;
    private javax.swing.JButton settingsButton;
    private javax.swing.JRadioButton singleStudentRadio;
    private javax.swing.JTextField startRegNoField;
    private javax.swing.JComboBox streamCombo;
    // End of variables declaration//GEN-END:variables
}
