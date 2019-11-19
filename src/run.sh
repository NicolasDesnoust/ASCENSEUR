#!/bin/bash
javac --module-path ../resources/lib/javafx-sdk-11.0.2/lib --add-modules=javafx.controls,javafx.graphics,javafx.media $(find ./*/* | grep .java)

java --module-path ../resources/lib/javafx-sdk-11.0.2/lib --add-modules=javafx.controls,javafx.graphics,javafx.media application.Main
