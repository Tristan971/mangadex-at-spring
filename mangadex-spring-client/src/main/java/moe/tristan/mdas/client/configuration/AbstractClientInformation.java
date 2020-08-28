package moe.tristan.mdas.client.configuration;

import org.immutables.value.Value.Immutable;

import com.treatwell.immutables.styles.ValueObjectStyle;

@Immutable
@ValueObjectStyle
abstract class AbstractClientInformation {

    public abstract String getApplicationName();
    public abstract String getApplicationVersion();
    public abstract String getSpecificationVersion();

    @Override
    public String toString() {
        return String.format("%s %s (%s)", getApplicationName(), getApplicationVersion(), getSpecificationVersion());
    }

}
