import re
with open("file_inp.xml", "r") as file_inp:
    main_string = file_inp.read()[39:]

main_string = re.sub(r'<(\w+)>', r"\1: ", main_string)
main_string = re.sub(r'(\n)?\s*</\w+>', '', main_string)
main_string = re.sub(r'    ', r'  ', main_string)
main_string = re.sub(r"    <(\w+) (\w+)\s*=\s*'(\w+)'>", r"  - \1: \n      '@\2': \3", main_string)

with open("file_out2.yml", "w") as file_out:
    file_out.write(main_string)
