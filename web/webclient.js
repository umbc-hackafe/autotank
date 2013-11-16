$(document).ready(function(){
  $(document).keydown(keydown);
  $(document).keyup(keyup);
});

var url = "http://130.85.228.132:8080/ioctl/xmlrpc/"

var treadspeed = 0;
var treaddir = 0;
var turretspeed = 0;

function keydown(event){
  if((event.which == 65 || event.which == 37) && treaddir > -1){
    // a and left
    treaddir = -1;
    setTreadSpeeds();
  }
  else if((event.which == 68 || event.which == 39) && treaddir < 1){
    // d and right
    treaddir = 1;
    setTreadSpeeds();
  }
  else if((event.which == 87 || event.which == 38) && treadspeed < 1){
    // w and up
    treadspeed = 1;
    setTreadSpeeds();
  }
  else if((event.which == 83 || event.which == 40) && treadspeed > -1){
    // s and down
    treadspeed = -1;
    setTreadSpeeds();
  }
  else if((event.which == 74 || event.which == 100) && turretspeed > -1){
    // j or num 4
    turretspeed = -1;
    setTurretProp();
  }
  else if((event.which == 76 || event.which == 102) && turretspeed < 1){
    // l or num 6
    turretspeed = 1;
    setTurretProp();
  }
}

function keyup(event){
  if((event.which == 65 || event.which == 37) && treaddir < 0){
    // a and left
    treaddir = 0;
    setTreadSpeeds();
  }
  else if((event.which == 68 || event.which == 39) && treaddir > 0){
    // d and right
    treaddir = 0;
    setTreadSpeeds();
  }
  else if((event.which == 87 || event.which == 38) && treadspeed > 0){
    // w and up
    treadspeed = 0;
    setTreadSpeeds();
  }
  else if((event.which == 83 || event.which == 40) && treadspeed < 0){
    // s and down
    treadspeed = 0;
    setTreadSpeeds();
  }
  else if((event.which == 74 || event.which == 100) && turretspeed < 0){
    // j or num 4
    turretspeed = 0;
    setTurretProp();
  }
  else if((event.which == 76 || event.which == 102) && turretspeed > 0){
    // l or num 6
    turretspeed = 0;
    setTurretProp();
  }
}

function setTurretProp(){
  if(turretspeed > 0){
    $.xmlrpc({
      url: url,
      methodName: 'setTurretDirection',
      params: [false],
      success: function(response, status, jqXHR) { },
      error: function(jqXHR, status, error) { }
    });
    
    $.xmlrpc({
      url: url,
      methodName: 'setTurretSpeed',
      params: [100],
      success: function(response, status, jqXHR) { },
      error: function(jqXHR, status, error) { }
    });
  }
  else if(turretspeed < 0){
    $.xmlrpc({
      url: url,
      methodName: 'setTurretDirection',
      params: [true],
      success: function(response, status, jqXHR) { },
      error: function(jqXHR, status, error) { }
    });
    
    $.xmlrpc({
      url: url,
      methodName: 'setTurretSpeed',
      params: [100],
      success: function(response, status, jqXHR) { },
      error: function(jqXHR, status, error) { }
    });
  }
  else{
    $.xmlrpc({
      url: url,
      methodName: 'setTurretSpeed',
      params: [0],
      success: function(response, status, jqXHR) { },
      error: function(jqXHR, status, error) { }
    });
  }
}

function setTreadSpeeds(){
  var leftTreadSpeed = 0;
  var leftTreadDir = true;
  var rightTreadSpeed = 0;
  var rightTreadDir = true;
  
  if(treadspeed > 0){
    leftTreadSpeed = treaddir >= 0 ? 100 : 0;
    rightTreadSpeed = treaddir <= 0 ? 100 : 0;
    
    leftTreadDir = true;
    rightTreadDir = true;
  }
  else if(treadspeed < 0){
    leftTreadSpeed = treaddir >= 0 ? 100 : 0;
    rightTreadSpeed = treaddir <= 0 ? 100 : 0;

    leftTreadDir = false;
    rightTreadDir = false;
  }
  else {    
    leftTreadSpeed = treaddir != 0 ? 100 : 0;
    rightTreadSpeed = treaddir != 0 ? 100 : 0;

    if(treaddir < 0){
      leftTreadDir = false;
      rightTreadDir = true;
    }
    else{
      leftTreadDir = true;
      rightTreadDir = false;
    }
  }
  
  $.xmlrpc({
    url: url,
    methodName: 'setTreadSpeedDir',
    params: [0, leftTreadDir, leftTreadSpeed],
    success: function(response, status, jqXHR) { },
    error: function(jqXHR, status, error) { }
  });

  $.xmlrpc({
    url: url,
    methodName: 'setTreadSpeedDir',
    params: [1, rightTreadDir, rightTreadSpeed],
    success: function(response, status, jqXHR) { },
    error: function(jqXHR, status, error) { }
  });
}
