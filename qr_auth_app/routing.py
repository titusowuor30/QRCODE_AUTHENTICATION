# authentication/routing.py

from django.urls import path
from .consumers import AuthConsumer

websocket_urlpatterns = [
    path('ws/auth/<str:key>/', AuthConsumer.as_asgi()),
]
