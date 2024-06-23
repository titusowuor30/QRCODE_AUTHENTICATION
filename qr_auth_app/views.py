# qr_auth_app/views.py
from django.shortcuts import render, redirect
from django.http import JsonResponse
from django.contrib.auth.tokens import default_token_generator
from django.views.decorators.csrf import csrf_exempt
from .models import AuthRequest
import qrcode
from io import BytesIO
import base64
import uuid
from django.contrib.auth import authenticate, login
import jwt
from django.conf import settings

def login_with_key(request):
    key = request.GET.get('key')
    try:
        auth_request = AuthRequest.objects.get(login_key=key, status='completed')
        user = auth_request.user
        login(request, user)
        auth_request.status = 'used'  # Invalidate the key after use
        auth_request.save()
        return redirect('home')
    except AuthRequest.DoesNotExist:
        return redirect('login')
    
def qr_login(request):
    return render(request, 'qr_auth_app/qr_login.html')

def generate_auth_key():
    # Generate a unique key using UUID
    return str(uuid.uuid4())

def generate_qr_code_data(key):
    # Generate QR code image and return its data
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    qr.add_data(key)
    qr.make(fit=True)
    img = qr.make_image(fill='black', back_color='white')
    
    buffer = BytesIO()
    img.save(buffer, format="PNG")
    return buffer.getvalue()

def get_qr_code(request):
    key = generate_auth_key()
    AuthRequest.objects.create(key=key, status='pending')
    qr_code_data = generate_qr_code_data(key)
    qr_code_base64 = base64.b64encode(qr_code_data).decode('utf-8')
    return JsonResponse({'qr_code_base64': qr_code_base64, 'key': key})

@csrf_exempt
def accept_qr_login(request):
    if request.method == 'POST':
        key = request.POST.get('key')
        token = request.POST.get('token')
        try:
            payload = jwt.decode(token, settings.SECRET_KEY, algorithms=['HS256'])
            user_id = payload['user_id']
            auth_request = AuthRequest.objects.get(key=key)
            if auth_request.status == 'pending' and auth_request.user.id == user_id:
                login_key = default_token_generator.make_token(auth_request.user)
                auth_request.login_key = login_key
                auth_request.status = 'completed'
                auth_request.save()
                return JsonResponse({'status': 'success', 'login_key': login_key})
        except (AuthRequest.DoesNotExist, jwt.ExpiredSignatureError, jwt.InvalidTokenError):
            pass
    return JsonResponse({'status': 'failure'})

def check_auth_status(request, key):
    try:
        auth_request = AuthRequest.objects.get(key=key)
        if auth_request.status == 'completed':
            return JsonResponse({'status': 'completed', 'login_key': auth_request.login_key})
    except AuthRequest.DoesNotExist:
        pass
    return JsonResponse({'status': 'pending'})
