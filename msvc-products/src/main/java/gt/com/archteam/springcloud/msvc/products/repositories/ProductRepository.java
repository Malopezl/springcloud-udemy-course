package gt.com.archteam.springcloud.msvc.products.repositories;

import org.springframework.data.repository.CrudRepository;

import gt.com.archteam.libs.msvc.commons.entities.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

}
