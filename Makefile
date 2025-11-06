.PHONY: help clean start stop restart restart-service logs status db watch

# Aide rapide
help:
	@echo "Commandes rapides :"
	@echo "  make start    - Lance les conteneurs (docker-compose up -d)"
	@echo "  make stop     - Arrête les conteneurs (docker-compose down)"
	@echo "  make restart        - Redémarre tous les services"
	@echo "  make restart-service - Redémarre un service spécifique (ex: make restart-service s=user-service)"
	@echo "  make logs      - Affiche les logs (ajouter s=service pour filtrer)"
	@echo "  make status   - Affiche l'état des conteneurs"
	@echo "  make db       - Nettoie la base de données"
	@echo "  make build    - Reconstruit et relance les conteneurs"
	@echo "  make watch    - Démarre la surveillance des fichiers pour les mises à jour automatiques (docker compose watch)"

# Alias pour up
start:
	docker-compose up -d

# Alias pour down
stop:
	docker-compose down

# Redémarrage complet
restart: stop start

# Redémarrage d'un service spécifique avec reconstruction
restart-service:
	docker-compose up -d --build $(s)

# Logs avec filtre optionnel
ifdef s
logs:
	docker-compose logs -f $(s)
else
logs:
	docker-compose logs -f
endif

# État des conteneurs
status:
	docker-compose ps

# Nettoyage de la base de données
db:
	docker exec mongodb mongosh -u root -p example --eval "use userdb; db.user.drop(); db.user_session.drop()"
	docker-compose down -v mongodb     
	docker-compose up -d mongodb
	@echo "✅ Base de données nettoyée"

# Rebuild complet
build:
	docker-compose up -d --build

# Lance les conteneurs avec la configuration de surveillance


# Démarre la surveillance des fichiers pour les mises à jour automatiques
watch:
	docker compose watch
