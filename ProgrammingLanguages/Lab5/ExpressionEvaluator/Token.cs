namespace ExpressionEvaluator;

public record struct Token(TokenKind Kind, string Text, long Number, int Position = 0);
