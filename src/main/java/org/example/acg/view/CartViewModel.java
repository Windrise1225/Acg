package org.example.acg.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.example.acg.entity.Cart;
import org.example.acg.entity.Product;
import org.example.acg.entity.User;
import org.example.acg.service.CartService;
import org.example.acg.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@Route("cart")
@SpringComponent
@RouteScope
public class CartViewModel extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver {

    private static final String BG_COLOR_PAGE = "#f8f9fa";
    private static final String PRIMARY_COLOR = "#764ba2";
    private static final String SECONDARY_COLOR = "#667eea";

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    private VerticalLayout productListLayout;
    private User currentUser;
    private Div totalPriceLabel;
    private boolean isDataLoaded = false;
    private boolean isUiBuilt = false;

    public CartViewModel() {
        setSizeFull();
        getElement().setProperty("style",
                "background: " + BG_COLOR_PAGE + "; " +
                        "display: flex; " +
                        "flex-direction: column; " +
                        "align-items: stretch; " +
                        "width: 100%; " +
                        "height: 100%; " +
                        "margin: 0; " +
                        "padding: 0; " +
                        "box-sizing: border-box; " +
                        "overflow-x: hidden; " +
                        "overflow-y: auto;");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (!isUiBuilt) {
            buildUI();
            isUiBuilt = true;
        }
    }

    /**
     * 构建购物车页面的整体 UI 结构。
     * 初始化主容器、导航栏、商品列表区域和结算区域，并将它们组装到主卡片中。
     */
    private void buildUI() {
        removeAll();
        Div mainContainer = createMainCard();
        HorizontalLayout navbar = createNavbar();

        productListLayout = createProductListContainer();
        Div loadingMsg = new Div("正在加载购物车...");
        loadingMsg.getElement().setProperty("style", "padding: 20px; color: #888; text-align: center; width: 100%;");
        productListLayout.add(loadingMsg);

        HorizontalLayout settlementSection = createSettlementSection();

        mainContainer.add(navbar, productListLayout, settlementSection);
        add(mainContainer);
    }

    /**
     * 在进入视图前执行的身份验证逻辑。
     * 检查当前会话中是否存在用户信息，若未登录则重定向到登录页面。
     *
     * @param event 路由进入事件
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            this.currentUser = (User) session.getAttribute("user");
        }
        if (currentUser == null) {
            event.rerouteTo("login");
            return;
        }
    }

    /**
     * 在导航完成后执行的数据加载逻辑。
     * 确保 UI 已构建且用户已登录，然后异步加载购物车数据，防止重复加载。
     *
     * @param event 导航完成事件
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (isUiBuilt && currentUser != null && !isDataLoaded) {
            UI ui = UI.getCurrent();
            if (ui != null) {
                ui.access(() -> {
                    loadCartData();
                    isDataLoaded = true;
                });
            } else {
                loadCartData();
                isDataLoaded = true;
            }
        }
    }

    /**
     * 加载并渲染购物车数据。
     * 从服务层获取当前用户的购物车列表，计算总价，并动态生成商品卡片。
     * 若购物车为空，则显示提示信息。
     */
    private void loadCartData() {
        if (productListLayout == null) return;
        productListLayout.removeAll();

        List<Cart> cartList = cartService.listByUserId(currentUser.getId());

        if (cartList == null || cartList.isEmpty()) {
            Div emptyMsg = new Div("购物车是空的，快去选购吧！");
            emptyMsg.getElement().setProperty("style",
                    "display: flex; " +
                            "justify-content: center; " +
                            "height: 100%; " +
                            "width: 100%; " +
                            "color: #888; " +
                            "font-size: 18px; " +
                            "text-align: center;");
            productListLayout.add(emptyMsg);
            if (totalPriceLabel != null) totalPriceLabel.setText("¥ 0.00");
            return;
        }

        double total = 0;
        for (Cart cart : cartList) {
            Product product = productService.getProductById(cart.getProductId());
            if (product == null) continue;

            String imageUrl = "/images/Goods/" + product.getImage();
            String title = product.getName();
            String specs = product.getIntroduction();
            BigDecimal price = cart.getProductPrice();
            int quantity = cart.getProductQuantity();

            total += price.multiply(BigDecimal.valueOf(quantity)).doubleValue();

            HorizontalLayout card = createProductCard(imageUrl, title, specs, price, quantity, cart);
            productListLayout.add(card);
        }

        if (totalPriceLabel != null) {
            totalPriceLabel.setText("¥ " + String.format("%.2f", total));
        }
    }

    /**
     * 创建商品列表的滚动容器。
     * 设置该容器为可垂直滚动，并占据主卡片中间的剩余空间。
     *
     * @return 商品列表垂直布局组件
     */
    private VerticalLayout createProductListContainer() {
        VerticalLayout list = new VerticalLayout();
        list.setWidthFull();
        list.setPadding(false);
        list.setSpacing(true);
        list.setAlignItems(Alignment.STRETCH);
        list.getElement().setProperty("style",
                "flex-grow: 1; " +
                        "overflow-y: auto; " +
                        "padding: 10px; " +
                        "margin: 0; " +
                        "width: 100%; " +
                        "box-sizing: border-box;");
        return list;
    }

    /**
     * 创建单个商品卡片的布局。
     * 采用三栏式布局：左侧为商品图片，中间为商品信息及数量控制，右侧为单价和删除操作。
     *
     * @param imageUrl 图片路径
     * @param title    商品标题
     * @param specs    商品规格/简介
     * @param price    商品单价
     * @param quantity 购买数量
     * @param cart     购物车实体对象
     * @return 包含商品信息的水平布局组件
     */
    private HorizontalLayout createProductCard(String imageUrl, String title, String specs, BigDecimal price, int quantity, Cart cart) {
        HorizontalLayout card = new HorizontalLayout();
        card.setWidthFull();
        card.setHeight("140px");
        card.setPadding(false);
        card.setSpacing(false);
        card.setAlignItems(Alignment.CENTER);

        String cardStyle = "background: #ffffff; " +
                "border-radius: 16px; " +
                "box-shadow: 0 4px 15px rgba(0,0,0,0.05); " +
                "margin-bottom: 15px; " +
                "transition: transform 0.2s; " +
                "overflow: hidden; " +
                "width: 100%; " +
                "box-sizing: border-box; " +
                "display: flex; " +
                "flex-direction: row; " +
                "align-items: center;";
        card.getElement().setProperty("style", cardStyle);

        Div imageContainer = new Div();
        imageContainer.setWidth("140px");
        imageContainer.setHeight("140px");
        imageContainer.getStyle().set("flex-shrink", "0");
        String imgStyle = "background-image: url('" + imageUrl + "'); " +
                "background-size: cover; " +
                "background-position: center; " +
                "background-color: #f0f0f0; " +
                "height: 100%; " +
                "width: 15%;";
        imageContainer.getElement().setProperty("style", imgStyle);

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(false);
        infoLayout.setSpacing(false);
        infoLayout.setAlignItems(Alignment.START);
        infoLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        infoLayout.getElement().setProperty("style",
                "flex-grow: 1; " +
                        "padding: 0 20px; " +
                        "margin: 0; " +
                        "display: flex; " +
                        "flex-direction: column; " +
                        "justify-content: center; " +
                        "box-sizing: border-box;");

        H2 titleLabel = new H2(title);
        titleLabel.getElement().setProperty("style",
                "font-size: 18px; " +
                        "font-weight: 600; " +
                        "margin: 0 0 8px 0; " +
                        "color: #333; " +
                        "white-space: nowrap; " +
                        "overflow: hidden; " +
                        "text-overflow: ellipsis;");

        Span specsLabel = new Span(specs != null ? specs : "");
        specsLabel.getElement().setProperty("style",
                "font-size: 14px; " +
                        "color: #888; " +
                        "margin-bottom: 12px; " +
                        "display: block; " +
                        "white-space: nowrap; " +
                        "overflow: hidden; " +
                        "text-overflow: ellipsis;");

        HorizontalLayout qtyControl = new HorizontalLayout();
        qtyControl.setAlignItems(Alignment.CENTER);
        qtyControl.setSpacing(true);
        qtyControl.getElement().setProperty("style",
                "margin: 0; " +
                        "padding: 0; " +
                        "display: flex; " +
                        "align-items: center; " +
                        "justify-content: center; " +
                        "height: 32px;");

        Button btnMinus = new Button("-");
        btnMinus.setWidth("32px");
        btnMinus.setHeight("32px");
        btnMinus.addClickListener(e -> {
            if (cart.getProductQuantity() > 1) handleUpdateQuantity(cart, -1);
            else handleDeleteItem(cart);
        });

        Span qtyText = new Span(String.valueOf(cart.getProductQuantity()));
        qtyText.getElement().setProperty("style",
                "font-weight: bold; " +
                        "width: 30px; " +
                        "text-align: center; " +
                        "display: inline-block;");

        Button btnPlus = new Button("+");
        btnPlus.setWidth("32px");
        btnPlus.setHeight("32px");
        btnPlus.addClickListener(e -> handleUpdateQuantity(cart, 1));

        qtyControl.add(btnMinus, qtyText, btnPlus);
        infoLayout.add(titleLabel, specsLabel, qtyControl);

        VerticalLayout actionLayout = new VerticalLayout();
        actionLayout.setPadding(false);
        actionLayout.setSpacing(true);
        actionLayout.setAlignItems(Alignment.END);
        actionLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        actionLayout.setFlexShrink(0);
        actionLayout.getElement().setProperty("style",
                "padding: 15px; " +
                        "margin: 0; " +
                        "height: 100%; " +
                        "display: flex; " +
                        "flex-direction: column; " +
                        "justify-content: space-between; " +
                        "align-items: flex-end; " +
                        "box-sizing: border-box;");

        Span priceLabel = new Span("单价(¥)：" + String.format("%.2f", price));
        priceLabel.getElement().setProperty("style",
                "font-size: 20px; " +
                        "font-weight: 700; " +
                        "color: " + PRIMARY_COLOR + "; " +
                        "white-space: nowrap;");

        Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        deleteBtn.getElement().setProperty("style",
                "color: #ff4d4f; " +
                        "cursor: pointer; " +
                        "padding: 8px;");
        deleteBtn.addClickListener(e -> handleDeleteItem(cart));

        actionLayout.add(priceLabel, deleteBtn);

        card.add(imageContainer, infoLayout, actionLayout);
        return card;
    }

    /**
     * 处理商品数量的增减操作。
     * 调用服务层更新数量，若成功则刷新列表，失败则显示错误提示。
     *
     * @param cart  购物车项
     * @param delta 数量变化值（+1 或 -1）
     */
    private void handleUpdateQuantity(Cart cart, int delta) {
        String result = cartService.changeQuantity(cart.getId(), delta);
        if ("success".equals(result)) {
            isDataLoaded = false;
            loadCartData();
        } else {
            Notification.show(result, 2000, Notification.Position.TOP_CENTER);
        }
    }

    /**
     * 处理商品删除操作。
     * 调用服务层删除指定购物车项，刷新列表并显示成功提示。
     *
     * @param cart 待删除的购物车项
     */
    private void handleDeleteItem(Cart cart) {
        cartService.deleteById(cart.getId());
        Notification.show("已移除商品", 2000, Notification.Position.TOP_CENTER);
        isDataLoaded = false;
        loadCartData();
    }

    /**
     * 创建底部结算区域。
     * 包含左侧的合计金额显示和右侧的去结算按钮，采用两端对齐布局。
     *
     * @return 结算区域水平布局组件
     */
    private HorizontalLayout createSettlementSection() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setHeight("80px");
        layout.setPadding(true);
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        String layoutStyle = "margin-top: auto; " +
                "background-color: " + PRIMARY_COLOR + "; " +
                "height: 80px; " +
                "width: 100%; " +
                "box-sizing: border-box; " +
                "flex-shrink: 0; " +
                "display: flex; " +
                "justify-content: space-between; " +
                "align-items: center;";

        layout.getElement().setProperty("style", layoutStyle);

        VerticalLayout priceInfo = new VerticalLayout();
        priceInfo.setPadding(false);
        priceInfo.setSpacing(false);
        priceInfo.setAlignItems(Alignment.START);
        priceInfo.getElement().setProperty("style",
                "margin: 0; " +
                        "padding: 0; " +
                        "display: flex; " +
                        "flex-direction: column; " +
                        "justify-content: center;");

        Span label = new Span("合计:");
        label.getElement().setProperty("style", "color: rgba(255,255,255,0.8); font-size: 14px; white-space: nowrap;");

        totalPriceLabel = new Div("¥ 0.00");
        totalPriceLabel.getElement().setProperty("style", "color: white; font-size: 24px; font-weight: bold; white-space: nowrap;");

        priceInfo.add(label, totalPriceLabel);

        Button settleBtn = new Button("去结算");
        settleBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        String btnStyle = "background: white; " +
                "color: " + PRIMARY_COLOR + "; " +
                "border: none; " +
                "white-space: nowrap; " +
                "margin-left: auto; " +
                "flex-shrink: 0; " +
                "height: 40px; " +
                "padding: 0 24px; " +
                "font-weight: 600; " +
                "cursor: pointer;";

        settleBtn.getElement().setProperty("style", btnStyle);
        settleBtn.addClickListener(e -> Notification.show("结算功能开发中..."));

        layout.add(priceInfo, settleBtn);

        return layout;
    }

    /**
     * 创建顶部导航栏。
     * 目前仅包含 Logo 区域，设置为固定高度并禁止压缩。
     *
     * @return 导航栏水平布局组件
     */
    private HorizontalLayout createNavbar() {
        HorizontalLayout navbar = new HorizontalLayout();
        navbar.setWidthFull();
        navbar.setHeight("80px");
        navbar.setPadding(true);
        navbar.setAlignItems(Alignment.CENTER);
        navbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        navbar.getElement().setProperty("style",
                "flex-shrink: 0; " +
                        "height: 80px; " +
                        "width: 100%; " +
                        "box-sizing: border-box;");

        navbar.add(createLogoSection());
        return navbar;
    }

    /**
     * 创建导航栏中的 Logo 区域。
     * 包含渐变背景的图标和“Shopping Cart”标题文本。
     *
     * @return 包含 Logo 的水平布局组件
     */
    private HorizontalLayout createLogoSection() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(true);
        layout.getElement().setProperty("style", "margin: 0; padding: 0;");

        Div logoIcon = new Div();
        logoIcon.setWidth("32px");
        logoIcon.setHeight("32px");
        String iconStyle = "background: linear-gradient(135deg, " + SECONDARY_COLOR + " 0%, " + PRIMARY_COLOR + " 100%); " +
                "border-radius: 8px; " +
                "width: 32px; " +
                "height: 32px; " +
                "flex-shrink: 0;";
        logoIcon.getElement().setProperty("style", iconStyle);

        H2 logoText = new H2("Shopping Cart");
        logoText.getElement().setProperty("style", "font-size: 24px; font-weight: 700; margin: 0; color: #333; white-space: nowrap;");
        layout.add(logoIcon, logoText);
        return layout;
    }

    /**
     * 创建主内容卡片容器。
     * 设置毛玻璃背景效果、圆角、阴影，并通过 calc 计算宽高以防止溢出父容器。
     *
     * @return 主内容 Div 组件
     */
    private Div createMainCard() {
        Div mainContainer = new Div();
        mainContainer.setWidth("100%");
        mainContainer.setMaxWidth("1500px");
        mainContainer.setHeight("95vh");

        String containerStyle = "background: rgba(255, 255, 255, 0.4); " +
                "backdrop-filter: blur(12px); " +
                "-webkit-backdrop-filter: blur(12px); " +
                "border: 1px solid rgba(255, 255, 255, 0.5); " +
                "box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.1); " +
                "border-radius: 24px; " +
                "margin: 20px auto; " +
                "overflow: hidden; " +
                "display: flex; " +
                "flex-direction: column; " +
                "align-items: stretch; " +
                "height: calc(100% - 40px); " +
                "max-height: calc(100vh - 40px); " +
                "width: calc(100% - 40px); " +
                "max-width: 1500px; " +
                "box-sizing: border-box;";

        mainContainer.getElement().setProperty("style", containerStyle);
        return mainContainer;
    }
}