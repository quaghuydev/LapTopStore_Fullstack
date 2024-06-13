# TÀI LIỆU HƯỚNG DẪN CÀI ĐẶT VÀ TRIỂN KHAI TRANG THƯƠNG MẠI ĐIỆN TỬ LAP TOP STORE

## Import dự án

Tải source về từ github: [https://github.com/buiquanghuy20130276/LapTopStore.git](https://github.com/buiquanghuy20130276/LapTopStore.git)
Hoặc giải nén từ file đã nộp.
Sau khi giải nén ra sẽ gồm 2 thư mục front-end và back-end.

### Back-End

1. Sử dụng IDE để mở source: Eclipse
2. Sau khi mở Eclipse:
   - Chọn file ➔ Import
   - Trong hộp thoại import: Chọn Maven ➔ Exist Maven Projects
3. Sau đó chọn Browse và chọn thư mục đã giải nén LaptopStore
4. Trong thư mục LaptopStore gồm 2 thư mục chọn thư mục back end và chọn DrComputer
5. Sau khi xong các bước trên Nhấn Finish và đợi cho eclipse tải thư viện
6. Tìm đến file pom.xml
   - Nháy chuột phải chọn Run As ➔ Maven Install để cài đặt dependency.

#### Cài plugin lombok cho eclipse

1. Trong Project Explorer Chọn Maven Dependencies
2. Sau đó chọn `lombok-1.18.30.jaz`
3. Nhấn chuột phải chọn Run As ➔ Java Application
4. Sau khi ra hộp thoại trên đợi để chương trình tìm nơi lưu eclipse
5. Sau đó chọn Install/ Update ➔ Quit Installer
6. Chọn file ➔ Restart để eclipse cập nhật thay đổi.

#### Cài đặt dữ liệu trong database

1. Cài đặt navicat hoặc chọn Admin của xampp.
2. Chạy file `laptopstore.sql` trong file project vừa giải nén.

#### Chạy chương trình

1. Trong thư mục src tìm đến file `DrComputerApplication.java`
2. Nhấn chuột phải chọn Run As ➔ Java Application

### Front-End

1. Sử dụng Visual Studio Code hoặc WebStorm
2. Mở thư mục frontend trong thư mục LaptopStore
3. Mở terminal chạy lệnh `npm install --force`
4. Tiếp theo chạy lệnh: `npm start`

