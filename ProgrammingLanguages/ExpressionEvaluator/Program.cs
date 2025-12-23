using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Reflection;
using System.Reflection.Emit;

namespace ExpressionEvaluator;

internal static class Program
{
    private static readonly Dictionary<string, long> VariableValues = new(StringComparer.OrdinalIgnoreCase);
    private static ExpressionNode? _currentExpression;
    private static List<string> _currentVariables = new();
    private static Func<long[], long>? _compiledExpression;

    private static void Main()
    {
        while (true)
        {
            Console.Write("> ");
            var line = Console.ReadLine();
            if (line == null)
                break;

            line = line.Trim();
            line = line.TrimStart('\uFEFF');
            if (line.Length == 0)
                continue;

            if (line.StartsWith("expr ", StringComparison.OrdinalIgnoreCase))
            {
                SetExpression(line[5..]);
            }
            else if (line.StartsWith("set ", StringComparison.OrdinalIgnoreCase))
            {
                SetVariable(line[4..]);
            }
            else if (string.Equals(line, "do", StringComparison.OrdinalIgnoreCase))
            {
                Execute();
            }
            else if (string.Equals(line, "exit", StringComparison.OrdinalIgnoreCase))
            {
                break;
            }
            else
            {
                Console.WriteLine("Unknown command. Use expr, set, do, or exit.");
            }
        }
    }

    private static void SetExpression(string expressionText)
    {
        try
        {
            var parser = new PrattParser(expressionText);
            _currentExpression = parser.Parse();
            _currentVariables = _currentExpression
                .CollectVariables()
                .Distinct(StringComparer.OrdinalIgnoreCase)
                .ToList();
            _compiledExpression = Compiler.BuildEvaluator(_currentExpression, _currentVariables);
            Console.WriteLine("Expression accepted.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error: {ex.Message}");
            _currentExpression = null;
            _compiledExpression = null;
            _currentVariables.Clear();
        }
    }

    private static void SetVariable(string args)
    {
        var parts = args.Split(' ', StringSplitOptions.RemoveEmptyEntries);
        if (parts.Length != 2)
        {
            Console.WriteLine("Usage: set <name> <value>");
            return;
        }

        if (!long.TryParse(parts[1], NumberStyles.Integer, CultureInfo.InvariantCulture, out var value))
        {
            Console.WriteLine("Invalid integer value.");
            return;
        }

        VariableValues[parts[0]] = value;
        Console.WriteLine($"Variable {parts[0]} set to {value}.");
    }

    private static void Execute()
    {
        if (_currentExpression == null || _compiledExpression == null)
        {
            Console.WriteLine("No expression set. Use expr <expression> first.");
            return;
        }

        foreach (var variable in _currentVariables)
        {
            if (!VariableValues.TryGetValue(variable, out _))
            {
                Console.WriteLine($"Variable {variable} is not set.");
                return;
            }
        }

        var args = new long[_currentVariables.Count];
        for (var i = 0; i < _currentVariables.Count; i++)
        {
            args[i] = VariableValues[_currentVariables[i]];
        }

        _compiledExpression(args);
    }

    public static long LogValue(string label, long value)
    {
        Console.WriteLine($"{label} = {value}");
        return value;
    }

    private abstract record ExpressionNode
    {
        public bool IsParenthesized { get; init; }
        public abstract long Precedence { get; }
        public abstract IEnumerable<string> CollectVariables();
        public abstract string Format(long parentPrecedence, bool isRightChild);
    }

    private sealed record NumberNode(long Value) : ExpressionNode
    {
        public override long Precedence => long.MaxValue;
        public override IEnumerable<string> CollectVariables() => Array.Empty<string>();
        public override string Format(long parentPrecedence, bool isRightChild)
        {
            var text = Value.ToString(CultureInfo.InvariantCulture);
            return IsParenthesized ? $"({text})" : text;
        }
    }

    private sealed record VariableNode(string Name) : ExpressionNode
    {
        public override long Precedence => long.MaxValue;
        public override IEnumerable<string> CollectVariables() => new[] { Name };
        public override string Format(long parentPrecedence, bool isRightChild)
        {
            return IsParenthesized ? $"({Name})" : Name;
        }
    }

    private sealed record UnaryNode(string Operator, ExpressionNode Operand) : ExpressionNode
    {
        public override long Precedence => 3;

        public override IEnumerable<string> CollectVariables() => Operand.CollectVariables();

        public override string Format(long parentPrecedence, bool isRightChild)
        {
            var inner = Operand.Format(Precedence, false);
            var text = Operator + inner;
            if (IsParenthesized || Precedence < parentPrecedence)
            {
                return $"({text})";
            }

            return text;
        }
    }

    private sealed record BinaryNode(string Operator, ExpressionNode Left, ExpressionNode Right, long NodePrecedence) : ExpressionNode
    {
        public override long Precedence => NodePrecedence;

        public override IEnumerable<string> CollectVariables()
        {
            foreach (var v in Left.CollectVariables())
                yield return v;
            foreach (var v in Right.CollectVariables())
                yield return v;
        }

        public override string Format(long parentPrecedence, bool isRightChild)
        {
            var left = Left.Format(Precedence, false);
            var right = Right.Format(Precedence, true);
            var text = $"{left}{Operator}{right}";
            if (IsParenthesized || Precedence < parentPrecedence || (Precedence == parentPrecedence && isRightChild))
            {
                return $"({text})";
            }

            return text;
        }
    }

    private sealed class PrattParser
    {
        private readonly string _text;
        private readonly List<Token> _tokens;
        private int _position;

        public PrattParser(string text)
        {
            _text = text;
            _tokens = Tokenize(text);
            _position = 0;
        }

        public ExpressionNode Parse()
        {
            var expr = ParseExpression(0);
            Expect(TokenKind.End);
            return expr;
        }

        private ExpressionNode ParseExpression(int precedence)
        {
            var token = NextToken();
            var left = ParsePrefix(token);

            while (precedence < CurrentPrecedence())
            {
                var op = NextToken();
                left = ParseInfix(left, op);
            }

            return left;
        }

        private ExpressionNode ParsePrefix(Token token)
        {
            return token.Kind switch
            {
                TokenKind.Number => new NumberNode(token.Number),
                TokenKind.Identifier => new VariableNode(token.Text),
                TokenKind.Plus => new UnaryNode("+", ParseExpression(PrattRules.Unary)),
                TokenKind.Minus => new UnaryNode("-", ParseExpression(PrattRules.Unary)),
                TokenKind.LParen =>
                    ParseExpressionInsideParentheses(),
                _ => throw new InvalidOperationException($"Unexpected token {token.Kind}")
            };
        }

        private ExpressionNode ParseInfix(ExpressionNode left, Token token)
        {
            return token.Kind switch
            {
                TokenKind.Plus => ParseBinary(left, "+", PrattRules.AddSub),
                TokenKind.Minus => ParseBinary(left, "-", PrattRules.AddSub),
                TokenKind.Multiply => ParseBinary(left, "*", PrattRules.MulDiv),
                TokenKind.Divide => ParseBinary(left, "/", PrattRules.MulDiv),
                _ => throw new InvalidOperationException($"Unexpected token {token.Kind}")
            };
        }

        private ExpressionNode ParseBinary(ExpressionNode left, string op, int precedence)
        {
            var right = ParseExpression(precedence);
            return new BinaryNode(op, left, right, precedence);
        }

        private ExpressionNode ParseExpressionInsideParentheses()
        {
            var expr = ParseExpression(0);
            Expect(TokenKind.RParen);
            return expr with { IsParenthesized = true };
        }

        private Token NextToken()
        {
            if (_position >= _tokens.Count)
                return new Token(TokenKind.End, string.Empty, 0);
            return _tokens[_position++];
        }

        private Token PeekToken() => _position < _tokens.Count ? _tokens[_position] : new Token(TokenKind.End, string.Empty, 0);

        private int CurrentPrecedence()
        {
            return PeekToken().Kind switch
            {
                TokenKind.Plus or TokenKind.Minus => PrattRules.AddSub,
                TokenKind.Multiply or TokenKind.Divide => PrattRules.MulDiv,
                _ => 0
            };
        }

        private void Expect(TokenKind kind)
        {
            var token = NextToken();
            if (token.Kind != kind)
            {
                throw new InvalidOperationException($"Expected {kind} but found {token.Kind} at position {token.Position} in '{_text}'");
            }
        }

        private static List<Token> Tokenize(string text)
        {
            var tokens = new List<Token>();
            var i = 0;
            while (i < text.Length)
            {
                var ch = text[i];
                if (char.IsWhiteSpace(ch))
                {
                    i++;
                    continue;
                }

                if (char.IsDigit(ch))
                {
                    var start = i;
                    while (i < text.Length && char.IsDigit(text[i]))
                        i++;
                    var numberText = text[start..i];
                    var value = long.Parse(numberText, CultureInfo.InvariantCulture);
                    tokens.Add(new Token(TokenKind.Number, numberText, value, start));
                    continue;
                }

                if (char.IsLetter(ch))
                {
                    var start = i;
                    while (i < text.Length && char.IsLetterOrDigit(text[i]))
                        i++;
                    var name = text[start..i];
                    tokens.Add(new Token(TokenKind.Identifier, name, 0, start));
                    continue;
                }

                tokens.Add(ch switch
                {
                    '+' => new Token(TokenKind.Plus, "+", 0, i++),
                    '-' => new Token(TokenKind.Minus, "-", 0, i++),
                    '*' => new Token(TokenKind.Multiply, "*", 0, i++),
                    '/' => new Token(TokenKind.Divide, "/", 0, i++),
                    '(' => new Token(TokenKind.LParen, "(", 0, i++),
                    ')' => new Token(TokenKind.RParen, ")", 0, i++),
                    _ => throw new InvalidOperationException($"Unexpected character '{ch}' at position {i}")
                });
            }

            tokens.Add(new Token(TokenKind.End, string.Empty, 0, text.Length));
            return tokens;
        }

        private readonly record struct Token(TokenKind Kind, string Text, long Number, int Position = 0);

        private enum TokenKind
        {
            Number,
            Identifier,
            Plus,
            Minus,
            Multiply,
            Divide,
            LParen,
            RParen,
            End
        }
    }

    private static class PrattRules
    {
        public const int AddSub = 1;
        public const int MulDiv = 2;
        public const int Unary = 3;
    }

    private static class Compiler
    {
        private static readonly MethodInfo LogMethod =
            typeof(Program).GetMethod(nameof(LogValue), BindingFlags.Static | BindingFlags.Public | BindingFlags.NonPublic)
            ?? throw new InvalidOperationException("LogValue method not found via reflection.");

        public static Func<long[], long> BuildEvaluator(ExpressionNode root, List<string> variables)
        {
            var method = new DynamicMethod(
                "EvalExpr",
                typeof(long),
                new[] { typeof(long[]) },
                typeof(Program).Module,
                true);

            var il = method.GetILGenerator();
            var temp = il.DeclareLocal(typeof(long));

            EmitNode(root, il, temp, variables);
            il.Emit(OpCodes.Ret);

            return (Func<long[], long>)method.CreateDelegate(typeof(Func<long[], long>));
        }

        private static void EmitNode(ExpressionNode node, ILGenerator il, LocalBuilder temp, List<string> variables)
        {
            switch (node)
            {
                case NumberNode number:
                    il.Emit(OpCodes.Ldc_I8, number.Value);
                    break;
                case VariableNode variable:
                    var index = variables.IndexOf(variable.Name);
                    il.Emit(OpCodes.Ldarg_0);
                    il.Emit(OpCodes.Ldc_I4, index);
                    il.Emit(OpCodes.Ldelem_I8);
                    break;
                case UnaryNode unary:
                    EmitNode(unary.Operand, il, temp, variables);
                    il.Emit(unary.Operator switch
                    {
                        "+" => OpCodes.Nop,
                        "-" => OpCodes.Neg,
                        _ => throw new InvalidOperationException($"Unsupported unary operator {unary.Operator}")
                    });
                    EmitLog(unary.Format(0, false), il, temp);
                    break;
                case BinaryNode binary:
                    EmitNode(binary.Left, il, temp, variables);
                    EmitNode(binary.Right, il, temp, variables);
                    il.Emit(binary.Operator switch
                    {
                        "+" => OpCodes.Add,
                        "-" => OpCodes.Sub,
                        "*" => OpCodes.Mul,
                        "/" => OpCodes.Div,
                        _ => throw new InvalidOperationException($"Unsupported operator {binary.Operator}")
                    });
                    EmitLog(binary.Format(0, false), il, temp);
                    break;
                default:
                    throw new InvalidOperationException("Unknown expression node.");
            }
        }

        private static void EmitLog(string label, ILGenerator il, LocalBuilder temp)
        {
            il.Emit(OpCodes.Stloc, temp);
            il.Emit(OpCodes.Ldstr, label);
            il.Emit(OpCodes.Ldloc, temp);
            il.Emit(OpCodes.Call, LogMethod);
        }
    }
}
