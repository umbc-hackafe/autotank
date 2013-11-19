#define turretDirPin 2
#define turretSpeedPin 3

#define turretChangeDir 'a'
#define turretChangeSpd 'b'
#define treadSetSpeedDir 'c'
#define turretSetDir 'd'

byte turretDir = false;
byte turretSpeed = 0;

const byte treadFwdPin[2] = {12, 7};
const byte treadRevPin[2] = {13, 8};
signed char treadDir[2] = {0, 0};

void setup(){
  pinMode(turretDirPin, OUTPUT);
  pinMode(turretSpeedPin, OUTPUT);
  
  digitalWrite(turretDirPin, turretDir ? HIGH : LOW);
  analogWrite(turretSpeedPin, turretDir ? ~turretSpeed : turretSpeed);
  
  Serial.begin(9600);
  
  for(int i = 0; i < 2; i++){
    pinMode(treadFwdPin[i], OUTPUT);
    pinMode(treadRevPin[i], OUTPUT);
    
    digitalWrite(treadFwdPin[i], treadDir[i] > 0 ? LOW : HIGH);
    digitalWrite(treadRevPin[i], treadDir[i] < 0 ? LOW : HIGH);
  }
} 

void loop(){
  while(Serial.available() > 0){
    byte cmd = Serial.read();
    
    if(cmd == turretChangeSpd){
      while(Serial.available() < 1);
      
      turretSpeed = Serial.read();
      
      analogWrite(turretSpeedPin, turretDir ? ~turretSpeed : turretSpeed);
    }
    else if(cmd == turretChangeDir){
      changeTurretDir();
    }
    else if(cmd == turretSetDir){
      while(Serial.available() < 1);
      
      signed char dir = Serial.read();
      
      setTurretDir(dir);
    }
    else if(cmd == treadSetSpeedDir){
      while(Serial.available() < 2);
      
      byte tread = Serial.read();
      byte spd = Serial.read();
      
      setTreadSpeedDir(tread, spd);
    }
    
  }
}

void setTreadSpeedDir(byte tread, signed char spd){
  tread = constrain(tread, 0, 1);
  spd = constrain(spd, -1, 1);
  
  treadDir[tread] = spd;
  
  digitalWrite(treadFwdPin[tread], spd > 0 ? LOW : HIGH);
  digitalWrite(treadRevPin[tread], spd < 0 ? LOW : HIGH);
}

void setTurretDir(byte dir){
  if(dir ^ turretDir){
    changeTurretDir();
  }
}

void changeTurretDir(){
  digitalWrite(turretDirPin, (turretDir = !turretDir) ? HIGH : LOW);
  analogWrite(turretSpeedPin, turretDir ? ~turretSpeed : turretSpeed);
}
