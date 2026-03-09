#468125%6=5
import re
import json


def find_word(string: str) -> str:
    consonants = 'бБвВгГдДжЖзЗйЙлЛмМнНрРпПфФкКтТшШсСхХцЦчЧЩщ'
    vowels = 'аеёиоуыэюяАЕЁИОУЫЭЮЯ'
    ans = list(re.findall(rf'\S*[{vowels}][{vowels}]\S*\b(?! \S*[{consonants}]\S*[{consonants}]\S*[{consonants}]\S*[{consonants}]\S*)', string))
    return [''] if len(ans)==0 else ans

s = input("Введите сообщение:\n")
my_json = {}
my_answers = find_word(s)
my_json['answers'] = my_answers

with open('result.json', 'w', encoding="utf-8") as file:
    dumped_json = json.dumps(my_json, ensure_ascii=False)
    file.write(dumped_json)