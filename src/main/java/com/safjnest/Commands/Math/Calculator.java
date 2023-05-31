package com.safjnest.Commands.Math;


import java.util.Stack;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.Commands.CommandsHandler;

/**
 * This class is used to calculate the result of a mathematical expression.
 * <p>There is one way to use it:</p>
 * By typing <code>%calc</code> followed by the expression you want to calculate.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.2.5
 */
public class Calculator extends Command{

    public Calculator(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        String command = event.getArgs();
        event.reply(String.valueOf(evaluateExpression(command)));
    }

    public static double evaluateExpression(String expression) {
        expression = addSpaces(expression);
        String[] tokens = expression.split("\\s+");
        Stack<Double> numbers = new Stack<Double>();
        Stack<String> operators = new Stack<String>();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    String op = operators.pop();
                    numbers.push(applyOperation(a, b, op));
                }
                if (!operators.isEmpty()) {
                    operators.pop(); // rimuove la parentesi aperta
                } else {
                    throw new IllegalArgumentException("Parentesi non corrette: " + expression);
                }
            } else if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                while (!operators.isEmpty() && hasPrecedence(token, operators.peek())) {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    String op = operators.pop();
                    numbers.push(applyOperation(a, b, op));
                }
                operators.push(token);
            } else if (token.equals("ln") || token.equals("cos") || token.equals("sin")) {
                String subEquation = "";
                int firstI = i;
                int contPar = 0;
                for (int j = i + 1; j < tokens.length; j++) {
                    if (tokens[j].equals("("))
                        contPar++;
                    else if (tokens[j].equals(")"))
                        contPar--;
                    if (contPar == 0) {
                        for (int k = i + 2; k < j; k++)
                            subEquation += tokens[k];
                        i = j;
                        break;
                    }
                }
                subEquation = addSpaces(subEquation);
                double subValue = evaluateExpression(addSpaces(subEquation));
                switch (tokens[firstI]) {
                    case "ln":
                        subValue = Math.log(subValue);
                        break;
                    case "sin":
                        subValue = Math.floor(Math.sin(subValue));
                        break;
                    case "cos":
                        subValue = Math.cos(subValue);
                        break;

                    default:
                        break;
                }
                numbers.push(subValue);
                tokens[i] = String.valueOf(subValue);
            } else if (isNumber(token)) {
                numbers.push(Double.parseDouble(token));
            }
        }

        while (!operators.isEmpty()) {
            String op = operators.pop();
            if (op.equals("(")) {
                throw new IllegalArgumentException("Parentesi non corrette: " + expression);
            }
            double b = numbers.pop();
            double a = numbers.pop();
            numbers.push(applyOperation(a, b, op));
        }

        return numbers.pop();
    }

    public static String addSpaces(String expression) {
        String result = "";
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if ("+-*/()".indexOf(c) != -1) {
                result += " " + c + " ";
            } else if (i + 2 <= expression.length() && expression.substring(i, i+2).equals("ln")) {
                result += "ln";
                i += 1;
            } else if (i + 3 <= expression.length() && expression.substring(i, i+3).equals("sin")) {
                result += "sin";
                i += 2;
            } else if (i + 3 <= expression.length() && expression.substring(i, i+3).equals("cos")) {
                result += "cos";
                i += 2;
            } else if (c == 'e') {
                result += " " + Math.E + " ";
            } else if (i+1 < expression.length() && expression.substring(i, i+2).equals("pi")) {
                result += " " + Math.PI + " ";
                i++;
            } else {
                result += c;
            }
        }
        return result.trim().replaceAll("\\s+", " ");
    }

    public static boolean hasPrecedence(String op1, String op2) {
        if (op2.equals("(") || op2.equals(")")) {
            return false;
        }
        if ((op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"))) {
            return false;
        }
        return true;
    }

    public static double applyOperation(double a, double b, String op) {
        if (op.equals("+")) {
            return a + b;
        } else if (op.equals("-")) {
            return a - b;
        } else if (op.equals("*")) {
            return a * b;
        } else if (op.equals("/")) {
            return a / b;
        } else if (op.equals("sin")) {
            return Math.sin(a);
        } else if (op.equals("cos")) {
            return Math.cos(a);
        } else if (op.equals("ln")) {
            return Math.log(a);
        }
        return 0;
    }

    public static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean checkParentheses(String input) {
        Stack<Character> stack = new Stack<>();
        for (char c : input.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
}