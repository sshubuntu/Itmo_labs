.data
    .org 0x100
    input_address: .word 0x80 ; адрес ввода
    output_address: .word 0x84 ; адрес вывода
    divider: .word 0x2; делитель
    n: .word 0x0 ; ближайшее нечетное число к исходному
    total: .word 0x0 ; ячейка для результата
    sub_n: .word 0x0; промежуточная ячейка для числа, которое возводится в квадрат
    just_1: .word 0x1; единица для прибавления и вычитания

.text
    .org 0x250
_start:
    load input_address
    load_acc ; грузим значение из ввода
    beqz n_less_0 ; если число 0, то -1
    ble n_less_0 ; если число < 0, то -1
    store n

check_odd:
    rem divider
    bnez pov_2 
    load n
    sub just_1
    store n

pov_2:
    load n
    div divider ; сумма нечетных чисел до нечетного n 
    add just_1  ; равна квадрату числа n/2+1
    store sub_n
    mul sub_n ; возводим в квадрат
    bvs overflow ; проверка на переполнение
    store total ; выгружаем результат
    jmp normal_load_total


n_less_0: 
    load_imm -1 ; грузим -1 если число не из области определения
    jmp stop

overflow:
    load_imm 0xCCCCCCCC ; грузим если переполнение
    jmp stop

normal_load_total: 
    load total

stop:
    store_ind output_address ; выгружаем результат в ячейку вывода
    halt