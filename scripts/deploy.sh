#!/usr/bin/env bash
ssh aventurier@onaple.fr "rm ~/sponge-server-1.12/mods/storyteller*.jar ~/sponge-server-1.12/mods/Storyteller*.jar"
rsync -r --delete-after --quiet $TRAVIS_BUILD_DIR/build/libs/* aventurier@onaple.fr:~/sponge-server-1.12/mods/