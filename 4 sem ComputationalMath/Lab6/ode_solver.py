import tkinter as tk
from tkinter import ttk, messagebox
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import numpy as np


class ODESolver:
    """Класс для решения ОДУ численными методами"""
    
    def __init__(self):
        self.equations = {
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
                "exact": None  # Нет аналитического решения
            }
        }
    
    def euler_method(self, f, x0, y0, xn, h):
        """Метод Эйлера"""
        n = int((xn - x0) / h)
        x_values = [x0 + i * h for i in range(n + 1)]
        y_values = [y0]
        
        for i in range(n):
            y_next = y_values[i] + h * f(x_values[i], y_values[i])
            y_values.append(y_next)
        
        return x_values, y_values
    
    def runge_kutta_4(self, f, x0, y0, xn, h):
        """Метод Рунге-Кутта 4-го порядка"""
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
    
    def milne_method(self, f, x0, y0, xn, h, epsilon=1e-6):
        """Метод Милна (предиктор-корректор)"""
        # Получаем первые 4 точки методом Рунге-Кутта
        x_rk, y_rk = self.runge_kutta_4(f, x0, y0, x0 + 3*h, h)
        
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
    
    def runge_rule(self, f, x0, y0, xn, h, method, p):
        """Правило Рунге для оценки погрешности"""
        # Решение с шагом h
        if method == "euler":
            _, y_h = self.euler_method(f, x0, y0, xn, h)
        elif method == "rk4":
            _, y_h = self.runge_kutta_4(f, x0, y0, xn, h)
        
        # Решение с шагом h/2
        if method == "euler":
            _, y_h2 = self.euler_method(f, x0, y0, xn, h/2)
        elif method == "rk4":
            _, y_h2 = self.runge_kutta_4(f, x0, y0, xn, h/2)
        
        # Берем значение в конечной точке
        y_end_h = y_h[-1]
        y_end_h2 = y_h2[-1]
        
        # Правило Рунге
        R = abs(y_end_h - y_end_h2) / (2**p - 1)
        
        return R


class ODESolverGUI:
    """GUI для решения ОДУ"""
    
    def __init__(self, root):
        self.root = root
        self.root.title("Решение ОДУ - Вариант 18")
        self.root.geometry("1200x800")
        
        self.solver = ODESolver()
        self.setup_ui()
    
    def setup_ui(self):
        """Настройка интерфейса"""
        # Левая панель - ввод данных
        left_frame = ttk.Frame(self.root, padding="10")
        left_frame.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S))
        
        # Выбор уравнения
        ttk.Label(left_frame, text="Выберите уравнение:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.equation_var = tk.StringVar()
        self.equation_combo = ttk.Combobox(left_frame, textvariable=self.equation_var, 
                                           values=list(self.solver.equations.keys()), 
                                           state="readonly", width=30)
        self.equation_combo.grid(row=0, column=1, pady=5)
        self.equation_combo.current(0)
        
        # Начальные условия
        ttk.Label(left_frame, text="x₀:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.x0_entry = ttk.Entry(left_frame, width=32)
        self.x0_entry.grid(row=1, column=1, pady=5)
        self.x0_entry.insert(0, "0")
        
        ttk.Label(left_frame, text="y₀:").grid(row=2, column=0, sticky=tk.W, pady=5)
        self.y0_entry = ttk.Entry(left_frame, width=32)
        self.y0_entry.grid(row=2, column=1, pady=5)
        self.y0_entry.insert(0, "1")
        
        # Интервал
        ttk.Label(left_frame, text="xₙ:").grid(row=3, column=0, sticky=tk.W, pady=5)
        self.xn_entry = ttk.Entry(left_frame, width=32)
        self.xn_entry.grid(row=3, column=1, pady=5)
        self.xn_entry.insert(0, "1")
        
        # Шаг
        ttk.Label(left_frame, text="Шаг h:").grid(row=4, column=0, sticky=tk.W, pady=5)
        self.h_entry = ttk.Entry(left_frame, width=32)
        self.h_entry.grid(row=4, column=1, pady=5)
        self.h_entry.insert(0, "0.1")
        
        # Точность
        ttk.Label(left_frame, text="Точность ε:").grid(row=5, column=0, sticky=tk.W, pady=5)
        self.epsilon_entry = ttk.Entry(left_frame, width=32)
        self.epsilon_entry.grid(row=5, column=1, pady=5)
        self.epsilon_entry.insert(0, "0.001")
        
        # Выбор методов
        ttk.Label(left_frame, text="Методы решения:").grid(row=6, column=0, columnspan=2, sticky=tk.W, pady=10)
        
        self.euler_var = tk.BooleanVar(value=True)
        ttk.Checkbutton(left_frame, text="Метод Эйлера", variable=self.euler_var).grid(row=7, column=0, columnspan=2, sticky=tk.W)
        
        self.rk4_var = tk.BooleanVar(value=True)
        ttk.Checkbutton(left_frame, text="Метод Рунге-Кутта 4-го порядка", variable=self.rk4_var).grid(row=8, column=0, columnspan=2, sticky=tk.W)
        
        self.milne_var = tk.BooleanVar(value=True)
        ttk.Checkbutton(left_frame, text="Метод Милна", variable=self.milne_var).grid(row=9, column=0, columnspan=2, sticky=tk.W)
        
        # Кнопка решения
        ttk.Button(left_frame, text="Решить", command=self.solve).grid(row=10, column=0, columnspan=2, pady=20)
        
        # Правая панель - результаты
        right_frame = ttk.Frame(self.root, padding="10")
        right_frame.grid(row=0, column=1, sticky=(tk.W, tk.E, tk.N, tk.S))
        
        # Таблица результатов
        ttk.Label(right_frame, text="Результаты:").pack(pady=5)
        
        # Создаем Treeview для таблицы
        self.tree = ttk.Treeview(right_frame, height=15)
        self.tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        
        # Scrollbar для таблицы
        scrollbar = ttk.Scrollbar(right_frame, orient=tk.VERTICAL, command=self.tree.yview)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        self.tree.configure(yscrollcommand=scrollbar.set)
        
        # Нижняя панель - график
        bottom_frame = ttk.Frame(self.root, padding="10")
        bottom_frame.grid(row=1, column=0, columnspan=2, sticky=(tk.W, tk.E, tk.N, tk.S))
        
        self.figure, self.ax = plt.subplots(figsize=(10, 4))
        self.canvas = FigureCanvasTkAgg(self.figure, bottom_frame)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)
        
        # Настройка весов для растягивания
        self.root.columnconfigure(1, weight=1)
        self.root.rowconfigure(1, weight=1)
    
    def validate_inputs(self):
        """Валидация всех полей ввода"""
        try:
            x0 = float(self.x0_entry.get())
            y0 = float(self.y0_entry.get())
            xn = float(self.xn_entry.get())
            h = float(self.h_entry.get())
            epsilon = float(self.epsilon_entry.get())
            
            if xn <= x0:
                messagebox.showerror("Ошибка", "xₙ должно быть больше x₀")
                return None
            
            if h <= 0:
                messagebox.showerror("Ошибка", "Шаг h должен быть положительным")
                return None
            
            if h >= (xn - x0):
                messagebox.showerror("Ошибка", "Шаг h слишком большой")
                return None
            
            if epsilon <= 0:
                messagebox.showerror("Ошибка", "Точность ε должна быть положительной")
                return None
            
            if not (self.euler_var.get() or self.rk4_var.get() or self.milne_var.get()):
                messagebox.showerror("Ошибка", "Выберите хотя бы один метод")
                return None
            
            if self.equation_var.get() not in self.solver.equations:
                messagebox.showerror("Ошибка", "Выберите уравнение")
                return None
            
            return x0, y0, xn, h, epsilon
            
        except ValueError:
            messagebox.showerror("Ошибка", "Введите корректные числовые значения")
            return None
    
    def solve(self):
        """Решение ОДУ выбранными методами"""
        params = self.validate_inputs()
        if params is None:
            return
        
        x0, y0, xn, h, epsilon = params
        
        # Получаем функцию
        eq_name = self.equation_var.get()
        f = self.solver.equations[eq_name]["func"]
        exact_func = self.solver.equations[eq_name]["exact"]
        
        # Очищаем таблицу
        for item in self.tree.get_children():
            self.tree.delete(item)
        
        # Очищаем график
        self.ax.clear()
        
        results = {}
        
        # Решаем выбранными методами
        if self.euler_var.get():
            x_euler, y_euler = self.solver.euler_method(f, x0, y0, xn, h)
            results["Эйлер"] = (x_euler, y_euler)
            # Оценка по правилу Рунге
            runge_error = self.solver.runge_rule(f, x0, y0, xn, h, "euler", 1)
            results["Эйлер_error"] = runge_error
        
        if self.rk4_var.get():
            x_rk4, y_rk4 = self.solver.runge_kutta_4(f, x0, y0, xn, h)
            results["Рунге-Кутта"] = (x_rk4, y_rk4)
            # Оценка по правилу Рунге
            runge_error = self.solver.runge_rule(f, x0, y0, xn, h, "rk4", 4)
            results["РК_error"] = runge_error
        
        if self.milne_var.get():
            try:
                x_milne, y_milne = self.solver.milne_method(f, x0, y0, xn, h, epsilon)
                results["Милн"] = (x_milne, y_milne)
            except Exception as e:
                messagebox.showwarning("Предупреждение", f"Метод Милна: {str(e)}")
        
        # Точное решение (если есть)
        if exact_func is not None:
            x_exact = np.linspace(x0, xn, 100)
            y_exact = [exact_func(x, x0, y0) for x in x_exact]
            results["Точное"] = (x_exact, y_exact)
        
        # Заполняем таблицу
        self.fill_table(results)
        
        # Строим график
        self.plot_results(results)
    
    def fill_table(self, results):
        """Заполнение таблицы результатов"""
        # Определяем столбцы
        columns = ["i", "x"]
        for method in ["Эйлер", "Рунге-Кутта", "Милн", "Точное"]:
            if method in results:
                columns.append(method)
        
        self.tree["columns"] = columns
        self.tree["show"] = "headings"
        
        # Заголовки
        for col in columns:
            self.tree.heading(col, text=col)
            self.tree.column(col, width=100)
        
        # Находим максимальную длину
        max_len = 0
        for method in results:
            if method.endswith("_error"):
                continue
            x_vals, _ = results[method]
            max_len = max(max_len, len(x_vals))
        
        # Заполняем строки
        for i in range(max_len):
            row = [str(i)]
            
            # x значение
            x_val = None
            for method in ["Эйлер", "Рунге-Кутта", "Милн"]:
                if method in results:
                    x_vals, _ = results[method]
                    if i < len(x_vals):
                        x_val = x_vals[i]
                        break
            
            if x_val is not None:
                row.append(f"{x_val:.4f}")
            else:
                row.append("")
            
            # y значения для каждого метода
            for method in ["Эйлер", "Рунге-Кутта", "Милн", "Точное"]:
                if method in results:
                    x_vals, y_vals = results[method]
                    if i < len(y_vals):
                        row.append(f"{y_vals[i]:.6f}")
                    else:
                        row.append("")
            
            self.tree.insert("", tk.END, values=row)
        
        # Добавляем строку с погрешностями
        error_row = ["", "Погрешность"]
        for method in ["Эйлер", "Рунге-Кутта", "Милн", "Точное"]:
            if method in results:
                if method == "Эйлер" and "Эйлер_error" in results:
                    error_row.append(f"{results['Эйлер_error']:.6e}")
                elif method == "Рунге-Кутта" and "РК_error" in results:
                    error_row.append(f"{results['РК_error']:.6e}")
                elif method == "Милн" and "Точное" in results:
                    # Погрешность для Милна - сравнение с точным
                    x_milne, y_milne = results["Милн"]
                    x_exact, y_exact = results["Точное"]
                    errors = []
                    for j, x_m in enumerate(x_milne):
                        # Находим ближайшее точное значение
                        idx = min(range(len(x_exact)), key=lambda i: abs(x_exact[i] - x_m))
                        errors.append(abs(y_milne[j] - y_exact[idx]))
                    max_error = max(errors) if errors else 0
                    error_row.append(f"{max_error:.6e}")
                else:
                    error_row.append("")
        
        self.tree.insert("", tk.END, values=error_row)
    
    def plot_results(self, results):
        """Построение графиков"""
        self.ax.clear()
        
        # Строим графики для каждого метода
        colors = {"Эйлер": "blue", "Рунге-Кутта": "green", "Милн": "red", "Точное": "black"}
        styles = {"Эйлер": "--", "Рунге-Кутта": "-.", "Милн": ":", "Точное": "-"}
        
        for method in ["Эйлер", "Рунге-Кутта", "Милн", "Точное"]:
            if method in results:
                x_vals, y_vals = results[method]
                self.ax.plot(x_vals, y_vals, 
                           label=method, 
                           color=colors[method], 
                           linestyle=styles[method],
                           linewidth=2 if method == "Точное" else 1.5)
        
        self.ax.set_xlabel("x")
        self.ax.set_ylabel("y")
        self.ax.set_title("Решение ОДУ численными методами")
        self.ax.legend()
        self.ax.grid(True, alpha=0.3)
        
        self.canvas.draw()


def main():
    """Главная функция"""
    root = tk.Tk()
    app = ODESolverGUI(root)
    root.mainloop()


if __name__ == "__main__":
    main()
