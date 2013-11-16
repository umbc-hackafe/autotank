from django.conf.urls import patterns, url
from ioControl import views

urlpatterns = patterns('',
  url(r'^$', views.index, name='index'),
  url(r'^motorrefresh/$', views.motorRefresh, name='motorRefresh'),
  url(r'^motorlist/$', views.motorList, name='motorList'),
  url(r'^motors/$', views.motors, name='motors'),
  url(r'^sensors/$', views.sensors, name='sensors'),
  url(r'^sensorrefresh/$', views.sensorRefresh, name='sensorRefresh'),
  url(r'^xmlrpc/$', 'django_xmlrpc.views.handle_xmlrpc',),
)
