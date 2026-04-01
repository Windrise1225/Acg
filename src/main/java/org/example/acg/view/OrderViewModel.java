package org.example.acg.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.example.acg.config.enums.OrderStatusEnum;
import org.example.acg.entity.Order;
import org.example.acg.entity.User;
import org.example.acg.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Scope("prototype")
@Route(value = "order")
@SpringComponent
public class OrderViewModel extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    private OrderService orderService;

    private Grid<Order> grid = new Grid<>();
    TextField txtSearch = new TextField();

    public OrderViewModel() {
        setSizeFull();
        loadData();

        HorizontalLayout navbar = createNavbar();

        grid.addColumn(Order::getOrderNumber).setHeader("订单号");
        grid.addColumn(Order::getSumPrice).setHeader("总价（元）");
        grid.addColumn(order -> {
            String statusCode = order.getStatus();

            if (statusCode == null || statusCode.isEmpty()) {
                return "未知状态";
            }

            return Arrays.stream(OrderStatusEnum.values())
                    .filter(enumItem -> enumItem.getCode().equals(statusCode))
                    .findFirst()
                    .map(OrderStatusEnum::getValue)
                    .orElse("未知状态 (" + statusCode + ")");
        }).setHeader("状态");
        grid.addColumn(Order::getCreateUserName).setHeader("创建人");
        grid.addColumn(order -> {
            ZonedDateTime zonedDateTime = order.getCreateTime();
            return zonedDateTime != null ? zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
        }).setHeader("创建时间");
        grid.addComponentColumn(order -> {
            Button btnCancel = new Button();
            btnCancel.setIcon(VaadinIcon.CLOSE.create());

            return btnCancel;
        }).setHeader("操作").setAutoWidth( true);

        add(navbar, grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        loadData();
    }


    private void loadData() {
        User user = (User) VaadinSession.getCurrent().getAttribute("user");
        if (orderService != null) {
            List<Order> orders = orderService.listByUserId(user.getId());
            grid.setItems(orders);
        } else {
            System.err.println("OrderService is still null!");
        }
    }

    private HorizontalLayout createNavbar() {
        HorizontalLayout navbar = new HorizontalLayout();

        txtSearch.setPlaceholder("请输入订单号");
        txtSearch.setWidth("300px");

        Button btnSearch = new Button("查询", VaadinIcon.SEARCH.create());
        btnSearch.addClickListener(e -> loadData());

        navbar.add(txtSearch, btnSearch);
        return navbar;
    }
}