import {
  UserOutlined,
  BellOutlined,
} from "@ant-design/icons";
import {SETTINGS_PAGE} from "../Modal/SettingsModal";

export default function SettingsSidebar({ page, setPage }) {
  const menuItems = [
    {
      key: SETTINGS_PAGE.ACCOUNT,
      label: "Account",
      icon: <UserOutlined />,
    },
    {
      key: SETTINGS_PAGE.NOTIFICATIONS,
      label: "Notifications",
      icon: <BellOutlined />,
    },
  ];

  return (
      <div className="w-64 bg-[#f8f6f4] border-r px-3 py-4">
        <h2 className="px-3 py-1 font-semibold mb-2">Settings</h2>

        {menuItems.map(item => (
            <div
                key={item.key}
                onClick={() => setPage(item.key)}
                className={`flex items-center gap-3 px-3 py-2 rounded-md cursor-pointer
            ${
                    page === item.key
                        ? "bg-[#fce9e4] text-[#d1453b] font-medium"
                        : "hover:bg-gray-200"
                }
          `}
            >
              {item.icon}
              {item.label}
            </div>
        ))}
      </div>
  );
}

