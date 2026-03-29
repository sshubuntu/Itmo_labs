import tkinter as tk
from pathlib import Path
from tkinter import filedialog, messagebox, ttk

import matplotlib.pyplot as plt
import numpy as np
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

from approximation import run_approximations
from data_utils import generate_variant18_points, parse_points, read_text_fallback
from models import ApproximationResult


class App(tk.Tk):
    def __init__(self) -> None:
        super().__init__()
        self.title("ЛР4. Вариант 18)")
        self.geometry("1300x760")
        self.results: list[ApproximationResult] = []
        self.x_data: np.ndarray | None = None
        self.y_data: np.ndarray | None = None
        self.pearson_value: float | None = None
        self._build_ui()
        self.fill_variant18()

    def _build_ui(self) -> None:
        left = ttk.Frame(self)
        left.pack(side=tk.LEFT, fill=tk.Y, padx=8, pady=8)

        ttk.Label(left, text="x y").pack(anchor="w")
        self.input_text = tk.Text(left, width=40, height=26)
        self.input_text.pack(fill=tk.BOTH, expand=False)
        self.input_text.bind("<KeyPress>", self._on_key_press)
        self.input_text.bind("<<Paste>>", self._on_paste)

        btns = ttk.Frame(left)
        btns.pack(fill=tk.X, pady=8)
        ttk.Button(btns, text="Вариант 18", command=self.fill_variant18).pack(fill=tk.X, pady=2)
        ttk.Button(btns, text="Загрузить из файла", command=self.load_points_from_file).pack(fill=tk.X, pady=2)
        ttk.Button(btns, text="Рассчитать", command=self.compute).pack(fill=tk.X, pady=2)

        ttk.Label(left, text="Коэффициент Пирсона (линейная):").pack(anchor="w", pady=(8, 0))
        self.pearson_label = ttk.Label(left, text="—")
        self.pearson_label.pack(anchor="w")

        ttk.Label(left, text="Лучшая аппроксимация:").pack(anchor="w", pady=(8, 0))
        self.best_label = ttk.Label(left, text="—", wraplength=300)
        self.best_label.pack(anchor="w")

        right = ttk.Frame(self)
        right.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=8, pady=8)

        cols = ("func", "formula", "sse", "rmse")
        self.tree = ttk.Treeview(right, columns=cols, show="headings", height=9)
        self.tree.heading("func", text="Функция")
        self.tree.heading("formula", text="Вид")
        self.tree.heading("sse", text="S")
        self.tree.heading("rmse", text="СКО")
        self.tree.column("func", width=180)
        self.tree.column("formula", width=400)
        self.tree.column("sse", width=120)
        self.tree.column("rmse", width=120)
        self.tree.pack(fill=tk.X, pady=(0, 8))

        self.figure, self.ax = plt.subplots(figsize=(9, 5), dpi=100)
        self.canvas = FigureCanvasTkAgg(self.figure, master=right)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)
        self.ax.set_title("Графики исходных данных и аппроксимаций")
        self.ax.grid(True, alpha=0.3)
        self.canvas.draw()

    def fill_variant18(self) -> None:
        x, y = generate_variant18_points()
        self.input_text.delete("1.0", tk.END)
        for xi, yi in zip(x, y):
            self.input_text.insert(tk.END, f"{xi:.1f} {yi:.6f}\n")

    def _is_allowed_text(self, text: str) -> bool:
        allowed_chars = set("0123456789.,-+ \t\r\n")
        return all(ch in allowed_chars for ch in text)

    def _on_key_press(self, event: tk.Event) -> str | None:
        ctrl_pressed = bool(event.state & 0x4)
        if ctrl_pressed:
            return None
        allowed_keys = {
            "BackSpace",
            "Delete",
            "Left",
            "Right",
            "Up",
            "Down",
            "Home",
            "End",
            "Prior",
            "Next",
            "Return",
            "KP_Enter",
            "Tab",
        }
        if event.keysym in allowed_keys:
            return None
        if event.char and self._is_allowed_text(event.char):
            return None
        self.bell()
        return "break"

    def _on_paste(self, _event: tk.Event) -> str | None:
        try:
            clipboard_text = self.clipboard_get()
        except tk.TclError:
            return "break"
        if not self._is_allowed_text(clipboard_text):
            messagebox.showerror(
                "Недопустимый ввод",
                "Разрешены только числа, пробелы, переносы строк, точка, запятая и знак минус.",
            )
            return "break"
        return None

    def load_points_from_file(self) -> None:
        path = filedialog.askopenfilename(
            title="Выберите файл с точками",
            filetypes=[("Text files", "*.txt *.csv"), ("All files", "*.*")],
        )
        if not path:
            return
        content = read_text_fallback(path)
        self.input_text.delete("1.0", tk.END)
        self.input_text.insert("1.0", content)

    def compute(self) -> None:
        try:
            x, y = parse_points(self.input_text.get("1.0", tk.END))
        except Exception as e:
            messagebox.showerror("Ошибка входных данных", str(e))
            return
        self.x_data, self.y_data = x, y
        self.results, self.pearson_value = run_approximations(x, y)
        self._refresh_table()
        self._refresh_plot()

    def _refresh_table(self) -> None:
        for item in self.tree.get_children():
            self.tree.delete(item)
        valid_results = [res for res in self.results if res.valid and res.rmse is not None]
        best = min(valid_results, key=lambda r: r.rmse) if valid_results else None
        for res in self.results:
            if res.valid:
                sse = f"{res.sse:.6f}" if res.sse is not None else "-"
                rmse = f"{res.rmse:.6f}" if res.rmse is not None else "-"
            else:
                sse, rmse = "-", "-"
            self.tree.insert("", tk.END, values=(res.name, res.formula, sse, rmse))
        self.pearson_label.configure(
            text=f"{self.pearson_value:.6f}" if self.pearson_value is not None else "—"
        )
        if best is None:
            self.best_label.configure(text="Нет допустимых аппроксимаций.")
        else:
            self.best_label.configure(text=f"{best.name}: {best.formula} (СКО={best.rmse:.6f})")

    def _refresh_plot(self) -> None:
        if self.x_data is None or self.y_data is None:
            return
        x = self.x_data
        y = self.y_data
        self.ax.clear()
        self.ax.scatter(x, y, c="black", label="Табличные точки", zorder=3)
        x_min = float(np.min(x))
        x_max = float(np.max(x))
        span = max(1e-9, x_max - x_min)
        pad = span * 0.1
        x_plot = np.linspace(x_min - pad, x_max + pad, 400)

        valid_results = [res for res in self.results if res.valid and res.rmse is not None]
        best_name = min(valid_results, key=lambda r: r.rmse).name if valid_results else ""
        for res in self.results:
            if not res.valid:
                continue
            y_plot = self._evaluate_model_for_plot(res, x_plot)
            if y_plot is None:
                continue
            lw = 2.5 if res.name == best_name else 1.2
            self.ax.plot(x_plot, y_plot, linewidth=lw, label=res.name)

        self.ax.grid(True, alpha=0.3)
        self.ax.legend(loc="best")
        self.ax.set_xlabel("x")
        self.ax.set_ylabel("y")
        self.ax.set_title("Графики исходной функции и аппроксимаций")
        self.canvas.draw()

    def _evaluate_model_for_plot(self, res: ApproximationResult, x_plot: np.ndarray) -> np.ndarray | None:
        if res.model == "linear":
            a, b = res.params
            return a * x_plot + b
        if res.model == "poly":
            return np.polyval(np.array(res.params), x_plot)
        if res.model == "exp":
            a, b = res.params
            return a * np.exp(b * x_plot)
        if res.model == "log":
            a, b = res.params
            y = np.full_like(x_plot, np.nan, dtype=float)
            mask = x_plot > 0
            y[mask] = a * np.log(x_plot[mask]) + b
            return y
        if res.model == "power":
            a, b = res.params
            y = np.full_like(x_plot, np.nan, dtype=float)
            mask = x_plot > 0
            y[mask] = a * (x_plot[mask] ** b)
            return y
        return None