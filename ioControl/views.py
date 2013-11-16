from django.http import HttpResponse
from django.utils import simplejson
from django.template import RequestContext, loader
from ioControl.models import Motor
def index(request):
  template = loader.get_template('zenlike/index.html')
  context = RequestContext(request, {
    'title' : 'Home',
    'version' : '1.0',
    'sections' : [('Home', './'), ('Motors', './motors'), ('Sensors', './sensors'),],
    'activePage' : 'Home',
  })
  return HttpResponse(template.render(context))

def motors(request):
  template = loader.get_template('zenlike/motors.html')
  context = RequestContext(request, {
    'title' : 'Home',
    'version' : '1.0',
    'sections' : [('Home', './'), ('Motors', './motors'), ('Sensors', './sensors'),],
    'activePage' : 'Motors',
  })
  return HttpResponse(template.render(context))

def motorRefresh(request):
  results = {'value1':4, 'value2':16, 'success':'Yes'}
  json = simplejson.dumps(results)
  return HttpResponse(json, mimetype='application/json')

def motorList(request):
  motors = Motor.objects.order_by('name')
  results = "Hello"
  json = simplejson.dumps(results)
  return HttpResponse(json, mimetype='application/json')

def sensors(request):
  template = loader.get_template('zenlike/sensors.html')
  context = RequestContext(request, {
    'title' : 'Home',
    'version' : '1.0',
    'sections' : [('Home', './'), ('Motors', './motors'), ('Sensors', './sensors'),],
    'activePage' : 'Sensors',
  })
  return HttpResponse(template.render(context))

def sensorRefresh(request):
  pass

def changeTurretDirection(request):
  pass

def setTurretDirection(request):
  pass

def setTurretSpeed(request):
  pass

def setTreadSpeedDir(request):
  pass
