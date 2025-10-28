import React, { useState } from "react";
import {
  InboxOutlined,
  FolderOutlined,
  TagOutlined,
  DownOutlined,
} from "@ant-design/icons";
import { Dropdown, Input } from "antd";

export default function ProjectSelectDropdown({
  selected = "Getting Started 👋",
  onSelect,
}) {
  const [search, setSearch] = useState("");

  const projects = [
    { id: 1, name: "Inbox", icon: <InboxOutlined />, group: "default" },
    {
      id: 2,
      name: "Getting Started 👋",
      group: "My Projects",
      icon: <TagOutlined />,
    },
    {
      id: 3,
      name: "Capture > Review > Complete",
      group: "My Projects",
      icon: <FolderOutlined />,
    },
    {
      id: 4,
      name: "Build (or Rebuild) Your Systems",
      group: "My Projects",
      icon: <FolderOutlined />,
    },
    {
      id: 5,
      name: "Level Up 🏆",
      group: "My Projects",
      icon: <FolderOutlined />,
    },
  ];

  const filtered = projects.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase())
  );

  const menu = (
    <div className="bg-white rounded-xl shadow-lg p-2 w-72">
      {/* Search input */}
      <Input
        placeholder="Type a project name"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        className="mb-2"
        size="small"
      />

      {/* Project list */}
      <div className="max-h-64 overflow-y-auto">
        {filtered.some((p) => p.group === "default") && (
          <div
            className="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 rounded-md cursor-pointer"
            onClick={() => onSelect?.("Inbox")}
          >
            <InboxOutlined className="text-gray-500" /> Inbox
          </div>
        )}

        {/* My Projects Section */}
        {filtered.some((p) => p.group === "My Projects") && (
          <>
            <div className="font-semibold text-gray-700 px-3 py-2">
              My Projects
            </div>
            {filtered
              .filter((p) => p.group === "My Projects")
              .map((p) => (
                <div
                  key={p.id}
                  className={`flex items-center justify-between px-3 py-2 rounded-md cursor-pointer hover:bg-gray-100 ${
                    selected === p.name ? "bg-gray-50" : ""
                  }`}
                  onClick={() => onSelect?.(p.name)}
                >
                  <div className="flex items-center gap-2">
                    {p.icon} {p.name}
                  </div>
                  {selected === p.name && (
                    <span className="text-red-500 text-xs">✔</span>
                  )}
                </div>
              ))}
          </>
        )}
      </div>
    </div>
  );

  return (
    <Dropdown overlay={menu} trigger={["click"]} placement="bottomLeft">
      <div className="flex items-center gap-1 cursor-pointer border rounded-md px-2 py-1 hover:bg-gray-50">
        <TagOutlined className="text-gray-500" />
        <span>{selected}</span>
        <DownOutlined className="text-xs" />
      </div>
    </Dropdown>
  );
}
