package org.example.acg.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.acg.config.enums.SexEnum;
import org.example.acg.entity.User;
import org.example.acg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@Route(value = "register")
@SpringComponent
// 注册页面
public class RegisterViewModel extends VerticalLayout {

    @Autowired
    private UserService userService;

    private final TextField tfName = new TextField();
    private final ComboBox<SexEnum> cbSex = new ComboBox<>();
    private final TextField tfPhone = new TextField();
    private final TextField tfEmail = new TextField();
    private final PasswordField tfPassword = new PasswordField();

    Button btnConfirm = new Button();

    VerticalLayout parentLayout = new VerticalLayout();
    VerticalLayout valueLayout = new VerticalLayout();
    HorizontalLayout btnLayout = new HorizontalLayout();


    public RegisterViewModel() {

        setSizeFull();
        // （垂直方向）居中：实现整体垂直居中
        setJustifyContentMode(JustifyContentMode.CENTER);
        // （水平方向）居中：实现整体水平居中
        setAlignItems(Alignment.CENTER);

        init();

        btnChick();

    }

    private void init() {
        cbSex.setItemLabelGenerator(SexEnum::getValue);
        cbSex.setItems(SexEnum.values());

        tfName.setLabel("用户名");
        tfName.setPlaceholder("请输入用户名");
        tfName.setWidth("40%");
        cbSex.setLabel("性别");
        cbSex.setPlaceholder("请选择性别");
        cbSex.setWidth("40%");
        tfPhone.setLabel("手机号");
        tfPhone.setPlaceholder("请输入手机号");
        tfPhone.setWidth("40%");
        tfEmail.setLabel("邮箱");
        tfEmail.setPlaceholder("请输入邮箱");
        tfEmail.setWidth("40%");
        tfPassword.setLabel("密码");
        tfPassword.setPlaceholder("请输入密码");
        tfPassword.setWidth("40%");

        valueLayout.add(tfName, cbSex, tfPhone, tfEmail, tfPassword);

        valueLayout.setAlignItems(Alignment.CENTER);

        btnConfirm.setText("注册");

        btnLayout.add(btnConfirm);

        parentLayout.add(valueLayout, btnLayout);

//        parentLayout.getStyle().setBackgroundColor("#9de8fa");
        parentLayout.setWidth("40%");
        parentLayout.setAlignItems(Alignment.CENTER);

        add(parentLayout);
    }

    private void btnChick() {
        btnConfirm.addClickListener(event -> {
            if (StringUtils.isEmpty( tfName.getValue())){
                tfName.setErrorMessage("请输入用户名！");
                tfName.setInvalid(true);
                return;
            }
            if (cbSex.getValue() == null){
                cbSex.setErrorMessage("请选择性别！");
                cbSex.setInvalid(true);
                return;
            }
            if (StringUtils.isEmpty( tfPhone.getValue())){
                tfPhone.setErrorMessage("请输入手机号！");
                tfPhone.setInvalid(true);
                return;
            }
            if (StringUtils.isEmpty( tfEmail.getValue())){
                tfEmail.setErrorMessage("请输入邮箱！");
                tfEmail.setInvalid(true);
                return;
            }
            if (StringUtils.isEmpty( tfPassword.getValue())){
                tfPassword.setErrorMessage("请输入密码！");
                tfPassword.setInvalid(true);
                return;
            }
            User user = userService.getUserByName(tfName.getValue());
            if (user != null) {
                tfName.setErrorMessage("用户已存在！");
                tfName.setInvalid(true);
                return;
            }

            User userNew = new User();

            userNew.setName(tfName.getValue());
            userNew.setSex(cbSex.getValue().getCode());
            userNew.setPhone(tfPhone.getValue());
            userNew.setEmail(tfEmail.getValue());
            userNew.setPassword(tfPassword.getValue());

            userService.insertUser( userNew);

            Notification success = new Notification();
            success.setText("注册成功！");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            success.open();
            success.setDuration(2000);
            success.setPosition(Notification.Position.TOP_CENTER);
            clearTf();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
    }

    private void clearTf(){
        tfName.clear();
        cbSex.clear();
        tfPhone.clear();
        tfEmail.clear();
        tfPassword.clear();
    }

}
