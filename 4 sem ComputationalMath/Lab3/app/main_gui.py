import math
import re
import tkinter as tk
from tkinter import messagebox, scrolledtext, ttk
from typing import Optional, Tuple

from integrand_functions import funcs, improrer_funcs, check_improper_convergence, get_exact_integral
from integration_methods import IntegrationMethods, improper_integrate


re_float = re.compile(r"^-?\d*\.?\d*$")
re_float_sci = re.compile(r"^-?(\d*\.?\d*)([eE][-+]?\d*)?$")
re_int = re.compile(r"^\d*$")


def _parse_float(s: str) -> Optional[float]:
    try:
        return float(s.strip().replace(",", "."))
    except ValueError:
        return None


def _parse_int(s: str) -> Optional[int]:
    try:
        return int(s.strip())
    except ValueError:
        return None


class NumEntry(ttk.Entry):
    def __init__(self, parent, *, is_int: bool = False, sci: bool = False, **kw):
        super().__init__(parent, **kw)
        self._is_int = is_int
        self._sci = sci
        self.configure(validate="key", validatecommand=(parent.register(self._ok), "%P"))

    def _ok(self, text: str) -> bool:
        if text == "":
            return True
        if self._is_int:
            return bool(re_int.match(text))
        return bool((re_float_sci if self._sci else re_float).match(text))

    def float(self) -> Optional[float]:
        return _parse_float(self.get())

    def int(self) -> Optional[int]:
        return _parse_int(self.get())


def main():
    root = tk.Tk()
    root.title("ЛР №3. Вариант 18")
    root.minsize(520, 480)
    root.geometry("620x560")

    func_by_name = {f["name"]: f for f in funcs}
    imp_by_name = {f["name"]: f for f in improrer_funcs}

    var_func_name = tk.StringVar(value=funcs[0]["name"])
    var_method_name = tk.StringVar(value="Средние прямоугольники")

    notebook = ttk.Notebook(root)
    notebook.pack(fill=tk.BOTH, expand=True, padx=8, pady=8)

    frame_main = ttk.Frame(notebook, padding=10)
    notebook.add(frame_main, text="Определённый интеграл")

    ttk.Label(frame_main, text="Функция:").grid(row=0, column=0, sticky=tk.W, pady=2)
    combo_func = ttk.Combobox(frame_main, textvariable=var_func_name, values=list(func_by_name.keys()), state="readonly",width=28,)
    combo_func.grid(row=0, column=1, columnspan=2, sticky=tk.EW, pady=2)

    ttk.Label(frame_main, text="Нижний предел:").grid(row=1, column=0, sticky=tk.W, pady=2)
    entry_a = NumEntry(frame_main, width=15)
    entry_a.insert(0, "2")
    entry_a.grid(row=1, column=1, sticky=tk.W, pady=2)

    ttk.Label(frame_main, text="Верхний предел:").grid(row=2, column=0, sticky=tk.W, pady=2)
    entry_b = NumEntry(frame_main, width=15)
    entry_b.insert(0, "4")
    entry_b.grid(row=2, column=1, sticky=tk.W, pady=2)

    ttk.Label(frame_main, text="ε:").grid(row=3, column=0, sticky=tk.W, pady=2)
    entry_eps = NumEntry(frame_main, sci=True, width=15)
    entry_eps.insert(0, "1e-5")
    entry_eps.grid(row=3, column=1, sticky=tk.W, pady=2)

    ttk.Label(frame_main, text="n0:").grid(row=4, column=0, sticky=tk.W, pady=2)
    entry_n0 = NumEntry(frame_main, is_int=True, width=15)
    entry_n0.insert(0, "4")
    entry_n0.grid(row=4, column=1, sticky=tk.W, pady=2)

    ttk.Label(frame_main, text="Метод:").grid(row=5, column=0, sticky=tk.W, pady=2)
    method_name_to_key = {
        "Левые прямоугольники": "rect_left",
        "Правые прямоугольники": "rect_right",
        "Средние прямоугольники": "rect_mid",
        "Трапеции": "trapezoid",
        "Симпсон": "simpson",
    }
    combo_method = ttk.Combobox(
        frame_main,
        textvariable=var_method_name,
        values=list(method_name_to_key.keys()),
        state="readonly",
        width=26,
    )
    combo_method.set("Средние прямоугольники")
    combo_method.grid(row=5, column=1, columnspan=2, sticky=tk.EW, pady=2)

    ttk.Separator(frame_main, orient=tk.HORIZONTAL).grid(
        row=6, column=0, columnspan=3, sticky=tk.EW, pady=10
    )

    text_result = scrolledtext.ScrolledText(frame_main, height=12, width=70, wrap=tk.WORD)
    text_result.grid(row=7, column=0, columnspan=3, sticky=tk.NSEW, pady=5)
    frame_main.columnconfigure(1, weight=1)
    frame_main.rowconfigure(7, weight=1)

    def _validate_main() -> Tuple[Optional[str], Optional[float], Optional[float], Optional[float], Optional[int], dict]:
        a = entry_a.float()
        b = entry_b.float()
        eps = entry_eps.float()
        n0 = entry_n0.int()
        func = func_by_name[var_func_name.get()]
        if a is None:
            return "Введите число в поле «Нижний предел».", None, None, None, None, func
        if b is None:
            return "Введите число в поле «Верхний предел».", None, None, None, None, func
        if eps is None:
            return "Введите число в поле «ε».", None, None, None, None, func
        if eps <= 0:
            return "Точность ε должна быть положительным числом.", None, None, None, None, func
        if n0 is None or n0 < 1:
            return "Начальное n должно быть целым числом ≥ 1.", None, None, None, None, func
        if a == b:
            return "Пределы интегрирования a и b не должны совпадать.", None, None, None, None, func
        if func["id"] == "ln" and (a <= 0 or b <= 0):
            return "Для функции ln(x) пределы a и b должны быть положительными.", None, None, None, None, func
        return None, a, b, eps, n0, func

    def run_integrate():
        err, a, b, eps, n0, func = _validate_main()
        if err:
            messagebox.showerror("Ошибка ввода", err)
            return
        if a > b:
            a, b = b, a
        n0 = max(4, n0)
        method_key = method_name_to_key.get(var_method_name.get(), "rect_mid")
        f = func["f"]

        text_result.delete(1.0, tk.END)
        try:
            I, n, err_est = IntegrationMethods.integrate_with_runge(
                f, a, b, eps, n0, method_key
            )
            exact = get_exact_integral(func["id"], a, b)
            lines = [
                f"Метод: {var_method_name.get()}",
                f"Интервал: [{a}, {b}]",
                f"Точность ε = {eps}",
                f"",
                f"Значение интеграла: {I}",
                f"Число разбиений n: {n}",
                f"Оценка погрешности: {err_est}",
            ]
            if exact is not None and math.isfinite(exact):
                rel = abs(I - exact) / abs(exact) * 100 if exact != 0 else 0
                lines.append(f"Точное значение: {exact}")
                lines.append(f"Относительная погрешность: {rel:.6f} %")
            text_result.insert(tk.END, "\n".join(lines))
        except Exception as e:
            text_result.insert(tk.END, f"Ошибка: {e}")
            messagebox.showerror("Ошибка", str(e))

    btn_run = ttk.Button(frame_main, text="Вычислить интеграл", command=run_integrate)
    btn_run.grid(row=8, column=0, columnspan=2, pady=8)

    frame_imp = ttk.Frame(notebook, padding=10)
    notebook.add(frame_imp, text="Несобственный интеграл")

    ttk.Label(frame_imp, text="Функция:").grid(row=0, column=0, sticky=tk.W, pady=2)
    var_imp_name = tk.StringVar(value=improrer_funcs[0]["name"])
    combo_imp = ttk.Combobox(frame_imp, textvariable=var_imp_name, values=list(imp_by_name.keys()), state="readonly", width=40)
    combo_imp.grid(row=0, column=1, sticky=tk.EW, pady=2)

    ttk.Label(frame_imp, text="Нижний предел:").grid(row=1, column=0, sticky=tk.W, pady=2)
    entry_imp_a = NumEntry(frame_imp, width=12)
    entry_imp_a.insert(0, "0")
    entry_imp_a.grid(row=1, column=1, sticky=tk.W, pady=2)

    ttk.Label(frame_imp, text="Верхний предел:").grid(row=2, column=0, sticky=tk.W, pady=2)
    entry_imp_b = NumEntry(frame_imp, width=12)
    entry_imp_b.insert(0, "1")
    entry_imp_b.grid(row=2, column=1, sticky=tk.W, pady=2)

    ttk.Label(frame_imp, text="Число разбиений n:").grid(row=3, column=0, sticky=tk.W, pady=2)
    entry_imp_n = NumEntry(frame_imp, is_int=True, width=12)
    entry_imp_n.insert(0, "1000")
    entry_imp_n.grid(row=3, column=1, sticky=tk.W, pady=2)

    ttk.Label(frame_imp, text="Метод:").grid(row=4, column=0, sticky=tk.W, pady=2)
    var_imp_method_name = tk.StringVar(value="Трапеции")
    combo_imp_method = ttk.Combobox(
        frame_imp,
        textvariable=var_imp_method_name,
        values=list(method_name_to_key.keys()),
        state="readonly",
        width=28,
    )
    combo_imp_method.grid(row=4, column=1, sticky=tk.EW, pady=2)

    text_imp_result = scrolledtext.ScrolledText(frame_imp, height=10, width=70, wrap=tk.WORD)
    text_imp_result.grid(row=5, column=0, columnspan=2, sticky=tk.NSEW, pady=8)
    frame_imp.columnconfigure(1, weight=1)
    frame_imp.rowconfigure(5, weight=1)

    def run_improper():
        a = entry_imp_a.float()
        b = entry_imp_b.float()
        n = entry_imp_n.int()
        if a is None:
            messagebox.showerror("Ошибка", "Введите число в поле «Нижний предел».")
            return
        if b is None:
            messagebox.showerror("Ошибка", "Введите число в поле «Верхний предел».")
            return
        if n is None or n < 2:
            messagebox.showerror("Ошибка", "n должно быть целым >= 2.")
            return
        if a >= b:
            messagebox.showerror("Ошибка", "Должно быть a < b.")
            return
        imp = imp_by_name[var_imp_name.get()]
        if imp["id"] == "imp_1sqrtx" and a < 0:
            messagebox.showerror("Ошибка", "Для 1/√x нижний предел a должен быть ≥ 0.")
            return
        if imp["id"] == "imp_1sqrt1mx" and b > 1:
            messagebox.showerror("Ошибка", "Для 1/√(1−x) верхний предел b не должен превышать 1.")
            return
        if imp["id"] == "imp_1sqrt_abs":
            c0 = imp.get("singularity_point", 0.5)
            if a >= c0 or b <= c0:
                messagebox.showerror("Ошибка", "Для 1/√|x−0.5| точка 0.5 должна быть строго внутри (a, b).")
                return
        conv, msg = check_improper_convergence(imp["id"], a, b)
        text_imp_result.delete(1.0, tk.END)
        text_imp_result.insert(tk.END, f"Сходимость: {msg}\n\n")
        if not conv:
            text_imp_result.insert(tk.END, "Интеграл не существует.")
            return
        method_key = method_name_to_key.get(var_imp_method_name.get(), "trapezoid")
        sing = imp.get("singularity", "a")
        c = imp.get("singularity_point")
        try:
            I = improper_integrate(
                imp["f"], a, b, n, method_key, sing, c
            )
            text_imp_result.insert(tk.END, f"Значение (численно): {I}\n")
            text_imp_result.insert(tk.END, f"Метод: {var_imp_method_name.get()}, n = {n}")
        except Exception as e:
            text_imp_result.insert(tk.END, f"Ошибка: {e}")

    ttk.Button(frame_imp, text="Проверить сходимость и вычислить", command=run_improper).grid(
        row=6, column=0, columnspan=2, pady=6
    )

    root.mainloop()


if __name__ == "__main__":
    main()
