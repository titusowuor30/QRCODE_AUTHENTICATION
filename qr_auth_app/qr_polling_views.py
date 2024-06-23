# auth_app/views.py

from django.shortcuts import render, redirect
from django.http import JsonResponse
from django.utils.crypto import get_random_string
from .models import AuthSession
import qrcode
from io import BytesIO
import base64

def generate_qr_code(session_id):
    qr = qrcode.QRCode(version=1, error_correction=qrcode.constants.ERROR_CORRECT_L, box_size=10, border=4)
    qr.add_data(session_id)
    qr.make(fit=True)
    img = qr.make_image(fill_color="black", back_color="white")
    buffer = BytesIO()
    img.save(buffer, format="PNG")
    img_str = base64.b64encode(buffer.getvalue()).decode()
    return img_str

def qr_code_view(request):
    session_id = get_random_string(32)
    AuthSession.objects.create(session_id=session_id)
    qr_code_image = generate_qr_code(session_id)
    return render(request, 'qr_code.html', {'qr_code_image': qr_code_image, 'session_id': session_id})

def check_auth_status(request, session_id):
    try:
        auth_session = AuthSession.objects.get(session_id=session_id)
        return JsonResponse({'status': auth_session.status})
    except AuthSession.DoesNotExist:
        return JsonResponse({'status': 'FAILED'})
