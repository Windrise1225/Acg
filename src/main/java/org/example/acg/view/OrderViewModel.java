package org.example.acg.view;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.example.acg.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@Route(value = "order")
@SpringComponent
public class OrderViewModel extends VerticalLayout {

    @Autowired
    private OrderService orderService;


    public OrderViewModel() {

    }
}
