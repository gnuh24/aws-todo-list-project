package aws.todolist.taskflow.context;

import aws.todolist.taskflow.entity.Account;

/**
 * RequestContext
 * 👉 Lưu trữ thông tin của request hiện tại (Account đang đăng nhập)
 * 👉 Sử dụng ThreadLocal để mỗi HTTP request có context riêng
 * 👉 Cho phép lấy Account ở mọi nơi (Service / Event / Utils)
 * ⚠️ CHỈ set ở Filter
 * ⚠️ BẮT BUỘC clear sau khi request kết thúc
 */
public final class RequestContext {

    /**
     * ThreadLocal lưu Account theo từng thread xử lý request
     * Mỗi request = 1 thread → dữ liệu không bị lẫn giữa user
     */
    private static final ThreadLocal<Account> ACCOUNT_HOLDER = new ThreadLocal<>();

    /**
     * Private constructor
     * → Không cho tạo instance
     * → Class này chỉ dùng static method
     */
    private RequestContext() {
    }

    /**
     * Lấy Account của request hiện tại
     *
     * @return Account đang đăng nhập
     * null nếu request không có header X-User-Id
     */
    public static Account getAccount() {
        return ACCOUNT_HOLDER.get();
    }

    /**
     * Set Account cho request hiện tại
     * <p>
     * 🚨 Chỉ được gọi trong Filter (OncePerRequestFilter)
     * 🚨 Không set trong Controller / Service
     */
    public static void setAccount(Account account) {
        ACCOUNT_HOLDER.set(account);
    }

    /**
     * Lấy ID của Account hiện tại
     * <p>
     * 👉 Dùng khi chỉ cần accountId (log, audit, event...)
     * 👉 Tránh truyền accountId lòng vòng
     */
    public static String getAccountId() {
        Account acc = ACCOUNT_HOLDER.get();
        return acc != null ? acc.getId() : null;
    }

    /**
     * Kiểm tra request hiện tại có account hay không
     * <p>
     * 👉 Hữu ích cho các API public / optional auth
     */
    public static boolean hasAccount() {
        return ACCOUNT_HOLDER.get() != null;
    }

    /**
     * XÓA context sau khi request kết thúc
     * <p>
     * 🚨 BẮT BUỘC phải gọi trong finally block của Filter
     * 🚨 Tránh memory leak & dữ liệu user bị dính sang request khác
     */
    public static void clear() {
        ACCOUNT_HOLDER.remove();
    }
}
