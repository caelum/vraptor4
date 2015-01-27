#!/bin/bash

rvm use --create 2.0.0@vraptor-site
cd vraptor-site
bundle install
rm -rf output deploy
bundle exec nanoc
grunt
git checkout gh-pages
cd ..
cp -R vraptor-site/deploy/* .
git commit -am 'automatically updating vraptor site'
git checkout master
