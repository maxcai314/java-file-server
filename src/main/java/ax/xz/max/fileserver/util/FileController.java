package ax.xz.max.fileserver.util;

import ax.xz.max.fileserver.util.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
		return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, "bad request info").build();
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

	@ExceptionHandler({IOException.class})
	public ErrorResponse handleIOException(IOException e) {
		return ErrorResponse.builder(e, HttpStatus.INTERNAL_SERVER_ERROR, "internal server error during file processing").build();
	}

	@ExceptionHandler({FileAlreadyExistsException.class})
	public ErrorResponse handleFileAlreadyExists(RuntimeException e) {
		return ErrorResponse.builder(e, HttpStatus.CONFLICT, "file already exists").build();
	}

	private ResponseEntity<Resource> wrapResource(Resource resource, String mimeType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(mimeType));
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}

	@GetMapping(value = "/{path}", produces = "*/*")
	public ResponseEntity<Resource> getFile(
			@PathVariable String path,
			@RequestParam(name = "password", required = false) Optional<String> password
	) throws IOException {
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

	@PostMapping(value = "/{path}")
	public ResponseEntity<String> postFile(
			@PathVariable String path,
			@RequestParam("file") MultipartFile file,
			@RequestParam(name = "password", required = false) Optional<String> password,
			@RequestParam(name = "public", required = false, defaultValue = "true") boolean isPublic
	) throws IOException {
		Path filePath = Path.of(path);
		if (!fileDataService.isValidPath(filePath))
			throw new BadRequestException();
		if (fileDataService.fileExists(filePath))
			throw new FileAlreadyExistsException();

		FileVisibility visibility = isPublic ? FileVisibility.PUBLIC : FileVisibility.PRIVATE;

		if (password.isEmpty() && visibility == FileVisibility.PRIVATE)
			throw new BadRequestException();

		fileDataService.addFile(filePath, visibility, password.orElse(null), new InputStreamResource(file.getInputStream()));
		return ResponseEntity.ok("file uploaded");
	}


}
