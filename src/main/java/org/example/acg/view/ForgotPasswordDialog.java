package org.example.acg.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.commons.lang3.StringUtils;
import org.example.acg.config.MsgUtil;
import org.example.acg.entity.User;
import org.example.acg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

@Scope("prototype")
@SpringComponent
public class ForgotPasswordDialog extends Dialog {
    private final TextField tfUserName = new TextField("用户名");
    private final TextField tfPhone = new TextField("手机号");
    private final TextField tfCaptcha = new TextField("验证码");
    private final PasswordField tfNewPassword = new PasswordField("新密码");

    private final Button btnConfirm = new Button("确定");
    private final Button btnCancel = new Button("取消");

    private String currentCaptchaCode = "";
    private Image imgCaptcha;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserService userService;

    public ForgotPasswordDialog() {
        setWidth("600px");
        init();
    }

    private void init() {
        tfPhone.setReadOnly(true);
        tfCaptcha.setReadOnly(true);
        tfNewPassword.setReadOnly(true);

        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        tfUserName.setWidth("80%");
        tfPhone.setWidth("80%");
        tfCaptcha.setWidth("60%");
        tfNewPassword.setWidth("80%");
        btnConfirm.setThemeName("primary");

        imgCaptcha = new Image(createCaptchaImageStream(), "验证码");
        imgCaptcha.getStyle().setMarginTop("33px");
        imgCaptcha.setHeight("40px");
        imgCaptcha.setWidth("100px");
        imgCaptcha.getStyle().setCursor("pointer");
        imgCaptcha.addClickListener(e -> refreshCaptchaLogic());

        HorizontalLayout captchaLayout = new HorizontalLayout(tfCaptcha, imgCaptcha);
        captchaLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        captchaLayout.setWidth("80%");

        HorizontalLayout btnLayout = new HorizontalLayout(btnCancel, btnConfirm);

        VerticalLayout layout = new VerticalLayout(tfUserName, tfPhone, captchaLayout, tfNewPassword, btnLayout);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);
        add(layout);

        tfUserName.addValueChangeListener(event -> {
            if (StringUtils.isNotEmpty(tfUserName.getValue())) {
                User user = userService.getUserByName(tfUserName.getValue());
                if (user == null) {
                    tfUserName.setErrorMessage("用户不存在");
                    tfUserName.setInvalid(true);
                    resetFieldsExcept(tfUserName);
                    return;
                }
                tfPhone.setReadOnly(false);
                tfCaptcha.setReadOnly(false);
                tfNewPassword.setReadOnly(false);
            } else {
                tfUserName.setErrorMessage("用户名不能为空");
                tfUserName.setInvalid(true);
                resetFieldsExcept(tfUserName);
            }
        });

        tfPhone.addValueChangeListener(event -> {
            if (StringUtils.isNotEmpty(tfPhone.getValue())) {
                User user = userService.getUserByName(tfUserName.getValue());
                if (user != null) {
                    if (!user.getPhone().equals(tfPhone.getValue())) {
                        tfPhone.setErrorMessage("手机号错误");
                        tfPhone.setInvalid(true);
                        resetFieldsExcept(tfPhone);
                        return;
                    }
                    tfCaptcha.setReadOnly(false);
                    tfNewPassword.setReadOnly(false);
                }
            } else {
                tfPhone.setErrorMessage("手机号不能为空");
                tfPhone.setInvalid(true);
                resetFieldsExcept(tfPhone);
            }
        });

        tfCaptcha.addValueChangeListener(event -> {
            String inputCaptcha = event.getValue();
            if (StringUtils.isNotEmpty(inputCaptcha)) {
                if (!inputCaptcha.equalsIgnoreCase(currentCaptchaCode)) {
                    tfCaptcha.setErrorMessage("验证码错误");
                    tfCaptcha.setInvalid(true);
                    tfNewPassword.setReadOnly(true);
                } else {
                    tfCaptcha.setInvalid(false);
                    tfCaptcha.setErrorMessage(null);
                    tfNewPassword.setReadOnly(false);
                }
            } else {
                tfCaptcha.setErrorMessage("验证码不能为空");
                tfCaptcha.setInvalid(true);
                tfNewPassword.setReadOnly(true);
            }
        });

        tfNewPassword.addValueChangeListener(event -> {
            if (StringUtils.isNotEmpty(tfNewPassword.getValue())) {
                User user = userService.getUserByName(tfUserName.getValue());

                if (user != null && passwordEncoder.matches(tfNewPassword.getValue(), user.getPassword())) {
                    tfNewPassword.setErrorMessage("新密码与旧密码相同,请确认是否还需要更改密码！");
                    tfNewPassword.setInvalid(true);
                } else {
                    tfNewPassword.setInvalid(false);
                    tfNewPassword.setErrorMessage(null);
                }
            } else {
                tfNewPassword.setErrorMessage("新密码不能为空");
                tfNewPassword.setInvalid(true);
            }
        });

        clickConfirm();
    }

    private void resetFieldsExcept(TextField exceptField) {
        if (!exceptField.equals(tfPhone)) {
            tfPhone.setReadOnly(true);
        }
        if (!exceptField.equals(tfCaptcha)) {
            tfCaptcha.setReadOnly(true);
        }
        if (!exceptField.equals(tfNewPassword)) {
            tfNewPassword.setReadOnly(true);
        }
    }

    private void refreshCaptchaLogic() {
        generateCaptchaText();
        imgCaptcha.setSrc(createCaptchaImageStream());
        tfCaptcha.clear();
        tfCaptcha.setInvalid(false);
        tfCaptcha.setErrorMessage(null);
    }

    private void clickConfirm() {
        btnConfirm.addClickListener(event -> {
            if (tfUserName.isInvalid() || tfPhone.isInvalid() || tfCaptcha.isInvalid() || tfNewPassword.isInvalid()) {
                MsgUtil.error("请填写正确的信息", Notification.Position.TOP_CENTER);
                return;
            }
            if (StringUtils.isEmpty(tfUserName.getValue()) || StringUtils.isEmpty(tfPhone.getValue()) || StringUtils.isEmpty(tfCaptcha.getValue()) || StringUtils.isEmpty(tfNewPassword.getValue())) {
                MsgUtil.error("请填写完整信息", Notification.Position.TOP_CENTER);
                return;
            }
            User user = userService.getUserByName(tfUserName.getValue());
            if (user == null) {
                MsgUtil.error("用户不存在", Notification.Position.TOP_CENTER);
                return;
            }
            String encode = passwordEncoder.encode(tfNewPassword.getValue());
            user.setPassword(encode);
            boolean i = userService.updateUser(user);
            if (i) {
                MsgUtil.success("修改密码成功", Notification.Position.TOP_CENTER);
                close();
            } else {
                MsgUtil.error("修改密码失败", Notification.Position.TOP_CENTER);
            }
        });
        btnCancel.addClickListener(event -> close());
    }

    private String generateCaptchaText() {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        this.currentCaptchaCode = sb.toString();
        return this.currentCaptchaCode;
    }

    private StreamResource createCaptchaImageStream() {
        return new StreamResource("captcha.jpg", () -> {
            String code = generateCaptchaText();
            int width = 100;
            int height = 40;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setColor(getRandColor(240, 255));
            g.fillRect(0, 0, width, height);
            g.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 24));

            for (int i = 0; i < 5; i++) {
                g.setColor(getRandColor(160, 220));
                Random random = new Random();
                int x1 = random.nextInt(width);
                int y1 = random.nextInt(height);
                int x2 = random.nextInt(width);
                int y2 = random.nextInt(height);
                g.drawLine(x1, y1, x2, y2);
            }

            for (int i = 0; i < 40; i++) {
                g.setColor(getRandColor(180, 240));
                Random random = new Random();
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                g.drawOval(x, y, 2, 2);
            }

            for (int i = 0; i < code.length(); i++) {
                g.setColor(getRandColor(40, 100));
                int x = 15 + i * 20 + new Random().nextInt(5);
                int y = 25 + new Random().nextInt(5);
                g.drawString(String.valueOf(code.charAt(i)), x, y);
            }

            g.dispose();

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(image, "jpg", out);
                return new ByteArrayInputStream(out.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return new ByteArrayInputStream(new byte[0]);
            }
        });
    }

    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) fc = 255;
        if (bc > 255) bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}