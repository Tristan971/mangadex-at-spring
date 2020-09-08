open module moe.tristan.mdas.client {

    // MDAS shared types/configuration
    requires moe.tristan.mdas.api;
    requires moe.tristan.mdas.webutils;

    // Core spring APIs
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    requires spring.webmvc;

    // Java EE APIs
    requires java.annotation;
    requires java.servlet;

    // Jetty APIs
    requires org.eclipse.jetty.io;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;

    // SLF4J
    requires org.slf4j;

    // Jackson + Immutables
    requires com.fasterxml.jackson.annotation;
    requires org.immutables.value;
    requires immutables.styles;

    // BouncyCastle, for SSL configuration
    requires bcprov.jdk15on;
    requires bcpkix.jdk15on;

}
