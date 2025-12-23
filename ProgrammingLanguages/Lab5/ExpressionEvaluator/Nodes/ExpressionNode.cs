namespace ExpressionEvaluator.Nodes;
public abstract record ExpressionNode
{
    public bool IsParenthesized { get; init; }
    public abstract long Precedence { get; }
    public abstract IEnumerable<string> CollectVariables();
    public abstract string Format(long parentPrecedence, bool isRightChild);
}