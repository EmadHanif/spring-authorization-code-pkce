# Spring Authorization Server With OAuth 2.0 Authorization Code (PKCE)

Spring Authorization Server offers a modern approach to implementing OAuth 2.x in Spring-based applications. This repository provides a comprehensive template for implementing OAuth 2.0 Authorization Code Grant with PKCE

## Setup Instructions

### Database Configuration

This project uses PostgreSQL. Update your database configuration in `application-dev.properties` to match your environment:

```properties
config.database-config.host=jdbc:postgresql://localhost:5432/[DATABASE_NAME]
config.database-config.username=postgres
config.database-config.password=[DATABASE_PASSWORD]
```

Replace `[DATABASE_NAME]` and `[DATABASE_PASSWORD]` with your database credentials.

#### Database Schema Setup

1. Create your database in PostgreSQL.
2. Initialize the schema by executing the provided SQL script: `src/main/resources/schema/create-oauth2.sql`

#### Using MySQL Instead of PostgreSQL:

If you're using MySQL, apply the following changes to the schema file before execution:
- Replace all `text` data types with `blob`
- Replace all `timestamptz` data types with `timestamp`

### Java KeyStore Configuration

**Note:** A default keystore is already embedded in this repository. This section is for reference only if you wish to generate your own keystore.

To generate a Java Keystore file for signing JWT tokens, use the following command with your own values:

```
keytool -genkeypair -alias myalias -keyalg RSA -keysize 2048 -keystore mykeystore.jks -storepass mypassword -validity 3650
```

After generating the keystore, update the following properties in `application.properties`:

```
jwt.keystore.jks-location=keystore/[JWK_FILE].jks
jwt.keystore.keypair-alias=[JWK_ALIAS]
jwt.keystore.password=[JWK_PASSWORD]
```

Spring Authorization Server also supports base64-encoded keystore configuration.


## PKCE Generator

This project includes an example utility class named `PKCEGenerator`

The class automatically generates:
- `code_verifier`
- `code_challenge` (S256)
- Sample **Authorization** and **Token** endpoints

You can then directly use the generated endpoints to test the PKCE flow.

#### Example Console Output

```
=== PKCE Values ===
Code Verifier: wGADIFYpyQDeg-kXMLDAS_KtkIyk0ZT1v7-S.G6qnwE6r18vPLWot-uq~pdprpd3asJvoMgM~BziL9iIQWc-ti_bpZjpF-zbtpP5cPswCcj4maHnUKe9jAWQOjF.RuKD
Code Challenge: IuH9aI-ON9pIIHnMvuRoXueRHgRQbCGXPESdI2oaDpw
```

### OAuth 2.0 Endpoints

All standard OAuth 2.0 endpoints are available:

- **POST /oauth2/token:** Exchange authorization code to generate access and refresh tokens
- **POST /oauth2/introspect:** Validates and inspects access tokens
- **POST /oauth2/revoke:** Revokes tokens *(custom logout implementation)*

## Authorization Code Flow with PKCE

### Authorization Request

Use the generated `code_challenge` from `PKCEGenerator`:

```
http://localhost:8080/oauth2/authorize?response_type=code&client_id=client&redirect_uri=http://localhost:4200/callback&scope=openid+user&code_challenge=IuH9aI-ON9pIIHnMvuRoXueRHgRQbCGXPESdI2oaDpw&code_challenge_method=S256
```
### Login Credentials

The authorization request redirects to the default Spring Security login page (`/login`).

Use the following **sample credentials** to authenticate:

- **Email:** `david_freed@gmail.com`
- **Password:** `adminadmin`


After successful authentication, the server redirects to the configured `redirect_uri` with the authorization code (`code=...`).

### Token Request

Provide the obtained `authorization_code` along with the corresponding `code_verifier`:

```
curl --request POST \
  --url http://localhost:8080/oauth2/token \
--header 'content-type: application/x-www-form-urlencoded' \
--header 'authorization: Basic Y2xpZW50OnNlY3JldA=='\
--data 'grant_type=authorization_code' \
--data 'code={{code}}' \
--data 'code_verifier={{code_verifier}}' \
--data 'redirect_uri=http://localhost:4200/callback'

```

### Generate Refresh Token

```
curl --request POST \
  --url http://localhost:8080/oauth2/token \
  --header 'authorization: Basic Y2xpZW50OnNlY3JldA==' \
  --header 'content-type: application/x-www-form-urlencoded' \
  --data grant_type=refresh_token \
  --data refresh_token={{refresh_token}}
```

**Best Practice:** Handle refresh tokens server-side and store them securely in HTTP-only cookies after successful authentication.

### Introspect Token

```
curl --request POST \
  --url http://localhost:8080/oauth2/introspect \
  --header 'authorization: Basic Y2xpZW50OnNlY3JldA==' \
  --header 'content-type: application/x-www-form-urlencoded' \
  --data token={{token}}
 ``` 

## Test Protected Endpoints

The example controller demonstrates secured API access.

### Public endpoint (No Authentication Required):
```bash
curl --request GET \
  --url http://localhost:8080/api/v1/example/m1
```

### Protected Endpoint:
```bash
curl --request GET \
  --url http://localhost:8080/api/v1/example/m2 \
  --header 'authorization: Bearer {{access_token}}'
```

Requires a valid access token with scope `user` and role `ROLE_ADMIN`.

### Protected Reactive Endpoint:
```bash
curl --request GET \
  --url http://localhost:8080/api/v1/example/m3 \
  --header 'authorization: Bearer {{access_token}}'
```

Returns RxJava `Single`. Requires a valid access token with scope `user` and role `ROLE_ADMIN`.