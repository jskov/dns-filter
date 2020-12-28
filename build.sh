#!/bin/bash

podman build --no-cache -v /opt/sources/_gradle:/cache/_gradle:Z,rw -v $(pwd):/src:Z,rw,shared -f modules/container/Containerfile .
