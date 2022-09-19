## RSocket command line tool :
https://github.com/rsocket/rsocket-cli
## Install:
brew install yschimke/tap/rsocket-cli
## Example:
rsocket-cli --route=vz.customer --fnf -i '{ "customerId":"1", "firstName":"raj", "lastName":"raj", "addresses":[]}' tcp://localhost:9090

## Mock Postman client :
curl -d '{ "customerId":"1", "firstName":"raj", "lastName":"raj", "addresses":[]}' -H "Content-Type: application/json" -X POST https://19c4b3f9-c9b9-415f-8f8c-f2d2e60605f4.mock.pstmn.io/customer