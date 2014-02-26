VRaptor site was built using `nanoc` as static site generator

To install dependencies, you can run `bundle install` inside `/vraptor-site` folder.

You will need **grunt** installed also. You can see how to [install it here](http://gruntjs.com/getting-started), but basically you can run the command:

```bash
npm install -g grunt-cli
```

Grunt is installed and managed via `npm`, the `Node.js` package manager.

# Some useful nanoc commands

**nanoc** : compile and parse the site content

**nanoc view** : to start the local server (without grunt optimizations)

**nanoc create-item item_name** : creates an empty nanoc page named `item_name`


# Nanoc syntaxe highlight

To highlight a block code, you can do something like:


\~~~ <br/>
\#!java <br/>
	my sample code here <br/>
\~~~


Another contents, like `inline code`, you can format just like github markdown.

You can see the full nanoc documentation here: http://nanoc.ws/docs/

# Guard

We have `guard` installed in this project. You can see modifications to your project without needing to manually recompile it. You just need to run `guard` command.

# Deploy

If you run `nanoc view` command, you'll see that localhost:3000/pt will be started, but without grunt optimizations (html, css and images compression, css will be without `-webkit` and `-moz` prefixes... and so on).

If you want to deploy site, you need to run `grunt` after `nanoc` command, and all compiled and optimized content will be available on **deploy** folder. All you need to do is to move that compiled content to `gh-pages` branch.


