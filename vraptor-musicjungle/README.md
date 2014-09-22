# VRaptor music jungle

This is an example application to help you to learn about VRaptor. 

You can easily import into your IDE as Maven project. Or you can run using maven with commands: `mvn jetty:run` (for Jetty lovers) or `mvn tomcat7:run` (for Tomcat lovers). Wildfly is also available with `mvn -P wildfly wildfly:run`.

This project also works with a production environment as a showcase for vraptor environment, just set your environment to production and configure the persistence.xml

**Warning**: `mvn jetty:run` and `mvn tomcat7:run` does not work with some maven's versions. If you want to use the plugins, we strongly recommend that you update your `mvn` version to `3.2.2` (or the lastest version) so it will works just fine.


Este é um projeto de exemplo para ajudar você a aprender sobre o VRaptor. Você pode facilmente importá-lo na sua IDE favorita como um projeto Maven. Ou você pode rodar este projeto com os comandos do Maven: `mvn jetty:run` ou `mvn tomcat7:run`. Wildfly também está disponível pelo comando `mvn -P wildfly wildfly:run`.

Este projeto também funciona como um showcase do vraptor environment, onde ao configurá-lo para production ele lerá os dados do persistence.xml da unidade mysql e conectará com o mysql configurado lá.

**Nota**: `mvn jetty:run` e `mvn tomcat7:run` não funcionam com algumas versões do maven. Se você pretende usar o plugin recomendamos fortemente que atualize a versão de seu maven para `3.2.2` (ou a mais recente). Assim tudo vai funcionar bem.
