# This is a simple django qr auth project with an android app
## The project coniders two scenarios:
- Polling technique(Synchronous)
- Websockets integration(Asynchronous)

## Testing steps
- Clone the django web app
- create a python env and activate it
- install requirements
- run the django web app using the command python manage.py runserver 0.0.0.0:8000 to expose it within your local network on port 8000
- Edit the code in the MainActibvity.kts to replace the ip address with your own local machine ip address
- Build the app, run in debug mode, link the app to your phone(use machine local ipv4 address) as an emulator or use the android studuio emulator and in this case, the ip address should be 10.0.2.2(default ip for android emulator)
- wait for the app to lauch and scan the qr code, this action triggers, an auth session in the server side and the auth key is used to validate the request and redirect usernaccordingly

### NOTE: Make sure your phone and the testing machine are in the same network



