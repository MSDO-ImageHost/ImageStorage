# API Specification

#### Access control
Any request must contain a valid session token
```json
{
    "session_token": "<SessionTokenID: valid and active JWT>"
}
```

## Create Image

Endpoint: `ImageStorage/createImage`

Request
```json
{
    "creator": "<JWT>"
}
```

Response
```json
{
    "image_created": "<boolean>",
    "created_at": "<ISO8601 timestamp>",
    "created_by": "<user id>",
    "image_id": "<imageURI>",
}
```

## Request Image

Endpoint: `ImageStorage/requestImage`

Request
```json
{
    "image_id": "<imageURI>"
}
```

Response
```json
{
    "image_found": "<boolean>",
    "image_data": "<datablob>"
}
```

## Delete Image

Endpoint: `ImageStorage/deleteImage`

Request
```json
{
    "image_id": "<imageURI>",
    "deletor": "<JWT>"
}
```

Response
```json
{
    "image_deleted": "<boolean>",
    "deleted_at": "<ISO8601 timestamp>"
}
```

## Return Encoded Image

Endpoint: `ImageStorage/returnEncodedImage`

Request
```json
{
    "image_id": "<imageURI>"
}
```

Response
```json
{
    "image_found": "<boolean>",
    "image_data": "<imageblob>"
}
```
