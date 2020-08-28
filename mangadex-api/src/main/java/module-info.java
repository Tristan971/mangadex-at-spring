open module moe.tristan.mdas.api {

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    requires org.immutables.value;
    requires immutables.styles;
    requires java.annotation;

    exports moe.tristan.mdas.api.ping;
    exports moe.tristan.mdas.api.stop;
    exports moe.tristan.mdas.api.image;

}
