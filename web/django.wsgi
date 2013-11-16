import os
import sys

path = '/var/www/tank'
if path not in sys.path:
  sys.path.append(path)
os.environ['DJANGO_SETTINGS_MODULE'] = 'tank.settings'

import django.core.handlers.wsgi
application = django.core.handlers.wsgi.WSGIHandler()

