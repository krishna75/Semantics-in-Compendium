/*
 * GoalsCriteriaPanel.java
 *
 * Created on 30 July 2007, 15:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This panel displays the Goals vs Criteria Matrix
 *
 * @author Simon Skrzypczak
 */
import com.compendium.ProjectCompendium;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class HelpPanel extends JPanel  {
    
    private JPanel centrePanel;
    private JPanel centrePanelContent;
    private JTextArea outputBox;

    public HelpPanel(String choose) throws IOException {
        
        
        centrePanel = new JPanel();
        centrePanelContent = new JPanel(new BorderLayout());

        outputBox = new JTextArea();
        outputBox.setEditable(false);
        JScrollPane scrl = new JScrollPane(outputBox);
        scrl.setPreferredSize(new Dimension(600, 800));
        
        centrePanelContent.add(scrl);
        centrePanel.add(centrePanelContent);
        


        this.setLayout(new BorderLayout());
        this.add(centrePanel, BorderLayout.CENTER);

        loadtext(choose);
    
    }

    private void loadtext(String choose) throws IOException {

        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Help/MCDM";
        String filePath = pathName + "/" + choose + ".doc";

        File f = new File(filePath);
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);

        StringBuffer sb = new StringBuffer();
        String eachLine = br.readLine();

        while(eachLine != null) {
            sb.append(eachLine);
            sb.append("\n");

            eachLine = br.readLine();
        }

        outputBox.setText(sb.toString());
    }


}