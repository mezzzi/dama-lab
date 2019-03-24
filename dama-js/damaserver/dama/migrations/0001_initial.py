# -*- coding: utf-8 -*-
# Generated by Django 1.10.2 on 2017-04-16 17:20
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Move',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('unique_id', models.BigIntegerField()),
                ('is_read', models.BooleanField()),
                ('is_top', models.BooleanField()),
                ('is_pending', models.BooleanField()),
                ('start_row', models.PositiveSmallIntegerField()),
                ('start_col', models.PositiveSmallIntegerField()),
                ('landing_row', models.PositiveSmallIntegerField()),
                ('landing_col', models.PositiveSmallIntegerField()),
            ],
        ),
    ]