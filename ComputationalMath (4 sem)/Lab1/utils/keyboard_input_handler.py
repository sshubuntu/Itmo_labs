import flet as ft
import numpy as np


def on_field_focus(e):
    if e.control.value == "0":
        e.control.value = ""
        e.control.update()

def on_field_blur(e):
    if not e.control.value:
        e.control.value = "0"
        e.control.update()

def create_matrix_inputs(size, matrix_inputs, vector_inputs, matrix_container, vector_container, page):
    matrix_inputs.clear()
    vector_inputs.clear()
    
    field_config = {
        'width': 80,
        'height': 40,
        'text_align': ft.TextAlign.CENTER,
        'value': "0",
        'keyboard_type': ft.KeyboardType.NUMBER,
        'on_focus': on_field_focus,
        'on_blur': on_field_blur
    }
    
    matrix_rows = []
    for _ in range(size):
        row = [ft.TextField(**field_config) for _ in range(size)]
        matrix_inputs.extend(row)
        matrix_rows.append(ft.Row(row, spacing=5))
    
    vector_fields = [ft.TextField(**field_config) for i in range(size)]
    vector_inputs.extend(vector_fields)
    
    matrix_container.controls = matrix_rows
    vector_container.controls = [ft.Row(vector_fields, spacing=5)]
    page.update()


def get_matrix_from_inputs(matrix_size, matrix_inputs, vector_inputs):
    n = matrix_size
    A = np.array([[float(matrix_inputs[i * n + j].value or "0") for j in range(n)] for i in range(n)])
    b = np.array([float(vector_inputs[i].value or "0") for i in range(n)])
    return A, b
