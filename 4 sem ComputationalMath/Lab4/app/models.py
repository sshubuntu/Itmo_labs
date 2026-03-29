from dataclasses import dataclass

import numpy as np


@dataclass
class ApproximationResult:
    name: str
    formula: str
    model: str
    params: tuple[float, ...]
    y_pred: np.ndarray | None
    sse: float | None
    rmse: float | None
    valid: bool
    reason: str = ""
