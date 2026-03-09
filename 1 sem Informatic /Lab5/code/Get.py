
with open("C:\labs\informatic\Lab5\SPFB.RTS-12.18_180901_181231.csv", "r") as file_inp, open("C:\labs\informatic\Lab5\Out_file.csv", "w") as file_out:
    file_out.write(file_inp.readline())
    for line in file_inp:
        date = line.split(",")[2]
        
        if date[:2] == "26" or (date[:2] == "03" and date[3:5] == "12"):
            file_out.write(line)