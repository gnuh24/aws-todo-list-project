import { MoreOutlined } from "@ant-design/icons";
import { Dropdown, Menu } from "antd";
import { useState } from "react";

export default function SidebarItem({
  icon,
  label,
  count,
  active,
  onClick,
  isProject,
  project,
  onDeleteProject,
  onArchiveProject,
  onEdit,
  className = "",
  countClass = "",
}) {
  const [hovered, setHovered] = useState(false);

  // MENU DROPDOWN CHO PROJECT
  const projectMenu = (
    <Menu
      onClick={({ key }) => {
        if (key === "Delete") {
          onDeleteProject(project); // gọi hàm delete gửi lên Sidebar
        }
        if (key === "edit") {
          onEdit(project); // gọi hàm delete gửi lên Sidebar
        }
        if (key === "archive") {
          onArchiveProject(project); // gọi hàm delete gửi lên Sidebar
        }
      }}
      items={[
        { key: "add-above", label: "Add project above" },
        { key: "add-below", label: "Add project below" },
        { type: "divider" },
        { key: "edit", label: "Edit project" },
        { key: "favorite", label: "Add to favorites" },
        { key: "duplicate", label: "Duplicate" },
        { type: "divider" },
        { key: "share", label: "Share" },
        { key: "copy-link", label: "Copy link to project" },
        { type: "divider" },
        { key: "import", label: "Import from CSV" },
        { key: "export", label: "Export as CSV" },
        { type: "divider" },
        { key: "activity", label: "Activity log" },
        { key: "calendar", label: "Calendar feed" },
        { type: "divider" },
        { key: "archive", label: "Archive", danger: true },
        { key: "Delete", label: "Delete", danger: true },
      ]}
    />
  );

  return (
    <div
      onClick={onClick}
      className={`group flex justify-between items-center px-3 py-2 cursor-pointer rounded-md 
        transition-colors duration-150 select-none
        ${
          active
            ? "bg-red-50 text-red-600 font-medium"
            : "text-gray-700 hover:bg-gray-50"
        } ${className}`}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
    >
      {/* ICON + LABEL */}
      <div className="flex items-center gap-2">
        {icon}
        <span className="truncate text-[13px]">{label}</span>
      </div>

      {/* COUNT */}
      {count && <span className={`text-xs ${countClass}`}>{count}</span>}

      {isProject && (
        <Dropdown overlay={projectMenu} trigger={["click"]}>
          <MoreOutlined
            className={`
        ml-2 text-gray-400 hover:text-gray-700 
        text-[16px] px-1 rounded-md 
        transition-opacity duration-150
        ${hovered ? "opacity-100" : "opacity-0"}
      `}
            onClick={(e) => e.stopPropagation()}
          />
        </Dropdown>
      )}
    </div>
  );
}
