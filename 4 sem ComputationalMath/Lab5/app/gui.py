import tkinter as tk
from pathlib import Path
from tkinter import filedialog, messagebox, ttk

import matplotlib.pyplot as plt
import numpy as np
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

from data_utils import generate_function_points, parse_points, read_text_file, variant18_table
from interpolation import (
    build_divided_differences,
    build_finite_differences,
    evaluate_all_methods,
    is_uniform_grid,
    newton_finite_value,
)


class Lab5App(tk.Tk):
    def __init__(self) -> None:
        super().__init__()
        self.title("ЛР5. Интерполяция (вариант 18)")
        self.geometry("1380x820")
        self.func_var = tk.StringVar(value="sin(x)")
        self.xs: list[float] = []
        self.ys: list[float] = []
        self._build_ui()
        self.load_variant18_file()

    def _build_ui(self) -> None:
        top = ttk.Frame(self)
        top.pack(side=tk.TOP, fill=tk.X, padx=8, pady=8)

        left = ttk.Frame(self)
        left.pack(side=tk.LEFT, fill=tk.Y, padx=8, pady=(0, 8))
        right = ttk.Frame(self)
        right.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=8, pady=(0, 8))

        result_box = ttk.LabelFrame(top, text="Результаты интерполяции")
        result_box.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=(0, 8))
        self.result_table = ttk.Treeview(result_box, columns=("method", "value"), show="headings", height=8)
        self.result_table.heading("method", text="Метод")
        self.result_table.heading("value", text="Значение в x*")
        self.result_table.column("method", width=320, anchor=tk.W)
        self.result_table.column("value", width=200, anchor=tk.W)
        self.result_table.pack(fill=tk.BOTH, expand=True)

        diff_box = ttk.LabelFrame(top, text="Таблица разностей")
        diff_box.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)
        self.diff_notebook = ttk.Notebook(diff_box)
        self.diff_notebook.pack(fill=tk.BOTH, expand=True)

        self.finite_tree = ttk.Treeview(self.diff_notebook, show="headings", height=8)
        self.divided_tree = ttk.Treeview(self.diff_notebook, show="headings", height=8)
        self.diff_notebook.add(self.finite_tree, text="Конечные")
        self.diff_notebook.add(self.divided_tree, text="Разделенные")

        ttk.Label(left, text="Точки (по одной паре x y в строке):").pack(anchor="w")
        self.input_text = tk.Text(left, width=44, height=18)
        self.input_text.pack(fill=tk.X)

        btn_frame = ttk.Frame(left)
        btn_frame.pack(fill=tk.X, pady=(8, 0))
        ttk.Button(btn_frame, text="Загрузить файл", command=self.load_file).pack(fill=tk.X, pady=2)
        ttk.Button(btn_frame, text="Вариант 18", command=self.load_variant18_file).pack(fill=tk.X, pady=2)

        func_frame = ttk.LabelFrame(left, text="Параметры функции")
        func_frame.pack(fill=tk.X, pady=8)
        ttk.Label(func_frame, text="f(x):").grid(row=0, column=0, sticky="w")
        ttk.Combobox(func_frame, values=["sin(x)", "cos(x)", "exp(x)"], textvariable=self.func_var, width=12).grid(
            row=0, column=1, sticky="w"
        )
        ttk.Label(func_frame, text="Левая граница:").grid(row=1, column=0, sticky="w")
        self.left_entry = ttk.Entry(func_frame, width=12)
        self.left_entry.insert(0, "0")
        self.left_entry.grid(row=1, column=1, sticky="w")
        ttk.Label(func_frame, text="Правая граница:").grid(row=2, column=0, sticky="w")
        self.right_entry = ttk.Entry(func_frame, width=12)
        self.right_entry.insert(0, "3.14")
        self.right_entry.grid(row=2, column=1, sticky="w")
        ttk.Label(func_frame, text="Точек:").grid(row=3, column=0, sticky="w")
        self.count_entry = ttk.Entry(func_frame, width=12)
        self.count_entry.insert(0, "7")
        self.count_entry.grid(row=3, column=1, sticky="w")
        ttk.Button(func_frame, text="Сгенерировать", command=self.generate_points).grid(row=4, column=0, columnspan=2, sticky="ew")

        query_frame = ttk.LabelFrame(left, text="Запрос интерполяции")
        query_frame.pack(fill=tk.X, pady=8)
        ttk.Label(query_frame, text="x*:").grid(row=0, column=0, sticky="w")
        self.x_query_entry = ttk.Entry(query_frame, width=16)
        self.x_query_entry.insert(0, "1.875")
        self.x_query_entry.grid(row=0, column=1, sticky="w")
        tk.Button(
            query_frame,
            text="Рассчитать",
            command=self.compute,
            bg="#2e7d32",
            fg="white",
            activebackground="#1b5e20",
            activeforeground="white",
            font=("Segoe UI", 10, "bold"),
            relief=tk.FLAT,
        ).grid(row=1, column=0, columnspan=2, sticky="ew", pady=(8, 0))

        self.figure, self.ax = plt.subplots(figsize=(9, 6), dpi=100)
        self.canvas = FigureCanvasTkAgg(self.figure, master=right)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)
        self.ax.grid(True, alpha=0.3)
        self.ax.set_title("График функции и интерполяции")
        self.canvas.draw()

    def _fill_input_with_points(self, xs: list[float], ys: list[float]) -> None:
        self.input_text.delete("1.0", tk.END)
        for x, y in zip(xs, ys):
            self.input_text.insert(tk.END, f"{x:.6f} {y:.6f}\n")

    def load_variant18_file(self) -> None:
        variant_path = Path(__file__).resolve().parent.parent / "test_data" / "variant18_points.txt"
        if variant_path.exists():
            content = read_text_file(str(variant_path))
            self.input_text.delete("1.0", tk.END)
            self.input_text.insert("1.0", content)
            return
        xs, ys = variant18_table()
        self._fill_input_with_points(xs, ys)

    def load_file(self) -> None:
        path = filedialog.askopenfilename(
            title="Выберите файл с точками",
            initialdir=str(Path(__file__).resolve().parent.parent / "test_data"),
            filetypes=[("Text files", "*.txt *.csv"), ("All files", "*.*")],
        )
        if not path:
            return
        content = read_text_file(path)
        self.input_text.delete("1.0", tk.END)
        self.input_text.insert("1.0", content)

    def generate_points(self) -> None:
        try:
            left = float(self.left_entry.get().replace(",", "."))
            right = float(self.right_entry.get().replace(",", "."))
            count = int(self.count_entry.get())
            xs, ys = generate_function_points(self.func_var.get(), left, right, count)
        except Exception as exc:
            messagebox.showerror("Ошибка", str(exc))
            return
        self._fill_input_with_points(xs, ys)

    def compute(self) -> None:
        try:
            xs, ys = parse_points(self.input_text.get("1.0", tk.END))
            x_query = float(self.x_query_entry.get().replace(",", "."))
        except Exception as exc:
            messagebox.showerror("Ошибка ввода", str(exc))
            return
        self.xs, self.ys = xs, ys

        methods = evaluate_all_methods(xs, ys, x_query)
        self._show_differences(xs, ys)
        self._show_results(methods, x_query)
        self._plot(xs, ys, x_query)

    def _fill_tree(self, tree: ttk.Treeview, columns: list[str], rows: list[list[str]]) -> None:
        tree.delete(*tree.get_children())
        tree["columns"] = columns
        for col in columns:
            tree.heading(col, text=col)
            tree.column(col, width=110, anchor=tk.W)
        for row in rows:
            tree.insert("", tk.END, values=row)

    def _show_differences(self, xs: list[float], ys: list[float]) -> None:
        finite = build_finite_differences(ys)
        finite_columns = ["x", "y"] + [f"Δ{i}" for i in range(1, len(finite))]
        finite_rows: list[list[str]] = []
        for i, x in enumerate(xs):
            row = [f"{x:.6f}", f"{finite[0][i]:.6f}"]
            for order in range(1, len(finite)):
                if i < len(finite[order]):
                    row.append(f"{finite[order][i]:.6f}")
                else:
                    row.append("")
            finite_rows.append(row)
        self._fill_tree(self.finite_tree, finite_columns, finite_rows)

        divided = build_divided_differences(xs, ys)
        divided_columns = ["x", "f"] + [f"f[{i}]" for i in range(1, len(divided))]
        divided_rows: list[list[str]] = []
        for i, x in enumerate(xs):
            row = [f"{x:.6f}", f"{divided[0][i]:.6f}"]
            for order in range(1, len(divided)):
                if i < len(divided[order]):
                    row.append(f"{divided[order][i]:.6f}")
                else:
                    row.append("")
            divided_rows.append(row)
        self._fill_tree(self.divided_tree, divided_columns, divided_rows)

    def _show_results(self, methods: dict[str, float], x_query: float) -> None:
        self.result_table.delete(*self.result_table.get_children())
        for name, value in methods.items():
            self.result_table.insert("", tk.END, values=(name, f"{value:.10f}"))
        self.result_table.insert("", 0, values=("x*", f"{x_query:.6f}"))

    def _plot(self, xs: list[float], ys: list[float], x_query: float) -> None:
        self.ax.clear()
        self.ax.scatter(xs, ys, color="black", label="Узлы", zorder=3)
        x_line = np.linspace(min(xs), max(xs), 400)
        y_lagrange = np.array([evaluate_all_methods(xs, ys, x)["Лагранж"] for x in x_line])
        self.ax.plot(x_line, y_lagrange, color="tab:blue", label="Полином Лагранжа")

        if is_uniform_grid(xs):
            midpoint = (xs[0] + xs[-1]) / 2.0
            y_newton = [
                newton_finite_value(xs, ys, x, backward=(x > midpoint))
                for x in x_line
            ]
            self.ax.plot(x_line, y_newton, color="tab:red", linestyle="--", label="Полином Ньютона")

        values_at_query = evaluate_all_methods(xs, ys, x_query)
        self.ax.scatter([x_query], [values_at_query["Лагранж"]], color="green", label="x*")
        self.ax.grid(True, alpha=0.3)
        self.ax.legend(loc="best")
        self.ax.set_title("График функции и интерполяции")
        self.ax.set_xlabel("x")
        self.ax.set_ylabel("y")
        self.canvas.draw()
