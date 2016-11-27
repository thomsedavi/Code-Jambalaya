def pywget(url):
    """Downloads a specified url to the local directory. If the url is a website,
    downloads each link to the local directory and changes the code to point to the
    local location of the object.
    
    Argument:
    url -- the location of the object to be downloaded.
    
    Error checking:
    url must be a string and point to an object that can be downloaded.
    """

    import os.path
    import urllib.request

    if not isinstance(url, str):
        print("Error: only accepts strings")
        return None
            
    try: urllib.request.urlopen(url)
    except:
        print("Network error")
        return None
        
    response = urllib.request.urlopen(url)
    data = response.read()
    
    # Breaks up the url by backslashes to find the file name,
    # then breaks the file name into name and extension.

    minus_slashes = url.split("/")
    pre_file_name = minus_slashes[len(minus_slashes) - 1]
    file_extension = pre_file_name.split(".")[len(pre_file_name.split(".")) - 1]
    file_name = pre_file_name[0:len(pre_file_name) - len(file_extension) - 1]
    
    # finds the directory of the file to match links and
    # only download files from that directory

    html_directory = url[0:len(url)-len(file_name)-len(file_extension)-1]
    
    if file_extension == "html":
        
        original_text = str(data)
        new_text = ""
        cursor = 0

        # Iteratively searches through the text looking for any <a href> or <img> tags.

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

                    # If the link only contains a local reference with no full HTML,
                    # adds directory to complete URL.

                    if (url_ref[0:5] != "http:") & (url_ref[0:6] != "https:") & \
                    (url_ref[0:4] != "ftp:") & (url_ref[0:2] != "//"):
                        url_ref = html_directory + url_ref
                        
                    if url_ref[0:2] == "//":
                        url_ref = "http:" + url_ref
                        
                    # Cuts out the original url reference to be replaced
                    # with new local URL

                    cursor = cursor_range
                    
                    # If url belongs to same HTML directory, download
                    # and return a local link reference.

                    if len(url_ref) > len(html_directory):
                        if url_ref[0:len(html_directory)] == html_directory:
                            url_ref = sub_pywget(html_directory, url_ref)

                    new_text = new_text + "<" + ref_type + "=\"" + url_ref + "\""
                else:
                    new_text = new_text + original_text[cursor]
                    
                cursor = cursor + 1                               
            else:
                new_text = new_text + original_text[cursor]
                cursor = cursor + 1
                
        cursor = 0
        final_text = ""
        
        # Replaces new lines "\n" symbols creates by converting data into string
        
        while cursor + 1 < len(new_text):
            if new_text[cursor:cursor+2] == "\\n":
                final_text = final_text + " "
                cursor = cursor + 2
            else:
                final_text = final_text + new_text[cursor]
                cursor = cursor + 1
         
        data = str.encode(final_text[2:len(new_text)-1])

    # If no file of this name exists save it under original name,
    # otherwise add a number to the end and increase that number
    # until no file by that number exists.

    if not os.path.isfile(file_name + "." + file_extension):
        file = open(file_name + "." + file_extension, "wb")
        file.write(data)
        file.close()
        print(file_name + "." + file_extension)
    else:
        ext_number = 1
        while os.path.isfile(file_name + "." + str(ext_number) + "." + file_extension):
            ext_number = ext_number + 1
        file = open(file_name + "." + str(ext_number) + "." + file_extension, "wb")
        file.write(data)
        file.close()
        print(file_name + "." + str(ext_number) + "." + file_extension)
        
def sub_pywget(html_directory, url_ref):
    """Downloads a specified url to the local directory and returns the new
    location in local directory.
    
    Arguments:
    html_directory -- can be removed from the beginning of url_ref
    to get a file name.
    url_ref -- the location of the object to be downloaded.
    
    Error checking:
    url must point to an object that can be downloaded.
    """

    import os.path
    import urllib.request
            
    try: urllib.request.urlopen(url_ref)
    except:
        print("Network error: " + url_ref)
        return url_ref
        
    response = urllib.request.urlopen(url_ref)
    data = response.read()
    
    url_ref = url_ref[len(html_directory):len(url_ref)]
        
    # Breaks up the url by backslashes to find the file name,
    # then breaks the file name into name and extension.

    minus_slashes = url_ref.split("/")
    pre_file_name = minus_slashes[len(minus_slashes) - 1]
    file_extension = pre_file_name.split(".")[len(pre_file_name.split(".")) - 1]
    file_name = pre_file_name[0:len(pre_file_name) - len(file_extension) - 1]
    
    dir_count = 0
    dir_name = ""
    
    # Creates any subdirectories in local directory to match subdirectories
    # or original url.
    
    while dir_count + 1 < len(minus_slashes):
        dir_name = dir_name + minus_slashes[dir_count] + "/"
        if not os.path.isdir(dir_name):
            os.makedirs(dir_name)
        dir_count = dir_count + 1
        
    file_name = dir_name + file_name

    # If no file of this name exists save it under original name,
    # otherwise add a number to the end and increase that number
    # until no file by that number exists.
    
    if not os.path.isfile(file_name + "." + file_extension):
        full_name = file_name + "." + file_extension
        file = open(full_name, "wb")
        file.write(data)
        file.close()
        return full_name
        print(file_name + "." + file_extension)
    else:
        ext_number = 1
        while os.path.isfile(file_name + "." + str(ext_number) + "." + file_extension):
            ext_number = ext_number + 1
        full_name = file_name + "." + str(ext_number) + "." + file_extension
        file = open(full_name, "wb")
        file.write(data)
        file.close()
        return full_name
        print(file_name + "." + str(ext_number) + "." + file_extension)