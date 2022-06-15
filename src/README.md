Roadmap project is an e-shop, where customer can
- search for items
- add or remove items from shoppingCart
- checkout

user can convert prices from EUR to GBP

Design Patterns used:
1 - Convert prices from EUR to GBP - Interpreter pattern (Behaviour Design Pattern)
2 - Calculate tax- Visitor pattern (Behaviour Design Pattern)
3 - Create Order- Singleton pattern (Creational Design Pattern)
4 - Ability to add and remove items from shoppingCart- Memento ?????
4 - Spring Integration uses JMS adapters to send and receive JMS messages and JDBC adapters to convert messages to database queries and result sets back to messages. Adapter pattern(Structural Design Pattern)
5 - ShoppingCartController - uses Facade Pattern (Structural Design Pattern)

To run application, please run RoadMapApplication class

APIs:
http://localhost:8888/eShop/ - indexPage
http://localhost:8888/eShop/items/{type}{currency} - gets items by type (acceptable values are: FRUIT, VEGETABLE, DRINK, SPICE, NUTS_AND_SEEDS, MEAT, DAIRY).
                                                     Displayable currency can be changed by passing path variable "currency" (acceptable values are: EUR, GBP).
                                                     Example: http://localhost:8888/eShop/items/byType?type=FRUIT&currency=GBP
http://localhost:8888/eShop/items{currency} - gets all items. Displayable currency can be changed by passing path variable "currency" (acceptable values are: EUR, GBP).
                                                Example: http://localhost:8888/eShop/items?currency=EUR
http://localhost:8888/eShop/item{id}{currency} - gets item by Id. Displayable currency can be changed by passing path variable "currency" (acceptable values are: EUR, GBP).
                                                http://localhost:8888/eShop/item?id=1&currency=EUR

The VAT in Latvia for food: 21%, Fresh fruits, berries and vegetables = 5%