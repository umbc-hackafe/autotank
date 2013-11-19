#!/usr/bin/python3
from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.server import SimpleXMLRPCRequestHandler

from motorserial import *

class RequestHandler(SimpleXMLRPCRequestHandler):
    rpc_paths = ('/RPC2',)

server = SimpleXMLRPCServer(("0.0.0.0", 8000), 
                            requestHandler=RequestHandler, 
                            allow_none = True)
server.register_introspection_functions()

tank = motorcontrol()

try:
    server.register_function(tank.changeTurretDirection)
    server.register_function(tank.setTurretDirection)
    server.register_function(tank.setTurretSpeed)
    server.register_function(tank.setTreadSpeedDir)

    server.serve_forever()
finally:
    tank.close()
