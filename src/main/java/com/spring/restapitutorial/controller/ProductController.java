package com.spring.restapitutorial.controller;

import com.spring.restapitutorial.entity.Product;
import com.spring.restapitutorial.service.IProductService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product")
    public ResponseEntity<?> saveProduct(@RequestBody Product product) {
        if (product == null) {
            return ResponseEntity.badRequest().body("Product data is required.");
        }
        productService.createProduct(product);
        return ResponseEntity.ok("Product saved successfully.");
    }

    @GetMapping(value = "/products-to-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public byte[] exportProductsToPdf() throws IOException {
        List<Product> productList = productService.getAllProduct();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("List of Products");
                contentStream.endText();

                int yPosition = 650;
                for (Product product : productList) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, yPosition);
                    contentStream.showText("Name: " + product.getName() + ", Price: " + product.getPrice() + ", Quantity:" + product.getQuantity());
                    contentStream.endText();
                    yPosition -= 20;
                }
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
