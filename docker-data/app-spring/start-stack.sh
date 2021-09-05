#!/usr/bin/env bash

cp -fv ./stack.env ./.env
source ./.env

export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1
docker-compose up -d --build --remove-orphans
