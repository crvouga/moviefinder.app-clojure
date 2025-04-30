.PHONY: test-int build db-up db-down db-start db-stop

test-int:
	sh -c "INTEGRATION_TEST=true lein eftest"

start:
	lein run

build:
	lein db-up

db-up:
	dbmate up

db-down:
	dbmate down

db-start:
	docker-compose -f docker-compose.local.yml up -d

db-stop:
	docker-compose -f docker-compose.local.yml down
