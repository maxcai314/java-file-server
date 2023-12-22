package ax.xz.max.fileserver.util;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Repository
@Transactional
public interface FileDataService {
    @Transactional(readOnly = true)
    boolean fileExists(Path path);
    @Transactional(readOnly = true)
    boolean isPasswordProtected(Path path);
    @Transactional(readOnly = true)
    boolean isPublic(Path path);

    boolean isValidPath(Path path);

    @Transactional
    boolean addFile(Path path, FileVisibility visibility, String password, Resource resource) throws IOException;

    @Transactional
    boolean deleteUnprotectedFile(Path path) throws IOException;
    @Transactional
    boolean deleteFile(Path path, String password) throws IOException;

    @Transactional(readOnly = true)
    Resource getPublicFileAsResource(Path path) throws IOException;
    @Transactional(readOnly = true)
    Resource getFileAsResource(Path path, String password) throws IOException;

    @Transactional(readOnly = true)
    boolean hasPermission(Path path, String password);

    String getMimeType(Path path) throws IOException;
}