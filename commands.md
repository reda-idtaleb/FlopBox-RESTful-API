# Run a local server to easily test cURL commands
Locate in the root directory (where the /src exists) and launch the local ftp server:
```bash
$ python3 ftpServer.py
```
Files for this server are stored in the /local_server_data folder. And here is its hierarchy:
```bash
/
├── ascii_file.txt
├── binary_file.jpg
└── dir_a
    ├── ascii_file_a.txt
    └── dir_b
        └── binary_file_b.jpg

2 directories, 4 queues
```

# General remarks
Client-side error handling is enhanced when sending requests, for example:
- An empty string as the value of a field is not allowed, otherwise an HTTP error response will be returned to the client:
    ```bash
    -d '{"username": "", "pwd": ""}'
    ```
    or:
    ```bash
    -d '{"address":""}'
    ```
    And this in all requests that require sending data.
- Some POST requests require fields to be filled in (this is detailed below).
- Some POST requests require field values ​​to be populated with a specific data type.
    
    Example when registering a server:
    ```bash
    -d '{"address":"localhost", "port":"2121"}'
    ```
    In this case the value of the `port` field must be a number (either in string format or in number format).
    
    On the other hand, this write will return an HTTP error response:
    ```bash
    -d '{"address":"localhost", "port":"false"}'
    ```
- Other specific cases are detailed in the following sections.
- The returned HTTP error messages show the error problem in detail to better guide the user to succeed in their request.

# Register in the FlopBox application:
- The `/signin` sub-resource of the `/users` resource allows you to register in the FlopBox API.
- Your login data must be sent in JSON format (`-d '{"username": "my_name","pwd": "my_password"}'`)
```bash
curl -v -H "Content-Type: application/json" -X POST -d '{"username": "reda","pwd": "reda"}' http://localhost:8080/v1/users/signin
```
## Remark:
- The two fields `username` and `pwd` must be present when sending the POST request.

# Create a server with an alias:
- In the requests to create a server, you must specify the resource `/servers` in the uri followed by an alias you want to create `v1/servers/{alias}`.
## By specifying only the server address
- Specify only the `address` field in the given part(`-d '{"address": "localhost"}'`).
**Note:** If the "port" field is not specified then the FlopBox application will default to port 21 for the specified server.
```bash
curl -v -H "Content-Type: application/json" -u reda:reda -X POST -d '{"address":"localhost"}' http://localhost:8080/v1/servers/myServer
```
## By specifying server address + port
- Specify the 2 fields `address` and `port` in the given part(`-d '{"address": "localhost", "port":"2121"}'`).
```bash
curl -v -H "Content-Type: application/json" -u reda:reda -X POST -d '{"address":"localhost", "port":"2121"}' http://localhost:8080 /v1/servers/myServer
```

## Remark
- Authorized fields are `address` or `port`.
- The value of the `port` field must be a number in string format.
- The `address` field must be filled in in all cases.
- If an alias is already associated with an FTP server address, then you can no longer associate this alias with another FTP server address.

# update information from a registered FTP server
- In the update requests, you must specify the resource `/servers` in the uri followed by an alias you want to update `v1/servers/{old_alias}`.
## Alias ​​update only
- Specify the `alias` field in the given part(`-d '{"alias": "myNewAlias"}'`).
```bash
curl -v -H "Content-Type: application/json" -u reda:reda -X PUT -d '{"alias": "myNewAlias"}' http://localhost:8080/v1/servers/myServer
```
## Update FTP server address only
- Specify that the `url` field in the given part(`-d '{"address": "127.0.0.1"}'`).
```bash
curl -v -H "Content-Type: application/json" -u reda:reda -X PUT -d '{"address": "127.0.0.1"}' http://localhost:8080/v1/servers/myNewAlias
```
## Update FTP server port only
- Specify that the `url` field in the given part(`-d '{"port": "21"}'`).
```bash
curl -v -H "Content-Type: application/json" -u reda:reda -X PUT -d '{"port": "21"}' http://localhost:8080/v1/servers/myNewAlias
```
## Update alias + FTP server address
- You can specify both fields at the same time in the given part (`-d '{"alias": "myServer", "address": "localhost"}'`).
```bash
curl -v -H "Content-Type: application/json" -u reda:reda -X PUT -d '{"alias": "myServer", "address": "localhost"}' http://localhost:8080 /v1/servers/myNewAlias
```
## Update of a port + the FTP address
- You can specify both fields at the same time in the given part(`-d '{"address": "127.0.0.1", "port": "2121"}'`).
```bash
curl -v -H "Content-Type: application/json" -u reda:reda -X PUT -d '{"address": "127.0.0.1", "port": "2121"}' http://localhost :8080/v1/servers/myServer
```
## Remarks
- The fields -data in json format- filled in must be among the following: (`address`, `alias`, `port`). Any other field will return an HTTP error response.

# Delete an alias
- You must specify the resource `/servers` in the uri `v1/servers/{alias_to_delete}` followed by the alias of the server to delete.
```bash
curl -v -u reda:reda -X DELETE http://localhost:8080/v1/servers/myServer
```

# List all FTP servers of a user
- You must specify the `/servers` resource in the `v1/servers/` uri.
```bash
curl -v -u reda:reda http://localhost:8080/v1/servers
```

# Access information from an FTP server
- You must specify the resource `/servers` in the uri `v1/servers/{alias}` followed by an alias.
```bash
curl -v -u reda:reda http://localhost:8080/v1/servers/myServer
```

# List files from an FTP server
- You must specify the subresource `/list` in the uri `v1/servers/myServer/files/list/{path/dir/to/list}`.
```bash
curl -v -u reda:reda -H "user:anonymous" -H "pwd:anonymous" http://localhost:8080/v1/servers/myServer/files/list/
```
- The query above lists the root files (because no path is specified after `list/`).
- You can not specify the header if the server is only in anonymous mode:
    ```bash
    curl -v -u reda:reda http://localhost:8080/v1/servers/myServer/files/list/
    ```
    In this case the `anonymous` value is automatically assigned to the `user` and `pwd` fields.

# Retrieve a file from an FTP server
- You must specify the subresource `/getFile` in the uri `v1/servers/myServer/files/getFile/{remote/path/file_name}`.
```bash
curl -v -u reda:reda -H "user:anonymous" -H "pwd:anonymous" -o <path/to/store/file.extension> http://localhost:8080/v1/servers/myServer/files /getFile/ascii_file.txt
```

### **Don't forget to specify the <path/to/store/file> parameter of the `-o` option in order to save the contents of the remote file on your machine. It is advisable to enter the file extension in this way.**

You can specify `the --output -` option to have the contents of the remote file displayed on the terminal. For binary files (like images) the `-o <path/to/store/file.extension>` option is essential.

For example, if you want to store the contents of the `ascii_file.txt` file in the `/Desktop` or `/Desktop` folder, the command would become:
```bash
curl -v -u reda:reda -H "user:anonymous" -H "pwd:anonymous" -o ~/Bureau/ascii_file.txt http://localhost:8080/v1/servers/myServer/files/getFile/ascii_file .txt
```

This also works with binary files such as images:

```bash
curl -v -u reda:reda -H "user:anonymous" -H "pwd:anonymous" -o ~/Bureau/binary_file.jpg http://localhost:8080/v1/servers/myServer/files/getFile/binary_file .jpg
```
- If the server is in anonymous mode, you can benefit from the compact version of the curl command (by removing the `-H "user:anonymous" -H "pwd:anonymous"` headers).

# Retrieve the contents of a folder from an FTP server
- You must specify the subresource `/getDir` in the uri `v1/servers/myServer/files/getDir/{remote/path/dir_name}`.
```bash
curl -v -u reda:reda -H "user:anonymous" -H "pwd:anonymous" http://localhost:8080/v1/servers/myServer/files/getDir/
```
- This command returns the tree content of the requested folder including the urls of each of the resources.
- Again, you can still use the curl command without the header if the server is in anonymous mode.

# Upload a file (binary/text) to an FTP server
- This service allows you to upload a file (binary or text) to an FTP server.
- You must specify the subresource `/storeFile` in the uri `v1/servers/myServer/files/storeFile/{`.
- The path + the name of the file to create on your FTP server is specified on the uri `v1/servers/myServer/files/storeFile/{remote/path/to_store/myFile_name}`.
    ```bash
    curl -v -X POST -u reda:reda -H "user:anonymous" -H "pwd:anonymous" -F file=@local_ascii.txt http://localhost:8080/v1/servers/myServer/files/storeFile /local_ascii.txt
    ```
- Don't forget to specify the `file` field of the -F(`-F file=@myFile`) option which specifies the absolute path of the file you want to send.

Same thing for binary files (like images):

```bash
curl -v -X POST -u reda:reda -H "user:anonymous" -H "pwd:anonymous" -F file=@local_binary.jpg http://localhost:8080/v1/servers/myServer/files/storeFile /local_binary.jpg
```
- The request above sends the file `image.png` in the remote directory to the root `/`.

# Upload a complete folder to an FTP server
- This service allows you to upload a complete file to an FTP server. To do so, you must first compress your file before sending it, in order to reduce the waiting time for the upload operation to be completed.
- The remote path where your file will be put is specified on the Uri.
- You must specify the subresource `/storeDir` in the uri `v1/servers/myServer/files/storeDir/{remote/path/to_store/dir_name}`.
    ```bash
    curl -v -X POST -u reda:reda -H "user:anonymous" -H "pwd:anonymous" -F file=@myDir.zip http://localhost:8080/v1/servers/myServer/files/storeDir /myDir
    ```
- The above request sends the compressed folder `myDir.zip` to the remote directory `/myDir`. The latter is created when the files are decompressed.
- Don't forget to specify the `file` field of the -F(`-F file=@myFile.zip`) option which specifies the absolute path of the compressed folder you want to send.
- The folders created on your FTP server will be renamed uniquely and this by adding a timing to the name of your folder. This will avoid problems with overwriting a folder if it already exists.

# Rename a file or a directory of an FTP server
- You must specify the subresource `/rename` in the uri `v1/servers/myServer/files/rename/{remote/path/to_rename/file_name}`.
- This request requires the `renameTo` field sent in json format (`-d '{"renameTo": "new_dir_a"}'`) which specifies the new name of the file to be renamed.
```bash
curl -v -H "Content-Type: application/json" -u reda:reda -H "user:anonymous" -H "pwd:anonymous" -X PATCH -d '{"renameTo": "new_dir_a"}' http ://localhost:8080/v1/servers/myServer/files/rename/dir_a
```
- The request above renames the remote folder "/dir_a" which is located at the root in `new_dir_a`.

# Delete a directory from an FTP server
- You must specify the subresource `/delete` in the uri `v1/servers/myServer/files/delete/{remote/path/to_delete/dir_name}`.
- The path of the remote folder to delete is specified on the uri (`.../delete/{distant/path/to/delete/my_dir}`
```bash
curl -v -u reda:reda -H "user:anonymous" -H "pwd:anonymous" -X DELETE http://localhost:8080/v1/servers/myServer/files/delete/new_dir_a/dir_b
```
- This request only deletes directories from the FTP server. A failure response is returned to the client if it attempts to delete a normal file.

# Creating a directory in an FTP server
- You must specify the subresource `/mkdir` in the uri `v1/servers/myServer/files/mkdir/{remote/path/to_create/dir/}`.
- The user must specify only the directory name in the data part (-d '{"dirName": "my_dir"}'). The path where the folder will be created on your FTP server is specified on the request uri(`.../mkdir/{distant/path/to/create/my_dir}`).

```bash
curl -v -H "Content-Type: application/json" -u reda:reda -H "user:anonymous" -H "pwd:anonymous" -X POST -d '{"dirName": "dir_c"}' http ://localhost:8080/v1/servers/myServer/files/mkdir/dir_a
```
- The request above creates a directory `/dir_c` in the remote directory `/dir_a/` (this directory is at the root).
