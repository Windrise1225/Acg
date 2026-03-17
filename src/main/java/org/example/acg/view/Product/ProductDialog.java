package org.example.acg.view.Product;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import lombok.Setter;
import org.example.acg.entity.Product;
import org.example.acg.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Scope("prototype")
@Component
public class ProductDialog extends Dialog {

    @Autowired
    private ProductService productService;

    TextField tfName = new TextField();
    TextField tfIntroduction = new TextField();
    TextField tfPrice = new TextField();
    TextField tfQuantity = new TextField();
    Button btnCancel = new Button();
    Button btnConfirm = new Button();

    String[] uploadedFileName = {null};
    private Product editingProduct = null;

    @Setter
    private Runnable onSuccess;

    public ProductDialog() {
        init();
    }

    private void init() {
        tfName.getElement().setAttribute("autocomplete", "off");
        tfIntroduction.getElement().setAttribute("autocomplete", "off");
        tfPrice.getElement().setAttribute("autocomplete", "off");
        tfQuantity.getElement().setAttribute("autocomplete", "off");

        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnCancel.setText("Cancel");
        btnConfirm.setText("Confirm");
        btnLayout.add(btnCancel, btnConfirm);

        tfName.setLabel("Name");
        tfName.setWidth("80%");
        tfIntroduction.setLabel("Introduction");
        tfIntroduction.setWidth("80%");
        tfPrice.setLabel("Price");
        tfPrice.setWidth("80%");
        tfQuantity.setLabel("Quantity");
        tfQuantity.setWidth("80%");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif", "image/webp", "image/jpg");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Div("拖拽图片到此处，或点击上传"));

        layout.add(upload, tfName, tfIntroduction, tfPrice, tfQuantity, btnLayout);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);
        add(layout);

        upload.addSucceededListener(event -> {
            try {
                String originalName = event.getFileName();
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String uniqueName = UUID.randomUUID().toString() + extension;
                String uploadPath = "src/main/resources/static/images/Goods/";
                File targetFile = new File(uploadPath + uniqueName);
                new File(uploadPath).mkdirs();
                FileOutputStream fos = new FileOutputStream(targetFile);
                fos.write(buffer.getInputStream().readAllBytes());
                fos.close();
                uploadedFileName[0] = uniqueName;
                upload.setDropLabel(new Div("上传成功: " + uniqueName));
            } catch (IOException e) {
                e.printStackTrace();
                upload.setDropLabel(new Div("上传失败"));
            }
        });

        chickBtn();
    }

    private void chickBtn() {
        btnConfirm.addClickListener(event -> {
            if (tfName.getValue().isEmpty()) {
                tfName.setErrorMessage("Please enter the name");
                tfName.setInvalid(true);
                return;
            }
            if (tfIntroduction.getValue().isEmpty()) {
                tfIntroduction.setErrorMessage("Please enter a brief introduction");
                tfIntroduction.setInvalid(true);
                return;
            }
            if (tfPrice.getValue().isEmpty()) {
                tfPrice.setErrorMessage("Please enter the price");
                tfPrice.setInvalid(true);
                return;
            }
            if (tfQuantity.getValue().isEmpty()) {
                tfQuantity.setErrorMessage("Please enter the quantity");
                tfQuantity.setInvalid(true);
                return;
            }

            boolean isEdit = (editingProduct != null && editingProduct.getId() != null);
            Product existingProduct = productService.getProductByName(tfName.getValue());

            if (!isEdit) {
                if (existingProduct != null) {
                    tfName.setErrorMessage("The product already exists");
                    tfName.setInvalid(true);
                    return;
                }
                Product product = new Product();
                product.setName(tfName.getValue());
                product.setIntroduction(tfIntroduction.getValue());
                product.setPrice(new BigDecimal(tfPrice.getValue()));
                product.setQuantity(Integer.parseInt(tfQuantity.getValue()));
                product.setCreateTime(ZonedDateTime.now());
                product.setImage(uploadedFileName[0] != null ? uploadedFileName[0] : "default.png");
                productService.insertProduct(product);

                close();
            } else {
                if (existingProduct != null && !existingProduct.getId().equals(editingProduct.getId())) {
                    tfName.setErrorMessage("The product name already exists");
                    tfName.setInvalid(true);
                    return;
                }
                editingProduct.setName(tfName.getValue());
                editingProduct.setIntroduction(tfIntroduction.getValue());
                editingProduct.setPrice(new BigDecimal(tfPrice.getValue()));
                editingProduct.setQuantity(Integer.parseInt(tfQuantity.getValue()));
                if (uploadedFileName[0] != null) {
                    editingProduct.setImage(uploadedFileName[0]);
                }
                productService.updateProduct(editingProduct);

                close();

            }
            if (onSuccess != null) {
                onSuccess.run();
            }
        });

        btnCancel.addClickListener(event -> {
            close();
        });
    }

    public void setData(Product product) {
        if (product != null) {
            setHeaderTitle("Modify the product");
            editingProduct = product;
            tfName.setValue(product.getName());
            tfIntroduction.setValue(product.getIntroduction());
            tfPrice.setValue(product.getPrice().toString());
            tfQuantity.setValue(product.getQuantity().toString());
            uploadedFileName = new String[]{null};
        } else {
            setHeaderTitle("Add product");
            editingProduct = null;
            tfName.clear();
            tfIntroduction.clear();
            tfPrice.clear();
            tfQuantity.clear();
            tfName.setInvalid(false);
            tfIntroduction.setInvalid(false);
            tfPrice.setInvalid(false);
            tfQuantity.setInvalid(false);
            uploadedFileName = new String[]{null};
        }
    }

    @Override
    public void open() {
        super.open();
    }
}