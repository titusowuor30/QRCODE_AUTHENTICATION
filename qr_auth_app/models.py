# qr_auth_app/models.py

from django.db import models
from django.contrib.auth.models import User

# Create your models here.
class AuthRequest(models.Model):
    key = models.CharField(max_length=255, unique=True,blank=True,null=True)
    status = models.CharField(max_length=50, default='Pending')  # 'pending', 'completed'
    login_key = models.CharField(max_length=255, null=True, blank=True)
    #user = models.ForeignKey(User, on_delete=models.CASCADE, null=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"AuthRequest {self.key} - Status: {self.status}"

