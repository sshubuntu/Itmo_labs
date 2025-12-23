using System.Globalization;

namespace ExpressionEvaluator.Nodes;

public  record NumberNode(long Value) : ExpressionNode
{
    public override long Precedence => long.MaxValue;
    public override IEnumerable<string> CollectVariables() => Array.Empty<string>();
    public override string Format(long parentPrecedence, bool isRightChild)
    {
        var text = Value.ToString(CultureInfo.InvariantCulture);
        return IsParenthesized ? $"({text})" : text;
    }
}