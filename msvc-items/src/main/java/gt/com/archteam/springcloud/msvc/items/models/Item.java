package gt.com.archteam.springcloud.msvc.items.models;

import gt.com.archteam.libs.msvc.commons.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private Product product;
    private int quantity;

    public Double getTotal() {
        return product.getPrice() * quantity;
    }
}
