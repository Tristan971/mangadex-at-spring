open module moe.tristan.mdas.client {

    requires moe.tristan.mdas.mangadex.api;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires spring.webmvc;

    requires org.slf4j;
    requires moe.tristan.mdas.webutils;

}
