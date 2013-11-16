#define turretDirPin 13
#define turretSpeedPin 10

#define turretChangeDir 'a'
#define turretChangeSpd 'b'
#define treadSetSpeedDir 'c'
#define turretSetDir 'd'

byte turretDir = true;
byte turretSpeed = 0;

const byte treadDigPin[2] = {2, 4};
const byte treadPwmPin[2] = {3, 5};
byte treadDig[2] = {false, false};
byte treadPwm[2] = {0, 0};

void setup(){
  pinMode(turretDirPin, OUTPUT);
  pinMode(turretSpeedPin, OUTPUT);
  
  Serial.begin(9600);
  
  digitalWrite(turretDirPin, turretDir ? HIGH : LOW);
  analogWrite(turretSpeedPin, turretSpeed);
  
  for(int i = 0; i < 2; i++){
    pinMode(treadDigPin[i], OUTPUT);
    pinMode(treadPwmPin[i], OUTPUT);
    
    digitalWrite(treadDigPin[i], treadDig[i] ? HIGH : LOW);
    analogWrite(treadPwmPin[i], treadPwm[i]);
  }
} 

void loop(){
  while(Serial.available() > 0){
    byte cmd = Serial.read();
    
    if(cmd == turretChangeSpd){
      while(Serial.available() < 1);
      
      turretSpeed = Serial.read();
      
      analogWrite(turretSpeedPin, turretSpeed);
    }
    else if(cmd == turretChangeDir){
      changeTurretDir();
    }
    else if(cmd == turretSetDir){
      while(Serial.available() < 1);
      
      byte dir = Serial.read();
      
      setTurretDir(dir);
    }
    else if(cmd == treadSetSpeedDir){
      while(Serial.available() < 3);
      
      byte tread = Serial.read();
      byte dir = Serial.read();
      byte spd = Serial.read();
      
      setTreadSpeedDir(tread, dir, spd);
    }
    
  }
}

void setTreadSpeedDir(byte tread, byte dir, byte spd){
  tread = constrain(tread, 0, 1);
  
  treadDig[tread] = dir;
  
  digitalWrite(treadDigPin[tread], dir ? HIGH : LOW);
  
  treadPwm[tread] = spd;
  analogWrite(treadPwmPin[tread], dir ? ~spd : spd);
}

void setTurretDir(byte dir){
  if(dir ^ turretDir){
    changeTurretDir();
  }
}

void changeTurretDir(){
  if(turretSpeed > 0){
    analogWrite(turretSpeedPin, 0);
    delay(200);
  }
  digitalWrite(turretDirPin, (turretDir = !turretDir) ? HIGH : LOW);
  if(turretSpeed > 0){
    analogWrite(turretSpeedPin, turretSpeed);
  }
}
