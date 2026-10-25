IMAGE ?= mortargoblin/temperature-localization:latest

.PHONY: compile test clean package run docker-build docker-run docker-push

compile:
	mvn compile

test:
	mvn clean test

package:
	mvn clean install

clean:
	mvn clean

run: package
	java -jar target/temperature-localization.jar

docker-build:
	docker build -t $(IMAGE) .

# -it because the app reads the language and temperatures from stdin
docker-run:
	docker run --rm -it $(IMAGE)

docker-push:
	docker push $(IMAGE)
