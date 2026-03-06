import numpy as np
from typing import Tuple


class GaussSeidelSolver:
    def __init__(self, A: np.ndarray, b: np.ndarray, epsilon: float = 1e-6, max_iterations: int = 1000):
        self.A = A.copy()
        self.b = b.copy()
        self.epsilon = epsilon
        self.max_iterations = max_iterations
        self.n = len(b)
        self.iterations = 0
        self.errors = []

    def check_diagonal_dominance(self, matrix: np.ndarray) -> bool:
        for i in range(len(matrix)):
            diagonal = abs(matrix[i, i])
            row_sum = sum(abs(matrix[i, j]) for j in range(len(matrix)) if j != i)
            if diagonal <= row_sum:
                return False
        return True
    
    def try_rearrange_for_dominance(self) -> Tuple[bool, np.ndarray, np.ndarray]:
        A = self.A.copy()
        b = self.b.copy()
        n = self.n
        
        used_rows = set()
        new_A = np.zeros_like(A)
        new_b = np.zeros_like(b)
        
        for new_pos in range(n):
            best_row = -1
            best_score = -1
            
            for old_row in range(n):
                if old_row in used_rows:
                    continue
                
                diagonal_val = abs(A[old_row, new_pos])
                row_sum = sum(abs(A[old_row, j]) for j in range(n) if j != new_pos)
                
                if diagonal_val > row_sum:
                    score = diagonal_val - row_sum
                    if score > best_score:
                        best_score = score
                        best_row = old_row
            
            if best_row == -1:
                for old_row in range(n):
                    if old_row not in used_rows:
                        diagonal_val = abs(A[old_row, new_pos])
                        if diagonal_val > best_score:
                            best_score = diagonal_val
                            best_row = old_row
            
            if best_row != -1:
                new_A[new_pos] = A[best_row]
                new_b[new_pos] = b[best_row]
                used_rows.add(best_row)
            else:
                return False, self.A, self.b
        
        if self.check_diagonal_dominance(new_A):
            return True, new_A, new_b
      
        return False, new_A, new_b
    
    def matrix_norm(self, matrix: np.ndarray) -> float:
        return np.linalg.norm(matrix, ord=2)
    
    def solveMatrix(self) -> Tuple[np.ndarray, bool, str]:
        has_dominance = self.check_diagonal_dominance(self.A)
        
        if not has_dominance:
            success, A_new, b_new = self.try_rearrange_for_dominance()
            if success:
                self.A = A_new
                self.b = b_new

        
        n = self.n
        C = np.zeros_like(self.A)
        d = np.zeros_like(self.b)
        
        
        for i in range(n):
            d[i] = self.b[i] / self.A[i, i]
            for j in range(n):
                if i != j:
                    C[i, j] = -self.A[i, j] / self.A[i, i]
        
        x = np.zeros(n)

        self.iterations = 0
        self.errors = []
        
        for iteration in range(self.max_iterations):
            x_prev = x.copy()
            for i in range(n):
                x[i] = d[i]
                for j in range(n):
                    if j < i:
                        x[i] += C[i, j] * x[j]
                    elif j > i:
                        x[i] += C[i, j] * x_prev[j]
            
            error = np.abs(x - x_prev)
            self.errors.append(error.copy())
            
            if np.max(error) < self.epsilon:
                self.iterations = iteration + 1
                return x, True, "Решение найдено"
        
        self.iterations = self.max_iterations
        return x, False, "Не удалось посчитать"
