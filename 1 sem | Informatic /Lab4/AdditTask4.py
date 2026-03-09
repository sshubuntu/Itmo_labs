from datetime import datetime
import subprocess

def time_counter(script):
    time_start = datetime.now()
    for i in range(100): 
        subprocess.run(['python', script])
    return str(datetime.now()-time_start)[5:]

dict_files = {'stupid_pars':'MainTask1.py','lib_pars':'AdditTask1.py', 'regex_pars':'AdditTask2.py','my_pars':'AdditTask3.py'}

print(*[f'{i} = {time_counter(j)}' for i,j in dict_files.items()], sep='\n')
