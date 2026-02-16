import numpy as np


def load_from_file(file_path: str):
    with open(file_path, 'r', encoding='utf-8') as f: lines = [line.strip() for line in f.readlines()]
    
    n = int(lines[0])
    eps = float(lines[1]) if len(lines) > 1 else 1e-6
    
    A = np.array([[float(x) for x in lines[2 + i].split()] for i in range(n)])
    b = np.array([float(x) for x in lines[2 + n].split()])
    
    return A, b, eps
