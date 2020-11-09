# API Specification

## Create Image

Endpoint: `ImageStorage/createImage`

Request
```json
{
    "creator": "<user id>"
}
```

Response
```json
{
    "image_created": "<boolean>",
    "created_at": "<ISO8601 timestamp>",
    "created_by": "<user id>",
    "image_id": "<imageid>",
    "image_url": "<imageurl>"
}
```

## Request Image

Endpoint: `ImageStorage/requestImage`

Request
```json
{
    "image_id": "<imageid>"
}
```

Response
```json
{
    "image_found": "<boolean>",
    "image_url": "<imageurl>"
}
```

## Delete Image

Endpoint: `ImageStorage/deleteImage`

Request
```json
{
    "image_id": "<imageid>",
    "deletor": "<user id>"
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
    "image_id": "<imageid>"
}
```

Response
```json
{
    "image_found": "<boolean>",
    "image_url": "<imagebloburl>"
}
```
