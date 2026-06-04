package br.com.linkflow.mock;

import br.com.linkflow.entity.Product;
import br.com.linkflow.entity.Product.Platform;
import br.com.linkflow.entity.Product.Trend;

import java.math.BigDecimal;
import java.util.List;

public class ProductMockData {

    public static List<Product> getProdutos() {
        return List.of(

            // ── ELETRODOMÉSTICOS ─────────────────────────────────────
            Product.builder()
                .externalId("ML-001").platform(Platform.MERCADO_LIVRE)
                .name("Airfryer Philips Walita 4.1L Digital")
                .description("Fritadeira elétrica sem óleo com 7 programas digitais, timer e temperatura até 200°C.")
                .price(new BigDecimal("399.90")).originalPrice(new BigDecimal("599.90"))
                .commissionPct(new BigDecimal("8.5"))
                .imageUrl("https://http2.mlstatic.com/airfryer-philips.jpg")
                .productUrl("https://www.mercadolivre.com.br/airfryer-philips")
                .category("eletrodomesticos").score(91).trend(Trend.RISING).build(),

            Product.builder()
                .externalId("SH-001").platform(Platform.SHOPEE)
                .name("Airfryer Mondial 4L AF-14")
                .description("Fritadeira digital 1400W, capacidade 4L, timer 30 min, temperatura 80-200°C.")
                .price(new BigDecimal("219.90")).originalPrice(new BigDecimal("319.90"))
                .commissionPct(new BigDecimal("9.0"))
                .imageUrl("https://cf.shopee.com.br/airfryer-mondial.jpg")
                .productUrl("https://shopee.com.br/airfryer-mondial")
                .category("eletrodomesticos").score(87).trend(Trend.RISING).build(),

            Product.builder()
                .externalId("ML-002").platform(Platform.MERCADO_LIVRE)
                .name("Cafeteira Nespresso Essenza Mini")
                .description("Máquina de café expresso compacta, 19 bar, compatível com cápsulas originais.")
                .price(new BigDecimal("489.90")).originalPrice(new BigDecimal("699.90"))
                .commissionPct(new BigDecimal("7.0"))
                .imageUrl("https://http2.mlstatic.com/nespresso-essenza.jpg")
                .productUrl("https://www.mercadolivre.com.br/nespresso-essenza")
                .category("eletrodomesticos").score(78).trend(Trend.STABLE).build(),

            Product.builder()
                .externalId("SH-002").platform(Platform.SHOPEE)
                .name("Sanduicheira e Grill Britânia 750W")
                .description("Grill e sanduicheira elétrica 750W, antiaderente, drena gordura, cabo removível.")
                .price(new BigDecimal("89.90")).originalPrice(new BigDecimal("149.90"))
                .commissionPct(new BigDecimal("10.0"))
                .imageUrl("https://cf.shopee.com.br/grill-britania.jpg")
                .productUrl("https://shopee.com.br/grill-britania")
                .category("eletrodomesticos").score(72).trend(Trend.STABLE).build(),

            // ── ELETRÔNICOS ──────────────────────────────────────────
            Product.builder()
                .externalId("ML-003").platform(Platform.MERCADO_LIVRE)
                .name("Fone Bluetooth JBL Tune 510BT")
                .description("Fone de ouvido over-ear sem fio, 40h de bateria, Pure Bass Sound, dobrável.")
                .price(new BigDecimal("199.90")).originalPrice(new BigDecimal("299.90"))
                .commissionPct(new BigDecimal("6.5"))
                .imageUrl("https://http2.mlstatic.com/jbl-tune510.jpg")
                .productUrl("https://www.mercadolivre.com.br/jbl-tune510")
                .category("eletronicos").score(85).trend(Trend.RISING).build(),

            Product.builder()
                .externalId("SH-003").platform(Platform.SHOPEE)
                .name("Smartwatch HW67 Pro Max")
                .description("Relógio inteligente com monitor cardíaco, SpO2, 7 dias de bateria, à prova d'água.")
                .price(new BigDecimal("129.90")).originalPrice(new BigDecimal("249.90"))
                .commissionPct(new BigDecimal("12.0"))
                .imageUrl("https://cf.shopee.com.br/smartwatch-hw67.jpg")
                .productUrl("https://shopee.com.br/smartwatch-hw67")
                .category("eletronicos").score(93).trend(Trend.RISING).build(),

            Product.builder()
                .externalId("ML-004").platform(Platform.MERCADO_LIVRE)
                .name("Caixa de Som JBL Go 3 Bluetooth")
                .description("Speaker portátil à prova d'água e poeira, 5h de bateria, som potente e compacto.")
                .price(new BigDecimal("179.90")).originalPrice(new BigDecimal("249.90"))
                .commissionPct(new BigDecimal("6.0"))
                .imageUrl("https://http2.mlstatic.com/jbl-go3.jpg")
                .productUrl("https://www.mercadolivre.com.br/jbl-go3")
                .category("eletronicos").score(69).trend(Trend.STABLE).build(),

            Product.builder()
                .externalId("SH-004").platform(Platform.SHOPEE)
                .name("Carregador Turbo 65W USB-C")
                .description("Carregador GaN 65W com 3 portas (2x USB-C + 1x USB-A), compatível com notebooks.")
                .price(new BigDecimal("79.90")).originalPrice(new BigDecimal("149.90"))
                .commissionPct(new BigDecimal("11.0"))
                .imageUrl("https://cf.shopee.com.br/carregador-65w.jpg")
                .productUrl("https://shopee.com.br/carregador-65w")
                .category("eletronicos").score(88).trend(Trend.RISING).build(),

            // ── BELEZA ───────────────────────────────────────────────
            Product.builder()
                .externalId("ML-005").platform(Platform.MERCADO_LIVRE)
                .name("Secador de Cabelo Taiff Íon 2200W")
                .description("Secador profissional com tecnologia iônica, 3 temperaturas, difusor incluso.")
                .price(new BigDecimal("129.90")).originalPrice(new BigDecimal("199.90"))
                .commissionPct(new BigDecimal("9.5"))
                .imageUrl("https://http2.mlstatic.com/secador-taiff.jpg")
                .productUrl("https://www.mercadolivre.com.br/secador-taiff")
                .category("beleza").score(76).trend(Trend.STABLE).build(),

            Product.builder()
                .externalId("SH-005").platform(Platform.SHOPEE)
                .name("Kit Skincare Vitamina C + Retinol")
                .description("Kit completo com sérum vitamina C 20%, creme retinol noturno e protetor solar FPS50.")
                .price(new BigDecimal("89.90")).originalPrice(new BigDecimal("179.90"))
                .commissionPct(new BigDecimal("14.0"))
                .imageUrl("https://cf.shopee.com.br/kit-skincare.jpg")
                .productUrl("https://shopee.com.br/kit-skincare")
                .category("beleza").score(95).trend(Trend.RISING).build(),

            // ── FITNESS ──────────────────────────────────────────────
            Product.builder()
                .externalId("ML-006").platform(Platform.MERCADO_LIVRE)
                .name("Esteira Elétrica Kikos T300 127V")
                .description("Esteira com velocidade até 12km/h, 12 programas, display LCD, suporta 100kg.")
                .price(new BigDecimal("1299.90")).originalPrice(new BigDecimal("1899.90"))
                .commissionPct(new BigDecimal("5.5"))
                .imageUrl("https://http2.mlstatic.com/esteira-kikos.jpg")
                .productUrl("https://www.mercadolivre.com.br/esteira-kikos")
                .category("fitness").score(63).trend(Trend.STABLE).build(),

            Product.builder()
                .externalId("SH-006").platform(Platform.SHOPEE)
                .name("Kit Elástico de Musculação 5 Níveis")
                .description("Conjunto com 5 faixas elásticas de resistência progressiva, bolsa e guia de exercícios.")
                .price(new BigDecimal("49.90")).originalPrice(new BigDecimal("99.90"))
                .commissionPct(new BigDecimal("13.0"))
                .imageUrl("https://cf.shopee.com.br/elastico-musculacao.jpg")
                .productUrl("https://shopee.com.br/elastico-musculacao")
                .category("fitness").score(82).trend(Trend.RISING).build(),

            // ── CASA ─────────────────────────────────────────────────
            Product.builder()
                .externalId("ML-007").platform(Platform.MERCADO_LIVRE)
                .name("Aspirador Robô Inteligente Wi-Fi")
                .description("Robô aspirador com mapeamento laser, app, 150min autonomia, compatível Alexa/Google.")
                .price(new BigDecimal("799.90")).originalPrice(new BigDecimal("1299.90"))
                .commissionPct(new BigDecimal("7.5"))
                .imageUrl("https://http2.mlstatic.com/robo-aspirador.jpg")
                .productUrl("https://www.mercadolivre.com.br/robo-aspirador")
                .category("casa").score(89).trend(Trend.RISING).build(),

            Product.builder()
                .externalId("SH-007").platform(Platform.SHOPEE)
                .name("Organizador Suspenso para Porta Banheiro")
                .description("Suporte com 4 ganchos e prateleira, aço inox, sem furar, capacidade 5kg.")
                .price(new BigDecimal("39.90")).originalPrice(new BigDecimal("79.90"))
                .commissionPct(new BigDecimal("15.0"))
                .imageUrl("https://cf.shopee.com.br/organizador-porta.jpg")
                .productUrl("https://shopee.com.br/organizador-porta")
                .category("casa").score(71).trend(Trend.STABLE).build(),

            Product.builder()
                .externalId("ML-008").platform(Platform.MERCADO_LIVRE)
                .name("Purificador de Ar Electrolux PA31G")
                .description("Purificador com 3 estágios de filtragem, silencioso, cobre ambientes até 30m².")
                .price(new BigDecimal("449.90")).originalPrice(new BigDecimal("649.90"))
                .commissionPct(new BigDecimal("6.0"))
                .imageUrl("https://http2.mlstatic.com/purificador-electrolux.jpg")
                .productUrl("https://www.mercadolivre.com.br/purificador-electrolux")
                .category("casa").score(74).trend(Trend.RISING).build(),

            // ── GAMES ────────────────────────────────────────────────
            Product.builder()
                .externalId("SH-008").platform(Platform.SHOPEE)
                .name("Controle Gamer sem Fio para PC/Android")
                .description("Gamepad Bluetooth, vibração dupla, gatilho analógico, bateria 600mAh, 8h de uso.")
                .price(new BigDecimal("99.90")).originalPrice(new BigDecimal("179.90"))
                .commissionPct(new BigDecimal("11.5"))
                .imageUrl("https://cf.shopee.com.br/controle-gamer.jpg")
                .productUrl("https://shopee.com.br/controle-gamer")
                .category("games").score(80).trend(Trend.STABLE).build(),

            Product.builder()
                .externalId("ML-009").platform(Platform.MERCADO_LIVRE)
                .name("Cadeira Gamer Reclinável DX Racer")
                .description("Cadeira ergonômica reclinável 180°, almofadas lombar e pescoço, suporta até 120kg.")
                .price(new BigDecimal("899.90")).originalPrice(new BigDecimal("1399.90"))
                .commissionPct(new BigDecimal("5.0"))
                .imageUrl("https://http2.mlstatic.com/cadeira-gamer.jpg")
                .productUrl("https://www.mercadolivre.com.br/cadeira-gamer")
                .category("games").score(58).trend(Trend.FALLING).build(),

            // ── MODA ─────────────────────────────────────────────────
            Product.builder()
                .externalId("SH-009").platform(Platform.SHOPEE)
                .name("Tênis Casual Masculino Slip On Confortável")
                .description("Tênis sem cadarço, solado antiderrapante, espuma viscoelástica, tamanhos 38-44.")
                .price(new BigDecimal("79.90")).originalPrice(new BigDecimal("149.90"))
                .commissionPct(new BigDecimal("12.0"))
                .imageUrl("https://cf.shopee.com.br/tenis-slip-on.jpg")
                .productUrl("https://shopee.com.br/tenis-slip-on")
                .category("moda").score(66).trend(Trend.STABLE).build(),

            Product.builder()
                .externalId("ML-010").platform(Platform.MERCADO_LIVRE)
                .name("Bolsa Feminina Couro Sintético Alça Transversal")
                .description("Bolsa transversal média, 3 compartimentos, fecho magnético, alça ajustável.")
                .price(new BigDecimal("69.90")).originalPrice(new BigDecimal("129.90"))
                .commissionPct(new BigDecimal("10.5"))
                .imageUrl("https://http2.mlstatic.com/bolsa-transversal.jpg")
                .productUrl("https://www.mercadolivre.com.br/bolsa-transversal")
                .category("moda").score(61).trend(Trend.STABLE).build(),

            Product.builder()
                .externalId("SH-010").platform(Platform.SHOPEE)
                .name("Perfume Importado 212 NYC Men 100ml")
                .description("Fragrância masculina amadeirada e aquática, longa duração, frasco elegante.")
                .price(new BigDecimal("159.90")).originalPrice(new BigDecimal("299.90"))
                .commissionPct(new BigDecimal("13.5"))
                .imageUrl("https://cf.shopee.com.br/perfume-212.jpg")
                .productUrl("https://shopee.com.br/perfume-212")
                .category("moda").score(77).trend(Trend.RISING).build()
        );
    }
}
