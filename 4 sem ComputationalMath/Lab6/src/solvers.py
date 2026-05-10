import math
import sys

class ODESolver:
    @staticmethod
    def _is_overflow_safe(value):
        """Check if a value is within safe numeric bounds."""
        if value is None:
            return False
        if isinstance(value, (int, float)):
            if math.isinf(value) or math.isnan(value):
                return False
            if abs(value) > 1e300:
                return False
        return True
    
    @staticmethod
    def euler_method(f, x0, y0, xn, h):
        n = int((xn - x0) / h)
        x_values = [x0 + i * h for i in range(n + 1)]
        y_values = [y0]
        
        for i in range(n):
            try:
                f_val = f(x_values[i], y_values[i])
                if not ODESolver._is_overflow_safe(f_val):
                    break
                y_next = y_values[i] + h * f_val
                if not ODESolver._is_overflow_safe(y_next):
                    break
                y_values.append(y_next)
            except (OverflowError, FloatingPointError):
                break
        
        x_values = x_values[:len(y_values)]
        
        return x_values, y_values
    
    @staticmethod
    def runge_kutta_4(f, x0, y0, xn, h):
        n = int((xn - x0) / h)
        x_values = [x0 + i * h for i in range(n + 1)]
        y_values = [y0]
        
        for i in range(n):
            x_i = x_values[i]
            y_i = y_values[i]
            
            try:
                k1 = h * f(x_i, y_i)
                if not ODESolver._is_overflow_safe(k1):
                    break
                k2 = h * f(x_i + h/2, y_i + k1/2)
                if not ODESolver._is_overflow_safe(k2):
                    break
                k3 = h * f(x_i + h/2, y_i + k2/2)
                if not ODESolver._is_overflow_safe(k3):
                    break
                k4 = h * f(x_i + h, y_i + k3)
                if not ODESolver._is_overflow_safe(k4):
                    break
                
                y_next = y_i + (k1 + 2*k2 + 2*k3 + k4) / 6
                if not ODESolver._is_overflow_safe(y_next):
                    break
                y_values.append(y_next)
            except (OverflowError, FloatingPointError):
                break
        
        # Trim x_values to match y_values length
        x_values = x_values[:len(y_values)]
        
        return x_values, y_values
    
    @staticmethod
    def milne_method(f, x0, y0, xn, h, epsilon=1e-6):
        x_rk, y_rk = ODESolver.runge_kutta_4(f, x0, y0, x0 + 3*h, h)
        
        if len(x_rk) < 4:
            return x_rk, y_rk
        
        x_values = x_rk[:4]
        y_values = y_rk[:4]
        
        n = int((xn - x0) / h)
        
        for i in range(3, n):
            x_i = x0 + (i + 1) * h
            
            try:
                f_i_1 = f(x_values[i-1], y_values[i-1])
                f_i_2 = f(x_values[i-2], y_values[i-2])
                f_i_3 = f(x_values[i-3], y_values[i-3])
                
                if not (ODESolver._is_overflow_safe(f_i_1) and 
                        ODESolver._is_overflow_safe(f_i_2) and 
                        ODESolver._is_overflow_safe(f_i_3)):
                    break
                
                y_pred = y_values[i-3] + (4*h/3) * (2*f_i_1 - f_i_2 + 2*f_i_3)
                if not ODESolver._is_overflow_safe(y_pred):
                    break
                
                f_pred = f(x_i, y_pred)
                if not ODESolver._is_overflow_safe(f_pred):
                    break
                
                y_corr = y_values[i-1] + (h/3) * (f_pred + 4*f_i_1 + f_i_2)
                if not ODESolver._is_overflow_safe(y_corr):
                    break
                
                max_iter = 10
                for _ in range(max_iter):
                    f_corr = f(x_i, y_corr)
                    if not ODESolver._is_overflow_safe(f_corr):
                        break
                    y_new = y_values[i-1] + (h/3) * (f_corr + 4*f_i_1 + f_i_2)
                    if not ODESolver._is_overflow_safe(y_new):
                        break
                    
                    if abs(y_new - y_corr) < epsilon:
                        y_corr = y_new
                        break
                    y_corr = y_new
                else:
                    pass
                
                x_values.append(x_i)
                y_values.append(y_corr)
            except (OverflowError, FloatingPointError):
                break
        
        return x_values, y_values
    
    @staticmethod
    def runge_rule(f, x0, y0, xn, h, method, p):
        if method == "euler":
            _, y_h = ODESolver.euler_method(f, x0, y0, xn, h)
        elif method == "rk4":
            _, y_h = ODESolver.runge_kutta_4(f, x0, y0, xn, h)
        
        if method == "euler":
            _, y_h2 = ODESolver.euler_method(f, x0, y0, xn, h/2)
        elif method == "rk4":
            _, y_h2 = ODESolver.runge_kutta_4(f, x0, y0, xn, h/2)
        
        if len(y_h) == 0 or len(y_h2) == 0:
            return float('inf')
        
        y_end_h = y_h[-1]
        y_end_h2 = y_h2[-1]
        
        try:
            R = abs(y_end_h - y_end_h2) / (2**p - 1)
            if not ODESolver._is_overflow_safe(R):
                return float('inf')
        except (OverflowError, FloatingPointError, ZeroDivisionError):
            return float('inf')
        
        return R
