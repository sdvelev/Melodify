package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.security.RequestManager;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private TokenManagerService tokenManagerService;
    @MockBean
    private ResourceLoader resourceLoader;

    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    @Autowired
    private ImageController imageController;

    @MockBean
    private Resource resource;

    private User user = new User(1L, "Name", "surname", "email", "password", "user.png", Collections.emptyList(), new Queue(),"user.com");

    @BeforeEach
    public void setup(){

        MockitoAnnotations.openMocks(this);

        String stringToReturn = "Bearer ";
        when(this.request.getHeader("Authorization")).thenReturn(stringToReturn);
     }

    @Test
    public void testGetImageFail() throws IOException {
            when(this.resource.exists()).thenReturn(true);
            when(this.resource.isReadable()).thenReturn(true);

            assertEquals(HttpStatus.NOT_FOUND , this.imageController.getImage("", "").getStatusCode());
    }

    @Test
    public void testUploadImage(){
        try(MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)){
            mockedRequestManager.when(()-> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user);

        }
    }
}
