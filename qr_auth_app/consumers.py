# authentication/consumers.py
from .models import AuthRequest
from asgiref.sync import sync_to_async
import json
from channels.generic.websocket import AsyncWebsocketConsumer

class AuthConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        self.key = self.scope['url_route']['kwargs']['key']
        self.group_name = f'auth_{self.key}'

        # Join room group
        await self.channel_layer.group_add(
            self.group_name,
            self.channel_name
        )

        await self.accept()

    async def disconnect(self, close_code):
        # Leave room group
        await self.channel_layer.group_discard(
            self.group_name,
            self.channel_name
        )

    async def receive(self, text_data):
        try:
            data = json.loads(text_data)
            status = data.get('status')  # Use .get() to safely access 'status'
        except json.JSONDecodeError:
            await self.send(text_data=json.dumps({
                'error': 'Invalid JSON received'
            }))
            return

        # Save status to database asynchronously
        key = self.scope['url_route']['kwargs']['key']
        auth_request = await sync_to_async(AuthRequest.objects.filter(key=key).first)()
        if auth_request is not None:
            await sync_to_async(auth_request.save)()

        # Send status back to the group along with the key
        await self.channel_layer.group_send(
            self.group_name,
            {
                'type': 'auth_status',
                'status': status,
                'key': key,
            }
        )

    async def auth_status(self, event):
        status = event['status']
        key = event['key']
        print(event)
        # Send status to WebSocket
        await self.send(text_data=json.dumps({
            'status': status,
            'key': key,
        }))
