# converter-service-harf
A java REST API used for transforming documents into image files.

#Usage

Run the jar(either as a service or plain in cmd).

A new enpoint on localhost port 9090 shall be opened.

Send a POST request to localhost:9090/convert, containing a parameter named filePath. This is the file you want to convert.

Encode this file path in base64 and send via "filePath" variable.

Response to this request is anticipated pagepaths. Please note that conversion methods works async so initial response shall return BEFORE any pages are converted.

As they convert pages and their real path will be resent. Handle them as desired.
