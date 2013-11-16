import serial

turretChangeDir = 'a'
turretChangeSpd = 'b'
treadSetSpeedDir = 'c'
turretSetDir = 'd'

class motorcontrol:
    def __init__(self, targetPort="/dev/ttyUSB0", speed=9600):
        self.targetPort = targetPort
        self.speed = speed
        
        self.ser = serial.Serial(port=self.targetPort, baudrate=self.speed, 
                               bytesize=serial.EIGHTBITS, parity=serial.PARITY_NONE,
                               stopbits=serial.STOPBITS_ONE)

    def changeTurretDirection(self):
        self.ser.write(bytes(turretChangeDir, "ascii"));

    def setTurretDirection(self, direction):
        dirvalue = bytearray(1)
        dirvalue[0] = 1 if direction else 0
        
        self.ser.write(bytes(turretSetDir, "ascii"))
        self.ser.write(dirvalue)
        
        

    def setTurretSpeed(self, speedPercent):
        speedVal = int(max(min(speedPercent / 100.0 * 255, 255), 0))
        speedvalue = bytearray(1)
        speedvalue[0] = speedVal
        
        self.ser.write(bytes(turretChangeSpd, "ascii"))
        self.ser.write(speedvalue)
        
    def setTreadSpeedDir(self, treadNum, Direction):
        speedVal = int(max(min(Direction, 1), -1))

        values = bytearray(2)
        values[0] = int(treadNum)
        values[1] = Direction if Direction >= 0 else 256 + Direction
        
        self.ser.write(bytes(treadSetSpeedDir, "ascii"))
        self.ser.write(values)



    def close(self):
        self.ser.close()
