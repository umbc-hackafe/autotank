from django.http import HttpResponse
from django.utils import simplejson
from django.template import RequestContext, loader
from ioControl.models import Motor
import xmlrpclib

s = xmlrpclib.ServerProxy('http://192.168.0.108:8000', allow_none=True)
def changeTurretDirection(*args):
  s.changeTurretDirection(*args)

def setTurretDirection(*args):
  s.setTurretDirection(*args)

def setTurretSpeed(*args):
  s.setTurretSpeed(*args)

def setTreadSpeedDir(*args):
  s.setTreadSpeedDir(*args)
