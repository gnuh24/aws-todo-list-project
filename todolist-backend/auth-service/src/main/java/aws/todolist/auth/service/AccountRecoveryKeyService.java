package aws.todolist.auth.service;

import java.util.List;

public interface AccountRecoveryKeyService {

    /**
     * Generate recovery keys cho account
     * Chỉ gọi sau khi verify 2FA thành công
     */
    List<String> generateRecoveryKeys(String accountId);

    /**
     * Verify recovery key khi login
     */
    boolean useRecoveryKey(String accountId, String rawRecoveryKey);

    /**
     * Đếm số recovery key còn unused
     */
    long countUnusedKeys(String accountId);
	
	void deleteAllByAccountId(String accountId);
}
