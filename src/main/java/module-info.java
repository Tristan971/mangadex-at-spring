open module moe.tristan.mdas {

    // Core spring APIs
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.data.jpa;
    requires spring.web;
    requires spring.webmvc;

    // Java EE APIs
    requires java.annotation;
    requires java.persistence;
    requires java.servlet;
    requires java.sql;

    // We're still far from the JPMS dreamland... I'm sorry, Mark.
    requires jdk.unsupported;

    // Jetty APIs
    requires org.eclipse.jetty.io;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;

    // SLF4J
    requires org.slf4j;

    // Jackson + Immutables
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires immutables.styles;
    requires org.immutables.value;

    // BouncyCastle, for SSL configuration
    requires bcprov.jdk15on;
    requires bcpkix.jdk15on;

    // Require micrometer API + unsupported
    requires micrometer.core;

    // Required by Spring Data
    requires com.fasterxml.classmate;
    requires net.bytebuddy;

}
