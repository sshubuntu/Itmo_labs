from pathlib import Path

import numpy as np


def parse_points(text: str) -> tuple[np.ndarray, np.ndarray]:
    xs: list[float] = []
    ys: list[float] = []
    for idx, line in enumerate(text.splitlines(), start=1):
        line = line.strip()
        if not line:
            continue
        parts = line.replace(",", ".").split()
        if len(parts) != 2:
            raise ValueError(f"Строка {idx}: нужно 2 числа (x y).")
        try:
            x, y = float(parts[0]), float(parts[1])
        except ValueError as exc:
            raise ValueError(f"Строка {idx}: нужно вводить только числа в формате 'x y' ") from exc
        xs.append(x)
        ys.append(y)
    if len(xs) < 2:
        raise ValueError("Недостаточно точек для аппроксимации.")
    return np.array(xs, dtype=float), np.array(ys, dtype=float)


def generate_variant18_points() -> tuple[np.ndarray, np.ndarray]:
    x = np.round(np.arange(0.0, 2.0 + 1e-9, 0.2), 10)
    y = 3.0 * x / (x ** 4 + 4.0)
    return x, y


def read_text_fallback(path: str) -> str:
    file_path = Path(path)
    return file_path.read_text(encoding="utf-8")
   
