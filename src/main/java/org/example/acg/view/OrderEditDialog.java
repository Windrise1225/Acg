package org.example.acg.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import lombok.Setter;
import org.example.acg.config.enums.OrderStatusEnum;
import org.example.acg.entity.Order;
import org.example.acg.entity.User;
import org.example.acg.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Scope("prototype")
@Component
public class OrderEditDialog extends Dialog {

    @Autowired
    private OrderService orderService;

    // 定义UI组件
    TextField tfOrderNumber = new TextField();
    TextField tfTotalPrice = new TextField();
    TextField tfCreateTime = new TextField();
    TextField tfCreateUser = new TextField();
    ComboBox<OrderStatusEnum> cbStatus = new ComboBox<>();

    Button btnCancel = new Button();
    Button btnConfirm = new Button();

    private Order editingOrder = null;

    @Setter
    private Runnable onSuccess;

    public OrderEditDialog() {
        init();
    }

    private void init() {
        // 关闭对话框的行为
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        // 主布局
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("400px");
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);

        // 按钮布局
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnCancel.setText("取消");
        btnConfirm.setText("确认");
        btnLayout.add(btnCancel, btnConfirm);

        // 设置字段标签和样式
        tfOrderNumber.setLabel("订单号");
        tfOrderNumber.setWidth("80%");
        tfOrderNumber.setReadOnly(true); // 订单号不可修改

        tfTotalPrice.setLabel("总价（元）");
        tfTotalPrice.setWidth("80%");
        tfTotalPrice.setReadOnly(true); // 总价不可修改

        tfCreateTime.setLabel("创建时间");
        tfCreateTime.setWidth("80%");
        tfCreateTime.setReadOnly(true); // 创建时间不可修改

        tfCreateUser.setLabel("创建人");
        tfCreateUser.setWidth("80%");
        tfCreateUser.setReadOnly(true); // 创建人不可修改

        cbStatus.setLabel("状态");
        cbStatus.setItems(OrderStatusEnum.values());
        cbStatus.setItemLabelGenerator(OrderStatusEnum::getValue);
        cbStatus.setWidth("80%");
        cbStatus.setRequired(true);
        cbStatus.setErrorMessage("请选择状态");

        layout.add(tfOrderNumber, tfTotalPrice, tfCreateTime, tfCreateUser, cbStatus, btnLayout);
        add(layout);

        clickBtn();
    }

    private void clickBtn() {
        btnConfirm.addClickListener(event -> {
            if (cbStatus.getValue() == null) {
                cbStatus.setErrorMessage("请选择状态");
                cbStatus.setInvalid(true);
                return;
            }

            if (editingOrder != null && editingOrder.getId() != null) {

                String status = cbStatus.getValue().getCode();

                editingOrder.setLastModifiedTime(ZonedDateTime.now());
                VaadinSession session = VaadinSession.getCurrent();
                User user = (User) session.getAttribute("user");
                editingOrder.setLastModifiedUserName(user.getName());
                editingOrder.setStatus(status);

                orderService.updateStatus(editingOrder);

                if (onSuccess != null) {
                    onSuccess.run();
                }
                close();
            }
        });

        btnCancel.addClickListener(event -> {
            close();
        });
    }

    /**
     * 设置要编辑的订单数据
     * @param order 订单实体
     */
    public void setData(Order order) {
        if (order != null) {
            setHeaderTitle("修改订单: " + order.getOrderNumber());
            editingOrder = order;

            // 填充表单数据
            tfOrderNumber.setValue(order.getOrderNumber());
            tfTotalPrice.setValue(String.valueOf(order.getSumPrice()));
            tfCreateTime.setValue(order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            tfCreateUser.setValue(order.getCreateUserName());

            cbStatus.setValue(OrderStatusEnum.fromCode( order.getStatus()));

        }
    }

}