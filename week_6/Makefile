IMAGE ?= mortargoblin/temperature-converter:latest

.PHONY: compile test clean package docker-build docker-run

compile:
	mvn compile

test:
	mvn clean test

package:
	mvn clean install

clean:
	mvn clean

docker-build:
	docker build -t $(IMAGE) .

docker-run:
	docker run --rm $(IMAGE)
