# purchase-service
Purchase service is a REST API That you can use to
* Persist purchase transactions in USD
* Convert the price of a purchase transaction to a given Currency
## Deliverables
After a successful maven build, This project produces two main deliverables.
* The jar file of the purchase service
* The zip file of the software distribution.
  * This zip file can be unzipped anywhere with Java and execute the application

## Build From Source Code
This is a maven Project. This can be built by running
```
mvn clean install
```

## Run From Source Code
First, build the project using the above-mentioned command
Then run the project using whichever IDE. (Do not run directly without building the application since some classes are generated using some maven plugins).

## Run From the Distribution

After a successful build, we can find the distribution in the target folder as a zip file similar to a name conversion like **purchase-service-{VERSION}-distribution.zip**
Unzip the folder
The folder structure of the distribution will be as follows
* **config** -  configuration files which are needed by the application
* **data** - stores files that the application uses to persist data
* **lib** - the application jar
* **log** - log files produced by the application
* **start.bat** - can be used to run the application

Run the **start.bat** file to execute the application.

## How to use the application
When you run the application The application will run on port **8080**. You can access this API using any REST client (eg Postman). This comes with a client that is provided by Open API which can be accessed via <a href="http://localhost:8080/swagger-ui/index.html" target="_blank">this</a>.
You can also use this client for testing. You can find the open API specification from <a href="https://github.com/pathi108/purchase-service/blob/main/src/main/resources/purchase.yml" target="_blank">here</a>.

The API endpoints are as follows

| Purpose                       | EndPoint                | Verb   |
| ------------------------------| ------------------------|--------|
| Create a purchase Transaction  | /purchase               | POST   |
| Convert a purchase ammount    | /purchase/{purchaseId}  | GET    |

### Create a Purchase Transaction
A sample JSON request body of the Create Purchase Transaction API looks as follows
``````
{
"description":"A subsidiary",
"transactionDate":"2020-07-21",
"amount":21
}
``````

Where 
* description is the description of the purchase
* transactionDate is the date of the transaction 
* amount is the price of the purchase transaction

### Convert a purchase amount
This API consumes a path parameter and a query parameter
* Path parameter - unique Id of the Purchase Transaction
* Query Parameter - country_currency_desc	of the currency you need to get converted to.
   * You find the list of country code desc from <a href="[https://github.com/pathi108/purchase-service/blob/main/src/main/resources/purchase.yml](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange
)https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange
" target="_blank">this</a> 





