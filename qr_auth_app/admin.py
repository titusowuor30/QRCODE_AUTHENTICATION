from django.contrib import admin
from .models import *

# Register your models here.
@admin.register(AuthRequest)
class AuthRequestAdmin(admin.ModelAdmin):
    list_display=['key','login_key','status','created_at']