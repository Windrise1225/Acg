package org.example.acg.view.Product;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import org.example.acg.config.MsgUtil;
import org.example.acg.entity.Product;
import org.example.acg.service.ProductService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;

@SpringComponent
@Scope("prototype")
@Route("product")
public class ProductViewModel extends VerticalLayout {

    Button btnAdd = new Button();
    Button btnEdit = new Button();
    Button btnDelete = new Button();
    Button btnRefresh = new Button();
    TextField tfSearch = new TextField();

    Grid<Product> grid = new Grid<>();

    @Autowired
    private ProductService productService;
    @Autowired
    private ObjectProvider<ProductDialog> goodsDialog;

    public ProductViewModel() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "#f8f9fa");

        Div mainContainer = new Div();
        mainContainer.setWidth("90%");
        mainContainer.setMaxWidth("1500px");
        mainContainer.setHeight("100%");
        mainContainer.getStyle()
                .set("background", "white")
                .set("border-radius", "24px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.05)")
                .set("margin", "20px auto")
                .set("overflow", "hidden");

        HorizontalLayout headerLayout = createHorizontalLayout();

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        createGrid();

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setPadding(false);
        contentLayout.setSpacing(false);
        contentLayout.setSizeFull();
        contentLayout.setAlignItems(Alignment.CENTER);

        contentLayout.add(headerLayout, grid);

        mainContainer.add(contentLayout);

        add(mainContainer);

        init();
        clickBtn();
    }

    @PostConstruct
    private void init() {
        refreshGrid();
    }

    public void refreshGrid() {
        if (productService != null) {
            List<Product> list = productService.list();
            grid.setItems(list);
        }
    }

    private void clickBtn() {
        tfSearch.addKeyDownListener(Key.ENTER, event -> {
            if ( tfSearch.getValue() == null || tfSearch.getValue().isEmpty()){
                refreshGrid();
            }
            List<Product> productList = productService.listProductLikeByName(tfSearch.getValue());
            if (productList != null) {
                grid.setItems(productList);
            }
        });

        tfSearch.addValueChangeListener(event -> {
            List<Product> productList = productService.listProductLikeByName(tfSearch.getValue());
            grid.setItems(productList);
        });

        btnAdd.addClickListener(event -> {
            ProductDialog dialog = goodsDialog.getIfAvailable();
            if (dialog == null) return;
            dialog.setData(null);

            dialog.setOnSuccess(() -> {
                refreshGrid();
                MsgUtil.success("添加成功！", Notification.Position.TOP_CENTER);
            });

            dialog.open();

        });

        btnEdit.addClickListener(event -> {
            ProductDialog dialog = goodsDialog.getIfAvailable();
            if (dialog == null) return;

            Product value = grid.asSingleSelect().getValue();
            if (value != null) {
                dialog.setData(value);

                dialog.setOnSuccess(() -> {
                    refreshGrid();
                    MsgUtil.success("修改成功！", Notification.Position.TOP_CENTER);
                });

                dialog.open();

            }else {
                MsgUtil.warning("请选择商品再进行修改！", Notification.Position.TOP_CENTER);
            }
        });

        btnRefresh.addClickListener(event -> {
            tfSearch.clear();
            refreshGrid();
        });

        btnDelete.addClickListener(event -> {
            Product value = grid.asSingleSelect().getValue();
            if (value != null) {
                ConfirmDialog confirm = new ConfirmDialog();
                confirm.setHeader("提示");
                confirm.setText("确定要删除此商品吗?");
                confirm.setConfirmText("确认");
                confirm.setCancelText("取消");
                confirm.setCancelable( true);
                confirm.setOpened(true);
                confirm.addConfirmListener(log -> {
                    if (productService.deleteProduct(value.getId())) {
                        refreshGrid();
                        MsgUtil.success("删除成功", Notification.Position.TOP_CENTER);
                    }
                });
                confirm.addCancelListener(log -> {
                });
            }
        });
    }

    private void createGrid() {
        grid.addColumn(Product::getName).setHeader("名称").setAutoWidth(true);
        grid.addColumn(Product::getIntroduction).setHeader("简介").setAutoWidth(true);
        grid.addColumn(Product::getPrice).setHeader("价格").setAutoWidth(true);
        grid.addColumn(Product::getQuantity).setHeader("数量").setAutoWidth(true);
        grid.addComponentColumn(product -> {
            String imageName = product.getImage();
            if (imageName == null || imageName.isEmpty()) {
                imageName = "default.png";
            }
            String imageUrl = "/images/Goods/" + imageName;
            Image img = new Image(imageUrl, "商品图片");
            img.setMaxWidth("50px");
            img.setMaxHeight("50px");
            img.getStyle().set("object-fit", "contain");
            return img;
        }).setHeader("商品图片").setAutoWidth(true);
    }

    private HorizontalLayout createHorizontalLayout() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setHeight("80px");
        headerLayout.setPadding(true);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        tfSearch.setClearButtonVisible( true);
        tfSearch.setPlaceholder("查询");
        tfSearch.setPrefixComponent(VaadinIcon.SEARCH.create());
        tfSearch.setWidth("200px");
        tfSearch.setHeight("40px");
        tfSearch.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("background", "#f9f9f9")
                .set("border", "none");
        tfSearch.getElement().getStyle().set("--lumo-text-field-size", "40px");

        btnAdd.setIcon(VaadinIcon.FILE_ADD.create());
        btnAdd.setText("添加");
        btnEdit.setIcon(VaadinIcon.EDIT.create());
        btnEdit.setText("修改");
        btnDelete.setIcon(VaadinIcon.TRASH.create());
        btnDelete.setText("删除");
        btnRefresh.setIcon(VaadinIcon.REFRESH.create());
        btnRefresh.setText("刷新");

        headerLayout.add(tfSearch, btnAdd, btnEdit, btnDelete, btnRefresh);
        return headerLayout;
    }
}