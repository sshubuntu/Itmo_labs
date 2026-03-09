def func(String: str) -> str:
    yaml_string = ""
    tab_count = 0
    for i in range(len(String)):
        lst_open = ['<root>', "<days>", "<first>", "<fifth>", "<sixth>", "<second>", "<fouth>","<seventh>"]
        lst_cls = [i[0]+'/'+i[1:] for i in lst_open]+['</day>']
        closetag = String[i:].find('>')+i
        
        if String[i:closetag+1] in lst_open:
            yaml_string+=f"{tab_count*'  '}{String[i+1:closetag]}:\n"
            tab_count+=1
            
        try:
            if String[i:i+4]=="<day":
                list_atr = String[i+5:String[i:].find(">")+i].split('=')
                yaml_string+=f"{(tab_count-1)*'  '}- {String[i+1:i+4]}: \n{(tab_count+1)*'  '}'@{list_atr[0].strip()}': {list_atr[1].strip()[1:-1]}\n"
                tab_count+=1
        except: pass

        if String[i:closetag+1] in ['<subj>', "<type>", "<teacher>", "<room>", "<building>", "<from>", "<to>", "<weeks>", "<format>"]:
            yaml_string+=f"{tab_count*'  '}{String[i+1:closetag]}: {String[closetag+1:String[i:].find('</')+i]}\n"
            
        if String[i:closetag+1] in lst_cls:
            tab_count-=1
          
    return yaml_string
        
with open("file_inp.xml", "r", encoding="utf-8") as file_inp:
    xml_string = file_inp.read().strip()[39:]

with open("file_out.yml", "w", encoding="utf-8") as file_out:
    file_out.write(func(xml_string))