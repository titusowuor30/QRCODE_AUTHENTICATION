import qrcode
from io import BytesIO
import uuid

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