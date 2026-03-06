const std = @import("std");

fn f(x: f64) f64 {
    return x*x*x + 2.64*x*x - 5.41*x - 11.76;
}


fn bisection(a: f64, b: f64, eps: f64, i: usize) void {
    if (@abs(b - a) < eps) {
        std.debug.print("Answer: {d:.3}\nIterations: {}\n", .{(a+b)/2, i});
        return;
    }

    if (f(a) * f((a + b) / 2) <= 0)
        bisection(a, (a + b) / 2, eps, i + 1)
    else
        bisection((a + b) / 2, b, eps, i + 1);
}

fn secant(prev: f64, x: f64, eps: f64, i: usize) void {
    if (@abs(x - f(x)*(x - prev)/(f(x) - f(prev)) - x) < eps) {
        std.debug.print("Answer: {d:.3}\nIterations: {}\n", .{x - f(x)*(x - prev)/(f(x) - f(prev)), i});
        return;
    }
    secant(x, x - f(x)*(x - prev)/(f(x) - f(prev)), eps, i + 1);
}

fn simple(x: f64, eps: f64, i: usize) void {
    if (@abs(std.math.cbrt(11.76 + 5.41*x - 2.64*x*x) - x) < eps) {
        std.debug.print("Answer: {d:.3}\nIterations: {}\n", .{std.math.cbrt(11.76 + 5.41*x - 2.64*x*x), i});
        return;
    }
    simple(std.math.cbrt(11.76 + 5.41*x - 2.64*x*x), eps, i + 1);
}

fn newton(x: f64, y: f64, eps: f64, i: usize) void {

    if (@abs((-(std.math.sin(y + 2) - x - 1.5)*1 - (y + std.math.cos(x - 2) - 0.5)*std.math.cos(y + 2)) / ((-1)*1 - std.math.cos(y + 2)*(-std.math.sin(x - 2)))) < eps 
    and
    @abs(((-1)*-(y + std.math.cos(x - 2) - 0.5) - std.math.sin(x - 2)*-(std.math.sin(y + 2) - x - 1.5)) / ((-1)*1 - std.math.cos(y + 2)*(-std.math.sin(x - 2)))) < eps) {
        std.debug.print("x = {d:.3}, y = {d:.3}\nIterations: {}\n", .{x, y, i});
        return;
    }

    newton(
        x + ((-(std.math.sin(y + 2) - x - 1.5)*1 - (y + std.math.cos(x - 2) - 0.5)*std.math.cos(y + 2)) / ((-1)*1 - std.math.cos(y + 2)*(-std.math.sin(x - 2)))),
        y + (((-1)*-(y + std.math.cos(x - 2) - 0.5) - std.math.sin(x - 2) * - (std.math.sin(y + 2) - x - 1.5)) / ((-1)*1 - std.math.cos(y + 2)*(-std.math.sin(x - 2)))),
        eps,
        i + 1
    );
}

pub fn main() !void {

    std.debug.print("1 - Half-division method\n4 - Secant\n5 - Simple Iteration\n6 - Newton system\n", .{});

    switch ((try std.io.getStdIn().reader().readByte()) - '0') {
        1 => bisection(-3, 0, 0.0001, 0),
        4 => secant(-3, -32, -0.0001, 0),
        5 => simple(-2, 0.00001, 0),
        6 => newton(0, 0, 0.01, 0),
        else => std.debug.print("Incorrect choice\n", .{}),
    }
}