package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.mysql;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.AuthenticationResponse;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.LoginRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.AuthenticationMySQLService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerSQLTest {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private AuthenticationMySQLController authController;
    @Mock
    private AuthenticationMySQLService authServices;


    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthenticationResponse authenticationResponse;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        registerRequest = RegisterRequest.builder()
                .firstname("miquel")
                .lastname("debon")
                .email("miquel.debon@gmail.com")
                .password("password").build();
        loginRequest = LoginRequest.builder()
                .email("miquel.debon@gmail.com")
                .password("password").build();
        authenticationResponse = AuthenticationResponse.builder()
                .token("token").build();
    }

    @Test
    public void authController_registerUser_returnToken() throws Exception{
        given(authServices.register(registerRequest)).willReturn(authenticationResponse);

        mockMvc.perform(post("/api/mysql/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(registerRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authenticationResponse.getToken()))
                .andDo(MockMvcResultHandlers.print());

        verify(authServices, times(1)).register(registerRequest);
    }


    @Test
    public void authController_loginUser_returnToken() throws Exception{
        given(authServices.authenticate(loginRequest)).willReturn(authenticationResponse);

        mockMvc.perform(post("/api/mysql/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(loginRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authenticationResponse.getToken()))
                .andDo(MockMvcResultHandlers.print());

        verify(authServices, times(1)).authenticate(loginRequest);
    }


}
