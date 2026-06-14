package br.com.linkflow.service;

import br.com.linkflow.client.ClaudeClient;
import br.com.linkflow.client.ElevenLabsClient;
import br.com.linkflow.client.HeyGenClient;
import br.com.linkflow.client.StorageClient;
import br.com.linkflow.dto.request.RegisterRequest;
import br.com.linkflow.dto.request.ScriptRequest;
import br.com.linkflow.dto.request.VideoCreateRequest;
import br.com.linkflow.entity.Script.Format;
import br.com.linkflow.entity.Script.Platform;
import br.com.linkflow.entity.User;
import br.com.linkflow.entity.VideoMode;
import br.com.linkflow.exception.BusinessException;
import br.com.linkflow.repository.VideoJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VideoServiceTest {

    @Autowired VideoService videoService;
    @Autowired VideoJobRepository videoJobRepository;
    @Autowired AuthService authService;
    @Autowired ScriptService scriptService;

    @MockBean
    ClaudeClient claudeClient;
    @MockBean ElevenLabsClient elevenLabsClient;
    @MockBean HeyGenClient heyGenClient;
    @MockBean StorageClient storageClient;

    private User usuario;
    private UUID scriptId;

    private static final String MOCK_CLAUDE = """
        {"titulo_sugerido":"Título","gancho_abertura":"Gancho",
         "topicos":["T1","T2"],"cta_afiliado":"CTA",
         "legenda_instagram":"Legenda","hashtags":["h1"],"stories":["S1"]}
        """;

    @BeforeEach
    void setup() {
        var auth = authService.register(
            new RegisterRequest("João", "joao@video.com", "Senha@123")
        );
        usuario = new User();
        usuario.setId(auth.user().id());
        usuario.setEmail(auth.user().email());
        usuario.setName("João");
        usuario.setPlan(User.Plan.FREE);

        when(claudeClient.completar(anyString())).thenReturn(MOCK_CLAUDE);
        var script = scriptService.gerar(new ScriptRequest(
            null, "Airfryer", null,
            Platform.YOUTUBE, Format.REVIEW, "descontraído", "médio"
        ), usuario);
        scriptId = script.id();

        when(elevenLabsClient.textToSpeech(anyString(), any()))
            .thenReturn("audio-bytes".getBytes());
        when(storageClient.uploadAudio(any(), any()))
            .thenReturn("https://r2.example.com/audio/test.mp3");
        when(heyGenClient.submeterVideo(anyString(), any()))
            .thenReturn("heygen-video-id-123");
    }

    @Test
    @DisplayName("Deve bloquear vídeos AVATAR no plano FREE")
    void deveBloqueararAvatarNoFree() {
        var request = new VideoCreateRequest(scriptId, VideoMode.AVATAR, null, null);

        assertThatThrownBy(() -> videoService.iniciar(request, usuario))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("não estão disponíveis no seu plano");
    }

    @Test
    @DisplayName("Deve buscar job por ID")
    void deveBuscarJobPorId() {
        usuario.setPlan(User.Plan.PRO);  // PRO tem avatar disponível
        var request = new VideoCreateRequest(scriptId, VideoMode.AVATAR, null, null);
        var criado = videoService.iniciar(request, usuario);
        var buscado = videoService.buscarPorId(criado.id(), usuario);

        assertThat(buscado.id()).isEqualTo(criado.id());
        assertThat(buscado.productName()).isEqualTo("Airfryer");
        assertThat(buscado.mode()).isEqualTo("AVATAR");
    }
}
