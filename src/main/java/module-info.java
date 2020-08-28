open module moe.tristan.mdas {

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires spring.webmvc;

    requires java.annotation;
    requires java.sql;

    requires com.fasterxml.jackson.databind;
    requires org.apache.tomcat.embed.core;
    requires org.slf4j;

    requires org.immutables.value;
    requires immutables.styles;

}
