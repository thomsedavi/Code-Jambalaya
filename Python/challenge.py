def pywget(url, depth):

    import os.path
    import urllib.request

    if not isinstance(url, str):
        print("Error: only accepts strings")
        return None
        
    if not isinstance(depth, int):
        print("Error: depth must be an integer")
        return None
            
    try: urllib.request.urlopen(url)
    except:
        print("Network error")
        return None
        
    response = urllib.request.urlopen(url)
    data = response.read()
    
    split = url.split("/")
    pre_file_name = split[len(split) - 1].split(".")
    file_name = pre_file_name[0]
    file_extension = pre_file_name[1]
    
    home_dir = split[2]
    
    if not os.path.exists(home_dir):
        home_dir = home_dir + "/"
        os.makedirs(home_dir)
    else:
        ext_number = 1
        while os.path.exists(home_dir + "." + str(ext_number)):
            ext_number = ext_number + 1
        home_dir = home_dir + "." + str(ext_number) + "/"
        os.makedirs(home_dir)
    
    dir_count = 3
    
    while dir_count + 1 < len(split):
        home_dir = home_dir + split[dir_count] + "/"
        if not os.path.exists(home_dir):
            os.makedirs(home_dir)
        dir_count = dir_count + 1

    html_dir = url[0:len(url)-len(file_name)-len(file_extension)-1]
        
    rec_pywget(url, depth, home_dir, html_dir)
    
def rec_pywget(url, depth, home_dir, html_dir):
    
    if depth < 0:
        return url

    import os.path
    import urllib.request

    home_url = home_dir + url[len(html_dir):len(url)]
    
    if os.path.isfile(home_url):
        return url[len(html_dir):len(url)]

    try: urllib.request.urlopen(url)
    except:
        print("Network error")
        return url
        
    response = urllib.request.urlopen(url)
    data = response.read()
    
    url = url[len(html_dir):len(url)] 
    
    split = url.split("/")
    
    dir_count = 0
    
    this_dir = home_dir
    
    while dir_count + 1 < len(split):
        this_dir = this_dir + split[dir_count] + "/"
        if not os.path.exists(this_dir):
            os.makedirs(this_dir)
        dir_count = dir_count + 1
    
    pre_file_name = split[len(split) - 1].split(".")
    file_extension = pre_file_name[len(pre_file_name) - 1]
    file_name = url[0:len(url) - len(file_extension) - 1]
    
    if file_extension == "html":
        
        original_text = str(data)
        new_text = ""
        cursor = 0
        while cursor < len(original_text):
            if original_text[cursor] == "<":
                
                if original_text[cursor:cursor+7] == "<a href" or original_text[cursor:cursor+4] == "<img":
                    if original_text[cursor:cursor+7] == "<a href":
                        ref_type = "a href"
                    elif original_text[cursor:cursor+4] == "<img":
                        ref_type = "img src"
                    while original_text[cursor] != "\"":
                        cursor = cursor + 1
                    cursor = cursor + 1
                    cursor_range = cursor
                    while original_text[cursor_range] != "\"":
                        cursor_range = cursor_range + 1                    
                    url_ref = original_text[cursor:cursor_range]
                    if (url_ref[0:5] != "http:") & (url_ref[0:6] != "https:") & (url_ref[0:4] != "ftp:") & (url_ref[0:2] != "//"):
                        url_ref = html_dir + url_ref
                        
                    if url_ref[0:2] == "//":
                        url_ref = "http:" + url_ref

                    cursor = cursor_range
                    
                    if len(url_ref) > len(html_dir):
                        if (url_ref[0:len(html_dir)] == html_dir):
                            print("Checking " + url_ref + " at depth " + str(depth))
                            url_ref = rec_pywget(url_ref, depth - 1, home_dir, html_dir)

                    new_text = new_text + "<" + ref_type + "=\"" + url_ref + "\""
                else:
                    new_text = new_text + original_text[cursor]
                    
                cursor = cursor + 1                               
            else:
                new_text = new_text + original_text[cursor]
                cursor = cursor + 1
                
        cursor = 0
        final_text = ""
        
        while cursor + 1 < len(new_text):
            if new_text[cursor:cursor+1] == "\\":
                final_text = final_text + " "
                cursor = cursor + 2
            else:
                final_text = final_text + new_text[cursor]
                cursor = cursor + 1
        
        data = str.encode(final_text[2:len(new_text)-1])    
    
    file = open(home_dir + file_name + "." + file_extension, "wb")
    file.write(data)
    file.close()
    print("returning " + file_name)
    return file_name + "." + file_extension