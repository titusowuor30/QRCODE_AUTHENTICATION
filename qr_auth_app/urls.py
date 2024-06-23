# qr_auth_app/urls.py
from django.urls import path
from .views import *

urlpatterns = [
    path('qr-login/', qr_login, name='qr-login'),
    path('authenticate/<str:key>/', authenticate_key, name='authenticate'),
    path('check-auth-status/<str:key>/', check_auth_status, name='check-auth-status'),
    path('get-qr-code/', qr_code_view, name='get-qr-code'),
    path('login/', login_with_key, name='login-with-key'),  # Add this line
    path('home/<str:key>/', home_page, name='home'),  # Add this line
]
