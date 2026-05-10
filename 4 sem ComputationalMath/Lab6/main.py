import tkinter as tk
from src.gui import ODESolverGUI

def main():
    root = tk.Tk()
    app = ODESolverGUI(root)
    root.mainloop()

if __name__ == "__main__":
    main()
