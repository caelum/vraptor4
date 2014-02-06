VRaptor site was built using `nanoc` as static site generator

To install dependencies, you can run `bundle install` inside `/vraptor-site` folder.


# Some useful nanoc commands

**nanoc** : compile and parse the site content

**nanoc view** : to start the local server

**nanoc create-item item_name** : creates an empty nanoc page named `item_name`


# Nanoc syntaxe highlight

To highlight a block code, you can do something like:

\~~~
#!java
	my sample code here
\~~~

Another contents, like `inline code`, you can format just like github markdown.

You can see the full nanoc documentation here: http://nanoc.ws/docs/

# Guard

We have `guard` installed in this project. You can see modifications to your project without needing to manually recompile it. You just need to run:

	guard
