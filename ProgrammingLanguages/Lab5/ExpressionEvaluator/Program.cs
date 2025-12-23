using System.Globalization;
using ExpressionEvaluator.Nodes;

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

            if (line.StartsWith("expr", StringComparison.OrdinalIgnoreCase))
            {
                var exprText = line.Length > 4 ? line[4..].TrimStart() : string.Empty;
                if (exprText.Length == 0)
                {
                    Console.WriteLine("Usage: expr <expression>");
                    continue;
                }

                SetExpression(exprText);
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
}
