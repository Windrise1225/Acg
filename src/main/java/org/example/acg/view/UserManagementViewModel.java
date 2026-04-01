package org.example.acg.view;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.context.annotation.Scope;

import java.awt.*;


@Scope("prototype")
@Route(value = "login")
@SpringComponent
public class UserManagementViewModel extends VerticalLayout {



    public UserManagementViewModel() {
        init();
    }
    private void init() {
        HorizontalLayout layout = new HorizontalLayout();

        TextField tfUsername = new TextField("用户名");
        tfUsername.setWidth("20%");
        tfUsername.setPlaceholder("请输入用户名");

        Button btnSearch = new Button("查询");
    }
}
