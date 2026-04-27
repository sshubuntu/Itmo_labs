import math


EPS = 1e-9


def is_uniform_grid(xs: list[float]) -> bool:
    if len(xs) < 3:
        return True
    h = xs[1] - xs[0]
    for i in range(1, len(xs) - 1):
        if abs((xs[i + 1] - xs[i]) - h) > 1e-7:
            return False
    return True


def build_finite_differences(ys: list[float]) -> list[list[float]]:
    table: list[list[float]] = [ys[:]]
    for _ in range(1, len(ys)):
        prev = table[-1]
        row = [prev[i + 1] - prev[i] for i in range(len(prev) - 1)]
        table.append(row)
    return table


def build_divided_differences(xs: list[float], ys: list[float]) -> list[list[float]]:
    table: list[list[float]] = [ys[:]]
    n = len(xs)
    for order in range(1, n):
        prev = table[-1]
        row = []
        for i in range(n - order):
            row.append((prev[i + 1] - prev[i]) / (xs[i + order] - xs[i]))
        table.append(row)
    return table


def lagrange_value(xs: list[float], ys: list[float], x: float) -> float:
    n = len(xs)
    total = 0.0
    for i in range(n):
        part = ys[i]
        for j in range(n):
            if i == j:
                continue
            part *= (x - xs[j]) / (xs[i] - xs[j])
        total += part
    return total


def newton_divided_value(xs: list[float], ys: list[float], x: float, backward: bool) -> float:
    dd = build_divided_differences(xs, ys)
    n = len(xs)
    if not backward:
        value = dd[0][0]
        mult = 1.0
        for order in range(1, n):
            mult *= x - xs[order - 1]
            value += dd[order][0] * mult
        return value
    value = dd[0][n - 1]
    mult = 1.0
    for order in range(1, n):
        mult *= x - xs[n - order]
        value += dd[order][n - 1 - order] * mult
    return value


def newton_finite_value(xs: list[float], ys: list[float], x: float, backward: bool) -> float:
    if not is_uniform_grid(xs):
        raise ValueError("Для конечных разностей узлы должны быть равноотстоящими.")
    diff = build_finite_differences(ys)
    h = xs[1] - xs[0]
    n = len(xs)
    if not backward:
        t = (x - xs[0]) / h
        value = ys[0]
        prod = 1.0
        for k in range(1, n):
            prod *= t - (k - 1)
            value += prod * diff[k][0] / math.factorial(k)
        return value
    t = (x - xs[-1]) / h
    value = ys[-1]
    prod = 1.0
    for k in range(1, n):
        prod *= t + (k - 1)
        value += prod * diff[k][n - 1 - k] / math.factorial(k)
    return value


def _gauss_factors_forward(order: int) -> list[int]:
    if order == 1:
        return [0]
    k = order // 2
    if order % 2 == 0:
        positives = list(range(k - 1, -1, -1))
        negatives = list(range(-1, -k - 1, -1))
    else:
        positives = list(range(k, -1, -1))
        negatives = list(range(-1, -k - 1, -1))
    return positives + negatives


def _gauss_factors_backward(order: int) -> list[int]:
    if order == 1:
        return [0]
    k = order // 2
    if order % 2 == 0:
        positives = list(range(k, -1, -1))
        negatives = list(range(-1, -(k - 1) - 1, -1))
    else:
        positives = list(range(k, -1, -1))
        negatives = list(range(-1, -k - 1, -1))
    return positives + negatives


def gauss_value(xs: list[float], ys: list[float], x: float, forward: bool) -> float:
    if not is_uniform_grid(xs):
        raise ValueError("Для формул Гаусса нужны равноотстоящие узлы.")
    diff = build_finite_differences(ys)
    n = len(xs)
    m = n // 2
    h = xs[1] - xs[0]
    t = (x - xs[m]) / h

    value = ys[m]
    for order in range(1, n):
        if forward:
            rel_index = -(order // 2)
            factors = _gauss_factors_forward(order)
        else:
            rel_index = -((order + 1) // 2)
            factors = _gauss_factors_backward(order)

        i = m + rel_index
        if i < 0 or i >= len(diff[order]):
            break
        prod = 1.0
        for f in factors:
            prod *= (t + f)
        value += prod * diff[order][i] / math.factorial(order)
    return value


def evaluate_all_methods(xs: list[float], ys: list[float], x: float) -> dict[str, float]:
    midpoint = (xs[0] + xs[-1]) / 2.0
    use_backward = x > midpoint + EPS
    use_gauss_forward = x >= xs[len(xs) // 2]

    result: dict[str, float] = {
        "Лагранж": lagrange_value(xs, ys, x),
        "Ньютон (разделенные, вперед)" if not use_backward else "Ньютон (разделенные, назад)":
            newton_divided_value(xs, ys, x, backward=use_backward),
    }

    if is_uniform_grid(xs):
        result[
            "Ньютон (конечные, вперед)" if not use_backward else "Ньютон (конечные, назад)"
        ] = newton_finite_value(xs, ys, x, backward=use_backward)
        result[
            "Гаусс 1 (x>a)" if use_gauss_forward else "Гаусс 2 (x<a)"
        ] = gauss_value(xs, ys, x, forward=use_gauss_forward)
    return result
