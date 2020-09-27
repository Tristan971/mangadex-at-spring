package moe.tristan.mdas.model;

import java.util.List;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.treatwell.immutables.styles.ValueObjectStyle;

@Immutable
@ValueObjectStyle
abstract class AbstractExceptionResponse {

    @Parameter
    public abstract String message();

    @Parameter
    public abstract List<String> causes();

}
