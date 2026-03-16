from typing import Callable, Optional, Tuple


class IntegrationMethods:

    @staticmethod
    def _rectangles(f: Callable[[float], float], a: float, b: float, n: int, shift: float) -> float:
        h = (b - a) / n
        return h * sum(f(a + (i + shift) * h) for i in range(n))

    @staticmethod
    def rectangles_left(f: Callable[[float], float], a: float, b: float, n: int) -> float:
        return IntegrationMethods._rectangles(f, a, b, n, 0.0)

    @staticmethod
    def rectangles_right(f: Callable[[float], float], a: float, b: float, n: int) -> float:
        return IntegrationMethods._rectangles(f, a, b, n, 1.0)

    @staticmethod
    def rectangles_mid(f: Callable[[float], float], a: float, b: float, n: int) -> float:
        return IntegrationMethods._rectangles(f, a, b, n, 0.5)

    @staticmethod
    def trapezoid(f: Callable[[float], float], a: float, b: float, n: int) -> float:
        h = (b - a) / n
        return h * ((f(a) + f(b)) / 2.0 + sum(f(a + i * h) for i in range(1, n)))

    @staticmethod
    def simpson(f: Callable[[float], float], a: float, b: float, n: int) -> float:
        if n % 2:
            n += 1
        h = (b - a) / n
        s_odd = sum(f(a + i * h) for i in range(1, n, 2))
        s_even = sum(f(a + i * h) for i in range(2, n, 2))
        return (h / 3.0) * (f(a) + f(b) + 4 * s_odd + 2 * s_even)

    runge_order = {"rect_left": 1, "rect_right": 1, "rect_mid": 2, "trapezoid": 2, "simpson": 4}

    @staticmethod
    def integrate_with_runge(f: Callable[[float], float], a: float, b: float, eps: float, n0: int, method_key: str) -> Tuple[float, int, Optional[float]]:
        method_map = {
            "rect_left": IntegrationMethods.rectangles_left,
            "rect_right": IntegrationMethods.rectangles_right,
            "rect_mid": IntegrationMethods.rectangles_mid,
            "trapezoid": IntegrationMethods.trapezoid,
            "simpson": IntegrationMethods.simpson,
        }
        method = method_map[method_key]
        p = IntegrationMethods.runge_order[method_key]

        n = max(1, int(n0))
        if method_key == "simpson" and n % 2:
            n += 1

        I_n = method(f, a, b, n)
        err = float("inf")

        for _ in range(60):
            if abs(err) <= eps:
                break
            n2 = 2 * n
            if method_key == "simpson" and n2 % 2:
                n2 += 1
            I_2n = method(f, a, b, n2)
            err = (I_2n - I_n) / (2**p - 1)
            I_n, n = I_2n, n2

        return I_n, n, err


def improper_integrate( f: Callable[[float], float], a: float, b: float, n: int, method_key: str, singularity: str, singularity_point: Optional[float] = None, delta: float = 1e-8,) -> float:
    method_map = {
        "rect_left": IntegrationMethods.rectangles_left,
        "rect_right": IntegrationMethods.rectangles_right,
        "rect_mid": IntegrationMethods.rectangles_mid,
        "trapezoid": IntegrationMethods.trapezoid,
        "simpson": IntegrationMethods.simpson,
    }
    method = method_map.get(method_key, IntegrationMethods.trapezoid)
    if method_key == "simpson" and n % 2:
        n += 1

    if singularity == "a":
        return method(f, a + delta, b, n)
    if singularity == "b":
        return method(f, a, b - delta, n)
    if singularity == "inner" and singularity_point is not None:
        c = singularity_point
        return method(f, a, c - delta, n) + method(f, c + delta, b, n)
    return 0.0


def newton_cotes_n6(f: Callable[[float], float], a: float, b: float) -> float:
    n = 6
    h = (b - a) / n
    weights = [41, 216, 27, 272, 27, 216, 41]
    scale = (b - a) / 840.0
    result = 0.0
    for i in range(7):
        x = a + i * h
        result += weights[i] * f(x)
    return result * scale
