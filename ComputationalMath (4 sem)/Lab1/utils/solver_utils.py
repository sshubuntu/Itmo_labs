from utils.gauss_seidel import GaussSeidelSolver

def format_results(A, b, epsilon, solver, solution, success, message):
    result = ["РЕЗУЛЬТАТЫ:", f"A:", str(A), f"\nB:", str(b), f"\nТочность: {epsilon}\n"]
    
    original_dominance = solver.check_diagonal_dominance(A)
    final_dominance = solver.check_diagonal_dominance(solver.A)

    if not original_dominance and not final_dominance:
        result.insert(-1, "Не удалось достичь диагонального преобладания")
    
    result.append(f"Спектральная норма матрицы: {solver.matrix_norm(solver.A):.10f}\n")
    
    if success and solution is not None:
        result.extend([f"Статус: {message} \n Количество итераций: {solver.iterations} \n\nВектор неизвестных:"])
        result.extend([f"  x{i+1} = {x:.10f}" for i, x in enumerate(solution)])
        
        if solver.errors:
            result.append("Вектор погрешностей:")
            result.extend([f"  |x{i+1}^(k) - x{i+1}^(k-1)| = {err:.10e}" for i, err in enumerate(solver.errors[-1])])
    else:
        result.append(f"Ошибка: {message}")
    
    return "\n".join(result)


def solve_system_from_data(A, b, epsilon):
    solver = GaussSeidelSolver(A, b, epsilon=epsilon)
    solution, success, message = solver.solveMatrix()
    formatted_result = format_results(A, b, epsilon, solver, solution, success, message)
    return formatted_result
