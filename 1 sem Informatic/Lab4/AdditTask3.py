import re
def sub_func(xml_string):
    xml_string = re.sub(r'<\?(\w+)>(.*?)<\?\/\1>', '', xml_string)
    xml_string = re.sub('\n', '', xml_string)
    xml_string = re.sub("'", '"', xml_string)
    return xml_string

def parse_simple_xml(xml_string):
    
    pattern1 = r'<(\w+)\s*>(.*?)<\/\1>'
    pattern2 = r'<(\w+)\s+(.+?)>(.*?)</\1>'
    # Используем findall для поиска всех совпадений
    matches1 = re.findall(pattern1, xml_string, re.DOTALL)
    matches2 = re.findall(pattern2, xml_string, re.DOTALL)
    
    if re.search(pattern1, xml_string)!=None:
        match1 = re.search(pattern1, xml_string).start()
        
    if re.search(pattern2, xml_string):    
        match2 = re.search(pattern2, xml_string).start()
    else:  
        match2 = -1
    
    
    result = {}
    if match2<match1 and match2!=-1:
        
        for tag, atributes,content in matches2:
            list_atributes=list(re.findall(r'(\w+)\s*=\s*"(\w+)"\s*', atributes))
            check = re.findall(pattern1, content, re.DOTALL)!=[] or re.findall(pattern2, content, re.DOTALL)!=[]
            content = content.strip()
            atribute_dict = {f"'@{atribute}'":value_of_atribute for atribute, value_of_atribute in list_atributes}
            if content =='':
                result[tag] = atribute_dict
            elif check and content.find('<')==0:
                help_lst = {**atribute_dict, **parse_simple_xml(content)}
            elif not check: 
                help_lst = [atribute_dict, content]
            
            else:
                help_lst= [atribute_dict, content[:content.find('<')], parse_simple_xml(content[content.find('<'):])  ]
                
                
            if tag not in result:
                result[tag] = help_lst

            elif isinstance(result[tag], dict) or isinstance(result[tag], str):
                result[tag] = [result[tag], help_lst]
            elif isinstance(result[tag], list):
                result[tag]+=help_lst
            
        
    else:
        
        for tag, content in matches1:
            check = re.findall(pattern1, content, re.DOTALL)!=[] or re.findall(pattern2, content, re.DOTALL)!=[]
            content = content.strip()
            
            if check and content.find('<')==0:
                help_lst = parse_simple_xml(content)
            elif not check: 
                help_lst = content
            
            else:
                help_lst= [content[:content.find('<')], parse_simple_xml(content[content.find('<'):])]
                

            if tag not in result:
                result[tag] = help_lst

            elif isinstance(result[tag], dict) or isinstance(result[tag], str):
                result[tag] = [result[tag], help_lst]

            elif isinstance(result[tag], list) and isinstance(help_lst, str):
                result[tag].append(help_lst) 

            elif isinstance(result[tag], list) and isinstance(help_lst, list):
                result[tag]+=help_lst  
    return result


def dict_to_yaml(dictionary, tab_count):
    yaml_string = ""
    
    for key, value in dictionary.items():
        if isinstance(value, dict):
        
            yaml_string += f"{tab_count*'  '}{key}:\n"
            yaml_string += dict_to_yaml(value, tab_count + 1)
        elif isinstance(value, list):
            
            for i in value:
                yaml_string += f"{(tab_count-1)*'  '}- {key}:\n"
                    
                if isinstance(i, str): 
                    yaml_string += f"{(tab_count+1)*'  '}{i}\n"
                if isinstance(i, dict):   
                    yaml_string += dict_to_yaml(i, tab_count + 1)
                else:
                    yaml_string += dict_to_yaml(i, tab_count + 1)
        elif isinstance(value, str):
            yaml_string += f"{tab_count*'  '}{key}: '{value}'\n" 
    return yaml_string
def ecranic_func(yaml_string):
    yaml_string = yaml_string.replace('&lt;', '<')
    yaml_string = yaml_string.replace('&amp;', '&')
    yaml_string = yaml_string.replace('&quot;', '"')
    yaml_string = yaml_string.replace('&apos;', "'")
    return yaml_string
def output_func(xml_string):
    return ecranic_func(dict_to_yaml(parse_simple_xml(sub_func(xml_string)),0))

with open("file_inp.xml", "r", encoding="utf-8") as file_inp:
    xml_string = file_inp.read().strip()



with open("file_out3.yml", "w", encoding="utf-8") as file_out:
    file_out.write(output_func(xml_string))
