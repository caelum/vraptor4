#!/bin/bash

source "$HOME/.rvm/scripts/rvm"

set -e # halt on error

rvm use --create 2.0.0@vraptor-site
cd vraptor-site
bundle install
npm install
rm -rf output deploy
bundle exec nanoc
grunt
cd ..
git checkout gh-pages
cp -R vraptor-site/deploy/* .
git commit -am 'automatically updating vraptor site'
