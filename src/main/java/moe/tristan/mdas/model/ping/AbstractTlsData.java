package moe.tristan.mdas.model.ping;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fasterxml.jackson.databind.util.StdDateFormat.DATE_FORMAT_STR_ISO8601;

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
    @JsonFormat(shape = STRING, pattern = DATE_FORMAT_STR_ISO8601)
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
