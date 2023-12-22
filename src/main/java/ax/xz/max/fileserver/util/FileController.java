package ax.xz.max.fileserver.util;

import ax.xz.max.fileserver.util.errors.BadRequestException;
import ax.xz.max.fileserver.util.errors.FileNotFoundException;
import ax.xz.max.fileserver.util.errors.NotAuthenticatedException;
import ax.xz.max.fileserver.util.errors.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/files")
public class FileController {

	private final FileDataService fileDataService;

	public FileController(@Autowired FileDataService fileDataService) {
		this.fileDataService = fileDataService;
	}

	@ExceptionHandler({BadRequestException.class})
	public ErrorResponse handleBadRequest(RuntimeException e) {
		return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, "bad file path request").build();
	}

	@ExceptionHandler({FileNotFoundException.class})
	public ErrorResponse handleNotFound(RuntimeException e) {
		return ErrorResponse.builder(e, HttpStatus.NOT_FOUND, "file not found").build();
	}

	@ExceptionHandler({NotAuthenticatedException.class})
	public ErrorResponse handleNotAuthenticated(RuntimeException e) {
		return ErrorResponse.builder(e, HttpStatus.UNAUTHORIZED, "authentication required").build();
	}

	@ExceptionHandler({PermissionDeniedException.class})
	public ErrorResponse handlePermissionDenied(RuntimeException e) {
		return ErrorResponse.builder(e, HttpStatus.FORBIDDEN, "permission denied").build();
	}

	private ResponseEntity<Resource> wrapResource(Resource resource, String mimeType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(mimeType));
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}

	@GetMapping(value = "/{path}", produces = "*/*")
	public ResponseEntity<Resource> getFile(@PathVariable String path, @RequestParam(value = "password", required = false) Optional<String> password) throws IOException {
		Path filePath = Path.of(path);
		if (!fileDataService.isValidPath(filePath))
			throw new BadRequestException();
		if (!fileDataService.fileExists(filePath))
			throw new FileNotFoundException();

		if (fileDataService.isPublic(filePath))
			// authentication not required, send file
			return wrapResource(fileDataService.getPublicFileAsResource(filePath), fileDataService.getMimeType(filePath));

		if (password.isEmpty())
			throw new NotAuthenticatedException();

		// try to authenticate
		if (!fileDataService.hasPermission(filePath, password.get()))
			throw new PermissionDeniedException();

		return wrapResource(fileDataService.getFileAsResource(filePath, password.get()), fileDataService.getMimeType(filePath));
	}
}
