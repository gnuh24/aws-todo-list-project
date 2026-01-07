import { useState } from "react";
import { Modal } from "antd";
import AccountSettings from "../Content/AccountSettings";
import ChangePassword from "../Content/ChangePassword";
import SettingsSidebar from "../Sidebar/SettingsSidebar";
import ChangeEmail from "../Content/ChangeEmail";
import Enable2FA from "../Content/Enable2FA"
import DeleteAccount from "../Content/DeleteAccount";
import NotificationSettings from "../Content/NotificationSettings";

export const SETTINGS_PAGE = {
    ACCOUNT: "ACCOUNT",
    NOTIFICATIONS: "NOTIFICATIONS",
    CHANGE_PASSWORD: "CHANGE_PASSWORD",
    CHANGE_EMAIL: "CHANGE_EMAIL",
    ENABLE_2FA: "ENABLE_2FA",
    DELETE_ACCOUNT: "DELETE_ACCOUNT",
};

export default function SettingsModal({ open, onClose }) {
    const [page, setPage] = useState(SETTINGS_PAGE.ACCOUNT);
    const [refreshKey, setRefreshKey] = useState(0);

    return (
        <Modal open={open} onCancel={onClose} footer={null} width={1000}  bodyStyle={{ top: "0px", padding: "20px 0 0 0" }}>
            <div className="flex h-[750px]">
                <SettingsSidebar page={page} setPage={setPage} />

                <div className="flex-1 overflow-y-auto px-10 py-8">
                    {page === SETTINGS_PAGE.ACCOUNT && (
                        <AccountSettings
                            refreshKey={refreshKey}
                            onGotoChangePassword={() => setPage(SETTINGS_PAGE.CHANGE_PASSWORD)}
                            onGotoChangeEmail={() => setPage(SETTINGS_PAGE.CHANGE_EMAIL)}
                            onGotoEnable2FA={() => setPage(SETTINGS_PAGE.ENABLE_2FA)}
                            onGotoDeleteAccount={() => setPage(SETTINGS_PAGE.DELETE_ACCOUNT)}
                        />
                    )}

                    {page === SETTINGS_PAGE.NOTIFICATIONS && (
                        <NotificationSettings />
                    )}

                    {page === SETTINGS_PAGE.CHANGE_PASSWORD && (
                        <ChangePassword onBack={() => setPage(SETTINGS_PAGE.ACCOUNT)} />
                    )}

                    {page === SETTINGS_PAGE.CHANGE_EMAIL && (
                        <ChangeEmail onBack={() => setPage(SETTINGS_PAGE.ACCOUNT)} />
                    )}

                    {page === SETTINGS_PAGE.ENABLE_2FA && (
                        <Enable2FA
                            onBack={() => {
                                setRefreshKey(k => k + 1);
                                setPage(SETTINGS_PAGE.ACCOUNT);
                            }}
                        />
                    )}

                    {page === SETTINGS_PAGE.DELETE_ACCOUNT && (
                        <DeleteAccount onBack={() => setPage(SETTINGS_PAGE.ACCOUNT)} />
                    )}
                </div>
            </div>
        </Modal>
    );
}
