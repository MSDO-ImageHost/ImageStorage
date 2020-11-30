# API Specification

#### Access control
Any request must contain a valid session token
```json
{
    "session_token": "<SessionTokenID: valid and active JWT>"
}
```

## Create Image

Request
```json
{
    "creator": "<JWT>",
    "image_data": "<ByteArray>",
    "image_id": "<String?: Optional ID of image to replace>"
}
```

Response
```json
{
    "status_code": "<Int: HTTP code>",
    "created_by": "<String?: user ID>",
    "image_id": "<String?: image ID>"
}
```

## Request Image

Request
```json
{
    "image_id": "<String: image ID>"
}
```

Response
```json
{
    "status_code": "<Int: HTTP code>",
    "image_data": "<Image?: data of image>"
}
```

## Delete Image

Request
```json
{
    "image_id": "<String: image ID>",
    "deletor": "<JWT>"
}
```

Response
```json
{
    "status_code": "<Int: HTTP code>"
}
```
