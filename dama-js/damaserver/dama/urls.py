from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^(?P<uniqueId>\d+)/$', views.index, name='index'),
    url(r'^send/$', views.send, name='send'),
    url(r'^recieve/$', views.recieve, name='recieve'),
]
