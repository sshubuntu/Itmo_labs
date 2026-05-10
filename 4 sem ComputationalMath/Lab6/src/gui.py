"""
Модуль графического интерфейса
"""
import tkinter as tk
from tkinter import ttk, messagebox
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import numpy as np

from .solvers import ODESolver
from .equations import DifferentialEquations


class ODESolverGUI:
    """GUI для решения ОДУ"""
    
    def __init__(self, root):
        self.root = root
        self.root.title("Решение ОДУ - Вариант 18")
        self.root.geometry("1200x800")
        
        self.solver = ODESolver()
        self.equations = DifferentialEquations.get_equations()
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
                                           values=list(self.equations.keys()), 
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
            
            if self.equation_var.get() not in self.equations:
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
        f = self.equations[eq_name]["func"]
        exact_func = self.equations[eq_name]["exact"]
        
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
            runge_error = self.solver.runge_rule(f, x0, y0, xn, h, "euler", 1)
            results["Эйлер_error"] = runge_error
        
        if self.rk4_var.get():
            x_rk4, y_rk4 = self.solver.runge_kutta_4(f, x0, y0, xn, h)
            results["Рунге-Кутта"] = (x_rk4, y_rk4)
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
        columns = ["i", "x"]
        for method in ["Эйлер", "Рунге-Кутта", "Милн", "Точное"]:
            if method in results:
                columns.append(method)
        
        self.tree["columns"] = columns
        self.tree["show"] = "headings"
        
        for col in columns:
            self.tree.heading(col, text=col)
            self.tree.column(col, width=100)
        
        max_len = 0
        for method in results:
            if method.endswith("_error"):
                continue
            x_vals, _ = results[method]
            max_len = max(max_len, len(x_vals))
        
        for i in range(max_len):
            row = [str(i)]
            
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
                    x_milne, y_milne = results["Милн"]
                    x_exact, y_exact = results["Точное"]
                    errors = []
                    for j, x_m in enumerate(x_milne):
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
