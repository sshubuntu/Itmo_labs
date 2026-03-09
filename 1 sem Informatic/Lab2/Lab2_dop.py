file  = open("result.txt","w")
# Функция проверяет корректность ввода
def current_input():
    string = input()
    if len(string)!=7 or not all(i in '01' for i in string):
        print('Неверные данные')
        return current_input()
    return string

# Функция переводит введенное число в двоичной сс в десятичную сс
def f_ss(num:str)-> int:
    ans = sum([int(num[i])*2**i for i in range(len(num))])
    return ans-1

# Функция вычисляет номер ошибочного бита
def syndrome(num_lst:list)-> str:
    s1 = sum(num_lst[::2])%2
    s2 = sum(num_lst[1:3]+num_lst[-2:])%2
    s3 = sum(num_lst[3:])%2
    S = ''.join(map(str, (s1, s2, s3)))
    return S

# Функция возвращает номер символа по индексу
def error_symbol(ind)-> str:
    return { 0: 'r1', 1: 'r2', 2: 'i1', 3: 'r3', 4: 'i2', 5: 'i3', 6: 'i4' }[ind]     

# Функция выводит ответ и проверяет было ли сообщение ошибочным
def output(ind:int, num_lst:list):
    if ind == -1:
        print('Сообщение верно')
        file.write("correct")
    else:
        # Заменяем ошибочный бит
        num_lst[ind]=(num_lst[ind]+1)%2
        # Превращаем список в строку и выводим
        message= ''.join(map(str, num_lst))
        print(f'Верное сообщение: {message[2]+message[4:]}\nОшибка в символе {error_symbol(ind)}')
        file.write(f"{error_symbol(ind)}")
   
# Вводим сообщение
message = current_input()
# Проверяем корректность ввода

# Превращаем введенную строку в список 0 и 1
num_lst = [int(i) for i in list(message)]
# Выводим ответ
output(f_ss(syndrome(num_lst)),num_lst)




    