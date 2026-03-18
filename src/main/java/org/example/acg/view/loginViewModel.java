package org.example.acg.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
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
// 登录页面
public class loginViewModel extends VerticalLayout {

    @Autowired
    private UserService userService;

    private final TextField tfName = new TextField();
    private final PasswordField tfPassword = new PasswordField();
    Span tip = new Span();

    Button btnConfirm = new Button();

    VerticalLayout parentLayout = new VerticalLayout();
    VerticalLayout valueLayout = new VerticalLayout();
    HorizontalLayout btnLayout = new HorizontalLayout();


    public loginViewModel() {

        setSizeFull();
        // （垂直方向）居中：实现整体垂直居中
        setJustifyContentMode(JustifyContentMode.CENTER);
        // （水平方向）居中：实现整体水平居中
        setAlignItems(Alignment.CENTER);

        init();

        btnChick();

    }

    private void init() {

        tfName.setLabel("用户名");
        tfName.setPlaceholder("请输入用户名");
        tfName.setWidth("40%");
        tfPassword.setLabel("密码");
        tfPassword.setPlaceholder("请输入密码");
        tfPassword.setWidth("40%");

        valueLayout.add(tfName, tfPassword);

        valueLayout.setAlignItems(Alignment.CENTER);

        btnConfirm.setText("登录");

        tip.setText("没有账户？前往注册。");
        tip.getStyle()
                .set("color", "#6c5ce7")       // 链接颜色
                .set("cursor", "pointer")      // 鼠标悬停变手型
                .set("text-decoration", "underline");

        btnLayout.add(btnConfirm);

        parentLayout.add(valueLayout, btnLayout,  tip);

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
            }
            if (StringUtils.isEmpty( tfPassword.getValue())){
                tfPassword.setErrorMessage("请输入密码！");
                tfPassword.setInvalid(true);
            }
            User user = userService.getUserByName(tfName.getValue());
            if (user != null) {
                if (user.getPassword().equals(tfPassword.getValue())){
                    VaadinSession.getCurrent().setAttribute("user", user);
                    getUI().ifPresent(ui -> ui.navigate(""));
                }else {
                    tfPassword.setErrorMessage("密码错误！");
                    tfPassword.setInvalid(true);
                }
            }else {
                tfName.setErrorMessage("用户不存在！");
                tfName.setInvalid(true);
            }
        });

        tip.addClickListener(event -> {
            UI.getCurrent().navigate("register");
        });
    }

}
