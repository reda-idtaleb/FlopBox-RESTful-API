# Commandes API v2

La base de chemin est http://localhost:8080/api/v1. L’ancienne base /v1 et l’authentification HTTP Basic ne font plus partie de l’API.

Créer un compte :

    curl --request POST http://localhost:8080/api/v1/auth/register --header "Content-Type: application/json" --data '{"username":"reda","password":"a-long-unique-password"}'

Se connecter :

    curl --request POST http://localhost:8080/api/v1/auth/login --header "Content-Type: application/json" --data '{"username":"reda","password":"a-long-unique-password"}'

Créer une configuration FTP :

    curl --request POST http://localhost:8080/api/v1/servers --header "Authorization: Bearer <accessToken>" --header "Content-Type: application/json" --data '{"alias":"demo","host":"ftp.example.net","port":21}'

Lister la racine d’un serveur FTP :

    curl "http://localhost:8080/api/v1/servers/demo/files?path=/" --header "Authorization: Bearer <accessToken>" --header "X-Ftp-Username: ftp-user" --header "X-Ftp-Password: ftp-password"

Télécharger un fichier :

    curl --output local-file.txt "http://localhost:8080/api/v1/servers/demo/files/download?path=/remote-file.txt" --header "Authorization: Bearer <accessToken>" --header "X-Ftp-Username: ftp-user" --header "X-Ftp-Password: ftp-password"

Envoyer un fichier :

    curl --request POST "http://localhost:8080/api/v1/servers/demo/files/upload?path=/remote-file.txt" --header "Authorization: Bearer <accessToken>" --header "X-Ftp-Username: ftp-user" --header "X-Ftp-Password: ftp-password" --form "file=@local-file.txt"

La documentation complète, les schémas et les appels interactifs sont sur /swagger-ui.html.
