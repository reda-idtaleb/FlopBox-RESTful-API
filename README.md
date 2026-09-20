# FlopBox API

FlopBox est une API REST sécurisée qui centralise les configurations de serveurs FTP d’un utilisateur et lui permet d’agir sur leurs fichiers distants. Cette version 2 remplace l’implémentation universitaire JAX-RS/JSON par une architecture Spring Boot conçue pour une exécution conteneurisée.

## Ce qui a changé

- Java 25, Spring Boot 4.1 et Spring Security ;
- PostgreSQL et migrations versionnées Flyway ;
- mots de passe hachés avec BCrypt et API protégée par des JWT courts ;
- OpenAPI et Swagger UI disponibles sur /swagger-ui.html ;
- clients FTP sans état : aucune session ni aucun identifiant FTP ne sont conservés ;
- image Docker non-root, health check, limites d’upload et protection contre les accès FTP locaux/privés en production ;
- tests d’intégration PostgreSQL avec Testcontainers, rapport de couverture, SBOM et CI GitHub Actions.

Le code de 2022 reste temporairement sous src/main/java/com pour servir de référence de migration. Seul com.flopbox est compilé et exécuté.

## Démarrage local

Prérequis : Docker Desktop avec Compose. C’est le chemin recommandé car il utilise la même version de Java et de PostgreSQL que la production.

    cp .env.example .env
    # Modifier au minimum POSTGRES_PASSWORD, DATABASE_PASSWORD et JWT_SECRET.
    docker compose -f compose.yaml -f compose.dev.yaml up --build

L’API répond ensuite sur http://localhost:8080 :

- Swagger UI : http://localhost:8080/swagger-ui.html
- Spécification OpenAPI : http://localhost:8080/v3/api-docs
- Sonde de vie : http://localhost:8080/actuator/health

Le profil dev autorise les adresses FTP privées afin de pouvoir employer le serveur de démonstration (ftpServer.py). Le profil de production les interdit afin de réduire le risque SSRF.

Pour compiler sans Docker, installer un JDK 25 et Maven 3.9+, puis lancer :

    mvn verify

Les tests d’intégration démarrent un PostgreSQL temporaire via Testcontainers et se désactivent automatiquement lorsqu’aucun moteur Docker n’est disponible.

## Utilisation de l’API

1. Créer un compte ou se connecter :

    curl --request POST http://localhost:8080/api/v1/auth/register \
      --header "Content-Type: application/json" \
      --data '{"username":"reda","password":"a-long-unique-password"}'

La réponse contient accessToken. L’envoyer à chaque appel protégé :

    curl http://localhost:8080/api/v1/servers \
      --header "Authorization: Bearer <accessToken>"

2. Déclarer un serveur FTP — seuls son hôte, son port et son alias sont persistés :

    curl --request POST http://localhost:8080/api/v1/servers \
      --header "Authorization: Bearer <accessToken>" \
      --header "Content-Type: application/json" \
      --data '{"alias":"demo","host":"ftp.example.net","port":21}'

3. Lister ses fichiers. Les identifiants FTP sont explicitement éphémères dans les en-têtes de la requête ; ne jamais les écrire dans une URL ou les logs :

    curl "http://localhost:8080/api/v1/servers/demo/files?path=/" \
      --header "Authorization: Bearer <accessToken>" \
      --header "X-Ftp-Username: ftp-user" \
      --header "X-Ftp-Password: ftp-password"

Les serveurs FTP anonymes fonctionnent sans ces deux en-têtes. Les opérations supplémentaires — upload, extraction ZIP, création/suppression de répertoires, renommage et téléchargement — sont documentées et exécutables depuis Swagger.

## Mise en production

Compose cible un hôte unique et lie l’API à 127.0.0.1:8080. Placer devant elle un reverse proxy (Caddy, Nginx ou Traefik) qui termine TLS et applique des limites de taille/rate limiting.

    # Sur le serveur : créer un .env propriétaire du compte de déploiement, chmod 600.
    docker compose --env-file .env -f compose.yaml up --build --detach
    docker compose ps
    curl --fail http://127.0.0.1:8080/actuator/health

Avant un déploiement réel :

1. Remplacer toutes les valeurs de .env.example, notamment JWT_SECRET par openssl rand -base64 48.
2. Ne jamais commiter .env ; utiliser un gestionnaire de secrets dès que l’infrastructure le permet.
3. Exposer uniquement le reverse proxy sur Internet ; PostgreSQL reste sur le réseau Docker privé.
4. Sauvegarder régulièrement le volume PostgreSQL avec pg_dump, et restaurer régulièrement une sauvegarde sur un environnement isolé.
5. Examiner les migrations Flyway et l’image versionnée avant chaque promotion. Une migration s’exécute automatiquement au démarrage.
6. Conserver les métriques/logs du conteneur et superviser /actuator/health. Une rotation du secret JWT invalide volontairement les jetons existants.

Un tag Git vX.Y.Z publie l’image ghcr.io/<owner>/flopbox-api:X.Y.Z via GitHub Actions. La CI produit à chaque validation le rapport JaCoCo et un SBOM CycloneDX (target/classes/META-INF/sbom/application.cdx.json).

## Architecture

    HTTP / OpenAPI
          |
    Spring Security (Bearer JWT)
          |
    Controllers -> Services -> Spring Data JPA -> PostgreSQL
          |
    FtpFileService -> client Apache Commons Net -> serveur FTP

Les règles métier et les accès externes sont séparés : les contrôleurs ne manipulent pas JPA directement, les mots de passe applicatifs ne sont jamais sérialisés, et le client FTP est créé puis fermé pour chaque requête. Cette isolation rend l’API stateless, testable et adaptable ultérieurement vers FTPS/SFTP ou un fournisseur d’identité externe.
