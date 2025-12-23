using System.Reflection;
using System.Reflection.Emit;
using ExpressionEvaluator.Nodes;

namespace ExpressionEvaluator;

public static class Compiler
    {
        private static readonly MethodInfo LogMethod = typeof(Compiler).GetMethod(nameof(LogValue), BindingFlags.Static | BindingFlags.NonPublic | BindingFlags.Public) ?? throw new InvalidOperationException("LogValue method not found for logging.");

        public static Func<long[], long> BuildEvaluator(ExpressionNode root, List<string> variables)
        {
            DynamicMethod method = new DynamicMethod("EvalExpr", typeof(long), new[] { typeof(long[]) }, typeof(Program).Module, true);
            ILGenerator il = method.GetILGenerator();
            LocalBuilder temp = il.DeclareLocal(typeof(long));
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
        
        private static long LogValue(string label, long value)
        {
            Console.WriteLine($"{label} = {value}");
            return value;
        }
    }