# ofs-rsocket-client :

### Modules :
1. client / domain / server - Proof of concept for RSocket
2. rsocket-client - An actual prod grade RSocket client

### Tech Details :
1. Java 17
2. SpringBoot 2.7.2
3. RSocket protocol https://rsocket.io/ - incorporated in projectreactor and hence Spring support is available


### Functionalities supported :
1. Fire and Forget - Send the request without looking for any response, returns a void
2. Request and Response - Send the request and receive the response back
3. Configuration Driven - properties get defaulted for invalid/no properties passed
4. Though this is a library, added custom spring boot tests (behaves like a client and server) to ease the developer experience.
   1. refer RSocketServerTest* -> behaves like a server
   2. refer RSocketClientTest  -> behaves like a client

### Default Configurations :
```
ofs:
  rsocket:
    rSocketEnabled: false
    connectOnStart: false
    host: localhost
    port: 9090
    route: default.fnf
    keepAlive:
      interval: 120000
      maxLifeTime: 240000
    reconnect:
      retry:
        maxAttempts: Integer.MAX_VALUE
        fixedDelay: 300000
```
### Run details :
```shell script
  mvn clean install
```
RSocket command line tool :
https://github.com/rsocket/rsocket-cli
```shell script
  brew install yschimke/tap/rsocket-cli
```
```shell script
  rsocket-cli --route=default.fnf --fnf -i '{ "customerId":"1", "firstName":"ofs", "lastName":"ofs", "addresses":[]}' tcp://localhost:9090
```
### How to get started :
1. Include the following dependency in the MAIN application's pom.xml file :
```
<dependency>
  <groupId>com.abc.ofs</groupId>
  <artifactId>ofs-rsocket-client</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
2. Add below annotation on the applications main class :
```
@SpringBootApplication(scanBasePackages = {"com.abc.ofs.rsocket"})
```
3.  **Only if Option2 not working** ,create the following bean in main application end:
```
@Bean("your_bean_name")
protected RSocketClient<your_class_type> rSocketProcessor(RSocketRequester rSocketRequester, RSocketProperties<your_class_type> rSocketProperties) {
    return new RSocketClient<>(rSocketRequester, rSocketProperties);
}
```
4. Add RSocket configuration in the main application properties file (refer "Default Configurations" above for reference)
5. Inject the dependency :
```
private final RSocketClient<your_class_type> rSocketClient;
```
5. Send the data asynchronously :
```
Mono<Boolean> requestFired = this.rSocketClient.fireAndForgetWithSubscription("the_route_the_data_to_be_Sent", "actual_data");
```

### Functionalities to be added :
1. FireAndForget interaction enhancement -> Add functionality to check if handler is present on server during application startup for the given route(use requestAndResponse Interaction hack )
### Learning materials :
1. https://rsocket.io/about/motivations
2. https://www.youtube.com/watch?v=Kt0LeN3TrkM
3. https://docs.spring.io/spring-framework/docs/current/reference/pdf/rsocket.pdf
4. https://spring.io/blog/2021/06/02/wiremock-for-rsocket - can be used in the main application side if its required - redundant for fire and forget


## RSocket command line tool :
https://github.com/rsocket/rsocket-cli
## Install:
brew install yschimke/tap/rsocket-cli
## Example:
rsocket-cli --route=vz.customer --fnf -i '{ "customerId":"1", "firstName":"raj", "lastName":"raj", "addresses":[]}' tcp://localhost:9090

## Mock Postman client :
curl -d '{ "customerId":"1", "firstName":"raj", "lastName":"raj", "addresses":[]}' -H "Content-Type: application/json" -X POST https://19c4b3f9-c9b9-415f-8f8c-f2d2e60605f4.mock.pstmn.io/customer
