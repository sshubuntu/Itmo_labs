import math

import numpy as np

from models import ApproximationResult


def linear_fit(x: np.ndarray, y: np.ndarray) -> ApproximationResult:
    a, b = np.linalg.lstsq(np.column_stack((x, np.ones_like(x))), y, rcond=None)[0]
    y_pred = a * x + b
    sse = float(np.sum((y_pred - y) ** 2))
    rmse = math.sqrt(sse / len(x))
    return ApproximationResult("Линейная", f"y = {a:.6f}x + {b:.6f}", "linear", (a, b), y_pred, sse, rmse, True)


def poly_fit(x: np.ndarray, y: np.ndarray, degree: int) -> ApproximationResult:
    coeffs = np.polyfit(x, y, degree)
    y_pred = np.polyval(coeffs, x)
    sse = float(np.sum((y_pred - y) ** 2))
    rmse = math.sqrt(sse / len(x))
    terms = []
    for i, c in enumerate(coeffs):
        power = degree - i
        if power > 1:
            terms.append(f"{c:.6f}x^{power}")
        elif power == 1:
            terms.append(f"{c:.6f}x")
        else:
            terms.append(f"{c:.6f}")
    formula = "y = " + " + ".join(terms).replace("+ -", "- ")
    return ApproximationResult(
        f"Полином {degree}-й степени", formula, "poly", tuple(float(c) for c in coeffs), y_pred, sse, rmse, True
    )


def exp_fit(x: np.ndarray, y: np.ndarray) -> ApproximationResult:
    if np.any(y <= 0):
        return ApproximationResult(
            "Экспоненциальная", "y = a*e^(bx)", "exp", tuple(), None, None, None, False, "Требуется y > 0."
        )
    ln_y = np.log(y)
    b, ln_a = np.linalg.lstsq(np.column_stack((x, np.ones_like(x))), ln_y, rcond=None)[0]
    a = float(np.exp(ln_a))
    y_pred = a * np.exp(b * x)
    sse = float(np.sum((y_pred - y) ** 2))
    rmse = math.sqrt(sse / len(x))
    return ApproximationResult(
        "Экспоненциальная", f"y = {a:.6f}*e^({b:.6f}x)", "exp", (a, b), y_pred, sse, rmse, True
    )


def log_fit(x: np.ndarray, y: np.ndarray) -> ApproximationResult:
    if np.any(x <= 0):
        return ApproximationResult(
            "Логарифмическая", "y = a*ln(x) + b", "log", tuple(), None, None, None, False, "Требуется x > 0."
        )
    ln_x = np.log(x)
    a, b = np.linalg.lstsq(np.column_stack((ln_x, np.ones_like(ln_x))), y, rcond=None)[0]
    y_pred = a * ln_x + b
    sse = float(np.sum((y_pred - y) ** 2))
    rmse = math.sqrt(sse / len(x))
    return ApproximationResult(
        "Логарифмическая", f"y = {a:.6f}*ln(x) + {b:.6f}", "log", (a, b), y_pred, sse, rmse, True
    )


def power_fit(x: np.ndarray, y: np.ndarray) -> ApproximationResult:
    if np.any(x <= 0) or np.any(y <= 0):
        return ApproximationResult(
            "Степенная", "y = a*x^b", "power", tuple(), None, None, None, False, "Требуются x > 0 и y > 0."
        )
    ln_x = np.log(x)
    ln_y = np.log(y)
    b, ln_a = np.linalg.lstsq(np.column_stack((ln_x, np.ones_like(ln_x))), ln_y, rcond=None)[0]
    a = float(np.exp(ln_a))
    y_pred = a * (x ** b)
    sse = float(np.sum((y_pred - y) ** 2))
    rmse = math.sqrt(sse / len(x))
    return ApproximationResult(
        "Степенная", f"y = {a:.6f}*x^{b:.6f}", "power", (a, b), y_pred, sse, rmse, True
    )


def pearson_r(x: np.ndarray, y: np.ndarray) -> float:
    x_mean = float(np.mean(x))
    y_mean = float(np.mean(y))
    num = float(np.sum((x - x_mean) * (y - y_mean)))
    den = math.sqrt(float(np.sum((x - x_mean) ** 2) * np.sum((y - y_mean) ** 2)))
    if den == 0:
        return float("nan")
    return num / den


def run_approximations(x: np.ndarray, y: np.ndarray) -> tuple[list[ApproximationResult], float]:
    results = [
        linear_fit(x, y),
        poly_fit(x, y, 2),
        poly_fit(x, y, 3),
        exp_fit(x, y),
        log_fit(x, y),
        power_fit(x, y),
    ]
    return results, pearson_r(x, y)
