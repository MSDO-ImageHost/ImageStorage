# API Specification

## Environment Variables

```yaml
      - DB_USER=""
      - DB_PASSWORD=""
      - DB_URI=jdbc:sqlite:test.sqlite
      - DB_DRIVER=org.sqlite.JDBC
      - AMQP_USER=guest
      - AMQP_PASSWORD=guest
      - AMQP_HOST=rabbitmq
      - AMQP_PORT=5672
      - IMG_DIR=img
```

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
