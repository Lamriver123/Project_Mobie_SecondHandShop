Xây dựng ứng dụng di động mua bán đồ cũ

Tên thành viên:
Phạm Ngọc Duy - 22110297
Nguyễn Hữu Ngọc Lam - 22110362

Mô tả dự án:
Xây dựng thành công một ứng dụng di động android hoạt động như một sàn thương mại điện tử chuyên biệt cho đồ cũ đảm bảo:
-	Phát triển các chức năng cốt lõi cho phép người dùng dễ dàng đăng bán các mặt hàng đồ cũ của mình.
-	Cung cấp công cụ tìm kiếm và lọc sản phẩm hiệu quả giúp người mua nhanh chóng tìm được món đồ ưng ý.
-	Xây dựng quy trình giỏ hàng, đặt hàng và theo dõi đơn hàng minh bạch.
-	Tích hợp tính năng đánh giá sản phẩm và người bán để tăng tính minh bạch và tin cậy.
-	Đảm bảo giao diện người dùng thân thiện, trực quan và dễ sử dụng trên nền tảng di động.
-	Áp dụng các công nghệ phù hợp để đảm bảo hiệu năng, bảo mật dữ liệu và khả năng mở rộng của hệ thống

Hướng dẫn cài đặt:

![Sửa đường dẫn tới thư mục ZaloPay](image.png)
Trong project dự án có sử dụng phương thức thanh toán qua ZaloPay và sử dụng ZaloPay Sandbox để làm môi trường demo.
Để có thể áp dụng được thì phải sửa đường dẫn đường dẫn trong file build.gradle.kts(:app)
implementation(fileTree(mapOf(
        "dir" to "D:\\HK2_Nam_3\\Lap_Trinh_Di_Dong\\ZaloPay",   // đường dẫn tới thư mục ZaloPay đươc lưu trong project
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))

![Hình minh họa](image-1.png)

Hướng tạo dữ liệu cho mysql:

Đầu tiên tạo database tên là "marketplace" trong mysql
Tiếp theo đổi username và password trong file application.properies thành username và password của mysql trên máy mình.
![Hình minh họa](image-2.png)

Tiếp theo chạy api để jpa tạo các table trong database marketplace.
Chạy app android và đăng ký 6 tài khoản để khớp với script.sql.
Tiếp theo mở file sql.txt copy và dán vào mysql để tạo data cho dự án.
file sql.txt được lưu tại "Project_Mobie_SecondHandShop\script_sql" của project.
![Hình minh họa](image-3.png)

