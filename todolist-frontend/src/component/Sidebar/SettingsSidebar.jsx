import {
  UserOutlined,
  BgColorsOutlined,
  BellOutlined,
  CheckSquareOutlined,
  CloudOutlined,
  TeamOutlined,
  CalendarOutlined,
  AppstoreOutlined,
  DatabaseOutlined,
  DashboardOutlined,
} from "@ant-design/icons";

export default function SettingsSidebar() {
  const menuItems = [
    { label: "Account", icon: <UserOutlined />, active: true },
    { label: "General", icon: <DashboardOutlined /> },
    { label: "Subscription", icon: <CloudOutlined /> },
    { label: "Theme", icon: <BgColorsOutlined /> },
    { label: "Sidebar", icon: <AppstoreOutlined /> },
    { label: "Quick Add", icon: <CheckSquareOutlined /> },
    { label: "Productivity", icon: <DatabaseOutlined /> },
    { label: "Reminders", icon: <BellOutlined /> },
    { label: "Notifications", icon: <BellOutlined /> },
    { label: "Backups", icon: <CloudOutlined /> },
    { label: "Integrations", icon: <AppstoreOutlined /> },
    { label: "Calendars", icon: <CalendarOutlined /> },
  ];

  return (
    <div className="w-64 bg-[#f8f6f4] border-r border-gray-300 h-full px-3 py-4 overflow-y-auto">
      <h2 className="px-3 py-1 text-[15px] font-semibold mb-2">Settings</h2>

      {menuItems.map((item) => (
        <div
          key={item.label}
          className={`flex items-center gap-3 px-3 py-2 cursor-pointer rounded-md text-[15px]
          ${
            item.active
              ? "bg-[#fce9e4] text-[#d1453b] font-medium"
              : "hover:bg-gray-200"
          }`}
        >
          {item.icon}
          {item.label}
        </div>
      ))}

      <div className="mt-6">
        <h4 className="text-xs uppercase text-gray-500 px-3 mb-2">
          Hút hầm cầu Quang Hồng
        </h4>

        <div className="flex items-center gap-3 px-3 py-2 cursor-pointer rounded-md hover:bg-gray-200">
          <div className="w-6 h-6 rounded bg-red-600 flex items-center justify-center text-white font-bold text-sm">
            H
          </div>
          <span>General</span>
        </div>

        <div className="flex items-center gap-3 px-3 py-2 cursor-pointer rounded-md hover:bg-gray-200">
          <TeamOutlined />
          People
        </div>

        <div className="flex items-center gap-3 px-3 py-2 cursor-pointer rounded-md hover:bg-gray-200">
          <span className="text-gray-600">+ Add team</span>
        </div>
      </div>
    </div>
  );
}
