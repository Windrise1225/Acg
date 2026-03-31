package org.example.acg.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.acg.config.MsgUtil;
import org.example.acg.config.enums.SexEnum;
import org.example.acg.entity.User;
import org.example.acg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@Route(value = "register")
@SpringComponent
public class RegisterViewModel extends VerticalLayout {

    @Autowired
    private UserService userService;

    // 组件定义
    private final TextField tfName = new TextField();
    private final ComboBox<SexEnum> cbSex = new ComboBox<>();
    private final TextField tfPhone = new TextField();
    private final TextField tfEmail = new TextField();
    private final PasswordField tfPassword = new PasswordField();

    private final Button btnConfirm = new Button("Register");
    private final Span backToLoginLink = new Span();

    // 颜色常量 (与登录页保持一致)
    private static final String PRIMARY_COLOR = "#764ba2";
    private static final String SECONDARY_COLOR = "#667eea";
    private static final String BG_COLOR_PAGE = "#f8f9fa";

    public RegisterViewModel() {
        setSizeFull();
        getStyle().set("background", BG_COLOR_PAGE);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        initUI();
        bindEvents();
    }

    private void initUI() {
        Div card = new Div();

        card.setWidth("450px");
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
                .set("gap", "18px")
                .set("align-items", "center");

        cbSex.setItemLabelGenerator(SexEnum::getValue);
        cbSex.setItems(SexEnum.values());
        cbSex.setPlaceholder("Please select gender");

        addLabeledField(card, "Username", tfName, VaadinIcon.USER);
        addLabeledField(card, "Gender", cbSex, VaadinIcon.USER_HEART);
        addLabeledField(card, "Phone", tfPhone, VaadinIcon.PHONE);
        addLabeledField(card, "Email", tfEmail, VaadinIcon.ENVELOPE);
        addLabeledField(card, "Password", tfPassword, VaadinIcon.LOCK);

        // 5. 配置按钮样式
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

        // 6. 底部返回登录链接
        backToLoginLink.setText("Do you already have an account? Go to login");
        backToLoginLink.getStyle()
                .set("color", PRIMARY_COLOR)
                .set("cursor", "pointer")
                .set("font-size", "14px")
                .set("margin-top", "5px");

        // 链接 hover 效果
        backToLoginLink.getElement().addEventListener("mouseenter", e ->
                backToLoginLink.getStyle().set("color", SECONDARY_COLOR));
        backToLoginLink.getElement().addEventListener("mouseleave", e ->
                backToLoginLink.getStyle().set("color", PRIMARY_COLOR));

        card.add(btnConfirm);
        card.add(backToLoginLink);

        add(card);
    }

    /**
     * 添加一个“标签在左，输入框在右”的字段行 (TextField 版本)
     */
    private void addLabeledField(Div container, String labelText, TextField field, VaadinIcon icon) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setSpacing(true);

        // 左侧标签
        Span label = new Span(labelText + ":");
        label.getStyle()
                .set("width", "80px")
                .set("flex-shrink", "0")
                .set("font-weight", "500")
                .set("color", "#555")
                .set("text-align", "right")
                .set("padding-right", "10px");

        // 右侧输入框
        field.setWidthFull();
        field.setPlaceholder("Please enter " + labelText);
        if (icon != null) {
            field.setPrefixComponent(icon.create());
        }
        field.getStyle().set("font-size", "15px");

        row.add(label, field);
        container.add(row);
    }

    /**
     * 添加一个“标签在左，输入框在右”的字段行 (PasswordField 版本)
     */
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
        if (icon != null) {
            field.setPrefixComponent(icon.create());
        }
        field.getStyle().set("font-size", "15px");

        row.add(label, field);
        container.add(row);
    }

    /**
     * 添加一个“标签在左，输入框在右”的字段行 (ComboBox 版本)
     */
    private void addLabeledField(Div container, String labelText, ComboBox<SexEnum> combo, VaadinIcon icon) {
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

        combo.setWidthFull();
        // ComboBox 不需要 placeholder 如果已经有 item，但为了统一可以保留
        combo.setPlaceholder("Please select " + labelText);
        if (icon != null) {
            combo.setPrefixComponent(icon.create());
        }
        combo.getStyle().set("font-size", "15px");

        row.add(label, combo);
        container.add(row);
    }

    private void bindEvents() {
        // 注册按钮点击
        btnConfirm.addClickListener(event -> handleRegister());

        // 密码框回车注册
        tfPassword.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, event -> handleRegister());

        // 返回登录链接点击
        backToLoginLink.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("login")));
    }

    // ================= 原有逻辑保持不变 =================

    private void handleRegister() {
        // 重置错误状态
        tfName.setInvalid(false);
        cbSex.setInvalid(false);
        tfPhone.setInvalid(false);
        tfEmail.setInvalid(false);
        tfPassword.setInvalid(false);

        if (StringUtils.isEmpty(tfName.getValue())) {
            tfName.setErrorMessage("Please enter your username!");
            tfName.setInvalid(true);
            return;
        }
        if (cbSex.getValue() == null) {
            cbSex.setErrorMessage("Please select gender!");
            cbSex.setInvalid(true);
            return;
        }
        if (StringUtils.isEmpty(tfPhone.getValue())) {
            tfPhone.setErrorMessage("Please enter your phone number!");
            tfPhone.setInvalid(true);
            return;
        }
        if (StringUtils.isEmpty(tfEmail.getValue())) {
            tfEmail.setErrorMessage("Please enter your email!");
            tfEmail.setInvalid(true);
            return;
        }
        if (StringUtils.isEmpty(tfPassword.getValue())) {
            tfPassword.setErrorMessage("Please input a password!");
            tfPassword.setInvalid(true);
            return;
        }

        User user = userService.getUserByName(tfName.getValue());
        if (user != null) {
            tfName.setErrorMessage("The user already exists!");
            tfName.setInvalid(true);
            return;
        }

        User userNew = new User();
        userNew.setName(tfName.getValue());
        userNew.setSex(cbSex.getValue().getCode());
        userNew.setPhone(tfPhone.getValue());
        userNew.setEmail(tfEmail.getValue());
        userNew.setPassword(tfPassword.getValue());

        userService.insertUser(userNew);

        MsgUtil.success("Registered successfully", Notification.Position.TOP_CENTER);

        clearTf();
        getUI().ifPresent(ui -> ui.navigate("login"));
    }

    private void clearTf() {
        tfName.clear();
        cbSex.clear();
        tfPhone.clear();
        tfEmail.clear();
        tfPassword.clear();
    }
}