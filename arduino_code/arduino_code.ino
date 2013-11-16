#define turretDirPin 13
#define turretSpeedPin 10

#define turretChangeDir 'a'
#define turretChangeSpd 'b'
#define treadSetSpeedDir 'c'
#define turretSetDir 'd'

byte turretDir = true;
byte turretSpeed = 0;

const byte treadFwdPin[2] = {2, 4};
const byte treadRevPin[2] = {3, 5};
signed byte treadDir[2] = {0, 0};

void setup(){
  pinMode(turretDirPin, OUTPUT);
  pinMode(turretSpeedPin, OUTPUT);
  
  digitalWrite(turretDirPin, turretDir ? HIGH : LOW);
  analogWrite(turretSpeedPin, turretSpeed);
  
  Serial.begin(9600);
  
  for(int i = 0; i < 2; i++){
    pinMode(treadFwdPin[i], OUTPUT);
    pinMode(treadRevPin[i], OUTPUT);
    
    digitalWrite(treadFwdPin[i], treadDir[i] > 0 ? HIGH : LOW);
    digitalWrite(treadRevPin[i], treadDir[i] < 0 ? HIGH : LOW);
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
      while(Serial.available() < 2);
      
      byte tread = Serial.read();
      byte spd = Serial.read();
      
      setTreadSpeedDir(tread, spd);
    }
    
  }
}

void setTreadSpeedDir(byte tread, byte spd){
  tread = constrain(tread, 0, 1);
  sod = constrain(spd, -1, 1);
  
  treadDir[tread] = spd;
  
  digitalWrite(treadFwdPin[tread], spd > 0 ? HIGH : LOW);
  digitalWrite(treadRevPin[tread], spd < 0 ? HIGH : LOW);
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
