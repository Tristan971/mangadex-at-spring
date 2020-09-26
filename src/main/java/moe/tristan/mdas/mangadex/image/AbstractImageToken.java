package moe.tristan.mdas.mangadex.image;

import java.time.LocalDate;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.treatwell.immutables.styles.ValueObjectStyle;

@Immutable
@ValueObjectStyle
abstract class AbstractImageToken {

    @Parameter
    public abstract LocalDate getExpires();

    @Parameter
    public abstract String getHash();

}
