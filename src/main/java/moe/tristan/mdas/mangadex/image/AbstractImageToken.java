package moe.tristan.mdas.mangadex.image;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static moe.tristan.mdas.mangadex.Constants.TIMESTAMP_FORMAT;

import java.time.ZonedDateTime;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.treatwell.immutables.styles.ValueObjectStyle;

@Immutable
@ValueObjectStyle
abstract class AbstractImageToken {

    @Parameter
    @JsonFormat(shape = STRING, pattern = TIMESTAMP_FORMAT)
    public abstract ZonedDateTime getExpires();

    @Parameter
    public abstract String getHash();

}
