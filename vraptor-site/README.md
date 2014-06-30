VRaptor site was built using `nanoc` as static site generator

To install dependencies, you need to install `ruby` with `bundler` gem. After you can run `bundle install` inside `/vraptor-site` folder.

You will need **grunt** installed also. You can see how to [install it here](http://gruntjs.com/getting-started), but basically you can run the command:

```bash
npm install -g grunt-cli
```

You will need also some dependencies of grunt. To install them, run

```bash
npm install
```

This installs Grunt too so, if you don't want (or don't have permissions) to run the first command, you can run just the command above and run grunt as

```bash
./node_modules/grunt-cli/bin/grunt
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

`html`, `xml`, `javascript`, `jsp`, `properties`, `bash` are also available formats.



Another contents, like `inline code`, you can format just like github markdown.

You can see the full nanoc documentation here: http://nanoc.ws/docs/

# Guard

We have `guard` installed in this project. You can see modifications to your project without needing to manually recompile it. You just need to run `guard` command.

# Deploy

If you run `nanoc view` command, you'll see that localhost:3000/pt will be started, but without grunt optimizations (html, css and images compression, css will be without `-webkit` and `-moz` prefixes... and so on).

If you want to deploy site, you need to run `grunt` after `nanoc` command, then run `grunt deploy`.
