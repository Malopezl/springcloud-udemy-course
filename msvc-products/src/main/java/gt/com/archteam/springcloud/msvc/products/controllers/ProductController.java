package gt.com.archteam.springcloud.msvc.products.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import gt.com.archteam.libs.msvc.commons.entities.Product;
import gt.com.archteam.springcloud.msvc.products.services.ProductService;

@RestController
public class ProductController {
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> list(@RequestHeader(name = "message-request", required = false) String message) {
        logger.info("Ingresando al metodo ProductController::list");
        logger.info("message: {}", message);
        return ResponseEntity.ok(this.productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> details(@PathVariable Long id) {
        logger.info("Ingresando al metodo ProductController::details");

        // if (id.equals(10L)) {
        // throw new IllegalStateException("Producto no encontrado");
        // }
        // if (id.equals(7L)) {
        // TimeUnit.SECONDS.sleep(4L);
        // }

        var productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            return ResponseEntity.ok(productOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        logger.info("Ingresando al metodo ProductController::create {}", product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody Product product) {
        logger.info("Ingresando al metodo ProductController::update {}", product);
        var productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            var updatedProduct = productOptional.orElseThrow();
            updatedProduct.setName(product.getName());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setCreatedAt(product.getCreatedAt());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(productService.save(updatedProduct));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        var productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            logger.info("Ingresando al metodo ProductController::delete {}", productOptional.get());
            this.productService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();

    }

}
