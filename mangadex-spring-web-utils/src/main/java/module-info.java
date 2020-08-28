open module moe.tristan.mdas.webutils {

    requires spring.context;
    requires spring.web;
    requires spring.webmvc;

    requires org.apache.tomcat.embed.core;

    requires org.slf4j;
    requires spring.boot;

    exports moe.tristan.mdas.webutils.logging;

}
