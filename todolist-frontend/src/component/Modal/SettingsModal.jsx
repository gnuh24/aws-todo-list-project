import { useState } from "react";
import { Modal } from "antd";
import AccountSettings from "../Content/AccountSettings";
import ChangePassword from "../Content/ChangePassword";
import SettingsSidebar from "../Sidebar/SettingsSidebar";
import ChangeEmail from "../Content/ChangeEmail";

export default function SettingsModal({ open, onClose }) {
  const [page, setPage] = useState("account");
  // "account" | "change_password" | ...

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
            />
          )}
          {page === "change_password" && (
            <ChangePassword onBack={() => setPage("account")} />
          )}
          {page === "change_email" && (
            <ChangeEmail onBack={() => setPage("account")} />
          )}
        </div>
      </div>
    </Modal>
  );
}
