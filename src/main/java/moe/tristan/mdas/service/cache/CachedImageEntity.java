package moe.tristan.mdas.service.cache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "MANGADEX", name = "IMAGES")
public class CachedImageEntity {

    @Id
    @Column(name = "ID", unique = true, length = 512)
    private String id;

    @Column(name = "SIZEINBYTES")
    private long sizeInBytes;

    protected CachedImageEntity() {
    }

    public CachedImageEntity(String id, long sizeInBytes) {
        this.id = id;
        this.sizeInBytes = sizeInBytes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

}
