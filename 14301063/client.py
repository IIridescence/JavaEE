import socket
import sys

HOST = '127.0.0.1'
PORT = 3333

sock=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
sock.connect((HOST,PORT))

sdata=raw_input("Please input something:\n")
sock.send(sdata)
rdata=sock.recv(1024)

sys.stdout.write("The string that receive is:\n")
sys.stdout.write(rdata)

sock.close()