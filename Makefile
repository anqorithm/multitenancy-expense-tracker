.PHONY: up demo logs down psql build

up:
	docker compose up --build -d
	@echo "waiting for app..."
	@until curl -sf http://127.0.0.1:8080/actuator/health >/dev/null; do sleep 2; done
	@echo "ready: http://127.0.0.1:8080"

demo:
	./demo.sh

logs:
	docker compose logs -f app

psql:
	docker compose exec postgres psql -U postgres -d multitenancy

down:
	docker compose down -v

build:
	mvn -q package -DskipTests
