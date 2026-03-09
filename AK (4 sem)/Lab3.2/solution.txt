.data
\ Определение адресов ввода-вывода
addr_in:    .word 0x80
addr_out:   .word 0x84

\ Маски для выделения байтов
m_byte1:    .word 0x000000FF
m_byte2:    .word 0x0000FF00
m_byte3:    .word 0x00FF0000
m_byte4:    .word 0xFF000000

.text
.org 0x200
_start:
    @p addr_in a! @     \ Введенное число в стек        
    swap  
    @p addr_out a! !    \ Сохраняем ответ в 
    halt

shift_l8:               \ Сдвиг влево на 8 бит
    2* 2* 2* 2* 2* 2* 2* 2* ;

shift_l24:              \ Сдвиг влево на 24 бита
    shift_l8 
    shift_l8 
    shift_l8 
;

shift_r8:               \ Сдвиг вправо на 8 бит
    2/ 2/ 2/ 2/ 2/ 2/ 2/ 2/ 
;

shift_r24:              \ Сдвиг вправо на 24 бита
    shift_r8 
    shift_r8 
    shift_r8 
;

\ Для удобного представления слева-направо обозначим позиции и номера байтов слева направо
\ 0.    Byte4 (Старший байт)
\ 1.    Byte3 
\ 2.    Byte2 
\ 3.    Byte1

swap:
    dup                 \ Дублируем число
    
    \ Byte1 -> 0.
    @p m_byte1  
    and                 \ Оставляем только Byte1
    shift_l24           \ Сдвигаем в 0.
    >r                  \ Byte1 на стек возвратов
    
    \ Byte2 -> 1.
    dup
    @p m_byte2 
    and                 \ Оставляем только Byte2
    shift_l8            \ Сдвигаем в 1.
    r>                  \ Byte1 со стека возвратов на стек данных
    xor                 \ Byte1 xor Byte2
    >r                  \ Byte1 + Byte2 на стек возвратов
    
    \ Byte3 -> 2.
    dup
    @p m_byte3
    and
    shift_r8 
    @p m_byte2          \ Маска для защиты от перемещения знакового бита в 2/
    and
    r>                  \ Byte1 + Byte2 со стека возвратов на стек данных
    xor                 \ Byte1 + Byte2 xor Byte3
    >r                  \ Byte1 + Byte2 + Byte3 на стек возвратов
    
    \ Byte4 -> 3.
    @p m_byte4
    and
    shift_r24
    @p m_byte1          \ Маска от знаковых битов
    and
    r>                  \ Byte1 + Byte2 + Byte3 со стека возвратов на стек данных
    xor                 \ Byte1 + Byte2 + Byte3 xor Byte4       
    ;                   \ Возврат из функции, на вершине стека результат.