package br.com.linkflow.controller;

import br.com.linkflow.client.ShopeeClient;
import br.com.linkflow.config.ShopeeProperties;
import br.com.linkflow.dto.request.ShopeeShortLinkRequest;
import br.com.linkflow.dto.response.ShopeeShortLinkResponse;
import br.com.linkflow.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoints da integração Shopee voltados ao criador (autenticados via JWT).
 *
 * <p>Hoje expõe a geração de short link de afiliado ({@code shope.ee/...}) a
 * partir de uma URL de produto da Shopee — útil para usar como destino de um
 * link do LinkFlow ({@code /api/links}).</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/shopee")
@RequiredArgsConstructor
public class ShopeeController {

    private final ShopeeClient shopeeClient;
    private final ShopeeProperties props;

    @PostMapping("/short-link")
    public ResponseEntity<?> gerarShortLink(
        @Valid @RequestBody ShopeeShortLinkRequest request,
        @AuthenticationPrincipal User user
    ) {
        if (!props.isEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Integração Shopee desabilitada: configure SHOPEE_APP_ID/SHOPEE_APP_SECRET."));
        }

        String shortLink = shopeeClient.gerarShortLink(request.originUrl(), request.subIds());
        if (shortLink == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Não foi possível gerar o short link da Shopee. Verifique a URL e as credenciais."));
        }

        log.info("Short link Shopee gerado para usuário {}.", user.getEmail());
        return ResponseEntity.ok(new ShopeeShortLinkResponse(shortLink));
    }
}
