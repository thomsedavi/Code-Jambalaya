def pywget(url):
    """Downloads a specified url to the local directory.
    
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

    # If no file of this name exists save it under original name,
    # otherwise add a number to the end and increase that number
    # until no file by that number exists.

    if not os.path.isfile(file_name + "." + file_extension):
        file = open(file_name + "." + file_extension, "wb")
        file.write(data)
        file.close()
        print(file_name + "." + file_extension + " downloaded")
    else:
        ext_number = 1
        while os.path.isfile(file_name + "." + str(ext_number) + "." + file_extension):
            ext_number = ext_number + 1
        file = open(file_name + "." + str(ext_number) + "." + file_extension, "wb")
        file.write(data)
        file.close()
        print(file_name + "." + str(ext_number) + "." + file_extension + " downloaded")