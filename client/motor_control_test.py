#!/usr/bin/python3
import motorserial
import sys
import readline

def main():
    if len(sys.argv) >= 2:
        target = sys.argv[1]
    else:
#       print ("Error: must pass the serial port to use")
#       sys.exit()
        target = "/dev/ttyUSB0"
        
    cont = motorserial.motorcontrol(target, 9600)
        
    while True:
        print ("use cd to change direction or cs to change speed")
        
        inputstr = input()
        
        command = inputstr[:2]
        
        value = str.strip(inputstr[2:])
        
        if command == "cs":
            try:
                value = float(value)
            except ValueError as err:
                print (err)
                continue
            
            cont.setSpeed(value)
            
        elif command == "cd":
            cont.changeDirection()
        else:
            print ("Invalid command type:", command)

main()
