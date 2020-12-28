#!/bin/bash

export VERSION=${VERSION:-workspace}

podman build --no-cache -v /opt/sources/_gradle:/cache/_gradle:Z,rw -v $(pwd):/src:Z,rw,shared -f modules/container/Containerfile --build-arg VERSION=${VERSION} . "$@"

