package gt.com.archteam.springcloud.msvc.items.controllers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import gt.com.archteam.libs.msvc.commons.entities.Product;
import gt.com.archteam.springcloud.msvc.items.models.Item;
import gt.com.archteam.springcloud.msvc.items.services.ItemService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RefreshScope
@RestController
public class ItemController {
    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${configuracion.texto}")
    private String text;

    @Autowired
    private Environment env;

    /*
     * Se agrega la anotacion @Qualifier para definir que implementacion utilizar.
     */
    public ItemController(@Qualifier("itemServiceWebClient") ItemService itemService,
            CircuitBreakerFactory circuitBreakerFactory) {
        this.itemService = itemService;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @GetMapping("/fetch-configs")
    public ResponseEntity<?> fetchConfigs(@Value("${server.port}") String port) {
        Map<String, String> body = new HashMap<>();
        body.put("text", text);
        body.put("port", port);
        logger.info(port);
        logger.info(text);

        if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
            body.put("autor.nombre", env.getProperty("configuracion.autor.nombre"));
            body.put("autor.email", env.getProperty("configuracion.autor.email"));
        }
        return ResponseEntity.ok(body);
    }

    @GetMapping
    public List<Item> list(@RequestParam(required = false) String name,
            @RequestHeader(name = "token-request", required = false) String token) {
        logger.info("Llamada a metodo del controller ItemController::List()");
        logger.info("Request parameter: {}", name);
        logger.info("Token: {}", token);
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id) {
        var item = circuitBreakerFactory.create("items").run(() -> itemService.findById(id), e -> {
            logger.error(e.getMessage());
            var product = new Product();
            product.setCreatedAt(LocalDate.now());
            product.setId(1L);
            product.setName("Camara Sony");
            product.setPrice(500.00);
            return Optional.of(new Item(product, 5));
        });
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        }
        return ResponseEntity.status(404).body(Collections.singletonMap("message", "No existe el producto"));
    }

    @CircuitBreaker(name = "items", fallbackMethod = "getFallBackMethodProduct")
    @GetMapping("/details/{id}")
    public ResponseEntity<?> details2(@PathVariable Long id) {
        var item = itemService.findById(id);

        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        }
        return ResponseEntity.status(404).body(Collections.singletonMap("message", "No existe el producto"));
    }

    @CircuitBreaker(name = "items", fallbackMethod = "getFallBackMethodProduct2")
    @TimeLimiter(name = "items")
    @GetMapping("/details3/{id}")
    public CompletableFuture<?> details3(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            var item = itemService.findById(id);

            if (item.isPresent()) {
                return ResponseEntity.ok(item.get());
            }
            return ResponseEntity.status(404).body(Collections.singletonMap("message", "No existe el producto"));
        });
    }

    /*
     * Este metodo tiene que tener el mismo tipo de Return que el metodo desde donde
     * se llama.
     */
    public ResponseEntity<?> getFallBackMethodProduct(Throwable e) {
        logger.error(e.getMessage());
        var product = new Product();
        product.setCreatedAt(LocalDate.now());
        product.setId(1L);
        product.setName("Camara Sony");
        product.setPrice(500.00);
        return ResponseEntity.ok(new Item(product, 5));
    }

    public CompletableFuture<?> getFallBackMethodProduct2(Throwable e) {
        return CompletableFuture.supplyAsync(() -> {
            logger.error(e.getMessage());
            var product = new Product();
            product.setCreatedAt(LocalDate.now());
            product.setId(1L);
            product.setName("Camara Sony");
            product.setPrice(500.00);
            return ResponseEntity.ok(new Item(product, 5));
        });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        logger.info("Create new product: {}", product);
        return itemService.save(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        logger.info("Update product: {}", product);
        return itemService.update(product, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        logger.info("Delete product with id: {}", id);
        itemService.delete(id);
    }

}
