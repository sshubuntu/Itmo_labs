import numpy as np
import matplotlib.pyplot as plt

def f(x):
    return x**3 + 2.64*x**2 - 5.41*x - 11.76

x = np.linspace(-4, 3, 1000)
y = f(x)

plt.figure(figsize=(10, 6))
plt.plot(x, y, label=r'$f(x) = x^3 + 2.64x^2 - 5.41x - 11.76$', linewidth=2)
plt.axhline(0, color='gray', linestyle='--', linewidth=0.5)  # Ось X
plt.axvline(0, color='gray', linestyle='--', linewidth=0.5)  # Ось Y

intervals = [(-3.2, -3.1), (-1.7, -1.6), (2.2, 2.3)]
colors = ['red', 'green', 'blue']
for (a, b), color in zip(intervals, colors):
    x_fill = np.linspace(a, b, 100)
    plt.fill_between(x_fill, f(x_fill), alpha=0.2, color=color)
    plt.axvline(a, color=color, linestyle=':', linewidth=0.8)
    plt.axvline(b, color=color, linestyle=':', linewidth=0.8)

plt.title('Графическое отделение корней', fontsize=14)
plt.xlabel('x', fontsize=12)
plt.ylabel('f(x)', fontsize=12)
plt.grid(True, alpha=0.3)
plt.legend(fontsize=10)
plt.tight_layout()
plt.savefig('graph_roots_isolation.png', dpi=300)
plt.show()