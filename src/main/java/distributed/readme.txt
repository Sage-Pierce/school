-Each server must have identical copies of the file "Servers.txt".

-Servers.txt must contain the ip addresses and ports of each server
in the format 255.255.255.255:2710

-The port MUST BE 2710. It is hardcoded in to the TCPServer.java

-Servers.txt must be supplied by one of the two following ways:
1-The full path to Servers.txt must be passed as a command line arg
2-The program will look for Servers.txt by default in the current 'working path'