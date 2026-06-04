package br.com.linkflow.controller;

import br.com.linkflow.dto.response.ProductResponse;
import br.com.linkflow.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/radar")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/radar?category=eletronicos&platform=SHOPEE&search=airfryer&page=0
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> buscar(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String platform,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(productService.buscar(category, platform, search, page));
    }

    // GET /api/radar/trending — top 6 produtos em alta
    @GetMapping("/trending")
    public ResponseEntity<List<ProductResponse>> trending() {
        return ResponseEntity.ok(productService.buscarEmAlta());
    }

    // GET /api/radar/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(productService.buscarPorId(id));
    }

    // GET /api/radar/categorias
    @GetMapping("/categorias")
    public ResponseEntity<Map<String, List<String>>> categorias() {
        return ResponseEntity.ok(Map.of("categorias", productService.listarCategorias()));
    }
}
