from pathlib import Path
import math


def parse_points(text: str) -> tuple[list[float], list[float]]:
    xs: list[float] = []
    ys: list[float] = []
    for idx, raw_line in enumerate(text.splitlines(), start=1):
        line = raw_line.strip()
        if not line:
            continue
        parts = line.replace(",", ".").split()
        if len(parts) != 2:
            raise ValueError(f"Строка {idx}: нужно вводить ровно два числа: x y.")
        try:
            x_val = float(parts[0])
            y_val = float(parts[1])
        except ValueError as exc:
            raise ValueError(f"Строка {idx}: неверный формат числа.") from exc
        xs.append(x_val)
        ys.append(y_val)
    if len(xs) < 2:
        raise ValueError("Нужно минимум две точки.")
    if len(set(xs)) != len(xs):
        raise ValueError("Значения x должны быть уникальны.")
    pairs = sorted(zip(xs, ys), key=lambda p: p[0])
    return [p[0] for p in pairs], [p[1] for p in pairs]


def read_text_file(path: str) -> str:
    return Path(path).read_text(encoding="utf-8")


def generate_function_points(name: str, x_left: float, x_right: float, count: int) -> tuple[list[float], list[float]]:
    if count < 2:
        raise ValueError("Количество точек должно быть не меньше 2.")
    if x_left >= x_right:
        raise ValueError("Левая граница должна быть меньше правой.")

    functions = {
        "sin(x)": math.sin,
        "cos(x)": math.cos,
        "exp(x)": math.exp,
    }
    if name not in functions:
        raise ValueError("Неизвестная функция.")
    f = functions[name]

    step = (x_right - x_left) / (count - 1)
    xs = [x_left + i * step for i in range(count)]
    ys = [f(x) for x in xs]
    return xs, ys


def variant18_table() -> tuple[list[float], list[float]]:
    x_vals = [1.10, 1.25, 1.40, 1.55, 1.70, 1.85, 2.00]
    y_vals = [0.2234, 1.2438, 2.2644, 3.2984, 4.3222, 5.3516, 6.3867]
    return x_vals, y_vals
