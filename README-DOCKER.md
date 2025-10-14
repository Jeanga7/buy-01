# Configuration Docker pour le projet Buy-01

Ce guide explique comment exécuter l'application avec Docker Compose.

## Prérequis

- Docker et Docker Compose installés sur votre machine
- Java 17 ou supérieur pour le développement

## Services inclus

- **Eureka Server** : Service de découverte sur le port 8761
- **User Service** : Service utilisateur sur le port 8081
- **MongoDB** : Base de données NoSQL sur le port 27017
- **Kafka** : Message broker sur le port 9092
- **ZooKeeper** : Service de coordination pour Kafka sur le port 2181

## Démarrer les services

1. **Construire et démarrer les conteneurs** :
   ```bash
   docker-compose up --build -d
   ```

2. **Arrêter les services** :
   ```bash
   docker-compose down
   ```

3. **Voir les logs** :
   ```bash
   # Tous les logs
   docker-compose logs -f
   
   # Logs d'un service spécifique
   docker-compose logs -f user-service
   ```

4. **Accéder aux services** :
   - Eureka Dashboard: http://localhost:8761
   - User Service: http://localhost:8081

## Configuration des profils Spring

Les services utilisent le profil `docker` par défaut. Assurez-vous d'avoir les fichiers de configuration appropriés (par exemple, `application-docker.yml` ou `application-docker.properties`) dans vos services Spring Boot.

## Variables d'environnement

Les variables d'environnement sont définies dans le fichier `.env` à la racine du projet.

## Volumes

- Les données de MongoDB sont persistées dans un volume nommé `mongodb_data`.

## Dépannage

- Si un service ne démarre pas, vérifiez les logs avec `docker-compose logs [nom_du_service]`
- Assurez-vous que les ports nécessaires ne sont pas déjà utilisés
- Vérifiez que vous avez assez de mémoire disponible pour exécuter tous les services

## Suppression Base de Données

```bash
docker exec mongodb mongosh -u root -p example --eval "use userdb; db.user.drop(); db.user_session.drop()"
```
