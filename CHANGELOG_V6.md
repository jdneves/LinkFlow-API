# Changelog v6 - Radar com fontes reais (Mercado Livre) + abstração de providers

## Resumo

O Radar de Produtos passa a suportar **fontes de dados reais**, começando pelo
**Mercado Livre**, sem alterar o contrato da API (`ProductResponse` intacto). Foi
introduzida uma abstração de **providers** com **fallback automático para o mock**
nas plataformas ainda não conectadas. A régua de **scoring do LinkFlow** agora é
aplicada de forma uniforme a **todas as fontes**.

## Mudanças Implementadas

### 1. Abstração de providers

**Nova interface `ProductProvider`** (`provider/`):
- `Platform platform()` — plataforma atendida
- `boolean isEnabled()` — lê flag de config
- `List<RawProduct> fetchCatalog()` — catálogo já normalizado (síncrono, usado só no sync)

**Novo DTO `RawProduct`** — apenas o que uma fonte fornece: `externalId, platform,
name, description, price, originalPrice, commissionPct (nullable), imageUrl,
productUrl, category`. **Não** inclui `score`/`trend`/`scoreDetail` — esses são
calculados pelo LinkFlow.

**`MockProductProvider`** — embrulha o `ProductMockData`, filtrando por plataforma.
É o fallback usado quando o provider real está desabilitado ou falha.

### 2. Integração Mercado Livre

**`MercadoLivreClient`** (`client/`) — segue o padrão dos demais clients:
- **OAuth 2.0** com `TokenManager` interno: cache do access token + refresh
  automático na expiração (rotação do refresh token tratada).
- Endpoints conforme a API oficial (`api.mercadolibre.com`): `POST /oauth/token`,
  `GET /sites/{site}/search?category=...`.
- Resiliência: `401/403` e demais erros são logados com `log.warn` e retornam lista
  vazia — **o sync nunca é derrubado**.

**`MercadoLivreProductProvider`** — itera as categorias do LinkFlow, busca os itens
da categoria raiz MLB correspondente e mapeia para `RawProduct`. Tag de afiliado
acrescentada ao link do produto.

**`MercadoLivreCategoryMapper`** — mapeia categorias LinkFlow ↔ categorias raiz MLB
(`eletrodomesticos→MLB5726`, `eletronicos→MLB1000`, `beleza→MLB1246`,
`fitness→MLB1276`, `casa→MLB1574`, `games→MLB1144`, `moda→MLB1430`).

**Comissão:** a busca do ML não retorna comissão de afiliado; ela é derivada do mapa
`ml.commission-by-category` (config). `// TODO: comissão real via programa de afiliados`.

### 3. Scoring uniforme

**Novo `ProductScoringService`** — calcula `score` (0–99, ponderando desconto 55% +
comissão 45%) e `trend` (derivado do score) a partir do `RawProduct`, e converte em
entidade `Product`. As métricas de borda (`scoreDetail`, `estimatedCommission`)
continuam em `ProductResponse.from(...)`, mantendo o contrato do Radar.

### 4. Sync por providers com fallback

`ProductService.sincronizarProdutos()` (scheduler de 6h) agora itera as plataformas:
para cada uma usa o provider real (se habilitado e com retorno) ou cai no
`MockProductProvider` da plataforma. O Radar continua servindo do banco/cache —
**nunca chama o Mercado Livre por request**, respeitando o rate limit (busca em lote
com pausa configurável entre categorias).

### 5. Configuração (env vars — só backend)

```properties
ml.enabled=${ML_ENABLED:false}
ml.client-id / ml.client-secret / ml.refresh-token
ml.site-id=MLB
ml.affiliate-tag
ml.limit-per-category=20
ml.throttle-ms=300
ml.api-base-url=https://api.mercadolibre.com
ml.commission-by-category.<categoria>=<percentual>
```

Com `ML_ENABLED=false` (default), o Radar mantém **exatamente o comportamento atual**
(dados mock).

### 6. Testes

- `MockProductProviderTest` — filtragem por plataforma.
- `MercadoLivreClientTest` — OAuth refresh, reuso de token, mapeamento e `403`
  (HTTP mockado via MockWebServer).
- `MercadoLivreProductProviderTest` — mapeamento ML → `RawProduct`, categoria e
  comissão por config.
- `ProductSyncFallbackTest` — provider habilitado usa dados reais; desabilitado/falha
  cai no mock; scoring aplicado a todas as fontes.

## Não houve alteração de banco

A entidade `Product`, o contrato `ProductResponse` e os enums `Platform`/categoria
permanecem inalterados — não há migration nesta versão.
