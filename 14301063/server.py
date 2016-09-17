import socket
import thread
import sys

def main():
    HOST = '127.0.0.1'
    PORT = 3333

    sock=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    sock.bind((HOST,PORT))
    sock.listen(5)

    while True:
        #sys.stdout.write("Waiting for connection...\n")
        connect, address = sock.accept()
        # sys.stdout.write("Connecting...\n")
        sys.stdout.write("A client has connected to the server\n")

        thread.start_new_thread(Receive, (connect,address))

def Receive(connect,address):
    data=connect.recv(1024)

    connect.sendall(data[::-1])
    #sys.stdout.write("The string has received...\n")
    #sys.stdout.write(data)
    #sys.stdout.write("\n")

    connect.close()

if __name__=="__main__":
    main()