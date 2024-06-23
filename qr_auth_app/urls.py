# qr_auth_app/urls.py
from django.urls import path
from .views import qr_login, accept_qr_login, check_auth_status, get_qr_code, login_with_key

urlpatterns = [
    path('qr-login/', qr_login, name='qr-login'),
    path('accept-qr-login/', accept_qr_login, name='accept-qr-login'),
    path('check-auth-status/<str:key>/', check_auth_status, name='check-auth-status'),
    path('get-qr-code/', get_qr_code, name='get-qr-code'),
     path('login/', login_with_key, name='login-with-key'),  # Add this line
]
