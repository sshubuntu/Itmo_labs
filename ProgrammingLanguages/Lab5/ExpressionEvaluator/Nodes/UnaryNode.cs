namespace ExpressionEvaluator.Nodes;

public sealed record UnaryNode(string Operator, ExpressionNode Operand) : ExpressionNode
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