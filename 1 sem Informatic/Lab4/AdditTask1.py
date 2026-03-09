import xmlplain

# Read to plain object
with open("file_inp.xml", encoding="utf-8") as inf:
  root = xmlplain.xml_to_obj(inf, strip_space=True, fold_dict=True)

# Output plain YAML
with open("file_out1.yml", "w", encoding="utf-8") as outf:
  xmlplain.obj_to_yaml(root, outf) 
