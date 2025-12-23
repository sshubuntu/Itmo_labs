using System.Globalization;
using ExpressionEvaluator.Nodes;

namespace ExpressionEvaluator;

public class PrattParser
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
    }