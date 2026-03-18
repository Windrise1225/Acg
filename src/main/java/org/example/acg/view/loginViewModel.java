package org.example.acg.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.acg.entity.User;
import org.example.acg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@Route(value = "login")
@SpringComponent
public class loginViewModel extends VerticalLayout {

    @Autowired
    private UserService userService;

    // 组件定义
    private final TextField tfName = new TextField();
    private final PasswordField tfPassword = new PasswordField();
    private final Span tipLink = new Span();
    private final Button btnConfirm = new Button("Log in");

    // 颜色常量
    private static final String PRIMARY_COLOR = "#764ba2";
    private static final String SECONDARY_COLOR = "#667eea";
    private static final String BG_COLOR_PAGE = "#f8f9fa";

    public loginViewModel() {
        setSizeFull();
        getStyle().set("background", BG_COLOR_PAGE);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        initUI();
        bindEvents();
    }

    private void initUI() {
        Div card = new Div();
        card.setWidth("400px");
        card.getStyle()
                .set("background", "white")
                .set("backdrop-filter", "blur(16px)")
                .set("-webkit-backdrop-filter", "blur(16px)")
                .set("border", "1px solid rgba(255, 255, 255, 0.8)")
                .set("box-shadow", "0 8px 32px 0 rgba(31, 38, 135, 0.15)")
                .set("border-radius", "24px")
                .set("padding", "40px 30px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "20px")
                .set("align-items", "center");


        btnConfirm.setWidth("100%");
        btnConfirm.setHeight("48px");
        btnConfirm.getStyle()
                .set("border-radius", "12px")
                .set("font-weight", "600")
                .set("font-size", "16px")
                .set("background", "linear-gradient(135deg, " + SECONDARY_COLOR + " 0%, " + PRIMARY_COLOR + " 100%)")
                .set("color", "white")
                .set("border", "none")
                .set("box-shadow", "0 4px 15px rgba(118, 75, 162, 0.3)")
                .set("cursor", "pointer");

        tipLink.setText("Don't have an account? Go to register");
        tipLink.getStyle()
                .set("color", PRIMARY_COLOR)
                .set("cursor", "pointer")
                .set("font-size", "14px")
                .set("margin-top", "10px");

        // 链接 hover 效果
        tipLink.getElement().addEventListener("mouseenter", e -> tipLink.getStyle().set("color", SECONDARY_COLOR));
        tipLink.getElement().addEventListener("mouseleave", e -> tipLink.getStyle().set("color", PRIMARY_COLOR));

        addLabeledField(card, "Username", tfName, VaadinIcon.USER);
        addLabeledField(card, "Password", tfPassword, VaadinIcon.LOCK);

        card.add(btnConfirm);

        card.add(tipLink);

        add(card);
    }

    /**
     * 添加一个“标签在左，输入框在右”的字段行
     */
    private void addLabeledField(Div container, String labelText, TextField field, VaadinIcon icon) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setSpacing(true);

        // 左侧标签
        Span label = new Span(labelText + ":");
        label.getStyle()
                .set("width", "80px")          // 固定标签宽度，保证对齐
                .set("flex-shrink", "0")       // 不被压缩
                .set("font-weight", "500")
                .set("color", "#555")
                .set("text-align", "right")    // 标签右对齐，更美观
                .set("padding-right", "10px");

        // 右侧输入框
        field.setWidthFull();
        field.setPlaceholder("Please enter " + labelText);
        field.setPrefixComponent(icon.create());

        // 恢复 Vaadin 默认样式（不自定义 background/border）
        field.getStyle()
                .set("font-size", "15px");

        row.add(label, field);
        container.add(row);
    }

    // 重载用于 PasswordField
    private void addLabeledField(Div container, String labelText, PasswordField field, VaadinIcon icon) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setSpacing(true);

        Span label = new Span(labelText + ":");
        label.getStyle()
                .set("width", "80px")
                .set("flex-shrink", "0")
                .set("font-weight", "500")
                .set("color", "#555")
                .set("text-align", "right")
                .set("padding-right", "10px");

        field.setWidthFull();
        field.setPlaceholder("Please enter " + labelText);
        field.setPrefixComponent(icon.create());

        field.getStyle().set("font-size", "15px");

        row.add(label, field);
        container.add(row);
    }

    private void bindEvents() {
        btnConfirm.addClickListener(event -> login());
        tfPassword.addKeyPressListener(Key.ENTER, event -> login());
        tipLink.addClickListener(event -> UI.getCurrent().navigate("register"));
    }

    private void login() {
        tfName.setInvalid(false);
        tfPassword.setInvalid(false);

        if (StringUtils.isEmpty(tfName.getValue())) {
            tfName.setErrorMessage("Please enter your username!");
            tfName.setInvalid(true);
            return;
        }
        if (StringUtils.isEmpty(tfPassword.getValue())) {
            tfPassword.setErrorMessage("Please enter your password!");
            tfPassword.setInvalid(true);
            return;
        }

        User user = userService.getUserByName(tfName.getValue());
        if (user != null) {
            if (user.getPassword().equals(tfPassword.getValue())) {
                VaadinSession.getCurrent().setAttribute("user", user);
                getUI().ifPresent(ui -> ui.navigate(""));
            } else {
                tfPassword.setErrorMessage("Incorrect password!");
                tfPassword.setInvalid(true);
            }
        } else {
            tfName.setErrorMessage("The user does not exist!");
            tfName.setInvalid(true);
        }
    }
}