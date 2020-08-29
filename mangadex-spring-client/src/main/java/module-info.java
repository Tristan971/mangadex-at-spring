open module moe.tristan.mdas.client {

    requires moe.tristan.mdas.api;
    requires moe.tristan.mdas.webutils;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires spring.webmvc;

    requires org.slf4j;

    requires java.annotation;
    requires com.fasterxml.jackson.annotation;
    requires org.immutables.value;
    requires immutables.styles;

}
