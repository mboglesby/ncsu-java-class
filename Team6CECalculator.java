
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

public class Team6CECalculator implements ActionListener, TestingCalculator
{

	public static void main(String[] args)
	{
	new Team6CECalculator();
	}

	
private JFrame window = new JFrame("Team6 COMPLEX EXPRESSION CALCULATOR       Operators are + - * / ^ and r        Operands are numbers, x, e, and pi");
private JButton clearButton = new JButton("Clear");
private JButton recallButton = new JButton("Recall");
private JTextField resultTextField = new JTextField(16);
private JTextField expressionTextField = new JTextField(32);
private JTextArea logTextArea = new JTextArea();
private JScrollPane logScrollPane = new JScrollPane(logTextArea);
private JTextField errorTextField = new JTextField(32);
private JLabel xLabel = new JLabel("for x = ", SwingConstants.RIGHT);
private JTextField xTextField = new JTextField(8);
private JButton expressionButton = new JButton("Expression Mode");
private String newLineCharacter = System.getProperty("line.separator");
private String expressionInstructions  = "Please enter a mathematical expression.";
private int recallIndex = 0;
private Vector<String> forXRecall = new Vector<String>(0);
private Vector<String> expressionRecall = new Vector<String>(0);


public Team6CECalculator() 
	{

	// Build the GUI
	JPanel topPanel = new JPanel();
	topPanel.add(clearButton);
	topPanel.add(resultTextField);
	topPanel.add(expressionTextField);
	topPanel.add(xLabel);
	topPanel.add(xTextField);
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
    expressionButton.addActionListener(this);
    
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Calendar cal = Calendar.getInstance();
    logTextArea.append(dateFormat.format(cal.getTime()));
    logTextArea.setCaretPosition(logTextArea.getDocument().getLength());

    errorTextField.setText(expressionInstructions);
    expressionTextField.requestFocus();
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
	   recallIndex--;
	   return;
	   }
	
	if ((ae.getSource() == expressionTextField) 
	 || (ae.getSource() == xTextField))	
	   {
	   try { 
		      float result = 0.0f;
		      String result1 = Float.toString(result);
		      result1 = evaluate(expressionTextField.getText().replaceAll(" ", "").trim(),xTextField.getText());
		      resultTextField.setText(result1);
			  logTextArea.append(newLineCharacter + expressionTextField.getText() + " = " +  resultTextField.getText());
	    	  logTextArea.append(" for x = " + xTextField.getText());
              logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
              expressionRecall.addElement(new String(expressionTextField.getText()));
              forXRecall.addElement(new String(xTextField.getText()));
              recallIndex = expressionRecall.size();
 	       }
	   catch(IllegalArgumentException iae)
	       {
		   errorTextField.setText(iae.getMessage());
	       }
	   }
	}

public String evaluate(String expression, String forX) throws IllegalArgumentException {
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
 
   	float xValue = 0.0f;	
   	if (forX != null) {
		forX = forX.trim();
		if (forX.length() != 0) {
         	if (forX.equalsIgnoreCase("e"))  
         		xValue = (float)E;
    		else if (forX.equalsIgnoreCase("-e")) 
    			xValue = (float)-E;
    		else if (forX.equalsIgnoreCase("pi")) 
    			xValue = (float)PI;
    		else if (forX.equalsIgnoreCase("-pi"))
    			xValue = (float)-PI;
    		else
            {
            	try {
                	xValue = Float.parseFloat(forX);
                } catch(NumberFormatException nfe) {
                	throw new IllegalArgumentException("Bad x value");
                }
            }
        }
    }
   	
    expression = evaluateForParenthesis(expression, forX);
    
    int numOp = 0;
    int currOpIndx = 0;
    for (int i=0; i<expression.length(); ) {
    	currOpIndx = i+Operator(expression.substring(i));
    	if (currOpIndx == expression.length()) {
    		break;
    	}
    	else {
    		numOp++;
    		i = currOpIndx + 1;
    	}
    }
    
    if (numOp == 1) {
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
        return resultString;
    }
    
    expression = evaluateForOp(expression, forX, '^', 'R');
    expression = evaluateForOp(expression, forX, '*', '/');
    expression = evaluateForOp(expression, forX, '+', '-');
    
    return expression;
}


public String evaluateForParenthesis(String expression, String forX) {
	for (int i=0; i<expression.length(); i++) {
 		if (expression.charAt(i) == '(') {
 			int numParenthesis = 1;
 			for (int j=i+1 ; j<expression.length(); j++) {
 				if (expression.charAt(j) == '(') {
 					numParenthesis++;
 				}
 				if (expression.charAt(j) == ')') {
 					if (numParenthesis == 1) {
 						if (j != expression.length()-1) {
 							if (i==0) {
 								expression = evaluate(expression.substring(i+1,j), forX) + expression.substring(j+1);
 							}
 							else {
 								expression = expression.substring(0, i) + evaluate(expression.substring(i+1,j), forX) + expression.substring(j+1);
 	 						}
 						}
 						else {
 							if (i==0) {
 								expression = evaluate(expression.substring(i+1,j), forX);
 							}
 							else {
 								expression = expression.substring(0, i) + evaluate(expression.substring(i+1,j), forX);
 							}
 						}
 						j = expression.length();
 					}
 					else {
 						numParenthesis--;
 					}
 				}
 			}
 			if (numParenthesis != 1) {
 				throw new IllegalArgumentException("Unmatched parenthesis");
 			}
 		}
 	}
	return expression;
}


public String evaluateForOp(String expression, String forX, char op1, char op2){	// * & /
	int lastOpIndx = -1;
	int currOpIndx = 0;
	int nextOpIndx = 0;
	for (int i=0; i<expression.length(); ) {
		currOpIndx = i+Operator(expression.substring(i));
		if (currOpIndx == expression.length()) {
			break;
		}
		nextOpIndx = (currOpIndx+1)+Operator(expression.substring(currOpIndx+1));
		if ((expression.charAt(currOpIndx) == op1) || (expression.charAt(currOpIndx) == op2)) {
			if (lastOpIndx == -1) {
				if (nextOpIndx >= expression.length()) {
					expression = evaluate(expression.substring(0, nextOpIndx), forX);
					i = expression.length();
				}
				else {
					expression = evaluate(expression.substring(0, nextOpIndx), forX) + expression.substring(nextOpIndx);
					lastOpIndx = -1;
					i=0;
				}
			}
			else {
				if (nextOpIndx >= expression.length()) {
					expression = expression.substring(0, lastOpIndx+1) + evaluate(expression.substring(lastOpIndx+1, nextOpIndx), forX);
					i = expression.length();
				}
				else {
					expression = expression.substring(0, lastOpIndx+1) + evaluate(expression.substring(lastOpIndx+1, nextOpIndx), forX) + expression.substring(nextOpIndx);
				}
			}
		}
		else {
			lastOpIndx = currOpIndx;
    		i = currOpIndx + 1;
		}
	}
	return expression;
}


public String evaluate_deprecated(String expression, String forX) throws IllegalArgumentException
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
 
   float xValue = 0.0f;	
   if (forX != null);
      {
    	  forX = forX.trim();
      if (forX.length() != 0);
         {
         if (forX.equalsIgnoreCase("e"))  xValue = (float)E;
    else if (forX.equalsIgnoreCase("-e")) xValue = (float)-E;
    else if (forX.equalsIgnoreCase("pi")) xValue = (float)PI;
    else if (forX.equalsIgnoreCase("-pi"))xValue = (float)-PI;
    else
            {
            try {
                xValue = Float.parseFloat(forX);
                }
            catch(NumberFormatException nfe)
                {
                throw new IllegalArgumentException("Bad x value");
                }
            }
         }
      }
   
	int operatorNum = Operator(expression);
	char operator = expression.charAt(operatorNum);
	String leftOperandString = expression.substring(0,operatorNum).trim(); 
	expression = expression.substring(operatorNum+1).trim();
	float leftOperandValue = convertOperand(leftOperandString, xValue);
	float rightOperandValue = 0;
      
	try {
		rightOperandValue = convertOperand(expression, xValue);
		float result = evaluateSimpleExpression(leftOperandValue,operator,rightOperandValue);
		String result1 = Float.toString(result);
		return result1;
		}
	catch (NumberFormatException nfe) {}
	
	while (true)
 	  	{
        operatorNum = Operator(expression);
        String rightOperandString = expression.substring(0,operatorNum).trim();
        rightOperandValue = convertOperand(rightOperandString, xValue);
        leftOperandValue  = evaluateSimpleExpression(leftOperandValue, operator, rightOperandValue);
        operator = expression.charAt(operatorNum);
        expression = expression.substring(operatorNum+1).trim();
        try { 
	        rightOperandValue = convertOperand(expression, xValue);
            float result = evaluateSimpleExpression(leftOperandValue,operator,rightOperandValue);
    		DecimalFormat formatter = new DecimalFormat("###.##");
    		formatter.setRoundingMode(RoundingMode.DOWN);
    		String resultString = formatter.format(result);
    		return resultString;
            }
        catch (NumberFormatException nfe)
            { 
            }
 	  	}
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
   /*
   if (expression.startsWith("- "))
	   throw new IllegalArgumentException("negative unary operator should not be followed by a blank");
   if (((i == 0) && !expression1)
	|| ((i == 1) &&  expression1))   
       throw new IllegalArgumentException("Missing left operand");
   if (i == expression.length()-1)
       throw new IllegalArgumentException("Missing right operand");
   if (i == expression.length())
       throw new IllegalArgumentException("No operator");
   */
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
       throw new NumberFormatException("invalid operand or unmatched parenthesis");
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

@Override
public ArraysHolder evaluate(String expression, String forX, String toX,
		String byX) throws IllegalArgumentException {
	// TODO Auto-generated method stub
	return null;
}
}



