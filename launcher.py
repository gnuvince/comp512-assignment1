# -*- coding:utf-8 -*-

import sys
import os

CLASS_PATH = "carcode/bin:flightcode/bin:hotelcode/bin:servercode/bin:tcpcode/bin:customercode/bin"

def usage():
    print "Usage: %s <tcp|rmi> <port> <component> [<extra args>]" % sys.argv[0]
    print "Components:"
    print "\tmiddleware car=host:port flight=host:port hotel=host:port customer=host:port"
    print "\tclient middleware-host"
    print "\tcar"
    print "\tflight"
    print "\thotel"
    print "\tcustomer middleware:port"

def get_protocol(arg):
    if arg in ["tcp", "rmi"]:
        return arg
    raise Exception("Invalid protocol")

def get_port(arg):
    return int(arg)

def get_component(arg):
    if arg in ["middleware", "client", "car", "flight", "hotel", "customer"]:
        return arg
    raise Exception("Invalid component")

def launch_component(component, protocol, port, extra_args):
    if protocol == "tcp":
        if component == "middleware":
            os.system("java -cp %s comp512.TCPMiddleWare %d %s" % (CLASS_PATH, port, extra_args))
        elif component == "client":
            os.system("java -cp %s comp512.TCPClient %s %d" % (CLASS_PATH, extra_args, port))
        elif component == "customer":
            os.system("java -cp %s ResImpl.CustomerManagerImpl tcp %d %s" % (CLASS_PATH, port, extra_args))
        else:
            os.system("java -cp %s ResImpl.%sManagerImpl tcp %d" % (CLASS_PATH, component.capitalize(), port))


def main():
    if len(sys.argv) < 4:
        usage()
        sys.exit(1)

    protocol = get_protocol(sys.argv[1])
    port = get_port(sys.argv[2])
    component = get_component(sys.argv[3])
    extra_args = " ".join(sys.argv[4:])

    launch_component(component, protocol, port, extra_args)


if __name__ == "__main__":
    main()
