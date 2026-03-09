# Удаление директории
chmod -R u=rwx lab0
rm -rf lab0 tmp
# Выполнение скриптов
sh scrypt1.sh
echo 'Point 1 Completed'
sh scrypt2.sh
echo 'Point 2 Completed'
sh scrypt3.sh
echo 'Point 3 Completed'
sh scrypt4.sh
echo 'Point 4 Completed'
sh scrypt5.sh
echo 'Point 5 Completed'

