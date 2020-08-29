open module moe.tristan.mdas.webutils {

    requires spring.context;
    requires spring.web;
    requires spring.webmvc;

    requires java.servlet;

    requires org.slf4j;
    requires spring.boot;

    exports moe.tristan.mdas.webutils.logging;

}
