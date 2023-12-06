package ax.xz.max.fileserver.util;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;


public interface FileDataRepository extends JpaRepository<FileDataEntity, Long> {
	// these methods might not be necessary to define
	@Modifying
	@Transactional
	boolean deleteByPath(String path);

	Optional<FileDataEntity> findByPath(String path);
	Optional<FileDataEntity> findByPathAndVisibility(String path, FileVisibility visibility);
	Optional<FileDataEntity> findByPathAndPassword(String path, String password);

	Set<FileDataEntity> findAllByVisibility(FileVisibility visibility);

	Set<FileDataEntity> findAllByUploadDateAfter(Instant uploadDate);
	Set<FileDataEntity> findAllByUploadDateBefore(Instant uploadDate); // add more methods as needed

	FileDataEntity findById(long id);
}
