#468125%6=5
#468125%4=1
#468125%8=5
# Smile: [</
import re

def counter(string: str) -> int:
    sub_string = re.escape('[</')
    return len(re.findall(sub_string, string))

string1 = ''
string2 = '[</[</[</'
string3 = '[<\n/'
string4 = '\[<\/'
string5 = '[<[[<[<//<//<[[<<[[<[<[[<[<//<//<//<//[<//<//<[<[[<[<//<//[<[<//<//<[[<[<//<//<[[<[<//<//'

assert counter(string1) == 0
print(counter(string1))

assert counter(string2) == 3
print(counter(string2))

assert counter(string3) == 0
print(counter(string3))

assert counter(string4) == 0
print(counter(string4))

assert counter(string5) == 7
print(counter(string5))



