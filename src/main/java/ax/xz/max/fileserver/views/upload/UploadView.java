package ax.xz.max.fileserver.views.upload;

import ax.xz.max.fileserver.util.*;
import ax.xz.max.fileserver.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpStatus.*;

@PageTitle("Upload")
@Route(value = "upload", layout = MainLayout.class)
public class UploadView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(UploadView.class);

    private static final int MEGA_BYTE = 2 << 19;

    private static final String UPLOAD_URL = "http://localhost:8080/files/";


    private final AtomicInteger numFiles = new AtomicInteger(0);

    public UploadView() {
        H1 header = new H1("Upload Files");
        header.getStyle().set("margin-bottom", "2px");

        Span text = new Span("Max file size: 50 MB");

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        CustomUpload upload = new CustomUpload(buffer);

        Button chooseFileButton = new Button("Upload files...");
        chooseFileButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        upload.setUploadButton(chooseFileButton);
        upload.setDropLabel(new Span("Drop files here"));

        upload.setMaxFiles(15);
        upload.setMaxFileSize(50 * MEGA_BYTE);

        Button submitButton = new Button("Submit");

        submitButton.setEnabled(false);

        upload.addSucceededListener(event -> submitButton.setEnabled(numFiles.incrementAndGet() > 0));
        upload.addFileRemoveListener(event -> submitButton.setEnabled(numFiles.decrementAndGet() > 0));

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
        // todo: give submitButton functionality
        submitButton.addClickListener(event -> {
            for (String fileName : buffer.getFiles()) {
                FileData data = buffer.getFileData(fileName);
                MultipartFile multipartFile = new StandardMultipartFile(data);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

                try {
                    body.add("file", new ByteArrayResource(multipartFile.getBytes()) {
                        @Override
                        public String getFilename() {
                            return fileName;
                        }
                    });
                } catch (IOException e) {
                    CustomNotification.persistError("failed to read file " + fileName);
                    throw new RuntimeException(e);
                }

                body.add("public", true);
                body.add("password", "1234");

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                String url = UriComponentsBuilder.fromHttpUrl(UPLOAD_URL)
                        .pathSegment(fileName)
                        .build()
                        .toUriString();

                logger.info("uploading file {} to {}", fileName, url);
                ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

                // handle response
                switch (response.getStatusCode()) {
                    case OK -> CustomNotification.show(fileName + " uploaded successfully");
                    case CONFLICT -> CustomNotification.showError(fileName + " already exists");
                    case BAD_REQUEST -> CustomNotification.showError(fileName + " bad request");
                    case INTERNAL_SERVER_ERROR -> CustomNotification.showError("internal server error while uploading " + fileName);
                    default -> CustomNotification.showError("unknown error while uploading " + fileName);
                }
            }

        });

        Div container = new Div(
                header, text, upload, submitButton
        );
        container.getStyle().set("margin-left", "8px");

        add(container);

    }

}