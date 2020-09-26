package moe.tristan.mdas.model;

import java.util.List;

import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.treatwell.immutables.styles.ValueObjectStyle;

import moe.tristan.mdas.mangadex.image.ImageMode;

@Immutable
@ValueObjectStyle
abstract class AbstractImageRequest {

    @Parameter
    public abstract ImageMode getMode();

    @Parameter
    public abstract String getChapter();

    @Parameter
    public abstract String getFile();

    @Parameter
    public List<String> getIdentifiers() {
        return List.of(getMode().name(), getChapter(), getFile());
    }

    @Derived
    public String getUniqueIdentifier() {
        return String.format("%s_%s_%s", getMode().name(), getChapter(), getFile());
    }

    @Override
    public String toString() {
        return getUniqueIdentifier();
    }

}
