package ax.xz.max.fileserver.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;


public interface FileDataRepository extends JpaRepository<FileDataEntity, Long> {
	Optional<FileDataEntity> findByPathAndVisibility(String path, FileVisibility visibility);
	Optional<FileDataEntity> findByPathAndPassword(String path, String password);

	boolean existsByPath(String string);
}
