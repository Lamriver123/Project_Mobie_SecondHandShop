package com.example.marketplacesecondhand.fragment.cart;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar; // Thêm ProgressBar để hiển thị trạng thái tải
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.adapter.cart.CartShopAdapter;
import com.example.marketplacesecondhand.databinding.FragmentCartDetailBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.CartResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.dto.response.UserResponse; // Import UserResponse
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.models.User; // Import User
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger; // Sử dụng AtomicInteger cho counter

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartDetailFragment extends Fragment {
    private FragmentCartDetailBinding binding;
    private CartShopAdapter cartShopAdapter;
    public static List<CartShop> cartShopList;
    private TextView textViewTotalPrice;
    private ProgressBar progressBar; // Khai báo ProgressBar
    private TextView textViewEmptyCart; // Khai báo TextView thông báo giỏ hàng trống


    private static final String TAG = "CartDetailFragment";

    private APIService apiService;

    // Biến để theo dõi số lượng shop đã fetch xong thông tin
    private AtomicInteger completedShopFetches;
    // Map để lưu thông tin shop đã fetch theo sellerId
    private Map<Integer, UserResponse> shopInfoMap;
    // Set để lưu trữ danh sách sellerId duy nhất cần fetch
    private Set<Integer> uniqueSellerIds;

    public CartDetailFragment() {
        super(R.layout.fragment_cart_detail);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCartDetailBinding.bind(view);
        textViewTotalPrice = requireActivity().findViewById(R.id.textViewTotalPrice);
        progressBar = binding.progressBarLoading; // Ánh xạ ProgressBar từ binding
        textViewEmptyCart = binding.textViewEmptyCart; // Ánh xạ TextView từ binding


        setupRecyclerView();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        fetchCartItems(); // Bắt đầu quá trình lấy dữ liệu
    }

    private void setupRecyclerView() {
        cartShopList = new ArrayList<>();
        cartShopAdapter = new CartShopAdapter(requireContext(), cartShopList);
        cartShopAdapter.setOnCartItemCheckListener(this::calculateTotalPrice);
        binding.recyclerViewCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewCart.setAdapter(cartShopAdapter);
    }

    private void fetchCartItems() {
        // Ẩn RecyclerView và hiển thị ProgressBar
        binding.recyclerViewCart.setVisibility(View.GONE);
        textViewTotalPrice.setVisibility(View.GONE); // Ẩn tổng tiền ban đầu
        textViewEmptyCart.setVisibility(View.GONE); // Ẩn thông báo trống
        progressBar.setVisibility(View.VISIBLE);

        DatabaseHandler db = new DatabaseHandler(requireContext());
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (userLoginInfo == null || userLoginInfo.getUserId() == 0) {
            // Người dùng chưa đăng nhập
            progressBar.setVisibility(View.GONE);
            textViewEmptyCart.setVisibility(View.VISIBLE); // Hiển thị thông báo giỏ hàng trống
            textViewEmptyCart.setText("Bạn cần đăng nhập để xem giỏ hàng.");
            cartShopList.clear();
            cartShopAdapter.notifyDataSetChanged();
            calculateTotalPrice(); // Đảm bảo tổng tiền là 0
            // TODO: Có thể điều hướng người dùng đến màn hình đăng nhập
            return;
        }

        int userId = userLoginInfo.getUserId();
        Log.d(TAG, "Fetching cart for user ID: " + userId);

        apiService.getCartItemsByUserId(userId).enqueue(new Callback<ApiResponse<List<CartResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CartResponse>>> call, @NonNull Response<ApiResponse<List<CartResponse>>> response) {
                if (!isAdded() || getContext() == null || binding == null) {
                    // Fragment không còn attach, bỏ qua kết quả
                    return;
                }

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CartResponse> apiCartItems = response.body().getData();

                    if (apiCartItems.isEmpty()) {
                        // Giỏ hàng trống
                        progressBar.setVisibility(View.GONE);
                        textViewEmptyCart.setVisibility(View.VISIBLE);
                        textViewEmptyCart.setText(response.body().getMessage() != null ? response.body().getMessage() : "Giỏ hàng của bạn trống.");
                        cartShopList.clear();
                        cartShopAdapter.notifyDataSetChanged();

                        calculateTotalPrice(); // Đảm bảo tổng tiền là 0
                    } else {
                        // Chuyển đổi dữ liệu giỏ hàng và bắt đầu fetch thông tin shop
                        List<CartShop> transformedList = transformApiDataToCartShopList(apiCartItems);

                        // Lưu tạm danh sách giỏ hàng đã nhóm
                        cartShopList.clear();
                        cartShopList.addAll(transformedList);

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.header_cart, new HeaderCartFragment())
                                .commit();

                        // Lấy danh sách sellerId duy nhất
                        uniqueSellerIds = new HashSet<>();
                        for (CartShop cartShop : cartShopList) {
                            if (cartShop.getUser() != null && cartShop.getUser().getId() > 0) {
                                uniqueSellerIds.add(cartShop.getUser().getId());
                            } else {
                                Log.e(TAG, "CartShop item has invalid or null User object");
                            }
                        }

                        if (uniqueSellerIds.isEmpty()) {
                            // Có mục trong giỏ hàng nhưng không xác định được shop ID (trường hợp lỗi)
                            progressBar.setVisibility(View.GONE);
                            textViewEmptyCart.setVisibility(View.VISIBLE);
                            textViewEmptyCart.setText("Không thể tải thông tin giỏ hàng chi tiết.");
                            cartShopList.clear(); // Xóa dữ liệu lỗi
                            cartShopAdapter.notifyDataSetChanged();
                            calculateTotalPrice(); // Đảm bảo tổng tiền là 0
                            return;
                        }


                        // Khởi tạo counter và map cho việc fetch thông tin shop
                        completedShopFetches = new AtomicInteger(0);
                        shopInfoMap = new HashMap<>();

                        // Bắt đầu fetch thông tin cho từng shop
                        for (Integer sellerId : uniqueSellerIds) {
                            fetchSingleShopInfo(sellerId);
                        }
                    }
                } else {
                    // Xử lý lỗi API hoặc dữ liệu rỗng/null
                    progressBar.setVisibility(View.GONE);
                    textViewEmptyCart.setVisibility(View.VISIBLE);
                    textViewEmptyCart.setText(response.body() != null && response.body().getMessage() != null ? response.body().getMessage() : "Lỗi khi tải giỏ hàng: " + response.code());
                    Log.e(TAG, "API call failed: " + response.code() + " - " + response.message());
                    cartShopList.clear();
                    cartShopAdapter.notifyDataSetChanged();
                    calculateTotalPrice(); // Đảm bảo tổng tiền là 0

                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CartResponse>>> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null || binding == null) return;

                progressBar.setVisibility(View.GONE);
                textViewEmptyCart.setVisibility(View.VISIBLE);
                textViewEmptyCart.setText("Lỗi kết nối: " + t.getMessage());
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                cartShopList.clear();
                cartShopAdapter.notifyDataSetChanged();
                calculateTotalPrice(); // Đảm bảo tổng tiền là 0

            }
        });
    }

    // Phương thức chuyển đổi danh sách phẳng CartResponse thành cấu trúc nhóm theo shop List<CartShop>
    private List<CartShop> transformApiDataToCartShopList(List<CartResponse> cartItems) {
        List<CartShop> transformedList = new ArrayList<>();
        if (cartItems == null || cartItems.isEmpty()) {
            return transformedList;
        }

        Map<Integer, List<CartResponse>> itemsGroupedBySeller = new HashMap<>();
        for (CartResponse item : cartItems) {
            if (item.getProduct() != null) {
                int sellerId = item.getProduct().getOwnerId();
                itemsGroupedBySeller.computeIfAbsent(sellerId, k -> new ArrayList<>()).add(item);
            } else {
                Log.w(TAG, "Cart item with null ProductResponse skipped.");
            }
        }

        for (Map.Entry<Integer, List<CartResponse>> entry : itemsGroupedBySeller.entrySet()) {
            int sellerId = entry.getKey();
            List<CartResponse> sellerCartItems = entry.getValue();

            // Tạo đối tượng User cho người bán, chỉ cần set ID ban đầu
            User sellerUser = new User();
            sellerUser.setId(sellerId);
            // KHÔNG set tên placeholder ở đây, tên sẽ được set sau khi fetch xong shop info

            List<CartProduct> cartProductsForShop = new ArrayList<>();
            for (CartResponse item : sellerCartItems) {
                CartProduct cartProduct = new CartProduct();
                cartProduct.setProductResponse(item.getProduct());
                cartProduct.setQuantityCart(item.getQuantity());
                cartProduct.setChecked(false); // Khởi tạo trạng thái chọn
                cartProductsForShop.add(cartProduct);
            }

            CartShop cartShop = new CartShop();
            cartShop.setUser(sellerUser); // Đặt người bán (User) với ID
            cartShop.setChecked(false);
            cartShop.setProducts(cartProductsForShop);

            transformedList.add(cartShop);
        }

        return transformedList;
    }

    // Phương thức để fetch thông tin của một shop cụ thể
    private void fetchSingleShopInfo(Integer shopId) {
        if (shopId == null || shopId <= 0) {
            Log.e(TAG, "shopId không hợp lệ khi fetchSingleShopInfo: " + shopId);
            // Tăng counter ngay cả khi ID không hợp lệ để không bị kẹt
            checkIfAllShopFetchesCompleted();
            return;
        }

        apiService.getShopInfo(shopId).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Response<ApiResponse<UserResponse>> response) {
                if (!isAdded() || getContext() == null || binding == null) return;

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // Lưu thông tin shop vào map nếu fetch thành công
                    shopInfoMap.put(shopId, response.body().getData());
                    Log.d(TAG, "Fetched shop info for ID: " + shopId);
                } else {
                    Log.w(TAG, "Failed to fetch shop info for ID: " + shopId + " - Code: " + response.code());
                    // Không thêm vào map nếu thất bại, hoặc có thể thêm null/đối tượng lỗi nếu muốn biểu thị rõ ràng
                }
                // Tăng counter và kiểm tra xem đã fetch xong hết chưa
                checkIfAllShopFetchesCompleted();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null || binding == null) return;

                Log.e(TAG, "Network error fetching shop info for ID: " + shopId + " - " + t.getMessage(), t);
                // Tăng counter và kiểm tra xem đã fetch xong hết chưa
                checkIfAllShopFetchesCompleted();
            }
        });
    }

    // Kiểm tra xem tất cả các API call fetchSingleShopInfo đã hoàn thành chưa
    private void checkIfAllShopFetchesCompleted() {
        int completedCount = completedShopFetches.incrementAndGet(); // Tăng counter một cách an toàn
        Log.d(TAG, "Completed shop fetches: " + completedCount + "/" + uniqueSellerIds.size());

        if (completedCount == uniqueSellerIds.size()) {
            // Tất cả các API call đã hoàn thành (thành công hoặc thất bại)
            Log.d(TAG, "All shop fetches completed. Processing cart data.");
            // Chuyển sang bước xử lý và hiển thị dữ liệu cuối cùng
            processCompleteCartData();
        }
    }

    // Phương thức xử lý dữ liệu giỏ hàng sau khi đã fetch xong thông tin shop
    private void processCompleteCartData() {
        // Duyệt qua danh sách cartShopList tạm thời
        for (CartShop cartShop : cartShopList) {
            int sellerId = cartShop.getUser().getId();
            // Lấy thông tin shop từ map đã fetch
            UserResponse shopDetails = shopInfoMap.get(sellerId);

            if (shopDetails != null) {
                // Nếu fetch thành công, cập nhật đối tượng User trong CartShop với thông tin đầy đủ
                User sellerUser = cartShop.getUser();
                // TODO: Ánh xạ các trường từ shopDetails (UserResponse) sang sellerUser (User)
                // Ví dụ:
                sellerUser.setUsername(shopDetails.getUsername());
                sellerUser.setFullName(shopDetails.getFullName()); // Nếu User có trường fullName
                // ... cập nhật các trường khác nếu cần
                Log.d(TAG, "Updated shop info for ID: " + sellerId + " with name: " + shopDetails.getUsername());

            } else {
                // Nếu fetch thất bại hoặc không có thông tin, đặt tên shop là lỗi
                User sellerUser = cartShop.getUser();
                sellerUser.setUsername("Lỗi tải shop (" + sellerId + ")");
                sellerUser.setFullName("Lỗi tải shop (" + sellerId + ")"); // Cập nhật cả fullName nếu dùng nó hiển thị
                Log.w(TAG, "Could not find shop info for ID: " + sellerId + ". Setting error placeholder.");
            }
            // Lưu ý: Bạn có thể cần đảm bảo model User có các setter tương ứng.
        }

        // Dữ liệu giỏ hàng và thông tin shop đã sẵn sàng, hiển thị lên RecyclerView
        progressBar.setVisibility(View.GONE);
        binding.recyclerViewCart.setVisibility(View.VISIBLE);
        textViewTotalPrice.setVisibility(View.VISIBLE); // Hiển thị tổng tiền

        // Cập nhật Adapter với danh sách đã xử lý
        // cartShopAdapter.setNewData(cartShopList); // Tùy vào cách adapter của bạn được thiết kế
        cartShopAdapter.notifyDataSetChanged(); // Thông báo Adapter toàn bộ dữ liệu đã thay đổi

        // Tính toán và hiển thị tổng tiền ban đầu (các mục chưa được chọn)
        calculateTotalPrice();
    }


    // Phương thức tính toán tổng giá tiền
    private void calculateTotalPrice() {
        int total = 0;
        for (CartShop shop : cartShopList) {
            for (CartProduct product : shop.getProducts()) {
                if (product.isChecked()) {
                    try {
                        ProductResponse pr = product.getProductResponse();
                        if (pr != null && pr.getCurrentPrice() != null && !pr.getCurrentPrice().isEmpty()) {
                            // Loại bỏ ký tự định dạng tiền tệ (nếu có) trước khi parse
                            String priceStr = pr.getCurrentPrice().replaceAll("[^\\d]", ""); // Chỉ giữ lại chữ số
                            total += Integer.parseInt(priceStr) * product.getQuantityCart();
                        } else {
                            Log.w(TAG, "Skipping product due to missing price or product data: " + (pr != null ? pr.getProductName() : "Unknown Product"));
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing price: " + (product.getProductResponse() != null ? product.getProductResponse().getCurrentPrice() : "N/A"), e);
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Null pointer when accessing product or price", e);
                    }
                }
            }
        }
        if (textViewTotalPrice != null) {
            textViewTotalPrice.setText("Tổng: " + formatCurrency(total) + "đ");
        }
    }

    // Phương thức định dạng tiền tệ
    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}