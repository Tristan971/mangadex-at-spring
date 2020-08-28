package moe.tristan.mdas.api.ping;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fasterxml.jackson.databind.util.StdDateFormat.DATE_FORMAT_STR_ISO8601;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.treatwell.immutables.styles.ValueObjectStyle;

@Immutable
@ValueObjectStyle
abstract class AbstractPingRequest {

    /**
     * @return the client secret
     */
    public abstract String getSecret();

    /**
     * @return the client current port
     */
    public abstract int getPort();

    /**
     * @return the currently available diskspace (for client usage) in bytes
     */
    @JsonProperty("disk_space")
    public abstract int getDiskSpace();

    /**
     * @return the maximum speed to dedicate to the client, in kilobytes per second
     */
    @JsonProperty("network_speed")
    public abstract int getNetworkSpeed();

    /**
     * @return the client's build version
     */
    @JsonProperty("build_version")
    public abstract String getBuildVersion();

    /**
     * @return the TLS timestamp from the previous ping
     */
    @JsonProperty("tls_created_at")
    @JsonFormat(shape = STRING, pattern = DATE_FORMAT_STR_ISO8601)
    public abstract Optional<ZonedDateTime> getTlsCreatedAt();

}
