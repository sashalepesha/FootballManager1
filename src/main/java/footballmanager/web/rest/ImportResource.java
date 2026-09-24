package footballmanager.web.rest;

import footballmanager.security.AuthoritiesConstants;
import footballmanager.service.ImportService;
import footballmanager.service.imports.ImportResult;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
public class ImportResource {

    private final ImportService importService;

    public ImportResource(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/{entityType}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<ImportResult> importEntity(@PathVariable String entityType, @RequestParam("file") MultipartFile file)
        throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ImportResult result = importService.importFile(entityType, file);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
