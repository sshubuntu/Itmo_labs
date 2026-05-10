import numpy as np

class DifferentialEquations:    
    @staticmethod
    def get_equations():
        return {
            "y' = x + y": {
                "func": lambda x, y: x + y,
                "exact": lambda x, x0, y0: (y0 + x0 + 1) * np.exp(x - x0) - x - 1
            },
            "y' = y - x^2": {
                "func": lambda x, y: y - x**2,
                "exact": lambda x, x0, y0: (y0 - x0**2 - 2*x0 - 2) * np.exp(x - x0) + x**2 + 2*x + 2
            },
            "y' = x^2 + y^2": {
                "func": lambda x, y: x**2 + y**2,
                "exact": None
            }
        }
