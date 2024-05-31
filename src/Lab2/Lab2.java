package Lab2;

import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lab2 {
    public static void convFunction(String[] queue) {
        String regex = "(sin|cos|tg|ctg|exp)\\((-?\\d+(\\.\\d+)?)\\)";
        Pattern pattern = Pattern.compile(regex);

        for (int i = 0; i < queue.length; i++) {
            String expression = queue[i];
            // Создаем Matcher объект для текущего выражения
            Matcher matcher = pattern.matcher(expression);
            while (matcher.find()) {
                double value = evaluateFunction(matcher.group());
                // Заменяем функцию числовым значением
                expression = expression.replaceFirst(Pattern.quote(matcher.group()), Double.toString(value));
            }
            queue[i] = expression;
        }
    }

    public static double evaluateFunction(String function) {
        // Извлекаем функцию и аргумент
        String[] parts = function.split("\\(");
        String functionName = parts[0];
        double argument = Double.parseDouble(parts[1].substring(0, parts[1].length() - 1));

        // Вычисляем значение функции и возвращаем результат
        switch (functionName) {
            case "sin":
                return Math.sin(argument);
            case "cos":
                return Math.cos(argument);
            case "tg":
                return Math.tan(argument);
            case "ctg":
                return 1.0 / Math.tan(argument);
            case "exp":
                return Math.exp(argument);
            default:
                return 0; // В случае неверной функции возвращаем 0
        }
    }

    public static double applyOperation(double operand1, double operand2, String operator) {
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero!");
                }
                return operand1 / operand2;
            case "^":
                return Math.pow(operand1, operand2);
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    public static int precedence(String operator) {
        switch (operator) {
            case "^":
                return 3;
            case "*":
            case "/":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return 0;
        }
    }

    public static void calcPostFix(String[] queue) {
        Stack<Double> stack = new Stack<>();
        // Создаем стек для работы
        // Перебераем все элементы очереди
        for (String item : queue) {
            if (item.matches("\\-?\\d+(\\.\\d+)?")) {
                // Если входящий элемент - число, то добавляем в стек
                stack.push(Double.parseDouble(item));
            } else if (item.equals("^")) {
                // Если входящий элемент ^ , берем два последних элемента и производим соответствующую операцию
                Double operand2 = stack.pop();
                Double operand1 = stack.pop();
                stack.push(Math.pow(operand1, operand2));
            } else if (item.equals("+")) {
                // Если входящий элемент + , берем два последних элемента и производим соответствующую операцию
                Double operand2 = stack.pop();
                Double operand1 = stack.pop();
                stack.push(operand1 + operand2);
            } else if (item.equals("*")) {
                // Если входящий элемент * , берем два последних элемента и производим соответствующую операцию
                Double operand2 = stack.pop();
                Double operand1 = stack.pop();
                stack.push(operand1 * operand2);
            } else if (item.equals("/")) {
                // Если входящий элемент / , берем два последних элемента и производим соответствующую операцию
                Double operand2 = stack.pop();
                Double operand1 = stack.pop();
                stack.push(operand1 / operand2);
            } else if (item.equals("-")) {
                // Если входящий элемент -, берем два последних элемента и производим соответствующую операцию
                Double operand2 = stack.pop();
                Double operand1 = stack.pop();
                stack.push(operand1 - operand2);
            }
        }

        //Удаление из десятичного числа цифры 0 после .
        String item = String.valueOf(stack.peek());
        if (item.matches("\\-?\\d+(\\.\\d+)?")) {
            // Если является, преобразуем строку в число
            double number = Double.parseDouble(item);
            System.out.print("Ответ: ");
            // Проверяем, имеется ли дробная часть и состоит ли она из одной цифры - нуля
            if (item.contains(".") && item.split("\\.")[1].equals("0")) {
                // Если после точки идет одна цифра - ноль, выводим число как целое
                System.out.println((int) number);
                System.out.println();
            } else {
                // В противном случае, выводим число с десятичной частью
                System.out.println(number);
                System.out.println();
            }
        } else {
            // Если строка не является десятичным числом, выводим сообщение об ошибке или обрабатываем исключение
            System.out.println("Неправильный формат числа");
        }
    }

    public static void calcInFix(String[] queue) {
        Stack<Double> stackOperands = new Stack<>();
        Stack<String> stackOperations = new Stack<>();


        for (String item : queue) {
            if (item.matches("\\-?\\d+(\\.\\d+)?")) {
                // Если входящий элемент - число, то добавляем в стек операндов
                stackOperands.push(Double.parseDouble(item));
            } else if (item.equals("(")) {
                // Если входящий элемент - открывающая скобка, добавляем в стек операций
                stackOperations.push(item);
            } else if (item.equals(")")) {
                // Если входящий элемент - закрывающая скобка, выполняем операции внутри скобок
                while (!stackOperations.isEmpty() && !stackOperations.peek().equals("(")) {
                    String operator = stackOperations.pop();
                    double operand2 = stackOperands.pop();
                    double operand1 = stackOperands.pop();
                    stackOperands.push(applyOperation(operand1, operand2, operator));
                }
                stackOperations.pop(); // Удаляем "(" из стека операций
            } else {
                // Если входящий элемент - оператор, обрабатываем его
                while (!stackOperations.isEmpty() && !stackOperations.peek().equals("(") && precedence(stackOperations.peek()) >= precedence(item)) {
                    String op = stackOperations.pop();
                    double operand2 = stackOperands.pop();
                    double operand1 = stackOperands.pop();
                    stackOperands.push(applyOperation(operand1, operand2, op));
                }
                stackOperations.push(item);
            }
        }

        // Выполняем оставшиеся операции в стеке операций
        while (!stackOperations.isEmpty()) {
            String operator = stackOperations.pop();
            double operand2 = stackOperands.pop();
            double operand1 = stackOperands.pop();
            stackOperands.push(applyOperation(operand1, operand2, operator));
        }

        //Удаление из десятичного числа цифры 0 после .
        String item = String.valueOf(stackOperands.peek());
        if (item.matches("\\-?\\d+(\\.\\d+)?")) {
            // Если является, преобразуем строку в число
            double number = Double.parseDouble(item);
            System.out.print("Ответ: ");
            // Проверяем, имеется ли дробная часть и состоит ли она из одной цифры - нуля
            if (item.contains(".") && item.split("\\.")[1].equals("0")) {
                // Если после точки идет одна цифра - ноль, выводим число как целое
                System.out.println((int) number);
                System.out.println();
            } else {
                // В противном случае, выводим число с десятичной частью
                System.out.println(number);
                System.out.println();
            }
        } else {
            // Если строка не является десятичным числом, выводим сообщение об ошибке или обрабатываем исключение
            System.out.println("Неправильный формат числа");
        }
    }

    public static String convInFixToPostFix(String[] list) {
        Queue<String> outputQueue = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();

        for (String item : list) {
            // Если текущий элемент - операнд, добавляем его в выходную очередь
            if (item.matches("\\-?\\d+(\\.\\d+)?")) {
                outputQueue.offer(item);
            }
            // Если текущий элемент - оператор
            else if (item.equals("^") || item.equals("+") || item.equals("*") || item.equals("/") || item.equals("-")) {
                // Пока стек не пуст и верхний оператор в стеке имеет больший или равный приоритет, чем текущий оператор
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(item)) {
                    // Извлекаем оператор из стека и добавляем его в выходную очередь
                    outputQueue.offer(operatorStack.pop());
                }
                // Добавляем текущий оператор в стек
                operatorStack.push(item);
            }
            // Если текущий элемент - открывающая скобка
            else if (item.equals("(")) {
                // Добавляем ее в стек
                operatorStack.push(item);
            }
            // Если текущий элемент - закрывающая скобка
            else if (item.equals(")")) {
                // Пока не встретим открывающую скобку, извлекаем операторы из стека и добавляем их в выходную очередь
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    outputQueue.offer(operatorStack.pop());
                }
                // Удаляем открывающую скобку из стека
                operatorStack.pop();
            }
        }

        // Добавляем оставшиеся операторы из стека в выходную очередь
        while (!operatorStack.isEmpty()) {
            outputQueue.offer(operatorStack.pop());
        }

        // Формируем строку из элементов очереди для вывода
        StringBuilder result = new StringBuilder();
        for (String element : outputQueue) {
            result.append(element).append(" ");
        }
        return result.toString().trim(); // Удаляем последний пробел и возвращаем строку
    }


    public static void main(String[] args) {

        int ans;
        String[] postfixExspression, infixExspression;

        do {
            Scanner in = new Scanner(System.in);
            System.out.println("---Меню---");
            System.out.println("    1) Вычисление инфиксного выражения");
            System.out.println("    2) Вычисленеи постфиксного выражения");
            System.out.println("    3) Перевод из инфиксной формы в постфиксную");
            System.out.println("    4) Выход\n");
            System.out.print("Выберете пунк меню: ");
            ans = in.nextInt();
            switch (ans) {
                case 1:
                    System.out.println("Пунк 1");
                    System.out.print("Введите инфиксное выражение(каждый новая цифра и символ через пробел): ");
                    in.nextLine(); //очищаем буфер
                    String inFixString = in.nextLine();
                    infixExspression = inFixString.split(" ");
                    convFunction(infixExspression);
                    calcInFix(infixExspression);
                    break;
                case 2:
                    System.out.println("Пунк 2");
                    System.out.print("Введите постфиксное выражение(каждый новая цифра и символ через пробел): ");
                    in.nextLine(); // очищаем буфер
                    String postfixstr = in.nextLine();
                    postfixExspression = postfixstr.split(" ");
                    convFunction(postfixExspression);
                    calcPostFix(postfixExspression);
                    break;
                case 3:
                    System.out.println("Пунк 3");
                    System.out.print("Введите инфиксное выражение(каждый новая цифра и символ через пробел): ");
                    in.nextLine(); // очищаем буфер
                    String infixstr = in.nextLine();
                    infixExspression = infixstr.split(" ");
                    convFunction(infixExspression);
                    System.out.println("Результат: " + convInFixToPostFix(infixExspression));
                    System.out.println();
                    break;
            }
        } while (ans != 4);
    }
}


//1) подсчет инфиксного выражения
//2) подсчет постфиксного выражения
//3) перевод из инфиксной в постфиксную
//4) унарные функции (sinx, exp(x)...)