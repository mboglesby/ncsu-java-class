
import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class Team6Grapher implements ActionListener, TestingCalculator
{

	public static void main(String[] args)
	{
	new Team6Grapher();
	}

	
private JFrame window = new JFrame("CALCULATOR       Operators are + - * / ^ and r        Operands are numbers, x, e, and pi");
private JButton clearButton = new JButton("Clear");
private JButton recallButton = new JButton("Recall");
private JTextField resultTextField = new JTextField(8);
private JTextField expressionTextField = new JTextField(16);
private JTextArea logTextArea = new JTextArea();
private JScrollPane logScrollPane = new JScrollPane(logTextArea);
private JTextField errorTextField = new JTextField();
private JLabel xLabel = new JLabel("for x = ", SwingConstants.RIGHT);
private JTextField xTextField = new JTextField(8);
private JLabel toXLabel = new JLabel("to x = ", SwingConstants.RIGHT);
private JTextField toXTextField = new JTextField(8);
private JLabel byXLabel = new JLabel("in increments of x = ", SwingConstants.RIGHT);
private JTextField byXTextField = new JTextField(8);
private JButton expressionButton = new JButton("Expression Mode");
private String newLineCharacter = System.getProperty("line.separator");
private String expressionInstructions  = "Please enter a mathematical expression.";
private int recallIndex = 0;
private Vector<String> forXRecall = new Vector<String>(0);
private Vector<String> expressionRecall = new Vector<String>(0);
private Vector<String> toXRecall = new Vector<String>(0);
private Vector<String> byXRecall = new Vector<String>(0);


public Team6Grapher() 
	{

	// Build the GUI
	JPanel topPanel = new JPanel();
	topPanel.add(clearButton);
	topPanel.add(resultTextField);
	topPanel.add(expressionTextField);
	topPanel.add(xLabel);
	topPanel.add(xTextField);
	topPanel.add(toXLabel);
	topPanel.add(toXTextField);
	topPanel.add(byXLabel);
	topPanel.add(byXTextField);
	topPanel.add(recallButton);
	window.getContentPane().add(topPanel, "North");
	
    window.getContentPane().add(logScrollPane, "Center");
    
    JPanel bottomPanel = new JPanel();
	bottomPanel.add(expressionButton);
	bottomPanel.add(errorTextField);
    window.getContentPane().add(bottomPanel, "South");
    

    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setSize(1200,300);
    resultTextField.setText("0");
    xTextField.setText("0");
    logTextArea.setEditable(false); 
    resultTextField.setEditable(false);
    errorTextField.setEditable(false);
    expressionTextField.requestFocus(); 
    
 
    expressionTextField.addActionListener(this); 
    clearButton.addActionListener(this);
    recallButton.addActionListener(this);
    xTextField.addActionListener(this);
    toXTextField.addActionListener(this);
    byXTextField.addActionListener(this);
    expressionButton.addActionListener(this);
    
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Calendar cal = Calendar.getInstance();
    logTextArea.append(dateFormat.format(cal.getTime()));
    logTextArea.setCaretPosition(logTextArea.getDocument().getLength());

    errorTextField.setText(expressionInstructions);
    window.setVisible(true);
	}


public void actionPerformed(ActionEvent ae) 
	{
	errorTextField.setText("");
    errorTextField.setBackground(Color.white);
    
    if (ae.getSource() == expressionButton)
    {
    errorTextField.setText(expressionInstructions);
    return;
    }
    
	if (ae.getSource() == clearButton)
	   {
	   expressionTextField.setText("");
	   xTextField.setText("  0  ");
	   resultTextField.setText("  0  ");
	   expressionTextField.requestFocus(); // set cursor in
       recallIndex = expressionRecall.size();
	   return; 
	   }
 
	if (ae.getSource() == recallButton)
	   {
	   if ((forXRecall.size() == 0) || (expressionRecall.size() == 0) || (recallIndex <= 0))
	      {
		  errorTextField.setText("No expression to recall.");
		  return;
	      }
	   xTextField.setText(forXRecall.get(recallIndex-1));
	   expressionTextField.setText(expressionRecall.get(recallIndex-1));
	   toXTextField.setText(toXRecall.get(recallIndex-1));
	   byXTextField.setText(byXRecall.get(recallIndex-1));
	   recallIndex--;
	   return;
	   }
	
	if ((ae.getSource() == expressionTextField) 
	 || (ae.getSource() == xTextField)
	 || (ae.getSource() == toXTextField)
	 || (ae.getSource() == byXTextField))	
	   {
	   try { 
		   	  evaluate(expressionTextField.getText(),xTextField.getText(),
		   				  			toXTextField.getText(),byXTextField.getText());
		   
		      String result1 = evaluate(expressionTextField.getText(),xTextField.getText());
		      resultTextField.setText(result1);
			  logTextArea.append(newLineCharacter + expressionTextField.getText() + " = " +  resultTextField.getText());
	    	  logTextArea.append(" for x = " + xTextField.getText());
              logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
              expressionRecall.addElement(new String(expressionTextField.getText()));
              forXRecall.addElement(new String(xTextField.getText()));
              toXRecall.addElement(new String(toXTextField.getText()));
              byXRecall.addElement(new String(byXTextField.getText()));
              recallIndex = expressionRecall.size();
 	       }
	   catch(IllegalArgumentException iae)
	       {
		   errorTextField.setText(iae.getMessage());
	       }
	   }
	}

public String evaluate(String expression, String forX) throws IllegalArgumentException
   {
   if ((expression == null)||(expression.length() == 0))
       throw new IllegalArgumentException("Check your expression.");
   if (expression.contains("R"))
	   expression.replace("R", "r");
   if (expression.contains("X"))
	   expression.replace("X", "x");
   if (expression.contains("E"))
	   expression.replace("E", "e");
   if (expression.contains("PI"))
	   expression.replace("PI", "pi");
   if ((forX.trim().equals("")) && !expression.contains("x"))
	   forX = "0";
 
   	float xValue;
   	try {
		xValue = findXValue(forX);
	}
	catch (IllegalArgumentException iae) {
		throw new IllegalArgumentException("Bad x value");
	}

   int operatorNum = Operator(expression);
   char operator = expression.charAt(operatorNum);
   String leftOperandString = expression.substring(0,operatorNum).trim(); 
   expression = expression.substring(operatorNum+1).trim();
   float leftOperandValue = convertOperand(leftOperandString, xValue);
   float rightOperandValue = convertOperand(expression, xValue);
   float result = evaluateSimpleExpression(leftOperandValue,operator,rightOperandValue);
   DecimalFormat formatter = new DecimalFormat("###.##");
   formatter.setRoundingMode(RoundingMode.DOWN);
   String resultString = formatter.format(result);
   //String result1 = Float.toString(result);
   return resultString;
   }

@Override
public ArraysHolder evaluate(String expression, String forX, String toX,
		String byX) throws IllegalArgumentException {
	if ((expression == null)||(expression.length() == 0))
		throw new IllegalArgumentException("Check your expression.");
	if (expression.contains("R"))
		expression.replace("R", "r");
	if (expression.contains("X"))
		expression.replace("X", "x");
	if (expression.contains("E"))
		expression.replace("E", "e");
	if (expression.contains("PI"))
		expression.replace("PI", "pi");
	if ((forX.trim().equals("")) && !expression.contains("x"))
		forX = "0";
	if (toX.trim().equals("") || byX.trim().equals("")) {
		toX = forX;
		byX = "0";
	}
		
	
	ArraysHolder resultAH = new ArraysHolder();
	float forXValue;
	float toXValue;
	float byXValue;
	
	try {
		forXValue = findXValue(forX);
	}
	catch (IllegalArgumentException iae) {
		throw new IllegalArgumentException("Bad 'for x' value");
	}
	
	try {
		toXValue = findXValue(toX);
	}
	catch (IllegalArgumentException iae) {
		throw new IllegalArgumentException("Bad 'to x' value");
	}
	
	try {
		byXValue = findXValue(byX);
	}
	catch (IllegalArgumentException iae) {
		throw new IllegalArgumentException("Bad 'by x' value");
	}
	
	int i=0;
	for (float currXValue = forXValue; currXValue<=toXValue; currXValue+=byXValue)
		i++;
	
	resultAH.expressionValues = new double[i];
	resultAH.xValues = new double[i];
	
	i=0;
	for (float currXValue = forXValue; currXValue<=toXValue; currXValue+=byXValue) {
		int operatorNum = Operator(expression);
		char operator = expression.charAt(operatorNum);
		String leftOperandString = expression.substring(0,operatorNum).trim(); 
		String rightOperandString = expression.substring(operatorNum+1).trim();
		float leftOperandValue = convertOperand(leftOperandString, currXValue);
		float rightOperandValue = convertOperand(rightOperandString, currXValue);
		float result = evaluateSimpleExpression(leftOperandValue,operator,rightOperandValue);
		resultAH.expressionValues[i] = result;
		resultAH.xValues[i] = currXValue;
		i++;
	}
	
	new Team6GraphWindow(expression,this,resultAH);
	return resultAH;
}

private float findXValue(String x) {
	float xValue = 0.0f;	
	if (x != null);
	{
		x = x.trim();
		if (x.length() != 0);
		{
			if (x.equalsIgnoreCase("e"))  xValue = (float)E;
			else if (x.equalsIgnoreCase("-e")) xValue = (float)-E;
			else if (x.equalsIgnoreCase("pi")) xValue = (float)PI;
			else if (x.equalsIgnoreCase("-pi"))xValue = (float)-PI;
			else
			{
				try {
					xValue = Float.parseFloat(x);
				}
				catch(NumberFormatException nfe)
				{
					throw new IllegalArgumentException("Bad x value");
				}
			}
		}
	}
	return xValue;
}

private int Operator(String expression) throws IllegalArgumentException
   {
   int     i;
   boolean expression1;
   if (expression.startsWith("-"))
      {
	  i = 1;
	  expression1 = true;
      }
    else
      {
	  i = 0;
	  expression1 = false;
      }
   for (; i < expression.length(); i++)
       {
       if ((expression.charAt(i) == '+')	
        || (expression.charAt(i) == '-')	   
        || (expression.charAt(i) == '*')	   
        || (expression.charAt(i) == '/')	   
        || (expression.charAt(i) == '^')	   
        || (expression.charAt(i) == 'r')
        || (expression.charAt(i) == 'R'))	   
            break;
       }
   if (expression.startsWith("- "))
	   throw new IllegalArgumentException("negative unary operator should not be followed by a blank");
   if (((i == 0) && !expression1)
	|| ((i == 1) &&  expression1))   
       throw new IllegalArgumentException("Missing left operand");
   if (i == expression.length()-1)
       throw new IllegalArgumentException("Missing right operand");
   if (i == expression.length())
       throw new IllegalArgumentException("No operator");
   return i;
   }


private float convertOperand(String operand, float xValue) throws NumberFormatException
   {
   //if (operand.contains("x") || operand.contains("X")) 
   if (operand.equalsIgnoreCase("e"))  return (float)E;
   if (operand.equalsIgnoreCase("-e")) return (float)-E;
   if (operand.equalsIgnoreCase("pi")) return (float)PI;
   if (operand.equalsIgnoreCase("-pi"))return (float)-PI;
   if (operand.equalsIgnoreCase("x"))  return xValue;
   if (operand.equalsIgnoreCase("-x")) return -xValue;
   try {
       return Float.parseFloat(operand);
       }
   catch(NumberFormatException nfe)
       {
       throw new NumberFormatException("operand " + operand + " is not numeric");
       }
   }

private float evaluateSimpleExpression(float leftOperand, char operator, float rightOperand)throws IllegalArgumentException
   {
   switch(operator)
      {
      case '+': return leftOperand + rightOperand;
      case '-': return leftOperand - rightOperand;
      case '*': return leftOperand * rightOperand;
      case '/': return leftOperand / rightOperand;
      case '^': return (float) pow(leftOperand, rightOperand);
      case 'r': 
      case 'R': return (float) pow(leftOperand, 1/rightOperand);
      default:  throw new IllegalArgumentException("Operator is not + - * / ^ or r.");
      }

   }
}



