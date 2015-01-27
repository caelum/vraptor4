#!/bin/bash

set -e # halt on error

rvm use --create 2.0.0@vraptor-site
cd vraptor-site
bundle install
rm -rf output deploy
bundle exec nanoc
grunt
cd ..
git checkout gh-pages
cp -R vraptor-site/deploy/* .
git commit -am 'automatically updating vraptor site'
