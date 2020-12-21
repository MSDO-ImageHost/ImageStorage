# API Specification

## Create Image

Request

```json
{
  "image_data": "<Base64 ByteArray>",
  "post_id": "<String>"
}
```

Response

Header ``status_code: 204``

```json
{
  "post_id": "<String?: image ID>"
}
```

## Request Image

Request

```json
{
  "post_id": "<String: image ID>"
}
```

Response

Header ``status_code: 200``

```json
{
  "post_id": "<String>",
  "image_data": "<Image?: data of image>"
}
```

Error

Header ``status_code: 400``

```json
{
  "post_id": "<String>"
}
```

## Delete Image

Request

```json
{
  "post_id": "<String: image ID>"
}
```

Response

Header ``status_code: 204``

```json
{
  "post_id": "<String: image ID>"
}
```
