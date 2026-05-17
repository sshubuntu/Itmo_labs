# RISC-IV architecture diagram

В этой папке добавлены два файла:

- `risc-iv-architecture.drawio` - поблочная схема тракта данных RISC-IV в строгом черно-белом стиле.
- `README.md` - это пояснение к схеме и потактовое выполнение команд.

Схема построена по документации `risc-iv.md`. Так как документация задает ISA и семантику команд, но не задает точные бинарные форматы полей, декодер на схеме работает с логическими полями инструкции: `opcode`, `rd`, `rs1`, `rs2`, `k/offset`.

## Общая модель процессора

Реализация на схеме - многотактная, не конвейерная: в тракте данных одновременно выполняется одна команда. Каждая команда проходит общий цикл:

| Такт | Стадия | Что происходит |
|---|---|---|
| `T0` | `IF` | По адресу `PC` читается 32-битная команда, она записывается в `IR`; параллельно считается `PC + 4` и сохраняется в `PC+4 latch`. |
| `T1` | `ID` | Декодер определяет тип команды, читает регистры `rs1` и `rs2`, формирует immediate/offset. |
| `T2` | `EX` | ALU, shifter, comparator или mul/div выполняют основную операцию; для load/store считается эффективный адрес; для branch/jump считается целевой `PC`. |
| `T3` | `MEM / PC` | Если команда обращается к памяти, выполняется чтение или запись. В этот же такт фиксируется следующий `PC`: `PC+4`, `PC+k` или `rs1`. |
| `T4` | `WB` | Если команда пишет результат, writeback mux выбирает источник и записывает значение в `rd`. Запись в `Zero` игнорируется. |

Команды, которым не нужны память или writeback, просто не используют соответствующие блоки. Например, `sw` не использует `T4`, а `add` не использует память в `T3`.

## Шины и основные блоки

Основные разрядности, указанные на схеме:

| Сигнал | Размер |
|---|---:|
| Инструкция | 32 бита |
| `PC`, адрес памяти, адрес перехода | 32 бита |
| Данные регистров | 32 бита |
| Индексы `rd`, `rs1`, `rs2` | 5 бит |
| Запись байта для `sb` | 8 бит |
| Результат условия ветвления | 1 бит |

Ключевые блоки:

- `PC register` хранит адрес текущей команды.
- `Instruction memory` читает 4-байтную инструкцию.
- `IR` удерживает инструкцию на время выполнения.
- `Decoder + control unit` формирует управляющие сигналы: `RegWrite`, `MemRead`, `MemWrite`, `WBSel`, `ALUOp`, `PCSel`, `PCWrite`, режим байта/слова.
- `Immediate generator` формирует `signext(k[11:0])`, `imm20 << 12` для `lui`, offset для памяти и `k` для переходов.
- `Register file` содержит 32 регистра по 32 бита. Регистр `Zero` всегда равен нулю, запись в него отбрасывается.
- `ALU / shifter / logic` выполняет сложение, вычитание, сравнение `slt`, логические операции и сдвиги.
- `Mul/Div unit` выполняет `mul`, `mulh`, `div`, `rem`.
- `Branch comparator` проверяет условия ветвления, включая signed и unsigned сравнения.
- `Data memory + MMIO` обслуживает только `lw`, `lb`, `sw`, `sb`; memory-mapped I/O находится в этом же адресном пространстве.
- `Load extender` пропускает 32-битное слово для `lw` или расширяет знак байта для `lb`.
- `Writeback mux` выбирает, что записать в `rd`: ALU result, load result или `PC+4` для `jal`.
- `Next PC mux` выбирает следующий адрес: `PC+4`, `PC+k` или значение регистра для `jr`.

## Обозначения в таблицах

- `A = R[rs1]` - значение первого исходного регистра.
- `B = R[rs2]` - значение второго исходного регистра.
- `PC4 = PC + 4` - адрес следующей последовательной инструкции.
- `EA = A + signext(offset[11:0])` - эффективный адрес для load/store.
- `Target = PC + k` - адрес перехода для `j`, `jal`, branch-команд.
- `ALUOut` - результат EX-стадии.
- `MDR` - данные, прочитанные из памяти.
- `R[rd] <- value` не меняет состояние, если `rd = Zero`.

Директивы `%hi(symbol)` и `%lo(symbol)` не являются отдельными машинными командами. Их обрабатывает ассемблер: `%hi` обычно попадает в immediate команды `lui`, а `%lo` - в immediate команды `addi`.

## Data Movement Instructions

| Команда | `T1 ID` | `T2 EX` | `T3 MEM / PC` | `T4 WB` |
|---|---|---|---|---|
| `lui rd, k` | Декодер выделяет `rd` и `k`; immediate generator делает `(k & 0x000FFFFF) << 12`. | `ALUOut <- imm20 << 12`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `mv rd, rs` | Читается `A = R[rs]`. | `ALUOut <- A`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `sw rs2, offset(rs1)` | Читаются `A = R[rs1]`, `B = R[rs2]`, формируется `signext(offset[11:0])`. | `EA <- A + signext(offset[11:0])`. | `M[EA] <- B`, записываются 32 бита; `PC <- PC4`. | Нет записи в регистр. |
| `sb rs2, offset(rs1)` | Читаются `A = R[rs1]`, `B = R[rs2]`, формируется offset. | `EA <- A + signext(offset[11:0])`. | `M[EA] <- B[7:0]`, записываются младшие 8 бит; `PC <- PC4`. | Нет записи в регистр. |
| `lw rd, offset(rs1)` | Читается `A = R[rs1]`, формируется offset. | `EA <- A + signext(offset[11:0])`. | `MDR <- M[EA]`, читаются 32 бита; `PC <- PC4`. | `R[rd] <- MDR`. |
| `lb rd, offset(rs1)` | Читается `A = R[rs1]`, формируется offset. | `EA <- A + signext(offset[11:0])`. | `MDR <- signext(M[EA][7:0])`; `PC <- PC4`. | `R[rd] <- MDR`. |

## Arithmetic Instructions

| Команда | `T1 ID` | `T2 EX` | `T3 MEM / PC` | `T4 WB` |
|---|---|---|---|---|
| `addi rd, rs1, k` | `A = R[rs1]`, `Imm = signext(k[11:0])`. | `ALUOut <- A + Imm`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `slti rd, rs1, k` | `A = R[rs1]`, `Imm = signext(k[11:0])`. | `ALUOut <- (A < Imm) ? 1 : 0`, signed comparison. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `add rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`. | `ALUOut <- A + B`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `sub rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`. | `ALUOut <- A - B`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `mul rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`. | `Product64 <- A * B`; `ALUOut <- Product64[31:0]`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `mulh rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`. | `Product64 <- A * B`; `ALUOut <- Product64[63:32]`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `div rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`. | `ALUOut <- A / B`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `rem rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`. | `ALUOut <- A % B`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |

Для `div` и `rem` документация не задает отдельное поведение при делении на ноль. На схеме это остается ответственностью блока `Mul/Div unit`.

## Bitwise Instructions

| Команда | `T1 ID` | `T2 EX` | `T3 MEM / PC` | `T4 WB` |
|---|---|---|---|---|
| `slli rd, rs1, k` | `A = R[rs1]`, `shamt = k & 0x1F`. | `ALUOut <- A << shamt`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `srli rd, rs1, k` | `A = R[rs1]`, `shamt = k & 0x1F`. | `ALUOut <- A >>> shamt`, zero-fill. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `srai rd, rs1, k` | `A = R[rs1]`, `shamt = k & 0x1F`. | `ALUOut <- A >> shamt`, sign-preserving. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `sll rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`, `shamt = B & 0x1F`. | `ALUOut <- A << shamt`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `srl rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`, `shamt = B & 0x1F`. | `ALUOut <- A >>> shamt`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `sra rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`, `shamt = B & 0x1F`. | `ALUOut <- A >> shamt`, sign-preserving. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `and rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`. | `ALUOut <- A & B`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `andi rd, rs1, k` | `A = R[rs1]`, `Imm = signext(k[11:0])`. | `ALUOut <- A & Imm`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `or rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`. | `ALUOut <- A \| B`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `ori rd, rs1, k` | `A = R[rs1]`, `Imm = signext(k[11:0])`. | `ALUOut <- A \| Imm`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `xor rd, rs1, rs2` | `A = R[rs1]`, `B = R[rs2]`. | `ALUOut <- A ^ B`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |
| `xori rd, rs1, k` | `A = R[rs1]`, `Imm = signext(k[11:0])`. | `ALUOut <- A ^ Imm`. | `PC <- PC4`. | `R[rd] <- ALUOut`. |

## Control Flow Instructions

| Команда | `T1 ID` | `T2 EX` | `T3 MEM / PC` | `T4 WB` |
|---|---|---|---|---|
| `j k` | Формируется `k`. | `Target <- PC + k`. | `PC <- Target`. | Нет записи. |
| `jal rd, k` | Формируется `k`, сохраняется `PC4` для link. | `Target <- PC + k`. | `PC <- Target`. | `R[rd] <- PC4`. |
| `jr rs` | `A = R[rs]`. | `Target <- A`. | `PC <- Target`. | Нет записи. |
| `beqz rs1, k` | `A = R[rs1]`, формируется `k`. | `take <- (A == 0)`, `Target <- PC + k`. | `PC <- take ? Target : PC4`. | Нет записи. |
| `bnez rs1, k` | `A = R[rs1]`, формируется `k`. | `take <- (A != 0)`, `Target <- PC + k`. | `PC <- take ? Target : PC4`. | Нет записи. |
| `bgt rs1, rs2, k` | `A = R[rs1]`, `B = R[rs2]`, формируется `k`. | `take <- (A > B)`, signed; `Target <- PC + k`. | `PC <- take ? Target : PC4`. | Нет записи. |
| `ble rs1, rs2, k` | `A = R[rs1]`, `B = R[rs2]`, формируется `k`. | `take <- (A <= B)`, signed; `Target <- PC + k`. | `PC <- take ? Target : PC4`. | Нет записи. |
| `bgtu rs1, rs2, k` | `A = R[rs1]`, `B = R[rs2]`, формируется `k`. | `take <- (unsigned(A) > unsigned(B))`; `Target <- PC + k`. | `PC <- take ? Target : PC4`. | Нет записи. |
| `bleu rs1, rs2, k` | `A = R[rs1]`, `B = R[rs2]`, формируется `k`. | `take <- (unsigned(A) <= unsigned(B))`; `Target <- PC + k`. | `PC <- take ? Target : PC4`. | Нет записи. |
| `beq rs1, rs2, k` | `A = R[rs1]`, `B = R[rs2]`, формируется `k`. | `take <- (A == B)`, `Target <- PC + k`. | `PC <- take ? Target : PC4`. | Нет записи. |
| `bne rs1, rs2, k` | `A = R[rs1]`, `B = R[rs2]`, формируется `k`. | `take <- (A != B)`, `Target <- PC + k`. | `PC <- take ? Target : PC4`. | Нет записи. |
| `halt` | Декодер распознает останов. | `Halt <- 1`, `PCWrite <- 0`. | Машина остановлена. | Нет записи. |

## Как объяснять выполнение команды на защите

Для любой команды удобно идти по одному шаблону:

1. `T0`: показать, что инструкция читается из instruction memory по `PC`, попадает в `IR`, а `PC+4` заранее сохраняется.
2. `T1`: назвать поля команды и какие регистры читаются. Если есть immediate или offset, показать, как immediate generator его расширяет или сдвигает.
3. `T2`: назвать активный исполнительный блок: ALU, shifter, comparator, mul/div или target adder.
4. `T3`: объяснить, нужен ли доступ к data memory/MMIO, и какой следующий `PC` выбирает `Next PC mux`.
5. `T4`: если команда пишет результат, назвать источник writeback mux и регистр назначения.

Пример для `lw a0, 8(sp)`:

- `T0`: `IR <- IMem[PC]`, `PC4 <- PC + 4`.
- `T1`: декодер видит `lw`, читает `A = R[Sp]`, формирует `Imm = signext(8)`.
- `T2`: ALU считает `EA = R[Sp] + 8`.
- `T3`: data memory читает 32-битное слово по `EA`, `PC <- PC4`.
- `T4`: writeback mux выбирает данные памяти, `R[A0] <- MDR`.

Пример для `beq t0, t1, label`:

- `T0`: команда читается, считается `PC4`.
- `T1`: читаются `A = R[T0]`, `B = R[T1]`, формируется `k` до `label`.
- `T2`: comparator считает `take = (A == B)`, target adder считает `Target = PC + k`.
- `T3`: если `take = 1`, то `PC <- Target`, иначе `PC <- PC4`.
- `T4`: записи в регистр нет.

Пример для `jal ra, func`:

- `T0`: команда читается, `PC4` сохраняется как адрес возврата.
- `T1`: декодер видит `jal`, immediate generator формирует `k`.
- `T2`: target adder считает `Target = PC + k`.
- `T3`: `PC <- Target`.
- `T4`: writeback mux выбирает `PC4`, `R[Ra] <- PC4`.
