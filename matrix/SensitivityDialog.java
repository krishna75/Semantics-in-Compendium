/*
 * SensitivityDialog.java
 *
 * Created on 31 July 2007, 13:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;



/**
 * This class holds information to be used in a sensitivity analysis
 *
 * @author Simon Skrzypcak
 */
import com.compendium.ui.dialogs.UIDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;

public class SensitivityDialog extends UIDialog implements ActionListener {
    
    private JPanel northPanelFlow;
    private JPanel northPanel;
    private JPanel northWestPanel;
    private JPanel northCenterPanel;
    private JPanel southPanel;
    
    private JLabel selectParamLabel;
    private JLabel selectRangeLabel;
    private JLabel selectSubDivideLabel;
    
    private JButton submitButton;
    private JButton cancelButton;
    
    private JTextField selectRangeValue;
    
    private JComboBox selectParameter;
    private JComboBox selectSubDivision;
    
    private Object[] parameterList;
    private Object[] subDivisionChoice = {2, 4, 6, 8, 10};
    
    private int paramPosition;
    private String parameter;
    private double range;
    private int subDiv;
    
    private final String SUBMIT = "submit";
    private final String CANCEL = "cancel";
    
    /** Creates a new instance of SensitivityDialog */
    public SensitivityDialog(JFrame parent, Object[] paramList) {
        super(parent, true);
        
        parameterList = paramList;
        this.setTitle("Enter details");
        this.setResizable(false);
        
        this.setLayout(new BorderLayout());
        
//      North Panel
        northPanelFlow = new JPanel();
        northPanelFlow.setLayout(new FlowLayout(0, 5, 5));
        
        northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        
        northWestPanel = new JPanel();
        northWestPanel.setLayout(new GridLayout(3, 0, 5, 5));
        
        selectParamLabel = new JLabel("Select the paramter:");
        selectRangeLabel = new JLabel("Enter the range of variation as a %:  +/-");
        selectSubDivideLabel = new JLabel("Select the number of sub-divisions:");
        
        northWestPanel.add(selectParamLabel);
        northWestPanel.add(selectRangeLabel);
        northWestPanel.add(selectSubDivideLabel);
        
        northCenterPanel = new JPanel();
        northCenterPanel.setLayout(new GridLayout(3, 0, 5, 5));
        
        selectParameter = new JComboBox(paramList);
        selectRangeValue = new JTextField();
        selectSubDivision = new JComboBox(subDivisionChoice);
        
        northCenterPanel.add(selectParameter);
        northCenterPanel.add(selectRangeValue);
        northCenterPanel.add(selectSubDivision);
        
        northPanel.add("West", northWestPanel);
        northPanel.add("Center", northCenterPanel);
        
        northPanelFlow.add(northPanel);
        
//      South Panel
        southPanel = new JPanel();
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        submitButton.setActionCommand(SUBMIT);
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand(CANCEL);
        
        southPanel.add(submitButton);
        southPanel.add(cancelButton);
        
        this.add("North", northPanelFlow);
        this.add("South", southPanel);
        
        this.pack();
        
        this.setVisible(true);
    }
    
//  Return the parameter that is the focus of the sensitivity analysis  
    public String getParameter() {
        return parameter;
    }
//  Set the parameter that is the focus of the sensitivity analysis
    public void setParameter(String param) {
        parameter = param;
    }
    
//  Return the position in the list of the chosen parameter  
    public int getParameterPosition() {
        return paramPosition;
    }
    
//  Set the position in the list of the chosen parameter
    public void setParameterPosition(int paramPos) {
        paramPosition = paramPos;
    }
    
//  Return the range over which the analysis is to be performed  
    public double getRange() {
        return range;
    }
    
//  Set the range over which the analysis is to be performed
    public void setRange(double ran) {
        range = ran;
    }
    
//  Return the number of sub-divisions for the analysis  
    public int getSubDiv() {
        return subDiv;
    }
    
//  Set the number of sub-divisions for the analysis
    public void setSubDiv(int sub) {
        subDiv = sub;
    }
    
//  Handle any events that occur  
    public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();
        if (comm.equals(SUBMIT)) {
//          Set the position of the parameter in the list
            paramPosition = selectParameter.getSelectedIndex();
//          Set the parameter by getting the parameter at the position given by
//          paramPosition
            parameter = String.valueOf(selectParameter.getSelectedItem());
//          Set the range over which the analysis is to be performed. Catch the
//          exception if the data input is not a number
            try {
                range = Double.parseDouble(selectRangeValue.getText());
                this.setVisible(false);
            }
            catch (NumberFormatException n) {
                JOptionPane.showMessageDialog(null, "Please enter a numeric value here!",
                        "Incorrect Number Format", JOptionPane.WARNING_MESSAGE);
            }
//          Set the number of sub-divisions that the analysis is to be performed for  
            subDiv = Integer.parseInt(String.valueOf(selectSubDivision.getSelectedItem()));
        }
//      Close the window  
        if (comm.equals(CANCEL)) {
            this.onCancel();
        }
    }
    
}
