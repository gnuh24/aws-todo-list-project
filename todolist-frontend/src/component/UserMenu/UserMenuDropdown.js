import {
  SettingOutlined,
  TeamOutlined,
  FileTextOutlined,
  GiftOutlined,
  SyncOutlined,
  LogoutOutlined,
  StarOutlined,
  PrinterOutlined,
} from "@ant-design/icons";

import { Menu } from "antd";
import { useNavigate } from "react-router-dom";

export default function UserMenuDropdown() {
  const navigate = useNavigate();

  const handleLogout = () => {
    // 🧹 Xoá thông tin người dùng
    localStorage.removeItem("USER_INFO");

    // 👉 Chuyển hướng ra trang login
    navigate("/login");

    console.log("Đã đăng xuất thành công!");
  };
  return (
    <Menu className="rounded-xl p-2 w-64">
      {/* Header */}
      <div className="flex items-center gap-3 px-3 py-2 border-b pb-3">
        <img
          src="https://i.pravatar.cc/40"
          alt="avatar"
          className="rounded-full w-10 h-10"
        />
        <div>
          <div className="font-semibold text-sm">Duongbinhminh10032004</div>
          <div className="text-xs text-gray-500">1/5 tasks</div>
        </div>
      </div>

      {/* Menu Items */}
      <Menu.Item key="settings" icon={<SettingOutlined />}>
        Settings
      </Menu.Item>
      <Menu.Item key="team" icon={<TeamOutlined />}>
        Add a team
      </Menu.Item>

      <Menu.Divider />

      <Menu.Item key="activity" icon={<FileTextOutlined />}>
        Activity log
      </Menu.Item>
      <Menu.Item key="print" icon={<PrinterOutlined />}>
        Print
      </Menu.Item>
      <Menu.Item key="whatsnew" icon={<GiftOutlined />}>
        What's new
      </Menu.Item>

      <Menu.Divider />

      <Menu.Item
        key="upgrade"
        icon={<StarOutlined />}
        className="text-yellow-500 font-medium"
      >
        Upgrade to Pro
      </Menu.Item>
      <Menu.Item key="sync" icon={<SyncOutlined />}>
        Sync <span className="text-gray-400 text-xs ml-2">52 minutes ago</span>
      </Menu.Item>

      <Menu.Divider />

      <Menu.Item
        onClick={handleLogout}
        key="logout"
        icon={<LogoutOutlined />}
        danger
      >
        Log out
      </Menu.Item>

      <div className="px-3 pt-2 text-[11px] text-gray-400 flex justify-between">
        <span>v9048</span>
        <span>Changelog</span>
      </div>
    </Menu>
  );
}
