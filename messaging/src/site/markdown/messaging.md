
# Messaging

Messaging is divided into several interacting features.
Sending and receiving requests but also how specific the destination is.

Requests can be sent either to sessions or to the server.
Sending to a session requires either the `SessionId` or the use of a session filter.
The session filter can be used to send to multiple sessions.
Sending to a session requires the `send_to_session` permission.
Sending to the server requires the `send_to_message_handler` permission.

Request streams and requests handlers both receive messages.
Request streams receive requests sent to sessions.
Request handlers receive requests sent to the server.
Request handlers need to be registered with the server to intercept the messages,
this needs the `register_handler` permission.

The division by destination allows for the separation of front- and backend clients.
It's expected that frontend clients can send to the server and receive requests
sent to it.
This needs the `send_to_message_handler` permission.
Backend clients can send to sessions and register request handlers with the server.
This needs the `send_to_session` and `register_handler` permissions.
The difference in permissions allows the server to enforce the roles of clients.
