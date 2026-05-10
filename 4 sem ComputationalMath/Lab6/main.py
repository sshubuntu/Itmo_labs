"""
Главный модуль для запуска приложения решения ОДУ
Вариант 18
"""
import tkinter as tk
from src.gui import ODESolverGUI


def main():
    """Точка входа в приложение"""
    root = tk.Tk()
    app = ODESolverGUI(root)
    root.mainloop()


if __name__ == "__main__":
    main()
