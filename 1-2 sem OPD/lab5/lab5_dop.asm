ORG 0x500
SPACE:
	CLA
	OUT 0x10
    OUT 0x10
	RET
STOP:
    HLT
START:
    CALL SPACE
    StartAddr : WORD 0x200
    CurAddr: WORD ?
    COUNT: WORD 0x0005
    LD StartAddr
    ST CurAddr
    INPUT:
        IN 0x19 ; 
        AND #0x40
        BEQ INPUT
        IN 0x18

    ISENTER:
        CMP #0x13
        BEQ STOP
        

    ISDIGIT: ; проверка, что символ является цифрой или точкой, иначе игнорируем
        CMP #0x30
        BMI ISPOINT
        CMP #0x3A
        BPL INPUT
        JUMP LOADING

    ISPOINT:
        CMP #0x2E
        BZC INPUT
    
    LOADING: ; выгрузка в массив с введенными числами
        ST (CurAddr)+ 
        
    LOOP COUNT
    JUMP INPUT

    TRANSFERING: ; необходимо для соединения ячеек, содержащих введеные значения в переменные POINT, DAY, MONTH и перевода из ASCII в числа
        MASK: WORD 0x3030
        LD (StartAddr)+
        SWAB
        ADD (StartAddr)+
        SUB MASK
        ST $DAY
        LD (StartAddr)+
        ST $POINT
        LD (StartAddr)+
        SWAB
        ADD (StartAddr)+
        SUB MASK
        ST $MONTH
        JUMP VALIDATE
        
    
    POINT:  WORD ?
    DAY : WORD 0x0108
    MONTH: WORD ?
    
    VALIDATE: ; проверка валидности даты
        CHECKPOINT:
            LD $POINT
            CMP #0x2E
            BNE ERROR
			JUMP LAUNCH_MONTH

        

        LAUNCH_MONTH:
            LD $MONTH
            CMP #0x1
            BEQ JANUARY
            
            CMP #0x2
            BEQ FEBRUARY

            CMP #0x3
            BEQ MARCH

            CMP #0x4
            BEQ APRIL

            CMP #0x5
            BEQ MAY

            CMP #0x6
            BEQ JUNE

            CMP #0x7
            BEQ JULY

            CMP #0x8
            BEQ AUGUST

            CMP #0x9
            BEQ SEPTEMBER

            CMP OCTOBERMASK
            BEQ OCTOBER

            CMP NOVEMBERMASK
            BEQ NOVEMBER

            CMP DECEMBERMASK
            BEQ DECEMBER

            JUMP ERROR
        
        

        DEFAULTMONTH:
            LD $DAY
            BEQ ERROR
            RET

        MONTH29:
            CALL DEFAULTMONTH
            CMP FEBRUARY_MASK 
            BPL ERROR
            RET

        MONTH30:
            CALL DEFAULTMONTH
            CMP MONTH30MASK
            BPL ERROR
            RET

        MONTH31:
            CALL DEFAULTMONTH
            CMP MONTH31MASK
            BPL ERROR
            RET
        ; нужны для сравнения и валидации данных, т.к. прямая загрузка вмещает [-128, 127]
        DAY19MASK: WORD 0x200
        DAY20MASK: WORD 0x201
        DAY21MASK: WORD 0x202
        DAY22MASK: WORD 0x203
        DAY23MASK: WORD 0x204
        DAY24MASK: WORD 0x205

		FEBRUARY_MASK: WORD 0x20A
        MONTH30MASK: WORD 0x301
        MONTH31MASK: WORD 0x302

		OCTOBERMASK: WORD 0x100
        NOVEMBERMASK: WORD 0x101
        DECEMBERMASK: WORD 0x102

        JANUARY:
            CALL MONTH31
            CMP DAY22MASK
            BMI CAPRICORN 
            JUMP AQUARIUS

        FEBRUARY:
            CALL MONTH29
            CMP DAY19MASK
            BMI AQUARIUS
            JUMP FISH
        
        MARCH:
            CALL MONTH31
            CMP DAY21MASK
            BMI FISH
            JUMP ARIES

        APRIL:
            CALL MONTH30
            CMP DAY20MASK
            BMI ARIES
            JUMP TAURUS

        MAY:
            CALL MONTH31
            CMP DAY21MASK
            BMI TAURUS
            JUMP TWINS
        
        JUNE:
            CALL MONTH30
            CMP DAY22MASK
            BMI TWINS
            JUMP JUMPCANCER

        JULY:
            CALL MONTH31
            CMP DAY23MASK
            BMI JUMPCANCER
            JUMP JUMPLION

        AUGUST:
            CALL MONTH31
            CMP DAY23MASK
            BMI JUMPLION
            JUMP JUMPVIRGIN

        SEPTEMBER:
            CALL MONTH30
            CMP DAY23MASK
            BMI JUMPVIRGIN
            JUMP JUMPSCALES

        OCTOBER:
            CALL MONTH31
            CMP DAY24MASK
            BMI JUMPSCALES
            JUMP JUMPSCORPION

        NOVEMBER:
            CALL MONTH30
            CMP DAY23MASK
            BMI JUMPSCORPION
            JUMP JUMPSAGITTARIUS

        DECEMBER:
            CALL MONTH31
            CMP DAY22MASK
            BMI JUMPSAGITTARIUS
            JUMP CAPRICORN

    
    
ERROR:
	LD #36
	OUT 0x10
	CLA
	OUT 0x10
	LD #126
	OUT 0x10
	LD #129
	OUT 0x10
    HLT

CAPRICORN:
	LD #32
	OUT 0x10
	LD #64
	OUT 0x10
	LD #60
	OUT 0x10
	LD #65
	OUT 0x10
	LD #34
	OUT 0x10
	LD #28
	OUT 0x10   
	LD #36
	OUT 0x10 
	LD #36
	OUT 0x10 
	LD #24
	OUT 0x10
	HLT

AQUARIUS:
	COUNT1: WORD 0x0004
	LD #18
	OUT 0x10
	LD #36
	OUT 0x10
	LOOP COUNT1
	JUMP AQUARIUS
	HLT

FISH:
	LD #137
	OUT 0x10
	LD #74
	OUT 0x10
	LD #60
	OUT 0x10
	LD #8
	OUT 0x10
	LD #8
	OUT 0x10
	LD #60
	OUT 0x10
	LD #74
	OUT 0x10
	LD #137
	OUT 0x10
	HLT

ARIES:
	LD #96
	OUT 0x10
	LD #144
	OUT 0x10
	LD #128
	OUT 0x10
	LD #255
	OUT 0x10
	LD #128
	OUT 0x10
	LD #144
	OUT 0x10
	LD #96
	OUT 0x10
	HLT

TAURUS:
	LD #64
	OUT 0x10
	LD #64
	OUT 0x10
	LD #46
	OUT 0x10
	LD #17
	OUT 0x10
	LD #17
	OUT 0x10
	LD #46
	OUT 0x10
	LD #64
	OUT 0x10
	LD #64
	OUT 0x10
	HLT

TWINS:
	LD #129
	OUT 0x10
	LD #129
	OUT 0x10
	LD #126
	OUT 0x10
	LD #36
	OUT 0x10
	LD #36
	OUT 0x10
	LD #126
	OUT 0x10
	LD #129
	OUT 0x10
	LD #129
	OUT 0x10
	HLT

;костыль, т.к. у нас относительная адресация и сноски работают от -127 до 128
JUMPCANCER: JUMP CANCER
JUMPLION: JUMP LION
JUMPVIRGIN: JUMP VIRGIN
JUMPSCALES: JUMP SCALES
JUMPSCORPION: JUMP SCORPION
JUMPSAGITTARIUS: JUMP SAGITTARIUS

CANCER:
	LD #76
	OUT 0x10
	LD #146
	OUT 0x10
	LD #146
	OUT 0x10
	LD #141
	OUT 0x10
	LD #177
	OUT 0x10
	LD #73
	OUT 0x10
	LD #73
	OUT 0x10
	LD #50
	OUT 0x10
	HLT

LION:
	LD #6
	OUT 0x10
	LD #9
	OUT 0x10
	LD #57
	OUT 0x10
	LD #70
	OUT 0x10
	LD #128
	OUT 0x10
	LD #128
	OUT 0x10
	LD #124
	OUT 0x10
	LD #2
	OUT 0x10
	LD #4
	OUT 0x10
	HLT

VIRGIN:
	LD #64
	OUT 0x10
	LD #128
	OUT 0x10
	LD #124
	OUT 0x10
	LD #128
	OUT 0x10
	LD #124
	OUT 0x10
	LD #129
	OUT 0x10
	LD #126
	OUT 0x10
	LD #69
	OUT 0x10
	LD #73
	OUT 0x10
	LD #48
	OUT 0x10
	HLT

SCALES:
	LD #10
	OUT 0x10
	LD #10
	OUT 0x10
	LD #10
	OUT 0x10
	LD #50
	OUT 0x10
	LD #66
	OUT 0x10
	LD #66
	OUT 0x10
	LD #50
	OUT 0x10
	LD #10
	OUT 0x10
	LD #10
	OUT 0x10
	LD #10
	OUT 0x10
	HLT

SCORPION:
	LD #64
	OUT 0x10
	LD #128
	OUT 0x10
	LD #124
	OUT 0x10
	LD #128
	OUT 0x10
	LD #124
	OUT 0x10
	LD #128
	OUT 0x10
	LD #126
	OUT 0x10
	LD #2
	OUT 0x10
	LD #7
	OUT 0x10
	LD #2
	OUT 0x10
	HLT

SAGITTARIUS:
	LD #1
	OUT 0x10
	LD #2
	OUT 0x10
	LD #36
	OUT 0x10
	LD #152
	OUT 0x10
	LD #152
	OUT 0x10
	LD #164
	OUT 0x10
	LD #192
	OUT 0x10
	LD #248
	OUT 0x10
	HLT




