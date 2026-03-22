.data

greet_buf:    .byte 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f, 0x5f

input_addr:    .word 0x80

output_addr:    .word 0x84

question:    .byte 'W', 'h', 'a', 't', ' ', 'i', 's', ' ', 'y', 'o', 'u', 'r', ' ', 'n', 'a', 'm', 'e', '?', 10, 0

hello_prefix:    .byte 'H', 'e', 'l', 'l', 'o', ',', ' '

name_buf:    .byte 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

.text

.org 0x200

_start:
    j nest_main__entry

nest_main__entry:
    lui sp, 1
    addi sp, sp, -4

    lui t0, %hi(input_addr)
    addi t0, t0, %lo(input_addr)
    lw s6, 0(t0)

    lui t0, %hi(output_addr)
    addi t0, t0, %lo(output_addr)
    lw s7, 0(t0)

    jal ra, nest_main__run
    halt

; ---------------------------------------------------------------------------
; nest_main__run: печать вопроса, чтение строки, ветвление (вызывает вложенные
; процедуры печати и буфера).
; ---------------------------------------------------------------------------
nest_main__run:
    addi sp, sp, -8
    sw ra, 0(sp)

    jal ra, nest_str__print_question

    jal ra, nest_line__read

    addi t0, zero, -1
    beq a0, t0, nest_main__domain_fail

    beqz a0, nest_main__overflow_fail

    jal ra, nest_buf__build_greet

    jal ra, nest_str__print_greet_out

    j nest_main__finish

nest_main__domain_fail:
    jal ra, nest_err__put_minus_one
    j nest_main__finish

nest_main__overflow_fail:
    jal ra, nest_err__put_cc_word

nest_main__finish:
    lw ra, 0(sp)
    addi sp, sp, 8
    jr ra

; ---------------------------------------------------------------------------
; nest_io__putc: a0 — байт на вывод (низшие 8 бит).
; ---------------------------------------------------------------------------
nest_io__putc:
    sb a0, 0(s7)
    jr ra

; ---------------------------------------------------------------------------
; nest_io__getc: байт ввода в a0 (низший байт слова с порта).
; ---------------------------------------------------------------------------
nest_io__getc:
    lw a0, 0(s6)
    addi t0, zero, 0xff
    and a0, a0, t0
    jr ra

; ---------------------------------------------------------------------------
; nest_str__print_cstr: a1 — указатель на C-строку; вызывает nest_io__putc.
; ---------------------------------------------------------------------------
nest_str__print_cstr:
    addi sp, sp, -8
    sw ra, 0(sp)

nest_str__print_cstr_loop:
    lw t1, 0(a1)
    addi t2, zero, 0xff
    and t3, t1, t2
    beqz t3, nest_str__print_cstr_done

    mv a0, t3
    jal ra, nest_io__putc

    addi a1, a1, 1
    j nest_str__print_cstr_loop

nest_str__print_cstr_done:
    lw ra, 0(sp)
    addi sp, sp, 8
    jr ra

; ---------------------------------------------------------------------------
; nest_str__print_question: вызывает nest_str__print_cstr.
; ---------------------------------------------------------------------------
nest_str__print_question:
    addi sp, sp, -8
    sw ra, 0(sp)

    lui a1, %hi(question)
    addi a1, a1, %lo(question)
    jal ra, nest_str__print_cstr

    lw ra, 0(sp)
    addi sp, sp, 8
    jr ra

; ---------------------------------------------------------------------------
; nest_str__print_greet_out: печать приветствия из greet_buf до '\0'.
; ---------------------------------------------------------------------------
nest_str__print_greet_out:
    addi sp, sp, -8
    sw ra, 0(sp)

    lui a1, %hi(greet_buf)
    addi a1, a1, %lo(greet_buf)
    jal ra, nest_str__print_cstr

    lw ra, 0(sp)
    addi sp, sp, 8
    jr ra

; ---------------------------------------------------------------------------
; nest_mem__set_range: заполнить a1 байт по адресу a0 значением (младший байт) a2.
; ---------------------------------------------------------------------------
nest_mem__set_range:
    addi sp, sp, -8
    sw ra, 0(sp)

nest_mem__set_range_loop:
    beqz a1, nest_mem__set_range_done

    sb a2, 0(a0)
    addi a0, a0, 1
    addi a1, a1, -1
    j nest_mem__set_range_loop

nest_mem__set_range_done:
    lw ra, 0(sp)
    addi sp, sp, 8
    jr ra

; ---------------------------------------------------------------------------
; nest_buf__build_greet: длина имени в a0; вызывает nest_mem__set_range.
; ---------------------------------------------------------------------------
nest_buf__build_greet:
    addi sp, sp, -16
    sw ra, 0(sp)
    sw s4, 4(sp)

    mv s4, a0

    lui a0, %hi(greet_buf)
    addi a0, a0, %lo(greet_buf)
    addi a1, zero, 32
    addi a2, zero, 0x5f
    jal ra, nest_mem__set_range

    lui t1, %hi(hello_prefix)
    addi t1, t1, %lo(hello_prefix)
    lui t2, %hi(greet_buf)
    addi t2, t2, %lo(greet_buf)
    addi t3, zero, 0
    addi t4, zero, 7

nest_buf__copy_hello:
    beq t3, t4, nest_buf__copy_name_start

    lw t5, 0(t1)
    addi t6, zero, 0xff
    and t5, t5, t6
    sb t5, 0(t2)

    addi t1, t1, 1
    addi t2, t2, 1
    addi t3, t3, 1
    j nest_buf__copy_hello

nest_buf__copy_name_start:
    lui t1, %hi(name_buf)
    addi t1, t1, %lo(name_buf)
    mv t3, zero
    addi t6, zero, 0xff

; Имя для приветствия: до первого NUL (как takewhile в hello_user_cstr).
nest_buf__copy_name:
    beq t3, s4, nest_buf__tail

    lw t5, 0(t1)
    and t5, t5, t6
    beqz t5, nest_buf__tail

    sb t5, 0(t2)

    addi t1, t1, 1
    addi t2, t2, 1
    addi t3, t3, 1
    j nest_buf__copy_name

nest_buf__tail:
    addi t0, zero, 0x21
    sb t0, 0(t2)
    addi t2, t2, 1
    sb zero, 0(t2)

    lw s4, 4(sp)
    lw ra, 0(sp)
    addi sp, sp, 16
    jr ra

; ---------------------------------------------------------------------------
; nest_line__read: read_line(..., 23) — максимум 22 символа до '\n';
; возврат: a0 = длина (0 — пустая или переполнение), -1 — недопустимый символ.
; ---------------------------------------------------------------------------
nest_line__read:
    addi sp, sp, -8
    sw ra, 0(sp)

    lui a2, %hi(name_buf)
    addi a2, a2, %lo(name_buf)
    addi a3, zero, 0

nest_line__read_loop:
    jal ra, nest_io__getc

    addi t0, zero, 10
    beq a0, t0, nest_line__read_done

    addi t0, zero, 22
    beq a3, t0, nest_line__read_ov_stop

    beqz a0, nest_line__read_store

    addi t0, zero, 32
    bgt t0, a0, nest_line__read_bad

    addi t0, zero, 126
    bgt a0, t0, nest_line__read_bad

nest_line__read_store:
    sb a0, 0(a2)
    addi a2, a2, 1
    addi a3, a3, 1
    j nest_line__read_loop

nest_line__read_done:
    mv a0, a3
    lw ra, 0(sp)
    addi sp, sp, 8
    jr ra

nest_line__read_bad:
    addi a0, zero, -1
    lw ra, 0(sp)
    addi sp, sp, 8
    jr ra

; Переполнение read_line: уже прочитано 23-й символ (не \n), дальше не читаем —
; хвост s[buf_size:] остаётся во входной очереди (как в Python).
nest_line__read_ov_stop:
    addi a0, zero, 0
    lw ra, 0(sp)
    addi sp, sp, 8
    jr ra

; ---------------------------------------------------------------------------
; nest_err__put_minus_one: одно слово -1 на 0x84 (как в assert варианта).
; ---------------------------------------------------------------------------
nest_err__put_minus_one:
    addi t4, zero, -1
    sw t4, 0(s7)
    jr ra

; ---------------------------------------------------------------------------
; nest_err__put_cc_word: overflow_error_value 0xCCCCCCCC (-858993460).
; ---------------------------------------------------------------------------
nest_err__put_cc_word:
    lui t4, %hi(0xCCCCCCCC)
    addi t4, t4, %lo(0xCCCCCCCC)
    sw t4, 0(s7)
    jr ra
