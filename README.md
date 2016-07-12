TESTANDO PRO LIVRO
![image](https://cloud.githubusercontent.com/assets/1529021/7015058/0844e14c-dca4-11e4-8d7b-e0546b6ec74d.png)

[![][travis img]][travis]
[![][maven img]][maven]
[![][release img]][release]
[![][license img]][license]

[travis]:https://travis-ci.org/caelum/vraptor4
[travis img]:https://travis-ci.org/caelum/vraptor4.svg?branch=master

[maven]:http://search.maven.org/#search|gav|1|g:"br.com.caelum"%20AND%20a:"vraptor"
[maven img]:https://maven-badges.herokuapp.com/maven-central/br.com.caelum/vraptor/badge.svg

[release]:https://github.com/caelum/vraptor4/releases
[release img]:https://img.shields.io/github/release/caelum/vraptor4.svg

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg

A web MVC action-based framework, on top of CDI, for fast and maintainable Java development. 

##Downloading directly or using it through Maven

For a quick start, you can use this snippet in your maven POM:

```xml
<dependency>
    <groupId>br.com.caelum</groupId>
    <artifactId>vraptor</artifactId>
    <version>4.2.0-RC3</version> <!--or the latest version-->
</dependency>
```

Or you can download it directly [at our artifacts repository](https://bintray.com/caelum/VRaptor4/br.com.caelum.vraptor).

More detailed prerequisites and dependencies can be found [here](http://www.vraptor.org/en/docs/dependencies-and-prerequisites/).

##Documentation
[More detailed documentation](http://www.vraptor.org/en/docs/one-minute-guide/) and [Javadoc](http://www.vraptor.org/javadoc/) are also available at [VRaptor's website](http://www.vraptor.org/en/).

Looking for more? Take a look at our [articles and presentations' page](http://www.vraptor.org/en/docs/articles-and-presentations).

##Building in your machine

If you want to build VRaptor, execute:

	mvn package

VRaptor uses Maven as build tool. So you can easily import it into your favorite IDE. In Eclipse you can import as "Maven project".

##Contribute to VRaptor

Do you want to contribute with code, documentation or bug report?

You can find guidelines to contribute to VRaptor [here](http://www.vraptor.org/en/docs/how-to-contribute/ "Contribute").

## Compatibility checks

You can check compatibility with previous versions of `vraptor-core` by running:

```
mvn clirr:clirr
```

A full report will be generated at `target/site/clirr-report.html` file.
