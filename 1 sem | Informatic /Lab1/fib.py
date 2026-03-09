num = int(input()) # Вводим число в 10 сс и инциализируем индекс массива

def fib_func(num:int, ind = 1)-> list: # Определяем функцию для получения массива чисел Фибоначчи
    
    fib_arr=[1,2]# Определяем начало массива с числами Фиббоначи

    while fib_arr[-1]<num: # Заполняем массив числами Фиббоначи до необходимого нам числа

        fib_arr.append(fib_arr[ind-1]+fib_arr[ind])
        ind+=1
    
    return fib_arr

fib_arr = list(reversed(fib_func(num)[:-1])) # Переворачиваем массив для удобства использования

fib_num =[0]*len(fib_arr) # Создаем пустой список для числа в фибоначчиевой сс

for i in range(len(fib_arr)): #Используем цикл для заполнения массива
    
    if fib_arr[i]<=num and fib_num[i-1]!=1: 
        fib_num[i] = 1
        num-=fib_arr[i]

print(''.join(map(str, fib_num))) # Выводим ответ в строчном формате

    

