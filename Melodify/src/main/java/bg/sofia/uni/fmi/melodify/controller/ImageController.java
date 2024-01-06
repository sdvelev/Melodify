package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static bg.sofia.uni.fmi.melodify.security.RequestManager.getUserByRequest;
import static bg.sofia.uni.fmi.melodify.security.RequestManager.isAdminByRequest;

@Controller
@RequestMapping(path = "images")
public class ImageController {

    private final ResourceLoader resourceLoader;
    private final TokenManagerService tokenManagerService;
    private final UserService userService;

    @Autowired
    public ImageController(ResourceLoader resourceLoader, TokenManagerService tokenManagerService,
                           UserService userService) {
        this.resourceLoader = resourceLoader;
        this.tokenManagerService = tokenManagerService;
        this.userService = userService;
    }

    @GetMapping("/{directory}/{imageName:.+}")
    public ResponseEntity<Resource> getImage(
        @PathVariable String directory,
        @PathVariable String imageName) throws IOException {

        try {
            String filePath = "file:./src/main/resources/images/" + directory + "/" + imageName;
            Resource resource = resourceLoader.getResource(filePath);

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

    @PostMapping("/users")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
                                              HttpServletRequest request) {
        try {
            User potentialUser = getUserByRequest(request, tokenManagerService, userService);
            String directory = "images/users/";
            String fileName = potentialUser.getId() + ".png";

            Path resourcePath = Path.of("src/main/resources/" + directory);

            if (!Files.exists(resourcePath)) {
                Files.createDirectories(resourcePath);
            }

            Files.copy(file.getInputStream(), resourcePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("/images/users/" + potentialUser.getId() + ".png");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload image");
        }
    }
}