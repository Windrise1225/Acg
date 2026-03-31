package org.example.acg.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.acg.config.enums.OrderStatusEnum;
import org.example.acg.entity.Order;
import org.example.acg.service.OrderService;
import org.example.acg.view.Product.ProductDialog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;


@Scope("prototype")
@Route(value = "orderManagement")
@SpringComponent
public class OrderManagementViewModel extends VerticalLayout implements BeforeEnterObserver {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ObjectProvider<OrderEditDialog> orderEditDialog;

    private Grid<Order> grid = new Grid<>();
    Button btnSearch = new Button("查询", VaadinIcon.SEARCH.create());
    TextField txtSearch = new TextField();

    public OrderManagementViewModel() {
        setSizeFull();

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
        grid.addColumn(Order::getLastModifiedUserName).setHeader("最后修改人");
        grid.addColumn(order -> {
            ZonedDateTime zonedDateTime = order.getLastModifiedTime();
            return zonedDateTime != null ? zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
        }).setHeader("最后修改时间");
        grid.addComponentColumn(order -> {
            Button btnEdit = new Button();
            btnEdit.setIcon(VaadinIcon.EDIT.create());
            btnEdit.addClickListener(e -> {
                OrderEditDialog dialog = orderEditDialog.getIfAvailable();
                if (dialog == null) return;
                Order value = grid.asSingleSelect().getValue();
                dialog.setData(value);

                dialog.setOnSuccess(() -> {
                    loadData();
                });
                dialog.open();
            });

            return btnEdit;
        }).setHeader("操作").setAutoWidth( true);

        add(navbar, grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        loadData();
    }


    private void loadData() {
        if (orderService != null) {
            List<Order> orders = orderService.list();
            grid.setItems(orders);
        } else {
            System.err.println("OrderService is still null!");
        }
    }


    private HorizontalLayout createNavbar() {
        HorizontalLayout navbar = new HorizontalLayout();

        txtSearch.setPlaceholder("请输入订单号");
        txtSearch.setWidth("300px");

        btnSearch.addClickListener(e -> {
            if (StringUtils.isNotEmpty(txtSearch.getValue())){
                List<Order> orders = orderService.likeByOrderNumber(txtSearch.getValue());
                grid.setItems(orders);
            }
        });

        navbar.add(txtSearch, btnSearch);
        return navbar;
    }
}
