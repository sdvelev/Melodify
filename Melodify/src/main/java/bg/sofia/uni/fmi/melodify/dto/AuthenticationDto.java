package bg.sofia.uni.fmi.melodify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;
}
