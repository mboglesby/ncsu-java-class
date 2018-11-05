
public interface TestingCalculator
{
public String       evaluate(String expression,
		                     String forX)
                             throws IllegalArgumentException;
public ArraysHolder evaluate(String expression, 
		                     String forX,
		                     String toX,
		                     String byX)
                             throws IllegalArgumentException;
}
