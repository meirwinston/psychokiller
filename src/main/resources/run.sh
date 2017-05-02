#!/usr/bin/env bash
java -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Denvironment=prod -classpath "lib\*;." com.psycokiller.cli.Application