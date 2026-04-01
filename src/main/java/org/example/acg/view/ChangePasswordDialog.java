package org.example.acg.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.acg.config.MsgUtil;
import org.example.acg.entity.User;
import org.example.acg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Scope("prototype")
@SpringComponent
public class ChangePasswordDialog extends Dialog {

    private final PasswordField tfOldPassword = new PasswordField("旧密码");
    private final PasswordField tfNewPassword = new PasswordField("新密码");
    private final PasswordField tfConfirmPassword = new PasswordField("确认密码");

    private final Button btnConfirm = new Button("确定");
    private final Button btnCancel = new Button("取消");

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User currentUser;

    @Autowired
    private UserService userService;

    public ChangePasswordDialog() {
        setWidth("600px");
        init();
    }

    private void init() {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        tfOldPassword.setWidth("80%");
        tfNewPassword.setWidth("80%");
        tfConfirmPassword.setWidth("80%");
        btnConfirm.setThemeName("primary");


        HorizontalLayout btnLayout = new HorizontalLayout(btnCancel, btnConfirm);

        VerticalLayout layout = new VerticalLayout(tfOldPassword, tfNewPassword, tfConfirmPassword, btnLayout);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);
        add(layout);

        initLogic();
    }

    private void initLogic() {
        tfOldPassword.addValueChangeListener(e -> {
            String oldPasswordValue = tfOldPassword.getValue();
            if (StringUtils.isNotEmpty(oldPasswordValue) && currentUser != null) {
                boolean isMatch = passwordEncoder.matches(oldPasswordValue, currentUser.getPassword());
                if (!isMatch) {
                    tfOldPassword.setErrorMessage("旧密码输入错误");
                    tfOldPassword.setInvalid(true);
                } else {
                    tfOldPassword.setInvalid(false);
                }
            }
        });

        tfConfirmPassword.addValueChangeListener(e -> {
            String newPasswordValue = tfNewPassword.getValue();
            String confirmPasswordValue = tfConfirmPassword.getValue();
            if (StringUtils.isNotEmpty(newPasswordValue) && StringUtils.isNotEmpty(confirmPasswordValue)) {
                if (!newPasswordValue.equals(confirmPasswordValue)) {
                    tfConfirmPassword.setErrorMessage("两次输入的密码不一致");
                    tfConfirmPassword.setInvalid(true);
                } else {
                    tfConfirmPassword.setInvalid(false);
                }
            }
        });

        btnConfirm.addClickListener(e -> handleConfirm());
        btnCancel.addClickListener(e -> close());

        tfNewPassword.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, event -> handleConfirm());
        tfConfirmPassword.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, event -> handleConfirm());
    }

    private void handleConfirm() {
        if (tfOldPassword.isInvalid() || tfNewPassword.isInvalid() || tfConfirmPassword.isInvalid()) {
            return;
        }

        String oldPassword = tfOldPassword.getValue();
        String newPassword = tfNewPassword.getValue();

        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
            MsgUtil.error("密码不能为空", Notification.Position.TOP_CENTER);
            return;
        }

        boolean isOldPasswordCorrect = passwordEncoder.matches(oldPassword, currentUser.getPassword());
        if (!isOldPasswordCorrect) {
            tfOldPassword.setInvalid(true);
            tfOldPassword.setErrorMessage("旧密码验证失败");
            return;
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        boolean isSuccess = userService.updateUser(currentUser);

        if (isSuccess) {
            MsgUtil.success("密码修改成功", Notification.Position.TOP_CENTER);
            close();
        } else {
            MsgUtil.error("修改失败，请稍后重试", Notification.Position.TOP_CENTER);
        }
    }

    public void setData(User user) {
        this.currentUser = user;
        tfOldPassword.setValue("");
        tfNewPassword.setValue("");
        tfConfirmPassword.setValue("");
        tfOldPassword.setInvalid(false);
        tfNewPassword.setInvalid(false);
        tfConfirmPassword.setInvalid(false);
    }
}