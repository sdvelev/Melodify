package bg.sofia.uni.fmi.melodify.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping(path = "images")
public class ImageController {

    private final ResourceLoader resourceLoader;

    public ImageController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/{directory}/{imageName:.+}")
    public ResponseEntity<Resource> getImage(
        @PathVariable String directory,
        @PathVariable String imageName) throws IOException {
        try {
            Resource resource = resourceLoader.getResource("classpath:/images/" + directory + "/" + imageName);

            if (resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDispositionFormData("inline", imageName);
                headers.setContentType(MediaType.IMAGE_PNG);

                return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}