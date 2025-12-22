import { useState } from "react";
import { Modal } from "antd";
import AccountSettings from "../Content/AccountSettings";
import ChangePassword from "../Content/ChangePassword";
import SettingsSidebar from "../Sidebar/SettingsSidebar";
import ChangeEmail from "../Content/ChangeEmail";
import Enable2FA from "../Content/Enable2FA"
import DeleteAccount from "../Content/DeleteAccount";

export default function SettingsModal({ open, onClose }) {
    const [page, setPage] = useState("account");
    // "account" | "change_password" | ...
    const [refreshKey, setRefreshKey] = useState(0);

    return (
        <Modal
            open={open}
            onCancel={onClose}
            footer={null}
            width={1000}
            className="!p-0"
            bodyStyle={{ padding: 0 }}
        >
            <div className="flex h-[650px] bg-white text-gray-700">
                <SettingsSidebar />

                <div className="flex-1 overflow-y-auto px-10 py-8">
                    {page === "account" && (
                        <AccountSettings
                            onGotoChangePassword={() => setPage("change_password")}
                            onGotoChangeEmail={() => setPage("change_email")}
                            onGotoEnable2FA={() => setPage("enable_2fa")}
                            onGotoDeleteAccount={() => setPage("delete_account")}
                            refreshKey={refreshKey}
                        />
                    )}
                    {page === "change_password" && (
                        <ChangePassword onBack={() => setPage("account")} />
                    )}
                    {page === "change_email" && (
                        <ChangeEmail onBack={() => setPage("account")} />
                    )}
                    {page === "enable_2fa" && (
                        <Enable2FA onBack={() => {
                            setRefreshKey(prev => prev + 1);
                            setPage("account");
                        }} />
                    )}
                    {page === "delete_account" && (
                        <DeleteAccount onBack={() => setPage("account")} />
                    )}

                </div>
            </div>
        </Modal>
    );
}
