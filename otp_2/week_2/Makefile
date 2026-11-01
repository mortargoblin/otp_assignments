IMAGE ?= mortargoblin/temperature-localization-fx:latest
# use sudo for docker only, the app itself must run as you to reach your display:
#   make run DOCKER="sudo docker"
DOCKER ?= docker

.PHONY: compile test clean package run db docker-build docker-run docker-push k8s-deploy k8s-delete

compile:
	mvn compile

test:
	mvn clean test

package:
	mvn clean install

clean:
	mvn clean

# start mariadb in docker (needed for the history table)
db:
	$(DOCKER) compose up -d db

run: db
	mvn javafx:run

docker-build:
	$(DOCKER) build -t $(IMAGE) .

# runs the image on the host x server, connected to the compose mariadb.
# the container runs as root, so first allow only local root on your display:
#   xhost +SI:localuser:root
docker-run: db
	$(DOCKER) run --rm --network week_2_default -e DISPLAY=$(DISPLAY) \
	  -e DB_URL=jdbc:mariadb://db:3306/temperature_db \
	  -v /tmp/.X11-unix:/tmp/.X11-unix $(IMAGE)

docker-push:
	$(DOCKER) push $(IMAGE)

k8s-deploy:
	kubectl apply -f temperature-app.yaml

k8s-delete:
	kubectl delete -f temperature-app.yaml
