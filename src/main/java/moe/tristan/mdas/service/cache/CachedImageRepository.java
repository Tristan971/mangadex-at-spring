package moe.tristan.mdas.service.cache;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CachedImageRepository extends JpaRepository<CachedImageEntity, String> {

}
