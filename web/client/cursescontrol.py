#!/usr/bin/python3
import curses
import xmlrpc.client

leftTreadFwd = True
rightTreadFwd = True
leftTreadSpeed = 0
rightTreadSpeed = 0

turretDirection = True
turretSpeed = 0

speedMode = 100

def main(scr):
    try:
        scr.clear()
    
        curses.curs_set(False)
        curses.init_pair(1, curses.COLOR_RED, curses.COLOR_BLACK)
        curses.init_pair(2, curses.COLOR_GREEN, curses.COLOR_BLACK)
        curses.init_pair(3, curses.COLOR_BLACK, curses.COLOR_RED)
        curses.init_pair(4, curses.COLOR_BLACK, curses.COLOR_GREEN)
        curses.init_pair(5, curses.COLOR_BLACK, curses.COLOR_WHITE)
        curses.init_pair(6, curses.COLOR_WHITE, curses.COLOR_BLACK)
        
        while True:
            redraw(scr)
            checkControls(scr)
    except KeyboardInterrupt:
        pass

def checkControls(scr):
    kn = str.upper(scr.getkey())
    
    global leftTreadFwd, leftTreadSpeed, rightTreadFwd, rightTreadSpeed, turretDirection, turretSpeed, speedMode

    if kn == 'Q':
        leftTreadFwd = True
        leftTreadSpeed = speedMode
        tank.setTreadSpeedDir(0, leftTreadFwd, leftTreadSpeed)
    elif kn == 'A':
        leftTreadSpeed = 0
        tank.setTreadSpeedDir(0, leftTreadFwd, leftTreadSpeed)
    elif kn == 'Z':
        leftTreadFwd = False
        leftTreadSpeed = speedMode
        tank.setTreadSpeedDir(0, leftTreadFwd, leftTreadSpeed)

    elif kn == 'E':
        rightTreadFwd = True
        rightTreadSpeed = speedMode
        tank.setTreadSpeedDir(1, rightTreadFwd, rightTreadSpeed)
    elif kn == 'D':
        rightTreadSpeed = 0
        tank.setTreadSpeedDir(1, rightTreadFwd, rightTreadSpeed)
    elif kn == 'C':
        rightTreadFwd = False
        rightTreadSpeed = speedMode
        tank.setTreadSpeedDir(1, rightTreadFwd, rightTreadSpeed)
            
    elif kn == 'J':
        turretSpeed = speedMode
        turretDirection = True
        tank.setTurretDirection(turretDirection)
        tank.setTurretSpeed(turretSpeed)
    elif kn == 'K':
        turretSpeed = 0
        tank.setTurretSpeed(turretSpeed)
    elif kn == 'L':
        turretSpeed = speedMode
        turretDirection = False
        tank.setTurretDirection(turretDirection)
        tank.setTurretSpeed(turretSpeed)
        

    elif len(kn) == 1 and ord(kn) >= ord('1') and ord(kn) <= ord('9'):
        speedMode = int(kn) * 10
    elif kn == '0':
        speedMode = 100

def redraw(scr):
    scr.erase()

    size = scr.getmaxyx()

    totalwidth = 44
    totalheight = 15

    # treads
    TrbX = int(size[1] / 2 - totalwidth / 2)
    TrbY = int(size[0] / 2 - totalheight / 2)

    TrbW = 20
    TrbH = 15

    drawbox(TrbY, TrbX, TrbW, TrbH, scr, curses.color_pair(6))

    scr.addstr(TrbY, TrbX+1, " TREAD CONTROL ", curses.color_pair(5))

    TrY = 3 + TrbY
    TrYs = 3
    TrlX = 6 + TrbX
    TrrX = 6 + TrlX

    scr.addch(TrY + 1, TrlX-3, 'F')
    scr.addch(TrY + TrYs + 1, TrlX-3, 'N')
    scr.addch(TrY + 2*TrYs + 1, TrlX-3, 'R')

    scr.addstr(TrY - 1, TrlX, "LEFT")
    scr.addstr(TrY - 1, TrrX, "RIGHT")

    # left tread dir
    drawbtn(TrY, TrlX, 'Q', leftTreadFwd and leftTreadSpeed != 0, scr)
    drawbtn(TrY + TrYs, TrlX, 'A', leftTreadSpeed == 0, scr)
    drawbtn(TrY + 2*TrYs, TrlX, 'Z', not leftTreadFwd and leftTreadSpeed != 0, scr)

    # right tread dir
    drawbtn(TrY, TrrX, 'E', rightTreadFwd and rightTreadSpeed != 0, scr)
    drawbtn(TrY + TrYs, TrrX, 'D', rightTreadSpeed == 0, scr)
    drawbtn(TrY + 2*TrYs, TrrX, 'C', not rightTreadFwd and rightTreadSpeed != 0, scr)

    scr.addch(TrY + 3*TrYs, TrlX-3, 'S')
    scr.addstr(TrY + 3*TrYs, TrlX+1, str.format("{0: >3d}%", leftTreadSpeed))
    scr.addstr(TrY + 3*TrYs, TrrX+1, str.format("{0: >3d}%", rightTreadSpeed))

    # Turret
    TubX = TrbX + TrbW + 2
    TubY = TrbY
    
    TubW = 23
    TubH = 10

    drawbox(TubY, TubX, TubW, TubH, scr, curses.color_pair(6))
    scr.addstr(TubY, TubX+1, " TURRET CONTROL ", curses.color_pair(5))

    # directional buttons.
    TuY = TubY + 3
    TuX = TubX + 3
    TuXs = 6
    
    scr.addstr(TuY - 1, TuX, "LEFT")
    scr.addstr(TuY - 1, TuX + TuXs, "HOLD")
    scr.addstr(TuY - 1, TuX + TuXs * 2, "RIGHT")

    drawbtn(TuY, TuX, 'J', turretDirection and turretSpeed != 0, scr)
    drawbtn(TuY, TuX + TuXs, 'K', turretSpeed == 0, scr)
    drawbtn(TuY, TuX + TuXs * 2, 'L', not turretDirection and turretSpeed != 0, scr)
    
    TusY = TuY + 4
    
    scr.addstr(TusY, TuX, "SPEED")
    scr.addstr(TusY, TuX + 8, str.format("{0: >3d}%", turretSpeed))

    #speed mode indicator
    TsbY = TubY + TubH
    TsbX = TubX
    
    TsbW = TubW
    TsbH = TrbH - TubH

    drawbox(TsbY, TsbX, TsbW, TsbH, scr, curses.color_pair(6))

    scr.addstr(TsbY, TsbX + 1, " SETTINGS ", curses.color_pair(5))

    scr.addstr(TsbY + 2, TsbX + 4, "SPEED")
    scr.addstr(TsbY + 2, TsbX + 4 + 8, str.format("{0: >3d}%", speedMode))

    scr.refresh()

def drawbtn(y, x, center, on, scr):
    color = curses.color_pair(2) if on else curses.color_pair(1)

    width = 5
    height = 3
    
    drawbox(y, x, width, height, scr, color)

    scr.addch(y+int(height/2), x+int(width/2), center, color)

def drawbox(y, x, width, height, scr, attr):
    for i in range(height):
        for k in range(width):
            on = k == 0 or i == 0 or k == width-1 or i == height - 1
            if on:
                scr.addch(y+i, x+k, '#', attr)

tank = xmlrpc.client.ServerProxy("http://130.85.228.132:8000")

try:
    curses.wrapper(main)
finally:
    tank.close()
