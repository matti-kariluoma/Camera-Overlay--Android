#!/bin/bash

keytool -genkey -v -keystore release.keystore -alias release -keyalg RSA -keysize 2048 -validity 65000
