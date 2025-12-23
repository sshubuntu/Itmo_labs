namespace ExpressionEvaluator.Nodes;

public record VariableNode(string Name) : ExpressionNode
{
    public override long Precedence => long.MaxValue;
    public override IEnumerable<string> CollectVariables() => new[] { Name };
    public override string Format(long parentPrecedence, bool isRightChild)
    {
        return IsParenthesized ? $"({Name})" : Name;
    }
}