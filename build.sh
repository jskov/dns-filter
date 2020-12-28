#!/bin/bash

export VERSION=${VERSION:-workspace}

podman build --no-cache -v /opt/sources/_gradle:/cache/_gradle:Z,rw -f modules/container/Containerfile --build-arg VERSION=${VERSION} . "$@"

