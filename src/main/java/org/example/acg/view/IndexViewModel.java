package org.example.acg.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.RouteScopeOwner;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import org.example.acg.config.MsgUtil;
import org.example.acg.entity.Product;
import org.example.acg.entity.User;
import org.example.acg.service.CartService;
import org.example.acg.service.ProductService;
import org.example.acg.view.Product.ProductDialog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 首页视图模型 (Index View Model)
 * 负责展示商品列表、导航栏、横幅广告以及处理搜索交互。
 */
@Route("")
@SpringComponent
@RouteScope
@RouteScopeOwner(IndexViewModel.class)
public class IndexViewModel extends VerticalLayout {

    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;

    // UI 颜色常量定义
    private static final String BG_COLOR_PAGE = "#f8f9fa";
    private static final String PRIMARY_COLOR = "#764ba2";
    private static final String SECONDARY_COLOR = "#667eea";

    // 当前登录用户
    private User currentUser;

    // 用于动态展示商品卡片的布局容器
    private HorizontalLayout productLayout;

    // 包含导航、横幅和商品列表的可滚动主内容区
    private Div scrollableContent;

    @Autowired
    private ObjectProvider<ChangePasswordDialog> changePasswordDialog;

    /**
     * 构造函数：初始化页面结构和样式
     */
    public IndexViewModel() {
        // 获取当前会话中的用户信息
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            this.currentUser = (User) session.getAttribute("user");
        }

        // 配置根布局样式
        configureMainLayout();

        // 创建主要容器、导航栏和横幅
        Div mainContainer = createMainCard();
        HorizontalLayout navbar = createNavbar();
        Div banner = createBanner();

        // 初始化商品列表布局
        productLayout = new HorizontalLayout();
        productLayout.setPadding(false);
        productLayout.getStyle()
                .set("flex-wrap", "wrap")
                .set("gap", "20px")
                .set("justify-content", "center")
                .set("width", "100%");


        // 组装可滚动内容区域
        scrollableContent = new Div(banner, productLayout);
        scrollableContent.setWidthFull();
        scrollableContent.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("height", "100%")
                .set("overflow-y", "auto")
                .set("overflow-x", "hidden");

        // 将内容添加到主容器并渲染
        mainContainer.add(navbar, scrollableContent);
        add(mainContainer);
    }

    /**
     * 生命周期回调：在组件构建完成后初始化数据
     */
    @PostConstruct
    public void init() {
        if (productService != null) {
            loadProducts();
        }
    }

    // ================= 数据加载与处理逻辑 =================

    private User getCurrentUser() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    /**
     * 加载所有商品并渲染到页面上
     */
    private void loadProducts() {
        renderProductList(productService.list());
    }

    /**
     * 根据关键词过滤商品并重新渲染
     * @param keyword 搜索关键词
     */
    private void filterProducts(String keyword) {
        List<Product> resultList;

        if (keyword == null || keyword.trim().isEmpty()) {
            // 关键词为空时，加载所有商品
            resultList = productService.list();
        } else {
            // 关键词不为空时，执行模糊查询
            resultList = productService.listProductLikeByName(keyword);
        }

        renderProductList(resultList);

        // 如果结果为空，renderProductList 内部会处理显示提示消息
    }

    /**
     * 通用方法：渲染商品列表
     * 如果列表为空，显示“暂无商品”或“未找到”提示
     * @param productList 商品列表
     */
    private void renderProductList(List<Product> productList) {
        productLayout.removeAll();

        if (productList == null || productList.isEmpty()) {
            Div emptyMsg = new Div("暂无商品");
            emptyMsg.getStyle()
                    .set("padding", "20px")
                    .set("color", "#666")
                    .set("width", "100%")
                    .set("text-align", "center");
            productLayout.add(emptyMsg);
            return;
        }

        for (Product product : productList) {
            Div card = createProductCard(product);
            productLayout.add(card);
        }
    }

    // ================= 组件创建工厂方法 =================

    /**
     * 创建单个商品卡片组件
     * @param product 商品实体
     * @return 包含商品信息的 Div 容器
     */
    private Div createProductCard(Product product) {
        Div card = new Div();
        card.setWidth("300px");
        card.setHeight("360px");
        card.getStyle()
                .set("box-sizing", "border-box")
                .set("background", "white")
                .set("border-radius", "16px")
                .set("overflow", "hidden")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)")
                .set("transition", "transform 0.2s")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("flex-shrink", "0");

        // 添加鼠标悬停浮动效果
        attachHoverEffect(card);

        // 构建图片元素
        Image imageElement = buildProductImage(product);

        // 构建文本信息区域
        Div textDiv = buildProductTextInfo(product);

        card.add(imageElement, textDiv);
        return card;
    }

    /**
     * 构建商品图片元素
     */
    private Image buildProductImage(Product product) {
        String imageUrl = "/images/Goods/" + product.getImage();
        Image imageElement = new Image(imageUrl, product.getName());
        imageElement.setWidth("100%");
        imageElement.setHeight("220px");
        imageElement.getStyle()
                .set("object-fit", "cover")
                .set("object-position", "center")
                .set("display", "block");
        return imageElement;
    }

    /**
     * 构建商品文本信息（名称、简介、价格）
     */
    private Div buildProductTextInfo(Product product) {
        Div textDiv = new Div();
        textDiv.getStyle()
                .set("padding", "16px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("flex-grow", "1")
                .set("justify-content", "space-between");

        H3 nameLabel = new H3(product.getName());
        nameLabel.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "600")
                .set("margin", "0 0 8px 0")
                .set("color", "#333")
                .set("white-space", "nowrap")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis");

        H4 introductionLabel = new H4(product.getIntroduction());
        introductionLabel.getStyle()
                .set("font-size", "14px")
                .set("font-weight", "400")
                .set("margin", "0 0 8px 0")
                .set("color", "#666")
                .set("white-space", "nowrap")
                .set("text-overflow", "ellipsis");

        H4 quantityLabel = new H4("剩余数量：" + product.getQuantity());
        quantityLabel.getStyle()
                .set("font-size", "14px")
                .set("font-weight", "400")
                .set("margin", "0 0 8px 0")
                .set("color", "#d37c8e")
                .set("white-space", "nowrap")
                .set("text-overflow", "ellipsis");

        HorizontalLayout priceActionLayout = new HorizontalLayout();
        priceActionLayout.setAlignItems(Alignment.CENTER);
        priceActionLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        priceActionLayout.setWidthFull();
        priceActionLayout.getStyle().set("margin-top", "auto");

        // 创建价格标签
        Div priceLabel = new Div();
        priceLabel.setText("¥ " + product.getPrice());
        priceLabel.getStyle()
                .set("font-size", "20px")
                .set("font-weight", "bold")
                .set("color", PRIMARY_COLOR)
                .set("margin-right", "12px");

        // 创建购物车按钮
        Button addToCartButton = new Button();
        addToCartButton.setIcon(VaadinIcon.CART_O.create());
        addToCartButton.getStyle()
                .setBorderRadius("50%")
                .setHeight("40px")
                .setWidth("40px")
                .setMinWidth("40px");

        // 绑定点击事件
        addToCartButton.addClickListener(e -> {
            User user = getCurrentUser();

            // 1. 检查是否登录
            if (user == null) {
                MsgUtil.warning("请先登录后再加入购物车", Notification.Position.TOP_CENTER);
                return;
            }

            // 2. 调用服务层逻辑
            String result = cartService.addToCart(user, product.getId());

            // 3. 根据结果反馈
            if ("success".equals(result)) {
                MsgUtil.success("已成功添加到购物车！", Notification.Position.TOP_CENTER);
                loadProducts();
            } else {
                MsgUtil.error(result, Notification.Position.TOP_CENTER);
            }
        });

        priceActionLayout.add(priceLabel, addToCartButton);

        textDiv.add(nameLabel, introductionLabel, quantityLabel, priceActionLayout);
        return textDiv;
    }

    /**
     * 创建搜索输入框组件
     */
    private TextField createSearchField() {
        TextField searchField = new TextField();
        searchField.setClearButtonVisible(true); // 启用清除按钮（小叉号）
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("200px");
        searchField.setHeight("40px");
        searchField.getStyle()
                .set("background", "#f9f9f9")
                .set("border", "none");

        // 绑定搜索事件监听器
        attachSearchListeners(searchField);

        return searchField;
    }

    /**
     * 创建购物车按钮
     */
    private Button createCartButton() {
        Button cartButton = new Button(VaadinIcon.CART_O.create());
        cartButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cartButton.getStyle()
                .set("border-radius", "50%")
                .set("width", "40px")
                .set("height", "40px")
                .set("min-width", "40px")
                .set("background", PRIMARY_COLOR)
                .set("color", "white");

        // 绑定点击事件：跳转到购物车页面
        cartButton.addClickListener(e -> UI.getCurrent().navigate("cart"));

        return cartButton;
    }

    /**
     * 创建用户按钮（登录/个人信息/退出）
     */
    private Button createUserButton() {
        Button userBtn = new Button();
        userBtn.setHeight("40px");

        if (currentUser != null) {
            // 已登录状态
            setupLoggedInUserButton(userBtn);
        } else {
            // 未登录状态
            setupGuestUserButton(userBtn);
        }
        return userBtn;
    }

    // ================= 辅助方法与事件绑定 =================

    /**
     * 配置根布局的全局样式
     */
    private void configureMainLayout() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", BG_COLOR_PAGE);
        getStyle().set("display", "flex");
        getStyle().set("flex-direction", "column");
    }

    /**
     * 创建带有毛玻璃效果的主容器卡片
     */
    private Div createMainCard() {
        Div mainContainer = new Div();
        mainContainer.setWidth("90%");
        mainContainer.setMaxWidth("1500px");
        mainContainer.setHeight("95vh");
        mainContainer.getStyle()
                .set("background", "rgba(255, 255, 255, 0.4)")
                .set("backdrop-filter", "blur(12px)")
                .set("-webkit-backdrop-filter", "blur(12px)")
                .set("border", "1px solid rgba(255, 255, 255, 0.5)")
                .set("box-shadow", "0 8px 32px 0 rgba(31, 38, 135, 0.1)")
                .set("border-radius", "24px")
                .set("margin", "20px auto")
                .set("overflow", "hidden")
                .set("display", "flex")
                .set("flex-direction", "column");
        return mainContainer;
    }

    /**
     * 创建顶部导航栏
     */
    private HorizontalLayout createNavbar() {
        HorizontalLayout navbar = new HorizontalLayout();
        navbar.setWidthFull();
        navbar.setHeight("80px");
        navbar.setPadding(true);
        navbar.setAlignItems(Alignment.CENTER);
        navbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        navbar.getStyle().set("flex-shrink", "0");

        navbar.add(createLogoSection(), createRightActionsSection());
        return navbar;
    }

    /**
     * 创建导航栏左侧的 Logo 区域
     */
    private HorizontalLayout createLogoSection() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);

        Div logoIcon = new Div();
        logoIcon.setWidth("32px");
        logoIcon.setHeight("32px");
        logoIcon.getStyle()
                .set("background", "linear-gradient(135deg, " + SECONDARY_COLOR + " 0%, " + PRIMARY_COLOR + " 100%)")
                .set("border-radius", "8px")
                .set("margin-right", "12px");

        H2 logoText = new H2("Acg");
        logoText.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "700")
                .set("margin", "0")
                .set("color", "#333");

        layout.add(logoIcon, logoText);
        return layout;
    }

    /**
     * 创建导航栏右侧的操作区域（搜索、购物车、管理、用户）
     */
    private HorizontalLayout createRightActionsSection() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(true);
        layout.add(
                createSearchField(),
                createCartButton(),
                createUserButton()
        );
        return layout;
    }

    /**
     * 创建底部横幅广告区域
     */
    private Div createBanner() {
        Div banner = new Div();
        banner.setHeight("700px");
        banner.getStyle()
                .set("margin", "0 20px 20px 20px")
                .set("flex-shrink", "0");

        String bgImage = "url('images/default.png')";
        String gradient = "linear-gradient(to right, rgba(118, 75, 162, 0.8), rgba(102, 126, 234, 0.6))";

        banner.getElement().getStyle()
                .set("background-image", gradient + ", " + bgImage)
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat")
                .set("border-radius", "20px")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.1)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        return banner;
    }

    /**
     * 为商品卡片添加鼠标悬停浮动效果
     */
    private void attachHoverEffect(Div card) {
        card.getElement().addEventListener("mouseenter", e ->
                card.getStyle().set("transform", "translateY(-5px)"));
        card.getElement().addEventListener("mouseleave", e ->
                card.getStyle().set("transform", "translateY(0)"));
    }

    /**
     * 为搜索框绑定事件监听器（回车搜索、值变化搜索）
     */
    private void attachSearchListeners(TextField searchField) {
        // 监听回车键触发搜索
        searchField.addKeyPressListener(Key.ENTER, e ->
                filterProducts(searchField.getValue())
        );

        // 监听值变化触发搜索（实时搜索）
        searchField.addValueChangeListener(event ->
                filterProducts(searchField.getValue())
        );

    }

    /**
     * 配置已登录用户的按钮状态（显示用户名、下拉菜单、退出功能）
     */
    private void setupLoggedInUserButton(Button userBtn) {
        userBtn.setText(currentUser.getName());
        userBtn.setIconAfterText(true);
        userBtn.setIcon(VaadinIcon.CARET_DOWN.create());

        ContextMenu contextMenu = new ContextMenu(userBtn);
        contextMenu.setOpenOnClick(true);

        MenuItem changePasswordItem = contextMenu.addItem("修改密码");
        changePasswordItem.addClickListener(e -> {
            ChangePasswordDialog dialog = changePasswordDialog.getIfAvailable();
            if (dialog == null) return;

            dialog.setData(currentUser);

            dialog.open();
        });

        MenuItem orderItem = contextMenu.addItem("订单");
        orderItem.setVisible(currentUser != null && !"admin".equals(currentUser.getName()));
        orderItem.addClickListener(e -> UI.getCurrent().navigate("order"));

        MenuItem orderManagement = contextMenu.addItem("订单管理");
        orderManagement.setVisible(currentUser != null && "admin".equals(currentUser.getName()));
        orderManagement.addClickListener(e -> UI.getCurrent().navigate("orderManagement"));

        MenuItem manageItem = contextMenu.addItem("商品管理");
        manageItem.setVisible(currentUser != null && "admin".equals(currentUser.getName()));
        manageItem.addClickListener(e -> UI.getCurrent().navigate("product"));

        MenuItem logoutItem = contextMenu.addItem("登出");
        logoutItem.addClickListener(e -> handleLogout());

    }

    /**
     * 配置未登录用户的按钮状态（显示“Sign in”、跳转登录页）
     */
    private void setupGuestUserButton(Button userBtn) {
        userBtn.setText("登录");
        userBtn.addClickListener(e -> UI.getCurrent().navigate("login"));
    }

    /**
     * 处理用户退出登录逻辑
     */
    private void handleLogout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute("user", null);
            UI.getCurrent().getPage().reload();
        }
    }
}