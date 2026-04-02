package org.example.acg.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import org.example.acg.config.MsgUtil;
import org.example.acg.config.enums.SexEnum;
import org.example.acg.entity.User;
import org.example.acg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Scope("prototype")
@Route(value = "userManagement")
@SpringComponent
public class UserManagementViewModel extends VerticalLayout {


    Grid<User> grid = new Grid<>();

    @Autowired
    private UserService userService;

    public UserManagementViewModel() {
        createGrid();
        init();
    }

    private void init() {
        setSizeFull();

        HorizontalLayout layout = new HorizontalLayout();

        TextField tfUsername = new TextField();
        tfUsername.setWidth("200px");
        tfUsername.setPlaceholder("请输入用户名");

        Button btnSearch = new Button("查询");
        btnSearch.setIcon(VaadinIcon.SEARCH.create());
        btnSearch.setThemeName("primary");

        btnSearch.addClickListener(e -> {
            String value = tfUsername.getValue();
            if (value != null && !value.isEmpty()){
                grid.setItems(userService.likeListByName(value));
            }
        });

        Button btnDelete = new Button("删除");
        btnDelete.setIcon(VaadinIcon.TRASH.create());
        btnDelete.getStyle().setBackgroundColor("#e37878")
                        .setColor("white");
        btnDelete.addClickListener(e -> {
            User user = grid.asSingleSelect().getValue();
            if (user == null){
                MsgUtil.warning("请选择要删除的用户！", Notification.Position.TOP_CENTER);
                return;
            }
            User userById = userService.getUserById(user.getId());
            if (userById !=  null){
                if (userById.getIsDelete() == 1){
                    MsgUtil.warning("此用户已被删除！", Notification.Position.TOP_CENTER);
                    return;
                }
            }
            ConfirmDialog confirm = new ConfirmDialog();
            confirm.setHeader("提示");
            confirm.setText("确定要删除此用户吗?");
            confirm.setConfirmText("确认");
            confirm.setCancelText("取消");
            confirm.setCancelable( true);
            confirm.setOpened(true);
            confirm.addConfirmListener(log -> {
                if (userService.deleteUser(user.getId())) {
                    MsgUtil.success("删除成功！", Notification.Position.TOP_CENTER);
                    refreshGrid();
                }
            });
        });

        Button btnReply = new Button("恢复用户");
        btnReply.setIcon(VaadinIcon.REPLY.create());
        btnReply.getStyle().setColor("white");
        btnReply.getStyle().setBackgroundColor("#4caf50");
        btnReply.addClickListener(e -> {
            User user = grid.asSingleSelect().getValue();
            if (user == null){
                MsgUtil.warning("请选择要恢复的用户！", Notification.Position.TOP_CENTER);
                return;
            }
            User userById = userService.getUserById(user.getId());
            if (userById !=  null){
                if (userById.getIsDelete() == 0){
                    MsgUtil.warning("此用户未被删除！", Notification.Position.TOP_CENTER);
                    return;
                }
            }
            ConfirmDialog confirm = new ConfirmDialog();
            confirm.setHeader("提示");
            confirm.setText("确定要恢复此用户吗?");
            confirm.setConfirmText("确认");
            confirm.setCancelText("取消");
            confirm.setCancelable( true);
            confirm.setOpened(true);
            confirm.addConfirmListener(log -> {
                if (userService.replyUser(user.getId())) {
                    MsgUtil.success("恢复成功!", Notification.Position.TOP_CENTER);
                    refreshGrid();
                }
            });
        });

        layout.add(tfUsername, btnSearch, btnDelete, btnReply);

        add(layout, grid);
    }

    @PostConstruct
    private void initGrid() {
        refreshGrid();
    }

    private void refreshGrid() {
        if (userService != null) {
            grid.setItems(userService.list());
        }
    }

    private void createGrid() {
        grid.addColumn(User::getName).setHeader("用户名").setAutoWidth(true);
        grid.addColumn(user -> {
                    String sex = user.getSex();
                    SexEnum sexEnum = SexEnum.fromCode(sex);
                    assert sexEnum != null;
                    return sexEnum.getValue() != null ? sexEnum.getValue() : "";
                })
                .setHeader("性别").setAutoWidth(true);
        grid.addColumn(User::getPhone).setHeader("手机号").setAutoWidth(true);
        grid.addColumn(User::getEmail).setHeader("邮箱").setAutoWidth(true);
        grid.addColumn(user -> {
                    ZonedDateTime createTime = user.getCreateTime();
                    if (createTime == null) {
                        return "";
                    }
                    return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
        ).setHeader("创建时间").setAutoWidth(true);
        grid.addColumn(user -> {
            int isDelete = user.getIsDelete();
            if (isDelete == 0){
                return "正常使用";
            }else if (isDelete == 1){
                return "已删除";
            }
            return "未知";
        }).setHeader("状态").setAutoWidth( true);
    }
}
