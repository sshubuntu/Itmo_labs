import flet as ft
from tkinter import Tk, filedialog
from utils.file_input_handler import load_from_file
from utils.keyboard_input_handler import create_matrix_inputs, get_matrix_from_inputs
from utils.solver_utils import solve_system_from_data


def main(page: ft.Page):
    page.title = "Лабораторная работа №1. Решение СЛАУ методом Гаусса-Зейделя"
    page.window.width = 1200
    page.window.height = 800
    page.scroll = ft.ScrollMode.AUTO
    
    matrix_size = 3
    input_method = "keyboard"
    
    result_text = ft.Text("", size=14, selectable=True, color="white")
    matrix_inputs = []
    vector_inputs = []
    selected_file_path = None
    selected_file_label = ft.Text("Файл не выбран", size=12, color="grey400")
    
    size_input = ft.TextField(label="Размерность <= 20", value="3", width=200, keyboard_type=ft.KeyboardType.NUMBER)
    epsilon_input = ft.TextField(label="Epsilon:", value="0.000001", width=200)
    
    input_method_radio = ft.RadioGroup(
        content=ft.Row([ft.Radio(value="keyboard", label="С клавиатуры"), ft.Radio(value="file", label="Из файла")]),
        value="keyboard"
    )
    
    matrix_container = ft.Column([], visible=True)
    vector_container = ft.Column([], visible=True)
    keyboard_input_container = ft.Container(visible=True)
    file_input_container = ft.Container(visible=False)
    
    def choose_file(e):
        nonlocal selected_file_path
        root = Tk()
        root.withdraw()
        root.attributes("-topmost", True)
        path = filedialog.askopenfilename(
            title="Выберите файл с данными (.txt)",
            filetypes=[("Текстовые файлы", "*.txt"), ("Все файлы", "*.*")]
        )
        root.destroy()
        if path:
            selected_file_path = path
            selected_file_label.value = path
        else:
            selected_file_path = None
            selected_file_label.value = "Файл не выбран"
        page.update()

    def solve_system(e):
        result_text.value = ""
        try:
            if input_method == "file":
                if not selected_file_path:
                    result_text.value = "Сначала выберите файл (.txt)"
                    page.update()
                    return
                A, b, epsilon = load_from_file(selected_file_path)
            else:
                A, b = get_matrix_from_inputs(matrix_size, matrix_inputs, vector_inputs)
                epsilon = float(epsilon_input.value or "0.000001")
            
            formatted_result = solve_system_from_data(A, b, epsilon)
            result_text.value = formatted_result
        except Exception as e:
            result_text.value = f"Ошибка: {e}"
        page.update()
    
    def on_size_change(e):
        nonlocal matrix_size
        try:
            new_size = int(size_input.value)
            if 1 <= new_size <= 20:
                matrix_size = new_size
                create_matrix_inputs(matrix_size, matrix_inputs, vector_inputs, matrix_container, vector_container, page)
            else:
                size_input.value = str(matrix_size)
                page.update()
        except ValueError:
            size_input.value = str(matrix_size)
            page.update()
    
    def on_input_method_change(e):
        nonlocal input_method
        input_method = input_method_radio.value
        keyboard_input_container.visible = (input_method == "keyboard")
        file_input_container.visible = (input_method == "file")
        page.update()
    
    size_input.on_submit = on_size_change
    input_method_radio.on_change = on_input_method_change
    create_matrix_inputs(matrix_size, matrix_inputs, vector_inputs, matrix_container, vector_container, page)
    
    keyboard_input_container.content = ft.Column([
        ft.Row(
            [size_input],
            alignment=ft.MainAxisAlignment.START,
            vertical_alignment=ft.CrossAxisAlignment.CENTER,
        ),
        epsilon_input,
        ft.Text("Матрица коэффициентов A:", size=14, weight=ft.FontWeight.BOLD),
        matrix_container,
        ft.Text("Вектор правых частей b:", size=14, weight=ft.FontWeight.BOLD),
        vector_container,
        ft.Button("Решить систему", on_click=solve_system, bgcolor="green400", width=300, height=40),
    ])
    
    file_input_container.content = ft.Column([
        ft.Row([
            ft.Button("Выбрать файл...", icon=ft.Icons.FOLDER_OPEN, on_click=choose_file),
            selected_file_label,
        ], alignment=ft.MainAxisAlignment.START, spacing=10),
        ft.Container(
            content=ft.Column([
                ft.Text("Формат файла:", size=14, weight=ft.FontWeight.BOLD, color="white"),
                ft.Text("1. Размерность n (n <= 20)", size=12, color="white"),
                ft.Text("2. Точность", size=12, color="white"),
                ft.Text("3. Следующие n строк: матрица", size=12, color="white"),
                ft.Text("4. Последняя строка: вектор правых частей b", size=12, color="white"),
                ft.Text("", size=8),
                ft.Text("Пример:", size=12, weight=ft.FontWeight.BOLD, color="white"),
                *[ft.Text(line, size=11, font_family="monospace", color="white") 
                  for line in ["3", "0.000001", "4 -1 1", "2 5 2", "1 2 4", "7 1 5"]],
            ], spacing=3),
            padding=10,
            border=ft.Border.all(1, "grey400"),
            border_radius=5,
            bgcolor="black"
        ),
        ft.Button("Решить систему из файла", on_click=solve_system, bgcolor="green400", width=300, height=40),
    ])
    
    page.add(ft.Container(
        content=ft.Column([
            ft.Row([ft.Text("Способ ввода данных:", size=16, weight=ft.FontWeight.BOLD)]),
            input_method_radio,
            ft.Divider(),
            keyboard_input_container,
            file_input_container,
            ft.Container(
                content=result_text,
                padding=10,
                border=ft.Border.all(1, "grey400"),
                border_radius=5,
                bgcolor="grey900",
                width=page.window.width - 40
            ),
        ], spacing=10, scroll=ft.ScrollMode.AUTO),
        padding=20
    ))


if __name__ == "__main__":
    ft.run(main)
