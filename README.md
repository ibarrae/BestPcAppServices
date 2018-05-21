# BestPcAppServices
This app is designed to "make" the user to have an active internet connect and gps on at all times via a notification that can not be closed, this works with a foreground service so the user can not stop this service until the app is uninstalled or the app is stopped manually.

First, it checks if the phone is avalaible to provide this service by checking if it was avalaible cellphone connection, following the next steps:
- Checks if the cellphone is connected to a valid network.
- Checks the phone signal level. If the signal is poor, none or unkown the app will not check for the other active services.

If phone has a valid mobile network connection, it follows the next steps:
- Checks if gps is on
- If gps is not on, it shows the user a dialog asking to activate gps or internet connection.
- If gps is on, it will check if it has an active internet connection.
- If cellphone does not have an active internet connection, it shows the user a dialog asking to activate gps or internet connection.

This services are checked every minute while the foreground service is running.
