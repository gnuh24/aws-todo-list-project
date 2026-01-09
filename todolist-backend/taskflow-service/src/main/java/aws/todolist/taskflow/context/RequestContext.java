package aws.todolist.taskflow.context;

import aws.todolist.taskflow.entity.Account;

/**
 * RequestContext
 * 👉 Lưu trữ thông tin của request hiện tại
 * - Account đang đăng nhập
 * - clientId (tab / device)
 * <p>
 * ⚠️ Chỉ set ở Filter
 * ⚠️ BẮT BUỘC clear sau khi request kết thúc
 */
public final class RequestContext {

    private static final ThreadLocal<Account> ACCOUNT_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> CLIENT_ID_HOLDER = new ThreadLocal<>();

    private RequestContext() {
    }

    /* ===================== ACCOUNT ===================== */

    public static Account getAccount() {
        return ACCOUNT_HOLDER.get();
    }

    public static void setAccount(Account account) {
        ACCOUNT_HOLDER.set(account);
    }

    public static String getAccountId() {
        Account acc = ACCOUNT_HOLDER.get();
        return acc != null ? acc.getId() : null;
    }

    public static boolean hasAccount() {
        return ACCOUNT_HOLDER.get() != null;
    }

    /* ===================== CLIENT ID ===================== */

    public static String getClientId() {
        return CLIENT_ID_HOLDER.get();
    }

    public static void setClientId(String clientId) {
        CLIENT_ID_HOLDER.set(clientId);
    }

    public static boolean hasClientId() {
        return CLIENT_ID_HOLDER.get() != null;
    }

    /* ===================== CLEAR ===================== */

    public static void clear() {
        ACCOUNT_HOLDER.remove();
        CLIENT_ID_HOLDER.remove(); // 🚨 rất quan trọng
    }
}
