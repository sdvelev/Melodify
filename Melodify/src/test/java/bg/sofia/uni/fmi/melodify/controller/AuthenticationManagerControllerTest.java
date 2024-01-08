package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.AuthenticationDto;
import bg.sofia.uni.fmi.melodify.dto.TokenUserIdDto;
import bg.sofia.uni.fmi.melodify.service.AuthenticationManagerService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationManagerControllerTest {

    @MockBean
    private AuthenticationManagerService authenticationManagerService;

    @InjectMocks
    @Autowired
    private AuthenticationManagerController authenticationManagerController;

    private AuthenticationDto validAuthenticationDto;
    private AuthenticationDto invalidAuthenticationDto;
    private TokenUserIdDto validToken;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        this.validAuthenticationDto = new AuthenticationDto("validUsername", "validPassword");
        this.invalidAuthenticationDto = new AuthenticationDto("", "");

        this.validToken = new TokenUserIdDto("validToken", 1L, false);
        when(this.authenticationManagerService.login(validAuthenticationDto)).thenReturn(validToken);
    }

    @Test
    public void testLogin() {

        assertEquals(this.validToken, this.authenticationManagerController.login(this.validAuthenticationDto));
        verify(this.authenticationManagerService, times(1)).login(validAuthenticationDto);
    }

    @Test
    public void testLoginServiceException() {
        when(this.authenticationManagerService.login(any())).thenThrow(new ValidationException("Validation failed"));

        assertThrows(ValidationException.class, () ->
                this.authenticationManagerController.login(validAuthenticationDto));
    }
}
