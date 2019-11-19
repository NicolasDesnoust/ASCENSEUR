#!/bin/bash
rm -f $(find ./*/* | grep .class)

echo 'Fichiers .class supprimés.'
