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
- Android apps by default are not configureed to share clear text data over the network an the app won't work in testing mode if you do not change this setting for your network ip address. Open the xml/network_security_config.xml in your res/xml folder, replace the ip adress of your local machine in the line bellow

```xml
        <domain includeSubdomains="true">192.168.8.6</domain>
```
- Build the app, run in debug mode, link the app to your phone(use machine local ipv4 address) as an emulator or use the android studuio emulator and in this case, the ip address should be 10.0.2.2(default ip for android emulator)
- wait for the app to lauch and scan the qr code, this action triggers, an auth session in the server side and the auth key is used to validate the request and redirect usernaccordingly

### NOTE: Make sure your phone and the testing machine are in the same network



