IMAGE ?= mortargoblin/temperature-converter-fx:latest

.PHONY: compile test clean package run db docker-build docker-run docker-push

compile:
	mvn compile

test:
	mvn clean test

package:
	mvn clean install

clean:
	mvn clean

# start mariadb in docker (needed by both run targets)
db:
	docker compose up -d db

# no db dependency: docker needs sudo, but the app must run as the desktop user
# to reach the display. start the db first with `sudo make db`
run:
	mvn javafx:run

docker-build:
	docker build -t $(IMAGE) .

# runs the image on the host x server, connected to the compose mariadb
# compose is here for convenience :DDDD
docker-run: db
	docker run --rm --network week_7_default -e DISPLAY=$(DISPLAY) \
	  -e DB_URL=jdbc:mariadb://db:3306/temperature_db \
	  -v /tmp/.X11-unix:/tmp/.X11-unix $(IMAGE)

docker-push:
	docker push $(IMAGE)
