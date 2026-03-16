import math
from typing import Dict, List, Optional, Tuple

funcs: List[Dict] = [
    {
        "id": "variant18",
        "name": "x³ − 5x² + 3x − 16",
        "f": lambda x: x**3 - 5 * x**2 + 3 * x - 16,
        "F": lambda x: x**4 / 4 - 5 * x**3 / 3 + 3 * x**2 / 2 - 16 * x,
    },
    {"id": "poly1", "name": "x² + 1", "f": lambda x: x**2 + 1, "F": lambda x: x**3 / 3 + x},
    {"id": "sin", "name": "sin(x)", "f": math.sin, "F": lambda x: -math.cos(x)},
    {"id": "exp", "name": "eˣ", "f": math.exp, "F": math.exp},
    {
        "id": "ln",
        "name": "ln(x)",
        "f": lambda x: math.log(x) if x > 0 else float("nan"),
        "F": lambda x: (x * math.log(x) - x) if x > 0 else float("nan"),
    },
]


def get_exact_integral(func_id: str, a: float, b: float) -> Optional[float]:
    for item in funcs:
        if item["id"] != func_id:
            continue
        try:
            return item["F"](b) - item["F"](a)
        except (ValueError, ZeroDivisionError, OverflowError):
            return None
    return None



improrer_funcs: List[Dict] = [
    {
        "id": "imp_1sqrtx",
        "name": "1/√x",
        "f": lambda x: 1.0 / math.sqrt(x) if x > 0 else float("nan"),
        "singularity": "a",
        "converges": True,
    },
    {
        "id": "imp_1sqrt1mx",
        "name": "1/√(1−x)",
        "f": lambda x: 1.0 / math.sqrt(1 - x) if x < 1 else float("nan"),
        "singularity": "b",
        "converges": True,
    },
    {
        "id": "imp_1sqrt_abs",
        "name": "1/√|x−0.5|",
        "f": lambda x: 1.0 / math.sqrt(abs(x - 0.5)) if x != 0.5 else float("nan"),
        "singularity": "inner",
        "singularity_point": 0.5,
        "converges": True,
    },
    {
        "id": "imp_1overx",
        "name": "1/x",
        "f": lambda x: 1.0 / x if x != 0 else float("nan"),
        "singularity": "a",
        "converges": False,
    },
]


def check_improper_convergence(imp_id: str, a: float, b: float) -> Tuple[bool, str]:
    for item in improrer_funcs:
        if item["id"] != imp_id:
            continue
        if not item.get("converges", True):
            return False, "Интеграл расходится."
        return True, "Интеграл сходится."
    return False, "Неизвестная функция."


def get_improper_function(imp_id: str) -> Optional[dict]:
    for item in improrer_funcs:
        if item["id"] == imp_id:
            return item
    return None
