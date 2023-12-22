package ax.xz.max.fileserver.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/files")
public class FileController {

	private final FileDataService fileDataService;

	public FileController(@Autowired FileDataService fileDataService) {
		this.fileDataService = fileDataService;
	}

	@GetMapping("/{path}")
	public ResponseEntity<?> getFile(@PathVariable String path, @RequestParam(value = "password", required = false) Optional<String> password) throws IOException {
		Path filePath = Path.of(path);
		if (!fileDataService.isValidPath(filePath))
			return ResponseEntity.badRequest().build();
		if (!fileDataService.fileExists(filePath))
			return ResponseEntity.notFound().build();

		if (fileDataService.isPublic(filePath))
			return ResponseEntity.ok().body(fileDataService.getPublicFileAsResource(filePath));
		else if (password.isEmpty())
			return ResponseEntity.status(401).build();
		else {
			// try to authenticate
			if (!fileDataService.hasPermission(filePath, password.get()))
				return ResponseEntity.status(403).build(); // permission denied
			else {
				// good, send file
				String mimeType = fileDataService.getMimeType(filePath);
				mimeType = "application/octet-stream";
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.parseMediaType(mimeType));
//				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.setContentDisposition(ContentDisposition.inline().filename(filePath.getFileName().toString()).build());
				return ResponseEntity.ok()
						.headers(headers)
						.body(fileDataService.getFileAsResource(filePath, password.get()));
			}
		}
	}
}
