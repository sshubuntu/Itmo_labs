    #468125%8=5
import re
import json

string = input("Введите сообщение:\n")

list_of_st= string[string.find(':')+1:].strip().split(', ')

New_list= [i for i in list_of_st if not(re.match(r"(^[А-Я])\S* (\1\.|\1\.-[А-Я]\.)(\1\. | )P0000", i))]
my_json = {}

my_answers = [', '.join(New_list)]

my_json["answers"] = my_answers

with open('result.json', 'w', encoding="utf-8") as file:
    dumped_json = json.dumps(my_json, ensure_ascii=False)
    file.write(dumped_json)
