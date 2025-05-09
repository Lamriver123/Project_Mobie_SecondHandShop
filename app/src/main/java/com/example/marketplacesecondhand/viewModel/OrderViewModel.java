package com.example.marketplacesecondhand.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderViewModel extends ViewModel {
    // MutableLiveData để kích hoạt việc làm mới dữ liệu
    private final MutableLiveData<Boolean> refreshTrigger = new MutableLiveData<>();

    /**
     * Lấy LiveData để các observer có thể theo dõi sự kiện làm mới.
     * @return LiveData chứa trạng thái kích hoạt làm mới.
     */
    public LiveData<Boolean> getRefreshTrigger() {
        return refreshTrigger;
    }

    /**
     * Gọi phương thức này để thông báo cho các observer rằng dữ liệu đơn hàng có thể đã thay đổi
     * và cần phải làm mới.
     */
    public void triggerOrderRefresh() {
        // Đặt giá trị thành true, các observer có thể reset hoặc chỉ phản ứng với sự kiện
        // Sử dụng mẫu SingleLiveEvent có thể tốt hơn cho các sự kiện một lần
        // nhưng MutableLiveData đơn giản hơn cho ví dụ này.
        refreshTrigger.setValue(true);
    }

    /**
     * Tùy chọn: Gọi phương thức này nếu bạn muốn "tiêu thụ" sự kiện.
     * Điều này có nghĩa là sau khi sự kiện được xử lý, bạn có thể đặt lại cờ.
     */
    public void onRefreshTriggered() {
        refreshTrigger.setValue(false); // Đặt lại nếu cần, hoặc các observer chỉ phản ứng
    }
}