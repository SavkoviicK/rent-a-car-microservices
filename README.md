# Rent-a-Car Microservices

## Opis projekta

Rent-a-Car Microservices je backend aplikacija razvijena u okviru fakultetskog projekta koristeći mikroservisnu arhitekturu. Sistem omogućava upravljanje korisnicima, vozilima, rezervacijama i plaćanjima kroz više nezavisnih servisa.

## Tehnologije

- Java 21
- Spring Boot
- Spring Security (JWT)
- Spring Cloud
- OpenFeign
- Eureka Server
- Config Server
- MySQL
- JPA / Hibernate
- Maven
- Docker
- IntelliJ IDEA

## Funkcionalnosti

- Registracija i prijava korisnika
- JWT autentifikacija i autorizacija
- Upravljanje vozilima
- Kreiranje i otkazivanje rezervacija
- Servis za plaćanje
- Komunikacija između mikroservisa pomoću OpenFeign-a
- API Gateway
- Service Discovery (Eureka)
- Centralizovana konfiguracija (Config Server)

## Struktura projekta

- api-gateway
- config-server
- config-repo
- eureka-server
- user-service
- vehicle-service
- reservation-service
- payment-service

## Pokretanje

1. Pokrenuti Config Server.
2. Pokrenuti Eureka Server.
3. Pokrenuti API Gateway.
4. Pokrenuti ostale mikroservise.
5. Po potrebi pokrenuti Docker kontejnere pomoću `docker-compose.yml`.

## Autor

Kristina Savković
