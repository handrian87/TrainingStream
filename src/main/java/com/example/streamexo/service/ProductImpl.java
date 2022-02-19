package com.example.streamexo.service;

import com.example.streamexo.model.Customer;
import com.example.streamexo.model.Order;
import com.example.streamexo.model.Product;
import com.example.streamexo.repo.CustomerRepo;
import com.example.streamexo.repo.OrderRepo;
import com.example.streamexo.repo.ProductRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductImpl {
    private ProductRepo productRepo;
    private OrderRepo orderRepo;
    private CustomerRepo customerRepo;
    public ProductImpl(ProductRepo productRepo, OrderRepo orderRepo, CustomerRepo customerRepo){
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
    }
    // Obtains a list of products belongs to category "Books" with price>100
    /*
    * This is obviously a filtering logic, the output should fulfill the two filtering requirements.
    * So, you can apply 2 filter() operations to obtain the result.
    * */
    public List<Product>BookWithPriceCondition(){
        return (productRepo.findAll()
                .stream()
                .filter(l -> l.getCategory().equals("Books"))
                .filter(p -> p.getPrice() > 100))
                .collect(Collectors.toList());
    }

    @GetMapping("/stream")
    public void displayList(){
        List<Product> t = BookWithPriceCondition();
        t.forEach(System.out::println);
    }

    //Obtain a list of order with prodcts belong to category "Baby"
    /* You need to start from the data flow from the order entities and then check if order’s products belong
    * to the category “Baby”. Hence, the filter logic looks
    * into the products stream of each order record and use anyMatch() to determine if any product fulfill the criteria.
    */
    @GetMapping("/productId")
    public List<Order> listOfCategoryBaby(){
        List<Order> lisOrder =
                orderRepo.findAll().stream()
                        .filter(o -> o.getProducts()
                                .stream()
                                .anyMatch(p->p.getCategory().equalsIgnoreCase("Baby")))
                        .collect(Collectors.toList());
        lisOrder.forEach(System.out::println);
        return lisOrder;
    }

    //Obtain a list of product with category="Toys" and then apply 10% discount
    /*
    * After you’ve obtained a product list with a category that belongs to “Toys” using filter(), you can then apply a
    * 10% discount to the product price by using map().
    * */
    @GetMapping("/listCatToy")
    public List<Product> listCategoryToysAndDiscount(){
        List<Product> lp = productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Toys"))
                .map(num -> num.withPrice(num.getPrice() * 0.9))
                .collect(Collectors.toList());
        lp.forEach(System.out::println);
        return lp;
    }

    //Obtain a list of products ordered by customer of tier 2 between 2 dates
    @GetMapping("/listProdOrdered")
    public List<Product> listProductOrderedByCustomrTierTwo(){
        List<Product>order = orderRepo.findAll()
                .stream()
                .filter(o -> o.getCustomer().getTier() == 2)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021,2,1)) >= 0)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021,4,1)) <= 0)
                .flatMap(oo -> oo.getProducts().stream().distinct())
                .collect(Collectors.toList());
        order.forEach(System.out::println);
        return order;
    }

    // Obtain the cheapest products of Books category.
    @GetMapping("/cheapest")
    public Product cheapestProductBookCatg(){
        Product cheapest = productRepo.findAll()
                .stream()
                .filter(cat -> cat.getCategory().equalsIgnoreCase("Books"))
                .sorted(Comparator.comparing(Product::getPrice))
                .findFirst()
                .get();
        System.out.println("Result from ressearch " +cheapest);
        return cheapest;
    }
}
