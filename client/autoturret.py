#!/usr/bin/python3
from motorserial import *
from time import sleep

turret = motorcontrol("/dev/ttyUSB0", 9600)

try:
    while True:
        turret.setSpeed(100)
        sleep(3)
        turret.setSpeed(0)
        sleep(.2)
        turret.changeDirection()
except:
    turret.setSpeed(0)
