package ax.xz.max.fileserver.util;

import java.nio.file.Path;
import java.util.Optional;

import jakarta.transaction.Transactional;


public interface FileDataService {
    boolean fileExists(Path path);
    boolean isPublic(Path path);

    Optional<Path> getPublicFile(Path path);
    Optional<Path> getFile(Path path, String password);

    @Transactional
    boolean addPublicFile(Path path);
    @Transactional
    boolean addFile(Path path, FileVisibility visibility, String password);

    @Transactional
    boolean deletePublicFile(Path path);
    @Transactional
    boolean deleteFile(Path path, String password);
}