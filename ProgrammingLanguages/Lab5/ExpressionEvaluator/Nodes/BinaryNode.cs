namespace ExpressionEvaluator.Nodes;

public record BinaryNode(string Operator, ExpressionNode Left, ExpressionNode Right, long NodePrecedence) : ExpressionNode
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