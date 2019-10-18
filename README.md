# MoneyTransfer

### How to start the MoneyTransfer application

1. Run `mvn clean install` to build your application
1. Run `java -jar target/money_transfer-1.0-SNAPSHOT.jar db migrate config.yml` to setup the H2 database
1. Start application with `java -jar target/money_transfer-1.0-SNAPSHOT.jar server config.yml`
1. Application will be up and running on `http://localhost:8080`

# API
### To create an account:

Request:
```sh
curl -X POST \
  http://localhost:8080/account \
  -H 'content-type: application/json' \
  -d '{
        "balance": 4000
}'
```
Response :
````json
{
    "id": 1,
    "balance": 4000
}
````


### To request a transfer:

Request :
````sh
curl -X POST \
  http://localhost:8080/transfer \
  -H 'content-type: application/json' \
  -d '{
	"senderAccountId" : "1",
	"recipientAccountId": "2",
	"amount": "45.56"
}'
````

Response:
````json
{
    "id": 1,
    "senderAccountId": 1,
    "recipientAccountId": 2,
    "amount": 45.56,
    "status": "SUBMITTED"
}
````

### To check the transferStatus

Request :
````sh
curl -X GET http://localhost:8080/transfer/1
````

Response :
````json
{
    "id": 1,
    "senderAccountId": 1,
    "recipientAccountId": 2,
    "amount": 45.56,
    "status": "PROCESSED"
}
````