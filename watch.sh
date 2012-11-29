#!/bin/bash

while true; do
    make
    inotifywait -e modify latex/*
done
