package compiler.parser;

import compiler.scanner.Scanner;
import compiler.scanner.Tag;
import compiler.scanner.Token;
import compiler.scanner.Number;

// TODO: importar aqui a classe Stack
import java.util.Stack;

public class Parser {
    // referência a um scanner criado separadamente pelo JFlex
    private final Scanner scanner;
    // token atualmente obtido pelo Scanner
    private Token token;

    // TODO: declarar pilha para armazenar os números (Double)
    private Stack<Double> stack;


    // classe do analisador sintático
    public Parser(Scanner scanner) throws Exception {
        // se não tem um scanner, então não é possível continuar
        if (scanner == null) {
            throw new Exception("Scanner não definido! Não foi possível criar o Parser!");
        }
        // armazena a referência
        this.scanner = scanner;
        // e obtém o primeiro token
        token = scanner.yylex();
        // Aqui é para criar a pilha que vai ser iniciada na classe do parse
        stack = new Stack<>();
    }

    // função para tratar erros
    private void error(Tag tag) throws Exception {
        throw new Exception("Erro durante a análise sintática: esperava marca com tag " + tag +
                " mas recebi marca com tag " + token.tag + "\n" +
                "Linha: " + token.line + " e Coluna: " + token.column);
    }

    private void error(Tag[] tags) throws Exception {
        String msg = "Erro durante a análise sintática: esperava marcas com tags [ ";
        for (Tag t : tags) {
            msg += t;
            msg += ' ';
        }
        msg += ']';
        msg += " mas recebi marca com tag " + token.tag + "\n" +
                "Linha: " + token.line + " e Coluna: " + token.column;
        throw new Exception(msg);
    }

    // TODO: executar uma operação binária com dois númneros
    // de acordo com o TAG do parâmetro e retornar seu resultado
    

    // função que verifica a marca atual e avança na entrada
    private void accept(Tag tag) throws Exception {
        // Se o token atual é o que se está esperando
        if (token.tag == tag) {
            // avança um token na entrada
            token = scanner.yylex();
        } else {
            // gera uma exceção
            error(tag);
        }
    }

    // função que inicia a análise sintática descendente
    // recursiva
    public void parse() throws Exception {
        // REGRA: goal = expr;
        expr();

        // TODO: neste ponto desempilhar e exibir o valor calculado
        double resultado = stack.pop();
        System.out.println("Resultado: " + resultado);

        // se, depois de expr() o token for EOF, tudo deu certo!
        if (token.tag == Tag.EOF) {
            System.out.println("Análise sintática terminada com sucesso!");
        } else {
            error(Tag.EOF);
        }
    }

    // REGRA: expr = term, eprime;
    private void expr() throws Exception {
        term();
        eprime();
    }

    // REGRA: eprime = { ('+' | '-'), term };
    private void eprime() throws Exception {
        while (token.tag == Tag.PLUS || token.tag == Tag.MINUS) {

            // TODO: Neste ponto, desempilhar um número
            double num1 = stack.pop();
            // TODO: Neste ponto salvar o valor token.tag
            Tag operation = token.tag;
            accept(token.tag);
            term();

            // TODO: Neste ponto, desempilhar outro número
            double num2 = stack.pop();
            // TODO: Neste ponto, executar uma operação binária e armazenar o resultado na pilha
            double result = operaçãoEmBinario (num2, num1, operation);
            stack.push(result);
        }
    }

    // REGRA: term = factor, tprime;
    private void term() throws Exception {
        factor();
        tprime();
    }

    // REGRA: tprime = { ( '*' | '/' | '%'), factor };
    private void tprime() throws Exception {
        while (token.tag == Tag.TIMES ||
                token.tag == Tag.DIV ||
                token.tag == Tag.MOD) {

            // TODO: Neste ponto, desempilhar um número
            double value1 = stack.pop();
            // TODO: Neste ponto salvar o tag
            Tag operation = token.tag;
            accept(token.tag);
            factor();

            // TODO: Neste ponto, desempilhar outro número
            double value2 = stack.pop();
            // TODO: Neste ponto, executar uma operação binária e armazenar o resultado na pilha
            double result = operaçãoEmBinario (value2, value1, operation);
            stack.push(result);
        }
    }

    // REGRA: factor = '(', expr, ')' | number;
    private void factor() throws Exception {
        switch (token.tag) {
            case LPAREN:
                accept(Tag.LPAREN);
                expr();
                accept(Tag.RPAREN);
                break;
            case NUMBER:
                // TODO: Neste ponto, empilhar um número
                stack.push(((Number) token).value);
                accept(Tag.NUMBER);
                break;
            default:
                error(new Tag[] { Tag.LPAREN, Tag.NUMBER });
                break;
        }
    }

    // EX01: Classe de operação em binario
    private Double operaçãoEmBinario (Double num1, Double num2, Tag operation) {
        switch (operation) {
            case PLUS:
                return num1 + num2;
            case MINUS:
                return num1 - num2;
            case TIMES:
                return num1 * num2;
            case DIV:
                if (num2 == 0) {
                    throw new ArithmeticException("Erro: divisão por zero");
                }
                return num1 / num2;
            case MOD:
                if (num2 == 0) {
                    throw new ArithmeticException("Erro: divisão por zero");
                }
                return num1 % num2;
            default:
                throw new IllegalArgumentException("Operação binária inválida: " + operation);
        }
    }
}
