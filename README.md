# Inventory Service

This service is the inventory of the T2-Project.
It manages the products and reservations.

The products are the teas that the store sells and reservations exist to express that a user plans to buy some units of a product.


## Build and Run

Refer to the [Documentation](https://t2-documentation.readthedocs.io/en/latest/microservices/deploy.html) on how to build, run or deploy the T2-Project services.


## HTTP Endpoints

* `/inventory/{id}`: PUT, POST, GET or DELETE the product with productId `id`.
* `/inventory/reservation`: POST to add a new reservation
* `/restock`: GET to restock all items
* `/generate`: GET to generate new products


## Usage

### Add a Reservation

Reserve 3 units of product "foo" for user "bar" (a product with that id must indeed be in the store):

```sh
curl -i -X POST -H "Content-Type:application/json" -d '{ "productId" : "foo", "sessionId" : "bar", "units" : 3}' http://localhost:8082/inventory/reservation
```

If the reservation succeeds, the reply contains the product with `units` being the reserved units.

```json
{
 "id":"foo",
 "name":"Sencha (25 bags)",
 "description":"very nice Sencha (25 bags) tea",
 "units":98,
 "price":0.6923181656954707
}
```

### Access the products

Refer to e.g. spring's [Accessing JPA Data with REST](https://spring.io/guides/gs/accessing-data-rest/) on how to talk to the endpoints generated with spring-boot-starter-data-rest in general.

These endpoints can only access the products.
There are no endpoints to access the reservations.

An exemplary request to GET get the product with id "foo":

```sh
curl localhost:8082/inventory/foo
```
Response:

```json
{
 "name" : "Sencha (25 bags)",
 "description" : "very nice Sencha (25 bags) tea",
 "units" : 101,
 "price" : 0.6923181656954707,
 "_links" : {
 "self" : {
 "href" : "http://inventory-cs/inventory/foo"
 },
 "inventory" : {
 "href" : "http://inventory-cs/inventory/foo"
 }
 }
}
```

### Restocking the Inventory

If all items are sold out, this is how you restock all of them.

```sh
curl localhost:8082/restock
```

If there are no products in the inventory (not as in '0 units of a product' but as in 'there is no product at all'), do this to generate new products.

```sh
curl localhost:8082/generate
```

## application properties

(./ src/main/resources/application.properties)

| property | read from env var | description |
| -------- | ----------------- | ----------- |
| t2.inventory.size | T2_INVENTORY_SIZE | number of items to be generated into the inventory repository on start up |
| t2.cart.url | T2_CART_URL | url of the cart service. must be provided to generate reservations on start up (because reservations and items in cart should be in sync) |
| spring.profiles.active | SPRING_PROFILES_ACTIVE | set to 'saga' to have the full saga experience. set to 'test' to run inventory solely as provider of items (no saga) |
| t2.inventory.TTL | T2_INVENTORY_TTL | time to live of reservations (in seconds) |
| t2.inventory.taskRate | T2_INVENTORY_TASKRATE | rate at which the inventory checks for items that exceeded their TTL (in milliseconds) |
setting this to less or equal than zero disables the collection of expired cart entries.

| property | read from env var | description |
| -------- | ----------------- | ----------- |
| spring.datasource.url | SPRING_DATASOURCE_URL | |
| spring.datasource.username | SPRING_DATASOURCE_USERNAME | |
| spring.datasource.password | SPRING_DATASOURCE_PASSWORD | |
| spring.datasource.driver-class-name | SPRING_DATASOURCE_DRIVER_CLASS_NAME | |
| eventuatelocal.kafka.bootstrap.servers | EVENTUATELOCAL_KAFKA_BOOTSTRAP_SERVERS | |
| eventuatelocal.zookeeper.connection.string | EVENTUATELOCAL_ZOOKEEPER_CONNECTION_STRING | |

c.f. [eventuate tram cdc](https://eventuate.io/docs/manual/eventuate-tram/latest/getting-started-eventuate-tram.html) for explanations
