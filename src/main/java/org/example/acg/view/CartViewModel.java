package org.example.acg.view;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.RouteScopeOwner;
import org.example.acg.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Route("cart")
@Component
@RouteScope
@RouteScopeOwner(CartViewModel.class)
public class CartViewModel extends VerticalLayout {

    // UI 颜色常量定义
    private static final String BG_COLOR_PAGE = "#f8f9fa";
    private static final String PRIMARY_COLOR = "#764ba2";
    private static final String SECONDARY_COLOR = "#667eea";

    @Autowired
    private CartService cartService;

    public CartViewModel() {

        // 配置根布局样式
        configureMainLayout();

        // 创建主要容器
        Div mainContainer = createMainCard();
        HorizontalLayout navbar = createNavbar();

        mainContainer.add(navbar);
        add(mainContainer);
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

        navbar.add(createLogoSection());
        return navbar;
    }

    /**
     * 创建导航栏的 Logo 区域
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

        H2 logoText = new H2("Shopping Cart");
        logoText.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "700")
                .set("margin", "0")
                .set("color", "#333");

        layout.add(logoIcon, logoText);
        return layout;
    }

    /**
     * 创建下面的 settlement 区域
     */

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
}
