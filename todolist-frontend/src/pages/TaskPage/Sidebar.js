import { Menu } from "antd";
import {
  InboxOutlined,
  CalendarOutlined,
  SearchOutlined,
  CheckCircleOutlined,
  PlusOutlined,
} from "@ant-design/icons";

export default function Sidebar() {
  return (
    <div className="w-64 border-r h-screen flex flex-col bg-white">
      {/* Profile */}
      <div className="flex items-center gap-2 p-4 border-b">
        <img
          src="https://i.pravatar.cc/40"
          alt="avatar"
          className="w-8 h-8 rounded-full"
        />
        <span className="font-medium">Duongbinhm...</span>
      </div>

      {/* Add task */}
      <button className="flex items-center gap-2 text-red-500 font-medium hover:text-red-600 px-4 py-2">
        <PlusOutlined /> Add task
      </button>

      {/* Menu */}
      <Menu
        mode="inline"
        defaultSelectedKeys={["inbox"]}
        items={[
          { key: "search", icon: <SearchOutlined />, label: "Search" },
          { key: "inbox", icon: <InboxOutlined />, label: "Inbox" },
          {
            key: "today",
            icon: <CalendarOutlined />,
            label: (
              <div className="flex justify-between w-full">
                <span>Today</span>
                <span className="text-red-500">2</span>
              </div>
            ),
          },
          { key: "upcoming", icon: <CalendarOutlined />, label: "Upcoming" },
          {
            key: "completed",
            icon: <CheckCircleOutlined />,
            label: "Completed",
          },
        ]}
      />

      {/* Projects */}
      <div className="mt-4 px-4 text-gray-500">My Projects</div>
      <Menu
        mode="inline"
        items={[
          {
            key: "project1",
            label: (
              <div className="flex justify-between w-full">
                <span># Getting Started 👋</span>
                <span className="text-gray-400">18</span>
              </div>
            ),
          },
        ]}
      />

      <div className="mt-auto border-t p-4 text-sm text-gray-500 space-y-2">
        <div className="cursor-pointer hover:text-black">+ Add a team</div>
        <div className="cursor-pointer hover:text-black">Help & resources</div>
      </div>
    </div>
  );
}
