#!/usr/bin/env bash
ssh aventurier@onaple.fr "rm ~/sponge-server-1.12/mods/storyteller*.jar"
rsync -r --delete-after --quiet $TRAVIS_BUILD_DIR/build/libs/* aventurier@onaple.fr:~/sponge-server-1.12/mods/