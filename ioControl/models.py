from django.db import models

class Sensor(models.Model):
  name = models.CharField(max_length=64)
  SENSOR_TYPES = (
    ('AN', 'Analog'),
    ('DG', 'Digital'),
    ('DI', 'Distance'),
    ('BE', 'Bearing'),
    ('VE', 'Velocity'),
  )
  type = models.CharField(max_length=2, choices=SENSOR_TYPES)
  deviceID = models.CharField(max_length=16)

class Motor(models.Model):
  name = models.CharField(max_length=64)
  MOTOR_TYPES = (
    ('RS', 'Reversable with Speed Control'),
    ('RN', 'Reversable without Speed Control'),
    ('SS', 'Non-Reversable with Speed Control'),
    ('SN', 'Non-Reversable without Speed Control'),
  )
  type = models.CharField(max_length=2, choices=MOTOR_TYPES)
  deviceID = models.CharField(max_length=16)

class Device(models.Model):
  name = models.CharField(max_length=64)
  deviceID = models.CharField(max_length=16)
  
