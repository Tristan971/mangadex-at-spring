package moe.tristan.mdas.api.ping;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static moe.tristan.mdas.api.Constants.TIMESTAMP_FORMAT;

import java.time.ZonedDateTime;

import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.treatwell.immutables.styles.ValueObjectStyle;

@Immutable
@ValueObjectStyle
abstract class AbstractTlsData {

    /**
     * @return a timestamp in RFC-3339 used in following {@link PingRequest}s.
     */
    @JsonProperty("created_at")
    @JsonFormat(shape = STRING, pattern = TIMESTAMP_FORMAT)
    public abstract ZonedDateTime getCreatedAt();

    /**
     * @return a PKCS_1 private key
     */
    @JsonProperty("private_key")
    public abstract String getPrivateKey();

    /**
     * @return an x509 certificate
     */
    public abstract String getCertificate();

}
