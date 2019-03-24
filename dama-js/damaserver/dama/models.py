from django.db import models

class Move(models.Model):
    unique_id = models.BigIntegerField()
    is_read = models.BooleanField()
    is_top = models.BooleanField()
    is_pending = models.BooleanField()
    start_row = models.PositiveSmallIntegerField()
    start_col = models.PositiveSmallIntegerField()
    landing_row = models.PositiveSmallIntegerField()
    landing_col = models.PositiveSmallIntegerField()

class Player(models.Model):
    unique_id = models.BigIntegerField()
    is_new = models.BooleanField()
