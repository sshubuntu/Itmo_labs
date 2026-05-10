"""
Модуль с численными методами решения ОДУ
"""


class ODESolver:
    """Класс для решения ОДУ численными методами"""
    
    @staticmethod
    def euler_method(f, x0, y0, xn, h):
        """
        Метод Эйлера для решения ОДУ
        
        Формула: y_{i+1} = y_i + h * f(x_i, y_i)
        Порядок точности: O(h)
        
        Args:
            f: функция правой части ОДУ y' = f(x, y)
            x0: начальное значение x
            y0: начальное значение y
            xn: конечное значение x
            h: шаг интегрирования
            
        Returns:
            tuple: (список x, список y)
        """
        n = int((xn - x0) / h)
        x_values = [x0 + i * h for i in range(n + 1)]
        y_values = [y0]
        
        for i in range(n):
            y_next = y_values[i] + h * f(x_values[i], y_values[i])
            y_values.append(y_next)
        
        return x_values, y_values
    
    @staticmethod
    def runge_kutta_4(f, x0, y0, xn, h):
        """
        Метод Рунге-Кутта 4-го порядка
        
        Формулы:
            k1 = h * f(x_i, y_i)
            k2 = h * f(x_i + h/2, y_i + k1/2)
            k3 = h * f(x_i + h/2, y_i + k2/2)
            k4 = h * f(x_i + h, y_i + k3)
            y_{i+1} = y_i + (k1 + 2*k2 + 2*k3 + k4) / 6
        
        Порядок точности: O(h^4)
        
        Args:
            f: функция правой части ОДУ y' = f(x, y)
            x0: начальное значение x
            y0: начальное значение y
            xn: конечное значение x
            h: шаг интегрирования
            
        Returns:
            tuple: (список x, список y)
        """
        n = int((xn - x0) / h)
        x_values = [x0 + i * h for i in range(n + 1)]
        y_values = [y0]
        
        for i in range(n):
            x_i = x_values[i]
            y_i = y_values[i]
            
            k1 = h * f(x_i, y_i)
            k2 = h * f(x_i + h/2, y_i + k1/2)
            k3 = h * f(x_i + h/2, y_i + k2/2)
            k4 = h * f(x_i + h, y_i + k3)
            
            y_next = y_i + (k1 + 2*k2 + 2*k3 + k4) / 6
            y_values.append(y_next)
        
        return x_values, y_values
    
    @staticmethod
    def milne_method(f, x0, y0, xn, h, epsilon=1e-6):
        """
        Метод Милна (предиктор-корректор)
        
        Прогноз: y_i(прогн) = y_{i-4} + (4h/3) * (2*f_{i-1} - f_{i-2} + 2*f_{i-3})
        Коррекция: y_i(корр) = y_{i-2} + (h/3) * (f_i(прогн) + 4*f_{i-1} + f_{i-2})
        
        Порядок точности: O(h^4)
        
        Args:
            f: функция правой части ОДУ y' = f(x, y)
            x0: начальное значение x
            y0: начальное значение y
            xn: конечное значение x
            h: шаг интегрирования
            epsilon: точность итерационной коррекции
            
        Returns:
            tuple: (список x, список y)
        """
        # Получаем первые 4 точки методом Рунге-Кутта
        x_rk, y_rk = ODESolver.runge_kutta_4(f, x0, y0, x0 + 3*h, h)
        
        x_values = x_rk[:4]
        y_values = y_rk[:4]
        
        n = int((xn - x0) / h)
        
        for i in range(3, n):
            x_i = x0 + (i + 1) * h
            
            # Прогноз
            f_i_1 = f(x_values[i-1], y_values[i-1])
            f_i_2 = f(x_values[i-2], y_values[i-2])
            f_i_3 = f(x_values[i-3], y_values[i-3])
            
            y_pred = y_values[i-3] + (4*h/3) * (2*f_i_1 - f_i_2 + 2*f_i_3)
            
            # Коррекция
            f_pred = f(x_i, y_pred)
            y_corr = y_values[i-1] + (h/3) * (f_pred + 4*f_i_1 + f_i_2)
            
            # Итерационная коррекция
            max_iter = 10
            for _ in range(max_iter):
                f_corr = f(x_i, y_corr)
                y_new = y_values[i-1] + (h/3) * (f_corr + 4*f_i_1 + f_i_2)
                
                if abs(y_new - y_corr) < epsilon:
                    break
                y_corr = y_new
            
            x_values.append(x_i)
            y_values.append(y_corr)
        
        return x_values, y_values
    
    @staticmethod
    def runge_rule(f, x0, y0, xn, h, method, p):
        """
        Правило Рунге для оценки погрешности
        
        Формула: R = |y_h - y_{h/2}| / (2^p - 1)
        
        Args:
            f: функция правой части ОДУ
            x0: начальное значение x
            y0: начальное значение y
            xn: конечное значение x
            h: шаг интегрирования
            method: метод решения ("euler" или "rk4")
            p: порядок точности метода
            
        Returns:
            float: оценка погрешности
        """
        # Решение с шагом h
        if method == "euler":
            _, y_h = ODESolver.euler_method(f, x0, y0, xn, h)
        elif method == "rk4":
            _, y_h = ODESolver.runge_kutta_4(f, x0, y0, xn, h)
        
        # Решение с шагом h/2
        if method == "euler":
            _, y_h2 = ODESolver.euler_method(f, x0, y0, xn, h/2)
        elif method == "rk4":
            _, y_h2 = ODESolver.runge_kutta_4(f, x0, y0, xn, h/2)
        
        # Берем значение в конечной точке
        y_end_h = y_h[-1]
        y_end_h2 = y_h2[-1]
        
        # Правило Рунге
        R = abs(y_end_h - y_end_h2) / (2**p - 1)
        
        return R
