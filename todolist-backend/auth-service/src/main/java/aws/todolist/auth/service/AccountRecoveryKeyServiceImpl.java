package aws.todolist.auth.service;

import aws.todolist.auth.entity.AccountRecoveryKey;
import aws.todolist.auth.repository.AccountRecoveryKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountRecoveryKeyServiceImpl implements AccountRecoveryKeyService {

    private static final int DEFAULT_RECOVERY_KEY_COUNT = 8;

    private final AccountRecoveryKeyRepository recoveryKeyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<String> generateRecoveryKeys(String accountId) {

        List<String> plainKeys = new ArrayList<>();
        List<AccountRecoveryKey> entities = new ArrayList<>();

        for (int i = 0; i < DEFAULT_RECOVERY_KEY_COUNT; i++) {

            String rawKey = generateRawRecoveryKey();
            String hash = passwordEncoder.encode(rawKey);

            AccountRecoveryKey entity = AccountRecoveryKey.builder()
                    .id(UUID.randomUUID().toString())
                    .accountId(accountId)
                    .keyHash(hash)
                    .isUsed(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            plainKeys.add(rawKey);
            entities.add(entity);
        }

        recoveryKeyRepository.saveAll(entities);

        // ⚠️ chỉ trả plain text 1 lần duy nhất
        return plainKeys;
    }

    @Override
    public boolean useRecoveryKey(String accountId, String rawRecoveryKey) {

        List<AccountRecoveryKey> keys =
                recoveryKeyRepository.findAllByAccountIdAndIsUsedFalse(accountId);

        for (AccountRecoveryKey key : keys) {
            if (passwordEncoder.matches(rawRecoveryKey, key.getKeyHash())) {

                key.setUsed(true);
                key.setUsedAt(LocalDateTime.now());
                recoveryKeyRepository.save(key);

                return true;
            }
        }

        return false;
    }

    @Override
    public long countUnusedKeys(String accountId) {
        return recoveryKeyRepository.countByAccountIdAndIsUsedFalse(accountId);
    }
	
	@Override
	public void deleteAllByAccountId(String accountId) {
		recoveryKeyRepository.deleteByAccountId(accountId);
	}
	
	
	/* ===============================
     *            UTIL
     * =============================== */
    private String generateRawRecoveryKey() {
        // Ví dụ: XXXX-XXXX-XXXX
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase()
                .replaceAll("(.{4})", "$1-")
                .substring(0, 14);
    }
}
