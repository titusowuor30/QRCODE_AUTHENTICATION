import redis

try:
    r = redis.Redis(host='localhost', port=6379, db=0)
    response = r.ping()
    print("PONG" if response else "No response")
except redis.ConnectionError:
    print("Failed to connect to Redis server")
