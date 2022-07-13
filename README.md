Roadmap project is an e-shop, where customer can
- search for items
- add or remove items from shoppingCart
- checkout

user can convert prices from EUR to GBP

Design Patterns used:
1 - Convert prices from EUR to GBP - Interpreter pattern (Behaviour Design Pattern)
2 - Calculate tax- Visitor pattern (Behaviour Design Pattern)
3 - Create Order- Singleton pattern (Creational Design Pattern)
4 - Ability to do and undo when adding checkout details in shopping cart- Memento (Behavioral Design Pattern)
4 - Spring Integration uses JMS adapters to send and receive JMS messages and JDBC adapters to convert messages to database queries and result sets back to messages. Adapter pattern(Structural Design Pattern)
5 - ShoppingCartController - uses Facade Pattern (Structural Design Pattern)

To run application, please run RoadMapApplication class

APIs:

GET http://localhost:8888/eShop/items/byType{type}{currency} - returns list of items by type (acceptable values are: FRUIT, VEGETABLE, DRINK, SPICE, NUTS_AND_SEEDS, MEAT, DAIRY).
                                                     Displayable currency can be changed by passing path variable "currency" (acceptable values are: EUR, GBP).
                                                     Example: http://localhost:8888/eShop/items/byType?type=FRUIT&currency=GBP

GET http://localhost:8888/eShop/items - returns list of all items.

GET http://localhost:8888/eShop/items{currency} - returns list of all items. Displayable currency can be changed by passing path variable "currency" (acceptable values are: EUR, GBP).
                                                Example: http://localhost:8888/eShop/items?currency=GBP

GET http://localhost:8888/eShop/item{id} - returns item by id.
                                            Example: http://localhost:8888/eShop/item?id=1

GET http://localhost:8888/eShop/item{id}{currency} - returns item by Id and price is converted to currency of choice. Displayable currency can be changed by passing path variable "currency" (acceptable values are: EUR, GBP).
                                                http://localhost:8888/eShop/item?id=1&currency=GBP

GET http://localhost:8888/cart/shoppingCart - returns order in shopping cart. It containc list of items in order as well as total price which includes also calculated tax and gross price.

POST http://localhost:8888/cart/addToCart{id}{amount} - adds items to order in shopping cart
                                                 Example: http://localhost:8888/cart/addToCart?id=2&amount=3

PUT http://localhost:8888/cart/removeFromCart{id}{amount} - removes items from order in shopping cart
                                                Example: http://localhost:8888/cart/removeFromCart?id=2&amount=1

PUT http://localhost:8888/cart/proceedToCheckout - returns Checkout object which contains order and empty CeckoutDetails object

POST http://localhost:8888/cart/addCustomerDetails - add customer details (Identity)
                                                Example:
                                                POST http://localhost:8888/cart/addCustomerDetails
                                                Accept: */*
                                                Content-Type: application/json
                                                {
                                                    "name":"Anna",
                                                    "lastName":"Liepa"
                                                }

GET http://localhost:8888/cart/addCustomerDetails/undo - undo added customer details

POST http://localhost:8888/cart/addShippingAddress - add shipping address. Can be proceeded only if customer details (Identity) is added
                                                    Example:
                                                    POST http://localhost:8888/cart/addShippingAddress
                                                    Accept: */*
                                                    Content-Type: application/json
                                                    {
                                                        "country":"Latvia",
                                                        "street":"Brivibas",
                                                        "houseNameOrNumber":"214-6",
                                                        "zip":"LV-1009"
                                                    }

GET http://localhost:8888/cart/addShippingAddress/undo - undo added shipping address

POST http://localhost:8888/cart/addPaymentDetails - add payment details details. Can be proceeded only if Identity and ShippingAddress are added
                                                    Example:
                                                    GET http://localhost:8888/cart/addSPaymentDetails HTTP/1.1
                                                    Content-Type: text/plain
                                                    Content-Length: 119
                                                    {
                                                        "cardNumber":"123456789123456789",
                                                        "cardOwner":"Anna Liepa",
                                                        "expiryDate":"01/25",
                                                        "cvc":"123"
                                                    }

GET http://localhost:8888/cart/addPaymentDetails/undo - undo added payment details details

GET http://localhost:8888/cart/proceedPayment - proceeds payment, returns order and sets  and reset Order and sets checkedOut=true.
