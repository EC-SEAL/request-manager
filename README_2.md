# Request Manager microservice

Reference *DELIVERABLE D3.1*

## Overview  

TODO.

The code is structured to have a clear MVC structure.

The underlying library integrating swagger to SpringBoot is [springfox](https://github.com/springfox/springfox)

The following environment variables are to be set:

          - ASYNC_SIGNATURE= boolean, if true denotes RSA signing for JWTs, else HS256 signing is conducted
			- KEYSTORE_PATH = path to the keystore holding the RSA certificate used for signing JWTs, encrypting JWEs, ...
			- KEY_PASS= password for the certificate
			- STORE_PASS= password for the keystore containing the certificate
			- HTTPSIG_CERT_ALIAS= for the httpsig protocol
			- SIGNING_SECRET= HS256 secret used for symmetric signing of jwts, e.g. QjG+wP1CbAH2z4PWlWIDkxP4oRlgK2vos5/jXFfeBw8=
			- CONFIGURATION_MANAGER_URL= location of the configuration manager
			- SESSION_MANAGER_URL= location of the session manager
			- SSL_* = information related to your ssl certificate
				 SSL_KEYSTORE_PATH
            	 SSL_STORE_PASS
            	 SSL_KEY_PASS
            	 SSL_CERT_ALIAS
            
 

For testing, try https://localhost:8063/swagger-ui.html  

