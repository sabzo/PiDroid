import socket
from threading import Thread

class Server(object):
  """
        Server Class to handle Android connections
  """

  def __init__ (self):
    self.PORT = 1080
    self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.socket.bind( ('', self.PORT))
    self.listen()
    self.sockets = {}

  def listen(self):
    self.socket.listen(5) # max queue connect requests
    while True:
      (client_socket, address) = socket.accept()
      client_socket.setblocking(0)
      # save file descriptor for later use
      fd = client_socket.fileno()
      sockets[fd] = client_socket
      write_to_file()

  def write_to_file(self):
      pass
