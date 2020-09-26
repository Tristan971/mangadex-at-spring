package moe.tristan.mdas.model;

import java.util.Optional;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.springframework.http.HttpHeaders;

import com.treatwell.immutables.styles.ValueObjectStyle;

@Immutable
@ValueObjectStyle
abstract class AbstractImageResponse {

    @Parameter
    public abstract byte[] getBytes();

    @Parameter
    public abstract CacheHintMode getCacheHint();

    public abstract Optional<HttpHeaders> getUpstreamHeaders();

}
