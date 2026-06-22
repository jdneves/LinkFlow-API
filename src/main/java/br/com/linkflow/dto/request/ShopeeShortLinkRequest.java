package br.com.linkflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * Pedido para converter uma URL de produto/loja da Shopee em um short link de
 * afiliado. {@code subIds} são opcionais (máx. 5) e servem ao rastreio (UTM).
 */
public record ShopeeShortLinkRequest(

    @NotBlank(message = "URL de origem é obrigatória")
    @URL(message = "URL de origem inválida")
    @Size(max = 1000)
    String originUrl,

    @Size(max = 5, message = "No máximo 5 subIds")
    List<@Size(max = 50) String> subIds
) {}