package moe.tristan.mdas.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import moe.tristan.mdas.webutils.logging.WebRequestsLoggingConfiguration;

@Configuration
@Import(WebRequestsLoggingConfiguration.class)
public class MangadexTestServerConfiguration {

}
