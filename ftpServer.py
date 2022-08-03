import os
from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer

local_data = os.getcwd()+"/server_data/"

authorizer = DummyAuthorizer()
authorizer.add_anonymous(local_data, perm="elradfmwMT")

handler = FTPHandler
handler.authorizer = authorizer

if __name__ == "__main__":
    server = FTPServer(("localhost", 2121), handler)
    server.serve_forever()
