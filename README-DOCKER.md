# Configuration Docker pour le projet Buy-01

Ce guide explique comment exécuter l'application avec Docker Compose.

## Prérequis

- Docker et Docker Compose installés sur votre machine
- Java 17 ou supérieur pour le développement
- Au moins 8 Go de RAM disponible

## Architecture

L'application suit une architecture microservices avec les composants suivants :

- **API Gateway** : Point d'entrée unique pour toutes les requêtes (port 8080)
- **Eureka Server** : Service de découverte (port 8761)
- **User Service** : Gestion des utilisateurs et authentification (port 8081)
- **Product Service** : Gestion des produits (port 8082)
- **MongoDB** : Base de données NoSQL (port 27017)
- **Redis** : Cache et rate limiting (port 6379)
- **Kafka** : Message broker (port 9092)
- **ZooKeeper** : Service de coordination pour Kafka (port 2181)

## Démarrer les services

1. **Construire et démarrer les conteneurs** :
   ```bash
   # Pour la première exécution ou après des changements
   docker-compose up --build -d
   
   # Pour redémarrer sans reconstruire
   # docker-compose up -d
   ```

2. **Arrêter les services** :
   ```bash
   # Arrêter les conteneurs
   docker-compose down
   
   # Supprimer aussi les volumes (attention: supprime les données)
   # docker-compose down -v
   ```

3. **Voir les logs** :
   ```bash
   # Tous les logs
   docker-compose logs -f
   
   # Logs d'un service spécifique
   docker-compose logs -f api-gateway
   docker-compose logs -f user-service
   docker-compose logs -f product-service
   docker-compose logs -f redis
   ```

4. **Accéder aux services** :
   - Eureka Dashboard: http://localhost:8761
   - API Gateway: http://localhost:8080
   - **Swagger UI (Centralisé via API Gateway)**: http://localhost:8080/swagger-ui.html
     (Affiche la documentation agrégée de tous les microservices. Utilisez le menu déroulant en haut à gauche pour sélectionner un service spécifique.)
   - User Service: http://localhost:8081
   - Product Service: http://localhost:8082
   - MongoDB: mongodb://localhost:27017
   - Redis CLI: `docker exec -it redis redis-cli`

5. **Vérifier l'état des conteneurs** :
   ```bash
   docker-compose ps
   ```

## Configuration des services

### API Gateway
- Port: 8080
- Gère le routage, l'authentification et le rate limiting
- Configuration dans `application.yml`

### User Service
- Port: 8081
- Gère l'authentification et les utilisateurs
- Utilise MongoDB pour le stockage

### Product Service
- Port: 8082
- Gère les produits
- Utilise MongoDB pour le stockage
- Écoute les événements Kafka

### Redis
- Utilisé pour le rate limiting et le cache
- Port: 6379
- Configuration dans `application.yml` de l'API Gateway

## Dépannage

### Problèmes de démarrage
- Vérifiez que les ports nécessaires ne sont pas déjà utilisés
- Vérifiez les logs avec `docker-compose logs`
- Assurez-vous d'avoir assez de mémoire disponible

### Réinitialiser l'environnement
```bash
# Arrêter et supprimer tous les conteneurs et volumes
docker-compose down -v

# Nettoyer les images non utilisées
docker system prune -f

# Relancer les services
docker-compose up --build -d
```

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
