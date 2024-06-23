# qr_auth_app/views.py
from django.shortcuts import render, redirect
from django.http import JsonResponse
from django.contrib.auth.tokens import default_token_generator
from django.views.decorators.csrf import csrf_exempt
from .models import AuthRequest
from django.contrib.auth import authenticate, login
import jwt
import base64
from django.conf import settings
from django.utils.crypto import get_random_string
from django.utils import timezone
from datetime import timedelta
from .utils import generate_auth_key,generate_qr_code_data

def home_page(request,key=None):
    if AuthRequest.objects.filter(key=key).first() is None:
        return redirect("qr-login")
    return render(request,"core/home.html")

def login_with_key(request):
    key = request.GET.get('key')
    try:
        auth_request = AuthRequest.objects.get(login_key=key, status='Completed')
        #user = auth_request.user
        #login(request, user)
        auth_request.status = 'used'  # Invalidate the key after use
        #auth_request.save()
        return redirect('home',key=key)
    except AuthRequest.DoesNotExist:
        return redirect('login')
    
def qr_login(request):
    return render(request, 'qr_auth_app/qr_login.html')


def qr_code_view(request):
    key = generate_auth_key()
    AuthRequest.objects.create(login_key=key,key=key, status='Pending')
    qr_code_data = generate_qr_code_data(key)
    qr_code_base64 = base64.b64encode(qr_code_data).decode('utf-8')
    return JsonResponse({'qr_code_base64': qr_code_base64, 'key': key})

@csrf_exempt
def authenticate_key(request,key):
    if request.method == 'POST':
        #token = request.POST.get('token')
        try:
            #payload = jwt.decode(token, settings.SECRET_KEY, algorithms=['HS256'])
            #user_id = payload['user_id']
            auth_request = AuthRequest.objects.get(key=key)
            if auth_request.status == 'Pending':# and auth_request.user.id == user_id:
                #login_key = default_token_generator.make_token(auth_request.user)
                #auth_request.login_key = login_key
                auth_request.status = 'Completed'
                auth_request.save()
                return JsonResponse({'status': 'Success', 'login_key': key})
        except (AuthRequest.DoesNotExist, jwt.ExpiredSignatureError, jwt.InvalidTokenError):
            pass
    return JsonResponse({'status': 'Failed'})

def check_auth_status(request, key):
    try:
        #delete expired
        expired_reqs=AuthRequest.objects.filter(created_at__lte=timezone.now()-timedelta(seconds=60))
        expired_reqs.delete()
        print(expired_reqs)
        auth_request = AuthRequest.objects.get(login_key=key)
        if auth_request.status == 'Completed':
            return JsonResponse({'status': 'Completed', 'login_key': auth_request.login_key})
    except AuthRequest.DoesNotExist:
        pass
    return JsonResponse({'status': 'Pending'})
