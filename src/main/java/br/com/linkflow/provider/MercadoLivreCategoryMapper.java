package br.com.linkflow.provider;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mapeia as categorias do LinkFlow para as categorias raiz do Mercado Livre
 * (site MLB) e vice-versa.
 *
 * <p>As categorias do Radar são: {@code eletrodomesticos, eletronicos, beleza,
 * fitness, casa, games, moda}. Cada uma é buscada na categoria raiz MLB
 * correspondente; os itens retornados (que trazem um {@code category_id} de
 * folha) são reconvertidos pela raiz.</p>
 */
@Component
public class MercadoLivreCategoryMapper {

    /** Categoria LinkFlow → categoria raiz MLB. */
    private static final Map<String, String> LINKFLOW_TO_ML = Map.of(
        "eletrodomesticos", "MLB5726", // Eletrodomésticos
        "eletronicos",      "MLB1000", // Eletrônicos, Áudio e Vídeo
        "beleza",           "MLB1246", // Beleza e Cuidado Pessoal
        "fitness",          "MLB1276", // Esportes e Fitness
        "casa",             "MLB1574", // Casa, Móveis e Decoração
        "games",            "MLB1144", // Games
        "moda",             "MLB23262" // Calçados (subcategoria de Moda com catálogo;
                                       // a raiz MLB1430 só traz USER_PRODUCT/ITEM)
    );

    /** Categoria raiz MLB → categoria LinkFlow (invertido de LINKFLOW_TO_ML). */
    private static final Map<String, String> ML_TO_LINKFLOW = invert();

    private static Map<String, String> invert() {
        Map<String, String> m = new LinkedHashMap<>();
        LINKFLOW_TO_ML.forEach((linkflow, ml) -> m.put(ml, linkflow));
        return m;
    }

    /** Categorias LinkFlow que possuem mapeamento para o Mercado Livre. */
    public java.util.Set<String> linkflowCategories() {
        return LINKFLOW_TO_ML.keySet();
    }

    /** Categoria raiz MLB usada para buscar uma categoria do LinkFlow. */
    public String mlCategoryId(String linkflowCategory) {
        return LINKFLOW_TO_ML.get(linkflowCategory);
    }

    /**
     * Converte um {@code category_id} do Mercado Livre para a categoria do
     * LinkFlow. Como o ML devolve categorias de folha (ex.: {@code MLB1055}),
     * comparamos pelo prefixo da raiz conhecida. Retorna {@code null} se não
     * houver correspondência.
     */
    public String toLinkflowCategory(String mlCategoryId) {
        if (mlCategoryId == null || mlCategoryId.isBlank()) return null;
        // Match direto pela raiz.
        String direct = ML_TO_LINKFLOW.get(mlCategoryId);
        if (direct != null) return direct;
        // Categorias de folha não têm relação textual com a raiz no ML, então
        // não há como derivar só pelo id — o provider usa a categoria buscada.
        return null;
    }
}
