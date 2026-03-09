# Пункт 4
mkdir tmp
touch tmp/file.txt
wc -m lab0/dwebble6/glaceon  lab0/dwebble6/electrocode lab0/tirtouga2/lileep  2> tmp/file.txt  | sort -nr
echo -e '\n===================\n'
ls lab0/tirtouga2 2> tmp/file.txt  | sort 
echo -e '\n===================\n'
grep -in 'n$' lab0/pineco1 2> tmp/file.txt
echo -e '\n===================\n'
(ls -lR lab0| grep  "a$" | sort -r  | tail -n 3) 2>&1
echo -e '\n===================\n'
(ls -luR lab0| grep  "e$" | sort -r | tail -n 3) 2>&1
cd lab0
cd tirtouga2
echo -e '\n===================\n'
(cat *|sort -r) 2>&1   
cd .. 